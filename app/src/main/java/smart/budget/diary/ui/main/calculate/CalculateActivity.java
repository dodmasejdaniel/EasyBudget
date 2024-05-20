package smart.budget.diary.ui.main.calculate;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smart.budget.diary.R;
import smart.budget.diary.base.BaseActivity;
import smart.budget.diary.models.IncomeExpenseRecord;
import smart.budget.diary.models.User;
import smart.budget.diary.models.Category;
import smart.budget.diary.ui.main.home.HomeFragment;
import smart.budget.diary.util.CalendarHelper;
import smart.budget.diary.util.CategoriesHelper;
import smart.budget.diary.util.CurrencyHelper;

public class CalculateActivity extends BaseActivity {

    private Calendar calendarStart;
    private Calendar calendarEnd;
    private User user;
    private List<IncomeExpenseRecord> walletEntryListDataSet;
    private PieChart pieChart;
    private ArrayList<TopCategoryStatisticsListViewModel> categoryModelsHome;
    private TopCategoriesStatisticsAdapter adapter;
    private TextView dividerTextView;

    private TextView incomesTextView;
    private TextView expensesTextView;
    boolean isListLoaded = false;
    Query dserExpensesBasedOnSelectedDates;
    ValueEventListener firebaseListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Calculate"); // set appbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize UI elements
        pieChart = findViewById(R.id.pie_chart);
        dividerTextView = findViewById(R.id.divider_textview);

        expensesTextView = findViewById(R.id.expenses_textview);
        incomesTextView = findViewById(R.id.incomes_textview);

        categoryModelsHome = new ArrayList<>();
        ListView favoriteListView = findViewById(R.id.favourite_categories_list_view);
        adapter = new TopCategoriesStatisticsAdapter(categoryModelsHome, this);
        favoriteListView.setAdapter(adapter);
        user = HomeFragment.user;
        calendarStart = CalendarHelper.getCurrentMonthStart();
        calendarEnd = CalendarHelper.getCurrentMonthEnd();
        // Set up Firebase query
        dserExpensesBasedOnSelectedDates = FirebaseDatabase.getInstance().getReference()
                .child("wallet-entries").child(getUid()).child("default").orderByChild("date")
                .startAt(-calendarEnd.getTimeInMillis()).endAt(-calendarStart.getTimeInMillis());

        firebaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                // store receved data changes from Firebase
                List<IncomeExpenseRecord> incomeExpenseRecordList = new ArrayList<>();
                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    IncomeExpenseRecord incomeExpenseRecord = snapshot.getValue(IncomeExpenseRecord.class);
                    if (incomeExpenseRecord != null) {
                        incomeExpenseRecord.key = snapshot.getKey();
                        incomeExpenseRecordList.add(incomeExpenseRecord);
                    }
                }
                if (!incomeExpenseRecordList.isEmpty()) {
                    CalculateActivity.this.walletEntryListDataSet = incomeExpenseRecordList;

                } else {
                    CalculateActivity.this.walletEntryListDataSet = new ArrayList<>();
                }
                isListLoaded = true;
                UpdateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dserExpensesBasedOnSelectedDates.addValueEventListener(firebaseListener);

    }


    private void UpdateUI() {
        if (user == null || !isListLoaded)
            return;
        if (calendarStart != null && calendarEnd != null && walletEntryListDataSet != null) {
            List<IncomeExpenseRecord> entryList = new ArrayList<>(walletEntryListDataSet);

            long expensesSumInDateRange = 0;
            long incomesSumInDateRange = user.getSumOfBudgetsOfSpecificMonths(calendarStart,calendarEnd);

            HashMap<Category, Long> categoryModels = new HashMap<>();
            for (IncomeExpenseRecord incomeExpenseRecord : entryList) {
                if (incomeExpenseRecord.amount > 0) {

                    continue;
                }
                expensesSumInDateRange += incomeExpenseRecord.amount;
                Category category = CategoriesHelper.searchCategory(user, incomeExpenseRecord.categoryID);
                if (categoryModels.get(category) != null)
                    categoryModels.put(category, categoryModels.get(category) + incomeExpenseRecord.amount);
                else
                    categoryModels.put(category, incomeExpenseRecord.amount);

            }

            categoryModelsHome.clear();

            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            ArrayList<Integer> pieColors = new ArrayList<>();

            for (Map.Entry<Category, Long> categoryModel : categoryModels.entrySet()) {
                float percentage = ((float) categoryModel.getValue() / (-1 * incomesSumInDateRange))*100;
                categoryModelsHome.add(new TopCategoryStatisticsListViewModel(categoryModel.getKey(),
                        categoryModel.getKey().getCategoryVisibleName(),
                        categoryModel.getValue(), percentage));
                pieEntries.add(new PieEntry(-categoryModel.getValue()));
                pieColors.add(categoryModel.getKey().getIconColor());
            }

            if(pieEntries.isEmpty())
                Toast.makeText(this, "No expenses to display", Toast.LENGTH_LONG).show();

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
            pieDataSet.setDrawValues(false);
            pieDataSet.setColors(pieColors);
            pieDataSet.setSliceSpace(2f);

            PieData data = new PieData(pieDataSet);
            pieChart.setData(data);
            pieChart.setTouchEnabled(false);
            pieChart.getLegend().setEnabled(false);
            pieChart.getDescription().setEnabled(false);
            pieChart.invalidate();

            Collections.sort(categoryModelsHome, new Comparator<TopCategoryStatisticsListViewModel>() {
                @Override
                public int compare(TopCategoryStatisticsListViewModel o1, TopCategoryStatisticsListViewModel o2) {
                    return Long.compare(o1.getMoney(), o2.getMoney());
                }
            });

            adapter.notifyDataSetChanged();

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");

            dividerTextView.setText("Date range: " + dateFormat.format(calendarStart.getTime())
                    + "  -  " + dateFormat.format(calendarEnd.getTime()));

            expensesTextView.setText(CurrencyHelper.formatCurrency(expensesSumInDateRange));
            incomesTextView.setText(CurrencyHelper.formatCurrency(incomesSumInDateRange));

        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calculate, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_date_range) {
            showSelectDateRangeDialog();
        }else{
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSelectDateRangeDialog() {
        SmoothDateRangePickerFragment datePicker = SmoothDateRangePickerFragment
                .newInstance(new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                    @Override
                    public void onDateRangeSet(SmoothDateRangePickerFragment view, int yearStart, int monthStart,
                            int dayStart, int yearEnd, int monthEnd, int dayEnd) {
                        calendarStart = Calendar.getInstance();
                        calendarStart.set(yearStart, monthStart, dayStart);
                        calendarStart.set(Calendar.HOUR_OF_DAY, 0);
                        calendarStart.set(Calendar.MINUTE, 0);
                        calendarStart.set(Calendar.SECOND, 0);

                        calendarEnd = Calendar.getInstance();
                        calendarEnd.set(yearEnd, monthEnd, dayEnd);
                        calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
                        calendarEnd.set(Calendar.MINUTE, 59);
                        calendarEnd.set(Calendar.SECOND, 59);
                        calendarUpdated();

                    }
                });
        Calendar calendarMaxDate = Calendar.getInstance();
        calendarMaxDate.set(Calendar.HOUR_OF_DAY, 23);
        calendarMaxDate.set(Calendar.MINUTE, 59);
        calendarMaxDate.set(Calendar.SECOND, 59);
        // Handle data changes from Firebase
        datePicker.setMaxDate(calendarMaxDate);
        datePicker.setMinDate(CalendarHelper.getMinCalendar());
        datePicker.show(getFragmentManager(), "TAG");
    }

    private void calendarUpdated() {
        dserExpensesBasedOnSelectedDates.removeEventListener(firebaseListener);
        dserExpensesBasedOnSelectedDates = FirebaseDatabase.getInstance().getReference()
                .child("wallet-entries").child(getUid()).child("default").orderByChild("date")
                .startAt(-calendarEnd.getTimeInMillis()).endAt(-calendarStart.getTimeInMillis());
        dserExpensesBasedOnSelectedDates.addValueEventListener(firebaseListener);

    }

}
