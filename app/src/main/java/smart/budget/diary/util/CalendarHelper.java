package smart.budget.diary.util;

import androidx.annotation.NonNull;

import com.google.type.DateTime;

import java.time.LocalDate;
import java.util.Calendar;

//Helper class for managing calendar-related operations.
public class CalendarHelper {
    public static Calendar getCurrentMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_MONTH,1);
        return cal;
    }


    public static Calendar getCurrentMonthEnd() {
        Calendar cal = getCurrentMonthStart();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.clear(Calendar.MILLISECOND);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DATE, -1);
        return cal;
    }

    public static int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH);
    }

    public static String getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR)+"";
    }

    public static String getPreviousYear() {
        Calendar calendar = Calendar.getInstance();
        return (calendar.get(Calendar.YEAR)-1)+"";
    }
    public static Calendar getMinCalendar() {
        Calendar calendarMinDate = Calendar.getInstance();
        calendarMinDate.set(2024, 0, 1);
        calendarMinDate.set(Calendar.HOUR_OF_DAY, 0);
        calendarMinDate.set(Calendar.MINUTE, 0);
        calendarMinDate.set(Calendar.SECOND, 0);
        return calendarMinDate;
    }
}
