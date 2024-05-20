package smart.budget.diary.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import smart.budget.diary.R;
import smart.budget.diary.ui.records.AddIncomeExpenseActivity;
import smart.budget.diary.ui.main.home.HomeFragment;

/**
 * Represents the main activity (HOME PAGE) of the EasyBudget app.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        setSupportActionBar(findViewById(R.id.toolbar));

        // Initialize the add wallet entry button
        FloatingActionButton addEntryButton = findViewById(R.id.add_wallet_entry_fab);
        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the user is logged in
                if (HomeFragment.user == null) return;
                startActivity(new Intent(MainActivity.this, AddIncomeExpenseActivity.class));
            }
        });

        // Initialize the view pager and its adapter
        ViewPager viewPager = findViewById(R.id.pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        // Initialize the tab layout and link it with the view pager
        TabLayout tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
    }
}