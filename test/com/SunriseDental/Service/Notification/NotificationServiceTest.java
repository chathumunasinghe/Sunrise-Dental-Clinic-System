package com.SunriseDental.Service.Notification;

import com.SunriseDental.Model.Appointment;
import com.SunriseDental.Model.Patient;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationServiceTest {

    /** A fake channel used only for testing — avoids sending real emails/SMS in tests. */
    static class FakeChannel implements NotificationChannel {
        AtomicInteger callCount = new AtomicInteger(0);
        boolean result;

        FakeChannel(boolean result) { this.result = result; }

        @Override
        public boolean send(Patient patient, Appointment appointment, String appointmentNumber) {
            callCount.incrementAndGet();
            return result;
        }
    }

    @Test
    void testSendAppointmentConfirmation_CallsAllChannels() {
        FakeChannel channel1 = new FakeChannel(true);
        FakeChannel channel2 = new FakeChannel(true);
        NotificationService service = new NotificationService(Arrays.asList(channel1, channel2));

        Patient patient = new Patient("PT0001", "Test Patient", "Colombo", "0770000000", "test@example.com");
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(Date.valueOf("2026-09-01"));
        appointment.setAppointmentTime("10:00");

        service.sendAppointmentConfirmation(patient, appointment, "APT0001");

        assertEquals(1, channel1.callCount.get(), "Every registered channel should be invoked exactly once");
        assertEquals(1, channel2.callCount.get());
    }

    @Test
    void testSendAppointmentConfirmation_OneChannelFailing_DoesNotStopOthers() {
        FakeChannel failingChannel = new FakeChannel(false);
        FakeChannel workingChannel = new FakeChannel(true);
        NotificationService service = new NotificationService(Arrays.asList(failingChannel, workingChannel));

        Patient patient = new Patient("PT0002", "Another Patient", "Kandy", "0711112222", "another@example.com");
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(Date.valueOf("2026-09-02"));
        appointment.setAppointmentTime("11:00");

        assertDoesNotThrow(() ->
            service.sendAppointmentConfirmation(patient, appointment, "APT0002")
        );

        assertEquals(1, workingChannel.callCount.get(), "A failing channel must not prevent other channels from running");
    }
}
