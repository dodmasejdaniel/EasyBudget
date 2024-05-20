package smart.budget.diary.ui.main.history;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import smart.budget.diary.R;
import smart.budget.diary.models.IncomeExpenseRecord;
import smart.budget.diary.models.Category;
import smart.budget.diary.ui.main.home.HomeFragment;
import smart.budget.diary.ui.records.EditIncomeExpenseActivity;
import smart.budget.diary.util.CategoriesHelper;
import smart.budget.diary.util.CurrencyHelper;

public class WalletEntriesAdapter extends ArrayAdapter<IncomeExpenseRecord> {

    Context context;
    private final FragmentActivity fragmentActivity;

    private List<IncomeExpenseRecord> walletEntries;


    public WalletEntriesAdapter(List<IncomeExpenseRecord> walletEntries, Context context, FragmentActivity fragmentActivity) {
        super(context,R.layout.history_listview_row,walletEntries);
        this.walletEntries=walletEntries;
        this.fragmentActivity=fragmentActivity;
        this.context=context;
    }
    

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.history_listview_row, parent, false);
        final TextView dateTextView;
        final TextView moneyTextView;
        final TextView categoryTextView;
        final TextView nameTextView;
        final ImageView iconImageView;

        IncomeExpenseRecord incomeExpenseRecord = walletEntries.get(position);
        Category category = CategoriesHelper.searchCategory(HomeFragment.user, incomeExpenseRecord.categoryID);
        moneyTextView = listItem.findViewById(R.id.money_textview);
        categoryTextView = listItem.findViewById(R.id.category_textview);
        nameTextView = listItem.findViewById(R.id.name_textview);
        dateTextView = listItem.findViewById(R.id.date_textview);
        iconImageView = listItem.findViewById(R.id.icon_imageview);

        iconImageView.setImageResource(category.getIconResourceID());
        iconImageView.setBackgroundTintList(ColorStateList.valueOf(category.getIconColor()));
        categoryTextView.setText(category.getCategoryVisibleName());
        nameTextView.setText(incomeExpenseRecord.description);
        Date date = new Date(-incomeExpenseRecord.date);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateTextView.setText(dateFormat.format(date));
        moneyTextView.setText(CurrencyHelper.formatCurrency(incomeExpenseRecord.amount));
        moneyTextView.setTextColor(ContextCompat.getColor(fragmentActivity,
                incomeExpenseRecord.amount < 0 ? R.color.primary_text_expense : R.color.primary_text_income));
        // open new screen (edit screen) on clicking record
        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fragmentActivity, EditIncomeExpenseActivity.class);
                intent.putExtra("wallet-entry-id", incomeExpenseRecord.key);
                intent.putExtra("position", position);
                fragmentActivity.startActivity(intent);
            }
        });
        return listItem;
    }

}