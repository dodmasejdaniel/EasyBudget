package smart.budget.diary.ui.categories;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import smart.budget.diary.R;
import smart.budget.diary.base.BaseActivity;
import smart.budget.diary.ui.main.home.HomeFragment;
public class AddCustomCategoryActivity extends BaseActivity {
    private TextInputEditText selectNameEditText;
    private Button addCustomCategoryButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom_category);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add custom category");
        if (HomeFragment.user == null)
            return;
        selectNameEditText = findViewById(R.id.select_name_edittext);
        addCustomCategoryButton = findViewById(R.id.add_custom_category_button);

        // add new cat. button
        addCustomCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustomCategory(selectNameEditText.getText().toString());
            }
        });
    }
    private void addCustomCategory(String categoryName) {
        if (categoryName == null || categoryName.length() == 0)
            return;
//        first add in user's customCategories array and update in firebase
        HomeFragment.user.customCategories.add(categoryName);
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(getUid()).child("customCategories").setValue(
                        HomeFragment.user.customCategories);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
