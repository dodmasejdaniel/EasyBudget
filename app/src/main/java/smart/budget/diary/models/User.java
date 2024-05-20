package smart.budget.diary.models;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smart.budget.diary.util.CalendarHelper;

/**
 * Store a user data, including budget limits and custom categories.
 */
@IgnoreExtraProperties
public class User {

    public Map<String,List<Long>> limit = new HashMap<>();;

    public long sum;
    public List<String> customCategories = new ArrayList<>();

    public User() {
        Calendar calendarMinDate = CalendarHelper.getMinCalendar();

        Calendar calendarMaxDate = Calendar.getInstance();
        calendarMaxDate.set(Calendar.YEAR, 2029);
        calendarMaxDate.set(Calendar.MONTH, 11);
        calendarMaxDate.set(Calendar.DATE, 31);
        calendarMaxDate.set(Calendar.HOUR_OF_DAY, 23);
        calendarMaxDate.set(Calendar.MINUTE, 59);
        calendarMaxDate.set(Calendar.SECOND, 59);

        while (calendarMinDate.before(calendarMaxDate)) {
            String year = calendarMinDate.get(Calendar.YEAR)+"";
            List<Long> budgets = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                budgets.add(0L); // Initialize each month's budget to 0 if user is new
            }
            limit.put(year, budgets);
            calendarMinDate.add(Calendar.YEAR, 1); // Move to the next year
        }
    }




    public long getSumOfBudgetsOfSpecificMonths(Calendar calendarMinDate, Calendar calendarMaxDate) {

        int startYear = calendarMinDate.get(Calendar.YEAR);
        int endYear = calendarMaxDate.get(Calendar.YEAR);

        long sum = 0;
        for (int year = startYear; year <= endYear; year++) {
            List<Long> budgets = limit.get(year+"");
            if (budgets != null) {
                int startMonth = (year == startYear) ? calendarMinDate.get(Calendar.MONTH) : 0;
                int endMonth = (year == endYear) ? calendarMaxDate.get(Calendar.MONTH) : 11;

                for (int i = startMonth; i <= endMonth; i++) {
                    sum += budgets.get(i);
                }
            }
        }
        return sum;
    }


}