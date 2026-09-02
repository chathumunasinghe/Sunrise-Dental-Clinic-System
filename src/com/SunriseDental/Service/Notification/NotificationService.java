package com.SunriseDental.Service.Notification;

import com.SunriseDental.Model.Appointment;
import com.SunriseDental.Model.Patient;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy Pattern (context class): holds a list of active
 * NotificationChannel strategies and asks each one to send the
 * confirmation, independently of how any individual channel works.
 * Channels can be added/removed here without touching AppointmentService
 * or any channel implementation.
 */
public class NotificationService {

    private final List<NotificationChannel> channels = new ArrayList<>();

    public NotificationService() {
        channels.add(new EmailNotificationChannel());
        channels.add(new SmsNotificationChannel());
    }

    /** Allows tests (or future config) to inject custom channels. */
    public NotificationService(List<NotificationChannel> customChannels) {
        this.channels.addAll(customChannels);
    }

    /**
     * Sends the confirmation through every active channel. A failure in one
     * channel (e.g. no SMS credit) does not stop the others from running,
     * and never blocks the appointment registration itself from succeeding.
     */
    public void sendAppointmentConfirmation(Patient patient, Appointment appointment, String appointmentNumber) {
        for (NotificationChannel channel : channels) {
            try {
                channel.send(patient, appointment, appointmentNumber);
            } catch (Exception e) {
                e.printStackTrace(); // log and continue with remaining channels
            }
        }
    }
}
