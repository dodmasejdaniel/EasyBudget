package smart.budget.diary.ui.records;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import smart.budget.diary.R;
import smart.budget.diary.base.BaseActivity;
import smart.budget.diary.models.User;
import smart.budget.diary.models.IncomeExpenseRecord;
import smart.budget.diary.models.Category;
import smart.budget.diary.models.IncomeExpenseType;
import smart.budget.diary.ui.main.home.HomeFragment;
import smart.budget.diary.util.CalendarHelper;
import smart.budget.diary.util.CategoriesHelper;
import smart.budget.diary.util.CurrencyHelper;

public class AddIncomeExpenseActivity extends BaseActivity {
    private Spinner selectCategorySpinner;
    private TextInputEditText selectNameEditText;
    private Calendar chosenDate;
    private TextInputEditText selectAmountEditText;
    private TextView chooseDayTextView;
    private Spinner selectTypeSpinner;
    private User user;
    private TextInputLayout selectAmountInputLayout;
    private TextInputLayout selectNameInputLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for this activity
        setContentView(R.layout.activity_add_wallet_entry);

        // Set up the toolbar as the action bar for this activity
        setSupportActionBar(findViewById(R.id.toolbar));

        // Enable the back button in the toolbar for navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the title of the toolbar
        getSupportActionBar().setTitle("Add new record");


        selectCategorySpinner = findViewById(R.id.select_category_spinner);
        selectNameEditText = findViewById(R.id.select_name_edittext);
        selectNameInputLayout = findViewById(R.id.select_name_inputlayout);
        selectTypeSpinner = findViewById(R.id.select_type_spinner);
        Button addEntryButton = findViewById(R.id.add_entry_button);
        chooseDayTextView = findViewById(R.id.choose_day_textview);
        selectAmountEditText = findViewById(R.id.select_amount_edittext);
        selectAmountInputLayout = findViewById(R.id.select_amount_inputlayout);
        chosenDate = Calendar.getInstance();
        chosenDate.set(Calendar.HOUR_OF_DAY, 0);
        chosenDate.set(Calendar.MINUTE, 0);
        chosenDate.set(Calendar.SECOND, 0);

        user = HomeFragment.user;
        initializeCategoryDropDown();
        initializeTypeDropDown();
        updateDate();
        chooseDayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });
        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectCatId = "";
                if (selectTypeSpinner.getSelectedItemPosition() == 1) {
                    Category income = new Category(":income", "Income", R.drawable.category_default,
                            Color.parseColor("#455a64"));
                    selectCatId = income.getCategoryID();
                } else {
                    selectCatId = ((Category) selectCategorySpinner.getSelectedItem()).getCategoryID();
                }
                addNewRecord(((selectTypeSpinner.getSelectedItemPosition() * 2) - 1) *
                        CurrencyHelper.convertAmountStringToLong(selectAmountEditText.getText().toString()),
                        chosenDate.getTime(),
                        selectCatId,
                        selectNameEditText.getText().toString());
            }
        });
    }

    private void initializeTypeDropDown() {
        IncomeExpenseTypeAdapter typeAdapter = new IncomeExpenseTypeAdapter(this,
                R.layout.new_entry_type_spinner_row, Arrays.asList(
                        new IncomeExpenseType("Expense", Color.parseColor("#ef5350"),
                                R.drawable.money_icon),
                        new IncomeExpenseType("Income", Color.parseColor("#66bb6a"),
                                R.drawable.money_icon)));
        selectTypeSpinner.setAdapter(typeAdapter);
        selectTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    findViewById(R.id.select_category_spinner_parent).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.select_category_spinner_parent).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initializeCategoryDropDown() {
        if (user == null)
            return;
        final List<Category> categories = CategoriesHelper.getCategories(user);
        EntryCategoriesAdapter categoryAdapter = new EntryCategoriesAdapter(this,
                R.layout.new_entry_type_spinner_row, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCategorySpinner.setAdapter(categoryAdapter);
    }

    private void updateDate() {
        SimpleDateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd");
        chooseDayTextView.setText(dataFormatter.format(chosenDate.getTime()));
    }

    public void addNewRecord(long balanceDifference, Date entryDate, String entryCategory, String entryName) {
        if (balanceDifference == 0) {
            Toast.makeText(this, "Amount should not be 0", Toast.LENGTH_SHORT).show();
            return;
        }
        if (entryName == null || entryName.length() == 0) {
            entryName = "";
        }
        FirebaseDatabase.getInstance().getReference().child("wallet-entries").child(getUid())
                .child("default").push()
                .setValue(new IncomeExpenseRecord(entryCategory, entryName, entryDate.getTime(), balanceDifference));
        user.sum += balanceDifference;
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(getUid()).setValue(user);
        finish();
    }

    // Pick a date using date picker dialog
    private void pickDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        chosenDate.set(year, monthOfYear, dayOfMonth);
                        updateDate();
                    }
                }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.getDatePicker().setMinDate(CalendarHelper.getMinCalendar().getTimeInMillis());
        datePickerDialog.show();
    }


    // Handle back button press in the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
