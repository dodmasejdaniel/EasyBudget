package smart.budget.diary.ui.main.calculate;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import smart.budget.diary.R;
import smart.budget.diary.models.Category;
import smart.budget.diary.util.CurrencyHelper;

public class TopCategoriesStatisticsAdapter extends ArrayAdapter<TopCategoryStatisticsListViewModel> implements View.OnClickListener {

    private ArrayList<TopCategoryStatisticsListViewModel> dataSet;
    Context context;


    public TopCategoriesStatisticsAdapter(ArrayList<TopCategoryStatisticsListViewModel> data, Context context) {
        super(context, R.layout.top_categories_statistics_listview_row, data);
        this.dataSet = data;
        this.context = context;

    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.top_categories_statistics_listview_row, parent, false);

        TopCategoryStatisticsListViewModel dataModel = getItem(position);
        Category category = dataModel.getCategory();

        TextView categoryNameTextView = listItem.findViewById(R.id.item_category);
        TextView sumTextView = listItem.findViewById(R.id.item_sum);
        TextView percentageTextView = listItem.findViewById(R.id.item_percentage);

        ImageView iconImageView = listItem.findViewById(R.id.icon_imageview);

        iconImageView.setImageResource(category.getIconResourceID());
        iconImageView.setBackgroundTintList(ColorStateList.valueOf(category.getIconColor()));

        categoryNameTextView.setText(dataModel.getCategoryName());
        sumTextView.setText(CurrencyHelper.formatCurrency(dataModel.getMoney()));

        percentageTextView.setText(String.format("%.2f", dataModel.getPercentage()) + "%");
        if (dataModel.getMoney() < 0)
            sumTextView.setTextColor(ContextCompat.getColor(context, R.color.gauge_expense));
        else
            sumTextView.setTextColor(ContextCompat.getColor(context, R.color.gauge_income));
        listItem.setClickable(false);
        listItem.setOnClickListener(null);
        return listItem;
    }
}
