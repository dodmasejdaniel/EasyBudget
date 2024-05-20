package smart.budget.diary.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Represents an income or expense record in the app.
 * Marked with @IgnoreExtraProperties to ignore extra properties when reading from Firebase Database.
 */
@IgnoreExtraProperties
public class IncomeExpenseRecord {
public String key;
    public String categoryID;
    public String description;
    public long date;
    public long amount;

    IncomeExpenseRecord(){

    }
    public IncomeExpenseRecord(String categoryID, String description, long date, long amount) {
        this.categoryID = categoryID;
        this.description = description;
        this.date = -date;
        this.amount = amount;
    }

}