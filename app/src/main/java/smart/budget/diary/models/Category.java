package smart.budget.diary.models;

import android.content.Context;

import java.util.Objects;

//Represents a category for tracking income and expenses.
public class Category {
    private final String id;
    private String visibleName;
    private final int iconResourceID;
    private final int backgroundColor;

    public Category(String id, int iconResourceID, int backgroundColor) {
        this.id = id;
        this.iconResourceID = iconResourceID;
        this.backgroundColor = backgroundColor;
    }

    public Category(String id, String visibleName, int iconResourceID, int backgroundColor) {
        this.id = id;
        this.visibleName = visibleName;
        this.iconResourceID = iconResourceID;
        this.backgroundColor = backgroundColor;
    }

    public String getCategoryID() {
        return id;
    }

    public String getCategoryVisibleName() {
        return visibleName;
    }

    public int getIconResourceID() {
        return iconResourceID;
    }

    public int getIconColor() {
        return backgroundColor;
    }

    @Override
    public String toString() {
        return getCategoryID();
    }

}
