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
import smart.budget.diary.models.IncomeExpenseRecord;
import smart.budget.diary.models.User;
import smart.budget.diary.models.Category;
import smart.budget.diary.models.IncomeExpenseType;
import smart.budget.diary.ui.main.history.HistoryFragment;
import smart.budget.diary.ui.main.home.HomeFragment;
import smart.budget.diary.util.CalendarHelper;
import smart.budget.diary.util.CategoriesHelper;
import smart.budget.diary.util.CurrencyHelper;

// same working as add expense/income screen
public class EditIncomeExpenseActivity extends BaseActivity {
    private Spinner selectCategorySpinner;
    private TextInputEditText selectNameEditText;
    private Calendar choosedDate;
    private TextInputEditText selectAmountEditText;
    private TextView chooseDayTextView;
    private Spinner selectTypeSpinner;
    private User user;
    private IncomeExpenseRecord incomeExpenseRecord;
    private Button removeEntryButton;
    private Button editEntryButton;
    private String walletId;
    private int position;
    private TextInputLayout selectAmountInputLayout;
    private TextInputLayout selectNameInputLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wallet_entry);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit record");

        walletId = getIntent().getExtras().getString("wallet-entry-id");
        position = getIntent().getExtras().getInt("position");

        selectCategorySpinner = findViewById(R.id.select_category_spinner);
        selectNameEditText = findViewById(R.id.select_name_edittext);
        selectNameInputLayout = findViewById(R.id.select_name_inputlayout);
        selectTypeSpinner = findViewById(R.id.select_type_spinner);
        editEntryButton = findViewById(R.id.edit_entry_button);
        removeEntryButton = findViewById(R.id.remove_entry_button);
        chooseDayTextView = findViewById(R.id.choose_day_textview);
        selectAmountEditText = findViewById(R.id.select_amount_edittext);
        selectAmountInputLayout = findViewById(R.id.select_amount_inputlayout);
        choosedDate = Calendar.getInstance();
        choosedDate.set(Calendar.HOUR_OF_DAY, 0);
        choosedDate.set(Calendar.MINUTE, 0);
        choosedDate.set(Calendar.SECOND, 0);

        initializeTypeDropDown();
        updateDate();
        chooseDayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });
        editEntryButton.setOnClickListener(new View.OnClickListener() {
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
                editWalletEntry(((selectTypeSpinner.getSelectedItemPosition() * 2) - 1) *
                        CurrencyHelper.convertAmountStringToLong(selectAmountEditText.getText().toString()),
                        choosedDate.getTime(),
                        selectCatId,
                        selectNameEditText.getText().toString());
            }
        });
        removeEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeWalletEntry();
            }
        });
        user = HomeFragment.user;
        incomeExpenseRecord = HistoryFragment.walletEntryListDataSetCZ.get(position);
        initializeCategoryDropDown();
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

    public void initializeCategoryDropDown() {
        if (incomeExpenseRecord == null || user == null)
            return;
        final List<Category> categories = CategoriesHelper.getCategories(user);
        EntryCategoriesAdapter categoryAdapter = new EntryCategoriesAdapter(this,
                R.layout.new_entry_type_spinner_row, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCategorySpinner.setAdapter(categoryAdapter);
        choosedDate.setTimeInMillis(-incomeExpenseRecord.date);
        updateDate();
        selectNameEditText.setText(incomeExpenseRecord.description);
        selectTypeSpinner.post(new Runnable() {
            @Override
            public void run() {
                if (incomeExpenseRecord.amount < 0)
                    selectTypeSpinner.setSelection(0);
                else
                    selectTypeSpinner.setSelection(1);
            }
        });
        selectCategorySpinner.post(new Runnable() {
            @Override
            public void run() {
                EntryCategoriesAdapter adapter = (EntryCategoriesAdapter) selectCategorySpinner.getAdapter();
                selectCategorySpinner.setSelection(adapter.getItemIndex(incomeExpenseRecord.categoryID));
            }
        });
        long amount = Math.abs(incomeExpenseRecord.amount);
        selectAmountEditText.setText(amount + "");
    }

    private void updateDate() {
        SimpleDateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd");
        chooseDayTextView.setText(dataFormatter.format(choosedDate.getTime()));
    }

    public void editWalletEntry(long balanceDifference, Date entryDate, String entryCategory, String entryName) {
        if (balanceDifference == 0) {
            Toast.makeText(this, "Amount should not be 0", Toast.LENGTH_SHORT).show();
            return;
        }
        if (entryName == null || entryName.length() == 0) {
            entryName = "";
        }
        long finalBalanceDifference = balanceDifference - incomeExpenseRecord.amount;
        user.sum += finalBalanceDifference;
        HistoryFragment.walletEntryListDataSetCZ.set(position,incomeExpenseRecord);
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(getUid()).setValue(user);
        FirebaseDatabase.getInstance().getReference().child("wallet-entries").child(getUid())
                .child("default").child(walletId)
                .setValue(new IncomeExpenseRecord(entryCategory, entryName, entryDate.getTime(), balanceDifference));
        finish();
    }

    public void removeWalletEntry() {
        user.sum -= incomeExpenseRecord.amount;
        HistoryFragment.walletEntryListDataSetCZ.remove(incomeExpenseRecord);
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(getUid()).setValue(user);
        FirebaseDatabase.getInstance().getReference().child("wallet-entries").child(getUid())
                .child("default").child(walletId).removeValue();
        finish();
    }


    private void pickDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        choosedDate.set(year, monthOfYear, dayOfMonth);
                        updateDate();
                    }
                }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.getDatePicker().setMinDate(CalendarHelper.getMinCalendar().getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
