package com.SunriseDental.Service;

import com.SunriseDental.Dao.AppointmentDAO;
import com.SunriseDental.Model.Dentist;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Works out which appointment slots a doctor actually has open on a given
 * day, instead of leaving the patient to type in any time they like.
 * A doctor's "consultation_days" field (e.g. "Mon, Wed, Fri — 9:00 AM to
 * 4:00 PM") is parsed into working days + hours; slots are generated at a
 * fixed interval across those hours and then anything already booked (or
 * already in the past, for today) is removed.
 */
public class AvailabilityService {

    private static final int SLOT_MINUTES = 30;

    // Used whenever a doctor's consultation_days text is missing or can't
    // be parsed, so booking still works with a sensible default clinic day
    // rather than failing outright.
    private static final LocalTime DEFAULT_START = LocalTime.of(9, 0);
    private static final LocalTime DEFAULT_END = LocalTime.of(17, 0);

    private static final DateTimeFormatter SLOT_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter HOUR_PARSE_FORMAT = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    private static final Pattern HOURS_PATTERN =
            Pattern.compile("(\\d{1,2}:\\d{2}\\s*[APap][Mm])\\s*to\\s*(\\d{1,2}:\\d{2}\\s*[APap][Mm])");

    private static final Map<String, DayOfWeek> DAY_ABBREVIATIONS = new HashMap<>();
    static {
        DAY_ABBREVIATIONS.put("mon", DayOfWeek.MONDAY);
        DAY_ABBREVIATIONS.put("tue", DayOfWeek.TUESDAY);
        DAY_ABBREVIATIONS.put("wed", DayOfWeek.WEDNESDAY);
        DAY_ABBREVIATIONS.put("thu", DayOfWeek.THURSDAY);
        DAY_ABBREVIATIONS.put("fri", DayOfWeek.FRIDAY);
        DAY_ABBREVIATIONS.put("sat", DayOfWeek.SATURDAY);
        DAY_ABBREVIATIONS.put("sun", DayOfWeek.SUNDAY);
    }

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    /** Result of checking one doctor's availability on one date. */
    public static class DayAvailability {
        /** Whether the doctor consults at all on this day of the week. */
        public final boolean workingDay;
        /** Open, bookable "HH:mm" slots — already excludes taken/past times. */
        public final List<String> slots;

        DayAvailability(boolean workingDay, List<String> slots) {
            this.workingDay = workingDay;
            this.slots = slots;
        }
    }

    public DayAvailability getAvailability(Dentist dentist, LocalDate date) {
        Set<DayOfWeek> workingDays = parseWorkingDays(dentist.getConsultationDays());
        boolean isWorkingDay = workingDays.isEmpty() || workingDays.contains(date.getDayOfWeek());
        if (!isWorkingDay) {
            return new DayAvailability(false, Collections.emptyList());
        }

        LocalTime[] hours = parseHours(dentist.getConsultationDays());
        Set<String> taken = new HashSet<>(
                appointmentDAO.getBookedTimes(dentist.getDentistId(), java.sql.Date.valueOf(date)));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<String> slots = new ArrayList<>();
        LocalTime cursor = hours[0];
        while (cursor.isBefore(hours[1])) {
            String slotStr = cursor.format(SLOT_FORMAT);
            boolean isPast = date.isEqual(today) && !cursor.isAfter(now);
            if (!taken.contains(slotStr) && !isPast) {
                slots.add(slotStr);
            }
            cursor = cursor.plusMinutes(SLOT_MINUTES);
        }
        return new DayAvailability(true, slots);
    }

    private Set<DayOfWeek> parseWorkingDays(String consultationDays) {
        Set<DayOfWeek> days = new HashSet<>();
        if (consultationDays == null || consultationDays.isBlank()) {
            return days; // empty = treated as "every day" by the caller
        }
        String daysPart = consultationDays.split("[—-]")[0];
        for (String token : daysPart.split(",")) {
            String key = token.trim().toLowerCase(Locale.ENGLISH);
            if (key.length() >= 3) {
                key = key.substring(0, 3);
            }
            DayOfWeek d = DAY_ABBREVIATIONS.get(key);
            if (d != null) {
                days.add(d);
            }
        }
        return days;
    }

    private LocalTime[] parseHours(String consultationDays) {
        if (consultationDays != null) {
            Matcher m = HOURS_PATTERN.matcher(consultationDays);
            if (m.find()) {
                try {
                    LocalTime start = LocalTime.parse(m.group(1).toUpperCase(Locale.ENGLISH).trim(), HOUR_PARSE_FORMAT);
                    LocalTime end = LocalTime.parse(m.group(2).toUpperCase(Locale.ENGLISH).trim(), HOUR_PARSE_FORMAT);
                    if (start.isBefore(end)) {
                        return new LocalTime[]{start, end};
                    }
                } catch (Exception ignored) {
                    // fall through to the default hours below
                }
            }
        }
        return new LocalTime[]{DEFAULT_START, DEFAULT_END};
    }
}
