package smart.budget.diary.ui.main.home;

import smart.budget.diary.models.Category;

// View model for top categories in the ListView
public class TopCategoryListViewModel {
    private long money;
    private final Category category;
    private String categoryName;

    // Constructor to initialize the view model
    public TopCategoryListViewModel(Category category, String categoryName, long money) {
        this.category = category;
        this.categoryName = categoryName;
        this.money = money;
    }

    // Getter for the category name
    public String getCategoryName() {
        return categoryName;
    }

    // Getter for the money amount
    public long getMoney() {
        return money;
    }

    // Getter for the category object
    public Category getCategory() {
        return category;
    }
}
