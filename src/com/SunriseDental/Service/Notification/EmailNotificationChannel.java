package com.SunriseDental.Service.Notification;

import com.SunriseDental.Model.Appointment;
import com.SunriseDental.Model.Patient;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Concrete Strategy: sends the appointment confirmation by email.
 *
 * This talks SMTP directly over a plain java.net.Socket (upgraded to TLS
 * with STARTTLS) instead of using the Jakarta Mail / JavaMail API. That
 * means NO extra JAR needs to be added to WEB-INF/lib for this class to
 * compile or run — everything it uses ships with the JDK itself.
 *
 * Fill in SENDER_EMAIL / SENDER_PASSWORD below (a Gmail "App Password",
 * not your normal Gmail password, if using Gmail) before enabling this.
 */
public class EmailNotificationChannel implements NotificationChannel {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String SENDER_EMAIL = "sunrisedentalclinic@example.com";
    private static final String SENDER_PASSWORD = "APP_PASSWORD_HERE";
    private static final int TIMEOUT_MS = 10000;

    @Override
    public boolean send(Patient patient, Appointment appointment, String appointmentNumber) {
        if (patient.getEmail() == null || patient.getEmail().trim().isEmpty()) {
            return false; // no email on file — skip silently, don't fail the whole registration
        }
        if ("APP_PASSWORD_HERE".equals(SENDER_PASSWORD) || SENDER_PASSWORD.trim().isEmpty()) {
            // Not configured yet — skip instead of attempting (and waiting up to
            // TIMEOUT_MS for) a live SMTP connection that's guaranteed to fail.
            return false;
        }

        String subject = "Sunrise Dental Clinic - Appointment Confirmation";
        String body =
                "Dear " + patient.getName() + ",\r\n\r\n" +
                "Your appointment has been confirmed.\r\n" +
                "Appointment Number: " + appointmentNumber + "\r\n" +
                "Date: " + appointment.getAppointmentDate() + "\r\n" +
                "Time: " + appointment.getAppointmentTime() + "\r\n\r\n" +
                "Please arrive 10 minutes early.\r\n\r\n" +
                "Sunrise Dental Clinic";

        try {
            sendSmtp(patient.getEmail(), subject, body);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void sendSmtp(String toEmail, String subject, String body) throws IOException {
        try (Socket plainSocket = new Socket()) {
            plainSocket.connect(new java.net.InetSocketAddress(SMTP_HOST, SMTP_PORT), TIMEOUT_MS);
            plainSocket.setSoTimeout(TIMEOUT_MS);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(plainSocket.getInputStream(), StandardCharsets.UTF_8));
            OutputStream rawOut = plainSocket.getOutputStream();

            readResponse(in);                                  // 220 greeting
            write(rawOut, "EHLO localhost");
            readResponse(in);                                  // 250-... capabilities
            write(rawOut, "STARTTLS");
            readResponse(in);                                  // 220 ready to start TLS

            // Upgrade the existing socket to TLS for the rest of the conversation.
            SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try (SSLSocket sslSocket = (SSLSocket) sslFactory.createSocket(plainSocket, SMTP_HOST, SMTP_PORT, true)) {
                sslSocket.startHandshake();

                BufferedReader sIn = new BufferedReader(
                        new InputStreamReader(sslSocket.getInputStream(), StandardCharsets.UTF_8));
                OutputStream sOut = sslSocket.getOutputStream();

                write(sOut, "EHLO localhost");
                readResponse(sIn);

                write(sOut, "AUTH LOGIN");
                readResponse(sIn);                             // 334 base64("Username:")
                write(sOut, Base64.getEncoder().encodeToString(SENDER_EMAIL.getBytes(StandardCharsets.UTF_8)));
                readResponse(sIn);                             // 334 base64("Password:")
                write(sOut, Base64.getEncoder().encodeToString(SENDER_PASSWORD.getBytes(StandardCharsets.UTF_8)));
                readResponse(sIn);                             // 235 authentication successful

                write(sOut, "MAIL FROM:<" + SENDER_EMAIL + ">");
                readResponse(sIn);
                write(sOut, "RCPT TO:<" + toEmail + ">");
                readResponse(sIn);
                write(sOut, "DATA");
                readResponse(sIn);                             // 354 start mail input

                StringBuilder message = new StringBuilder();
                message.append("From: ").append(SENDER_EMAIL).append("\r\n");
                message.append("To: ").append(toEmail).append("\r\n");
                message.append("Subject: ").append(subject).append("\r\n");
                message.append("Content-Type: text/plain; charset=UTF-8\r\n");
                message.append("\r\n");
                message.append(body.replace("\r\n.", "\r\n..")); // dot-stuffing per RFC 5321
                message.append("\r\n.");
                write(sOut, message.toString());
                readResponse(sIn);                             // 250 message accepted

                write(sOut, "QUIT");
                readResponse(sIn);
            }
        }
    }

    private void write(OutputStream out, String line) throws IOException {
        out.write((line + "\r\n").getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

    /** Reads one SMTP response, including multi-line replies (e.g. "250-...\r\n250 ..."). */
    private String readResponse(BufferedReader in) throws IOException {
        StringBuilder full = new StringBuilder();
        String line;
        do {
            line = in.readLine();
            if (line == null) {
                throw new IOException("SMTP server closed the connection unexpectedly.");
            }
            full.append(line).append('\n');
        } while (line.length() >= 4 && line.charAt(3) == '-');

        int code = Integer.parseInt(line.substring(0, 3));
        if (code >= 400) {
            throw new IOException("SMTP server returned an error: " + full);
        }
        return full.toString();
    }
}
