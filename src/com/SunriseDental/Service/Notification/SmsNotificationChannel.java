package com.SunriseDental.Service.Notification;

import com.SunriseDental.Model.Appointment;
import com.SunriseDental.Model.Patient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Concrete Strategy: sends the appointment confirmation by SMS through a
 * local SMS gateway's REST API (e.g. a Sri Lankan provider such as
 * notify.lk / Dialog / Hutch SMS API). The endpoint and API key are
 * placeholders — replace them with the clinic's actual gateway account
 * before deployment. Isolating this behind the same NotificationChannel
 * interface as email means the calling code (NotificationService) never
 * needs to know or care which channels are active.
 */
public class SmsNotificationChannel implements NotificationChannel {

    private static final String SMS_GATEWAY_URL = "https://app.notify.lk/api/v1/send";
    private static final String USER_ID = "YOUR_USER_ID";
    private static final String API_KEY = "YOUR_API_KEY";
    private static final String SENDER_ID = "SunriseDental";

    @Override
    public boolean send(Patient patient, Appointment appointment, String appointmentNumber) {
        if (patient.getContactNumber() == null || patient.getContactNumber().trim().isEmpty()) {
            return false;
        }
        if ("YOUR_USER_ID".equals(USER_ID) || "YOUR_API_KEY".equals(API_KEY)) {
            // Not configured yet — skip instead of calling out to a gateway
            // account that doesn't exist.
            return false;
        }

        String message = "Sunrise Dental: Appointment " + appointmentNumber + " confirmed for "
                + appointment.getAppointmentDate() + " " + appointment.getAppointmentTime() + ".";

        try {
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
            String url = String.format("%s?user_id=%s&api_key=%s&sender_id=%s&to=%s&message=%s",
                    SMS_GATEWAY_URL, USER_ID, API_KEY, SENDER_ID, patient.getContactNumber(), encodedMessage);

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(java.time.Duration.ofSeconds(8))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(java.time.Duration.ofSeconds(8))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
