package com.SunriseDental.Service.Notification;

import com.SunriseDental.Model.Appointment;
import com.SunriseDental.Model.Patient;

/**
 * Strategy Pattern: defines a common contract for any way of notifying a
 * patient about their appointment. New channels (WhatsApp, push notification,
 * etc.) can be added later by implementing this interface, without changing
 * NotificationService or any code that already depends on it.
 */
public interface NotificationChannel {
    boolean send(Patient patient, Appointment appointment, String appointmentNumber);
}
