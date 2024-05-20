package smart.budget.diary.models;

//Represents an income or expense type in the EasyBudget app.
public class IncomeExpenseType {
    public final String name;
    public final int color;
    public final int iconID;

    public IncomeExpenseType(String name, int color, int iconID) {
        this.name = name;
        this.color = color;
        this.iconID = iconID;
    }
}
