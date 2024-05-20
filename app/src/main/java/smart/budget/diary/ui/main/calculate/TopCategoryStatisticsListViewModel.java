package smart.budget.diary.ui.main.calculate;

import smart.budget.diary.models.Category;

public class TopCategoryStatisticsListViewModel {
    private final float percentage;
    private long money;
    private final Category category;
    private String categoryName;

    public TopCategoryStatisticsListViewModel(Category category, String categoryName,  long money, float percentage) {
        this.category = category;
        this.categoryName = categoryName;
       
        this.money = money;
        this.percentage = percentage;

    }

    public String getCategoryName() {
        return categoryName;
    }


    public long getMoney() {
        return money;
    }

    public Category getCategory() {
        return category;
    }

    public float getPercentage() {
        return percentage;
    }
}
