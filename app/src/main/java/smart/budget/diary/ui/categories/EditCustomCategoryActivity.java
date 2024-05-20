package smart.budget.diary.ui.categories;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

import smart.budget.diary.R;
import smart.budget.diary.base.BaseActivity;
import smart.budget.diary.ui.main.history.HistoryFragment;
import smart.budget.diary.ui.main.home.HomeFragment;

public class EditCustomCategoryActivity extends BaseActivity {
    private TextInputEditText selectNameEditText;
    private Button editCustomCategoryButton;
    private int categoryID;
    private Button removeCustomCategoryButton;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryID = getIntent().getExtras().getInt("category-id", 99);
        categoryName = getIntent().getExtras().getString("category-name");
        setContentView(R.layout.activity_edit_custom_category);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit custom category");

        selectNameEditText = findViewById(R.id.select_name_edittext);
        selectNameEditText.setText(categoryName);
        editCustomCategoryButton = findViewById(R.id.edit_custom_category_button);
        editCustomCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCustomCategory(selectNameEditText.getText().toString());
            }
        });
        removeCustomCategoryButton = findViewById(R.id.remove_custom_category_button);
        removeCustomCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // first remove cate. from customCategories and then update it on firebase
                HomeFragment.user.customCategories.remove(categoryID);
                for(int i = 0; i< HistoryFragment.walletEntryListDataSetCZ.size(); i++){
                    if(HistoryFragment.walletEntryListDataSetCZ.get(i).categoryID.equals(getIntent().getExtras().getString("category-name", ""))){
                        HistoryFragment.walletEntryListDataSetCZ.get(i).categoryID=":default";
                    }
                }
                FirebaseDatabase.getInstance().getReference().child("wallet-entries").child(getUid())
                        .child("default").setValue(HistoryFragment.walletEntryListDataSetCZ);
                FirebaseDatabase.getInstance().getReference()
                        .child("users").child(getUid()).child("customCategories").setValue(
                                HomeFragment.user.customCategories);
                finish();
            }
        });
    }

    private void editCustomCategory(String categoryName) {
        if (categoryName == null || categoryName.length() == 0)
            return;
        HomeFragment.user.customCategories.set(categoryID, categoryName);
        for(int i=0;i<HistoryFragment.walletEntryListDataSetCZ.size();i++){
            if(HistoryFragment.walletEntryListDataSetCZ.get(i).categoryID.equals(getIntent().getExtras().getString("category-name", ""))){
                HistoryFragment.walletEntryListDataSetCZ.get(i).categoryID=categoryName;
            }
        }
        FirebaseDatabase.getInstance().getReference().child("wallet-entries").child(getUid())
                .child("default").setValue(HistoryFragment.walletEntryListDataSetCZ);
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
