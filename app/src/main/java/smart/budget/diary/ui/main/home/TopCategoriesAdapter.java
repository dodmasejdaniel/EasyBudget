package smart.budget.diary.ui.main.home;

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

// Adapter for displaying top categories in a ListView
public class TopCategoriesAdapter extends ArrayAdapter<TopCategoryListViewModel> {

    private ArrayList<TopCategoryListViewModel> dataSet;
    Context context;

    // Constructor to initialize the adapter
    public TopCategoriesAdapter(ArrayList<TopCategoryListViewModel> data, Context context) {
        super(context, R.layout.favorites_listview_row, data);
        this.dataSet = data;
        this.context = context;
    }

    // Method to populate the ListView rows
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            // Inflate the layout for each row
            listItem = LayoutInflater.from(context).inflate(R.layout.favorites_listview_row, parent, false);

        // Get the data model for this position
        TopCategoryListViewModel dataModel = getItem(position);
        Category category = dataModel.getCategory();

        // Initialize views in the row layout
        TextView categoryNameTextView = listItem.findViewById(R.id.item_category);
        TextView sumTextView = listItem.findViewById(R.id.item_sum);
        ImageView iconImageView = listItem.findViewById(R.id.icon_imageview);

        // Set icon and background color
        iconImageView.setImageResource(category.getIconResourceID());
        iconImageView.setBackgroundTintList(ColorStateList.valueOf(category.getIconColor()));

        // Set category name and sum
        categoryNameTextView.setText(dataModel.getCategoryName());
        sumTextView.setText(CurrencyHelper.formatCurrency(dataModel.getMoney()));
        sumTextView.setTextColor(ContextCompat.getColor(context, R.color.gauge_expense));

        // Disable clicking on rows
        listItem.setClickable(false);
        listItem.setOnClickListener(null);
        return listItem;
    }
}
