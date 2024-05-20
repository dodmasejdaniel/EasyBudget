package smart.budget.diary.ui.main.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import smart.budget.diary.R;
import smart.budget.diary.base.BaseFragment;
import smart.budget.diary.models.IncomeExpenseRecord;
import smart.budget.diary.ui.main.history.HistoryFragment;
import smart.budget.diary.ui.main.calculate.CalculateActivity;
import smart.budget.diary.models.User;
import libraries.Gauge;
import smart.budget.diary.models.Category;
import smart.budget.diary.ui.categories.CustomCategoriesActivity;
import smart.budget.diary.ui.signin.LoginActivity;
import smart.budget.diary.util.CalendarHelper;
import smart.budget.diary.util.CategoriesHelper;
import smart.budget.diary.util.CurrencyHelper;

public class HomeFragment extends BaseFragment {
    public static User user; // Current user
    public static List<IncomeExpenseRecord> walletEntryListDataSet; // List of income and expense records for the user


    private Gauge gauge;
    private TopCategoriesAdapter adapter;
    private ArrayList<TopCategoryListViewModel> categoryModelsHome;
    private TextView userBudgetText;
    private TextView gaugeLeftLine1TextView;
    private TextView gaugeRightLine1TextView;
    private TextView gaugeBalanceLeftTextView;
    private TextView topExpenseTextView;
    private Switch incomeBudgetViewSwitch;


    // Flag to track if the data and list is loaded
    public static boolean isListLoaded=false;

    public static HomeFragment newInstance() {

        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        categoryModelsHome = new ArrayList<>();

        gauge = view.findViewById(R.id.gauge);
        gauge.setValue(50);
        pieChart = view.findViewById(R.id.pie_chart);
        gaugeHome = view.findViewById(R.id.guageHome);
        constraintLayout = view.findViewById(R.id.constraintLayout);

        userBudgetText = view.findViewById(R.id.user_budget_textview);
        gaugeLeftLine1TextView = view.findViewById(R.id.gauge_left_line1_textview);
        gaugeRightLine1TextView = view.findViewById(R.id.gauge_right_line1_textview);
        gaugeBalanceLeftTextView = view.findViewById(R.id.left_balance_textview);
        topExpenseTextView = view.findViewById(R.id.item_category);
        incomeBudgetViewSwitch =view.findViewById(R.id.switch1);
        ListView topExpenseCategories = view.findViewById(R.id.favourite_categories_list_view);
        adapter = new TopCategoriesAdapter(categoryModelsHome, getActivity().getApplicationContext());
        topExpenseCategories.setAdapter(adapter);
        DisplayBudgetView();  // default view is budget view
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item clicks
        int id = item.getItemId();
        if (id == R.id.baseline_calculate_24) {
            // Open calculate activity
            startActivity(new Intent(getActivity(), CalculateActivity.class));
        } else if (id == R.id.action_options) {
            // Open custom categories activity
            getActivity().startActivity(new Intent(getActivity(), CustomCategoriesActivity.class));
        } else if (id == R.id.baseline_logout_24) {
            // Log out the user and go to the login activity
            FirebaseAuth.getInstance().signOut();
            getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void DisplayBudgetView() {
        if (user == null || !isListLoaded) return;

        // this mean user enters new month and he has not set budget for this month, so copy previous month budget
        if(user.limit.get(CalendarHelper.getCurrentYear()).get(CalendarHelper.getCurrentMonth())==0){
            long previousMonthBudget;
            if(CalendarHelper.getCurrentMonth()==0){ // if this is new year, then copy previous years (december) budget
                previousMonthBudget=user.limit.get(CalendarHelper.getPreviousYear()).get(11);
            }else{  // else copy previous month budget
                previousMonthBudget=user.limit.get(CalendarHelper.getCurrentYear()).get(CalendarHelper.getCurrentMonth()-1);
            }
            // if there is no previous budget , then set default budget
            if(previousMonthBudget==0){
                previousMonthBudget=1000;
            }

            user.limit.get(CalendarHelper.getCurrentYear()).set(CalendarHelper.getCurrentMonth(),previousMonthBudget);
            // save new budget in database
            FirebaseDatabase.getInstance().getReference()
                    .child("users").child(getUid()).setValue(user);
        }
        List<IncomeExpenseRecord> entryList = new ArrayList<>(walletEntryListDataSet);

        Calendar startDate = CalendarHelper.getCurrentMonthStart();
        Calendar endDate = CalendarHelper.getCurrentMonthEnd();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM");


        long expensesSumInDateRange = 0;

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
        for (Map.Entry<Category, Long> categoryModel : categoryModels.entrySet()) {
            categoryModelsHome.add(new TopCategoryListViewModel(categoryModel.getKey(), categoryModel.getKey().getCategoryVisibleName(),
                     categoryModel.getValue()));
        }

        Collections.sort(categoryModelsHome, new Comparator<TopCategoryListViewModel>() {
            @Override
            public int compare(TopCategoryListViewModel o1, TopCategoryListViewModel o2) {
                return Long.compare(o1.getMoney(), o2.getMoney());
            }
        });


        adapter.notifyDataSetChanged();  // update UI

        if (incomeBudgetViewSwitch.isChecked()) {
            gaugeLeftLine1TextView.setText(dateFormat.format(startDate.getTime()));
            // set monthly limit
            userBudgetText.setText(CurrencyHelper.formatCurrency(user.limit.get(CalendarHelper.getCurrentYear()).get(CalendarHelper.getCurrentMonth())));
            gaugeRightLine1TextView.setText(dateFormat.format(endDate.getTime()));
            topExpenseTextView.setText("Top Expenses");

            long limit = user.limit.get(CalendarHelper.getCurrentYear()).get(CalendarHelper.getCurrentMonth());
            long expenses = -expensesSumInDateRange;
            int percentage = (int) (expenses * 100 / (double) limit);
            if (percentage > 100) percentage = 100;
            gauge.setValue(percentage);
            gaugeBalanceLeftTextView.setText(CurrencyHelper.formatCurrency(limit - expenses) + " left");


        }
        // update user budget
        userBudgetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMonthlyBudget();
            }
            
        });

        // display budget or income view based on switch selection
        incomeBudgetViewSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(incomeBudgetViewSwitch.isChecked()){
                    pieChart.setVisibility(View.GONE);
                    gaugeHome.setVisibility(View.VISIBLE);
                    constraintLayout.setBackgroundColor( Color.parseColor("#212121"));
                    DisplayBudgetView();
                }else{
                    DisplayIncomeView();
                    pieChart.setVisibility(View.VISIBLE);
                    gaugeHome.setVisibility(View.GONE);
                    constraintLayout.setBackgroundColor( Color.parseColor("#FFFFFF"));
                }

            }
        });
    }
    private PieChart pieChart;
    private View constraintLayout;
    private View gaugeHome;
    private void DisplayIncomeView() {
        if (user == null || !isListLoaded)
            return;
        if ( HistoryFragment.walletEntryListDataSet != null) {
            List<IncomeExpenseRecord> entryList = new ArrayList<>(HistoryFragment.walletEntryListDataSet);
            List<IncomeExpenseRecord> consolidatedIncome = new ArrayList<>();
            // Initialize the list with zeros for each month
            for (int i = 0; i < 12; i++) {
                consolidatedIncome.add(new IncomeExpenseRecord(":income","",0,0));
            }
             for (IncomeExpenseRecord record : HistoryFragment.walletEntryListDataSet) {
                if(record.amount>=0){
                    int month = new Date(-record.date).getMonth();
                        consolidatedIncome.get(month).amount+=record.amount;
                        consolidatedIncome.get(month).date=record.date;
                }
            }
            entryList=consolidatedIncome;

            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            ArrayList<Integer> pieColors = new ArrayList<>();

            int i=0;
            categoryModelsHome.clear();
            for (IncomeExpenseRecord incomeExpenseRecord : entryList) {
                if (incomeExpenseRecord.amount > 0) {
                    Category incomeCat= new Category(":income", "Income", R.drawable.category_default,CategoriesHelper.defaultCategories[i].getIconColor());
                    categoryModelsHome.add(new TopCategoryListViewModel(incomeCat,getMonthFirstLetter(i,true),incomeExpenseRecord.amount));
                    PieEntry pieEntry=new PieEntry(incomeExpenseRecord.amount);
                    pieEntries.add(pieEntry);
                    pieColors.add(CategoriesHelper.defaultCategories[i].getIconColor());
                }
                i++;
            }

            if(pieEntries.isEmpty()){
                Toast.makeText(getActivity(), "Please add monthly income to view chart.", Toast.LENGTH_LONG).show();
            }
            topExpenseTextView.setText("Monthly Income");

            adapter.notifyDataSetChanged();

            // set chart data and display it
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
            pieChart.invalidate();

        }

    }


    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }
    public static String getMonthFirstLetter(int index,boolean isFullMonthName) {
        String[] fullMonthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        if (index >= 0 && index < monthNames.length) {
            return isFullMonthName?fullMonthNames[index]: monthNames[index];
        } else {
            throw new IllegalArgumentException("Invalid month index");
        }
    }

    public boolean setMonthlyBudget() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        View layout = getLayoutInflater().inflate(R.layout.set_budget_dialog, null);
        alert.setView(layout);

        alert.setTitle("Set Budget:");
        TextInputEditText editText = layout.findViewById(R.id.edittext);
        editText.setText(user.limit.get(CalendarHelper.getCurrentYear()).get(CalendarHelper.getCurrentMonth()).toString());
        // Initialize and populate the Spinner with month names
        String[] fullMonthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        Spinner monthSpinner = layout.findViewById(R.id.month_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, fullMonthNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);

        // Set the default value of the Spinner to the current month
        Calendar calendar = Calendar.getInstance();
        int currentMonthIndex = calendar.get(Calendar.MONTH);
        monthSpinner.setSelection(currentMonthIndex);
        monthSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        editText.setText(user.limit.get(CalendarHelper.getCurrentYear()).get(i).toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                }
        );
        alert.setNegativeButton("Cancel", null);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(editText.getText().toString().equals("")||editText.getText().toString().equals("0")){
                    return;
                }
                String selectedMonth = (String) monthSpinner.getSelectedItem();
                int selectedMonthIndex=0;
                for(int i=0;i<fullMonthNames.length;i++){
                    if(Objects.equals(fullMonthNames[i], selectedMonth)){
                        selectedMonthIndex=i;
                    }
                }

                // logic to set budget for the selected month
                List<Long> budgets = user.limit.get(CalendarHelper.getCurrentYear());
                budgets.set(selectedMonthIndex, CurrencyHelper.convertAmountStringToLong(editText.getText().toString())); // default limit
                user.limit.put(CalendarHelper.getCurrentYear(),budgets);
                FirebaseDatabase.getInstance().getReference()
                        .child("users").child(getUid()).setValue(user);

            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
        return true;
    }
}
