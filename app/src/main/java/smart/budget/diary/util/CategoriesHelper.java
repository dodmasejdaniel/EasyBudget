package smart.budget.diary.util;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import smart.budget.diary.R;
import smart.budget.diary.models.User;

import smart.budget.diary.models.Category;

public class CategoriesHelper {
    public static Category searchCategory(User user, String categoryName) {
        if(categoryName.equals(":income")){
            return new Category(":income", "Income", R.drawable.category_default, Color.parseColor("#455a64"));
        }
        for(Category category : defaultCategories) {
            if(category.getCategoryID().equals(categoryName))
                return category;
        }
        for(String entry : user.customCategories) {
            if(entry.equals(categoryName)) {
                return new Category(":"+categoryName, entry, R.drawable.category_home, Color.parseColor("#0288d1"));
            }
        }
        return createDefaultCategoryModel("Others");
    }

    public static List<Category> getCategories(User user) {
        List<Category> categories = new ArrayList<>();
        categories.addAll(Arrays.asList(defaultCategories));
        categories.addAll(getCustomCategories(user));
        return categories;
    }

    public static List<Category> getCustomCategories(User user) {
        ArrayList<Category> categories = new ArrayList<>();
        for(String entry : user.customCategories) {
            Category cate=new Category(entry, entry, R.drawable.category_home, Color.parseColor("#0288d1"));
            categories.add(cate);
        }
        return categories;
    }

    public static Category[] defaultCategories = new Category[]{
            new Category(":food", "Food", R.drawable.category_food, Color.parseColor("#c2185b")),
            new Category(":clothing", "Clothing", R.drawable.category_clothing, Color.parseColor("#d32f2f")),
            new Category(":gas_station", "Fuel", R.drawable.category_gas_station, Color.parseColor("#7b1fa2")),
            new Category(":gaming", "Gaming", R.drawable.category_gaming, Color.parseColor("#512da8")),
            new Category(":gift", "Gift", R.drawable.category_gift, Color.parseColor("#303f9f")),
            new Category(":holidays", "Holidays", R.drawable.category_holidays, Color.parseColor("#1976d2")),
            new Category(":kids", "Kids", R.drawable.category_kids, Color.parseColor("#0097a7")),
            new Category(":pharmacy", "Pharmacy", R.drawable.category_pharmacy, Color.parseColor("#00796b")),
            new Category(":repair", "Repair", R.drawable.category_repair, Color.parseColor("#388e3c")),
            new Category(":shopping", "Shopping", R.drawable.category_shopping, Color.parseColor("#689f38")),
            new Category(":sport", "Sport", R.drawable.category_sport, Color.parseColor("#afb42b")),
            new Category(":transfer", "Transfer", R.drawable.category_transfer, Color.parseColor("#fbc02d")),
            new Category(":transport", "Transport", R.drawable.category_transport, Color.parseColor("#ffa000")),
            createDefaultCategoryModel("Others")
    };

    public static Category createDefaultCategoryModel(String visibleName) {
        return new Category(":default", visibleName, R.drawable.category_home,
                Color.parseColor("#0288d1"));
    }


}
