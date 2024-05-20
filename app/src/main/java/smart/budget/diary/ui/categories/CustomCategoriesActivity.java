package smart.budget.diary.ui.categories;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;
import smart.budget.diary.R;
import smart.budget.diary.base.BaseActivity;
import smart.budget.diary.models.User;
import smart.budget.diary.models.Category;
import smart.budget.diary.ui.main.home.HomeFragment;
import smart.budget.diary.util.CategoriesHelper;

// display custom categories in list
public class CustomCategoriesActivity extends BaseActivity {
    private User user;
    private ArrayList<Category> customCategoriesList;
    private CustomCategoriesAdapter customCategoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_categories);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Custom categories");

        customCategoriesList = new ArrayList<>();
        ListView listView = findViewById(R.id.custom_categories_list_view);
        customCategoriesAdapter = new CustomCategoriesAdapter(this, customCategoriesList, getApplicationContext());
        listView.setAdapter(customCategoriesAdapter);
        findViewById(R.id.add_custom_category_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomCategoriesActivity.this, AddCustomCategoryActivity.class));
            }
        });
    }

    private void dataUpdated() {
        CustomCategoriesActivity.this.user = HomeFragment.user;
        if (user == null)
            return;
        customCategoriesList.clear();
        customCategoriesList.addAll(CategoriesHelper.getCustomCategories(user));
        customCategoriesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataUpdated();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
