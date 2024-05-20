package smart.budget.diary.ui.main.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import smart.budget.diary.R;
import smart.budget.diary.base.BaseFragment;
import smart.budget.diary.models.IncomeExpenseRecord;
import smart.budget.diary.ui.categories.CustomCategoriesActivity;
import smart.budget.diary.ui.main.calculate.CalculateActivity;
import smart.budget.diary.ui.signin.LoginActivity;

public class HistoryFragment extends BaseFragment {
    Calendar calendarStart;
    Calendar calendarEnd;
    private ListView historyRecyclerView;
    private WalletEntriesAdapter historyRecyclerViewAdapter;
    private Menu menu;
    private TextView dividerTextView;
    public static List<IncomeExpenseRecord> walletEntryListDataSetCZ;
    public static List<IncomeExpenseRecord> walletEntryListDataSet;

    public static HistoryFragment newInstance() {

        return new HistoryFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        dividerTextView = view.findViewById(R.id.divider_textview);
        dividerTextView.setText("History");
        walletEntryListDataSetCZ = new ArrayList<>();
        walletEntryListDataSet = new ArrayList<>();

        historyRecyclerView = view.findViewById(R.id.history_recycler_view);
        historyRecyclerViewAdapter = new WalletEntriesAdapter(walletEntryListDataSetCZ, getActivity().getApplicationContext(),getActivity());
        historyRecyclerView.setAdapter(historyRecyclerViewAdapter);
        Query UserExpenseHistory= FirebaseDatabase.getInstance().getReference()
                .child("wallet-entries").child(getUid()).child("default").orderByChild("date");

        UserExpenseHistory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshots) {
                List<IncomeExpenseRecord> incomeExpenseRecordList = new ArrayList<>();
                for (DataSnapshot snapshot : snapshots.getChildren()) {
                    IncomeExpenseRecord incomeExpenseRecord = snapshot.getValue(IncomeExpenseRecord.class);
                    if(incomeExpenseRecord!=null){
                        incomeExpenseRecord.key= snapshot.getKey();
                        incomeExpenseRecordList.add(incomeExpenseRecord);
                    }
                }
                if(!incomeExpenseRecordList.isEmpty()){
                    HistoryFragment.walletEntryListDataSet=incomeExpenseRecordList;
                }
                dataUpdated();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void dataUpdated() {
        if (HistoryFragment.walletEntryListDataSetCZ == null||walletEntryListDataSetCZ==null)
            return;
        walletEntryListDataSetCZ.clear();
        walletEntryListDataSetCZ.addAll(HistoryFragment.walletEntryListDataSet);
        historyRecyclerViewAdapter.notifyDataSetChanged();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.history, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.baseline_calculate_24) {
            startActivity(new Intent(getActivity(), CalculateActivity.class));
        }
        if (id == R.id.action_options) {
            getActivity().startActivity(new Intent(getActivity(), CustomCategoriesActivity.class));
        }
        if (id == R.id.baseline_logout_24) {
             FirebaseAuth.getInstance().signOut();
             getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
              getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
