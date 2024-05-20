package smart.budget.diary.ui.categories;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import smart.budget.diary.R;
import smart.budget.diary.models.Category;

public class CustomCategoriesAdapter extends ArrayAdapter<Category> {

    private final Activity activity;
    Context context;

    public CustomCategoriesAdapter(Activity activity, List<Category> data, Context context) {
        super(context, R.layout.favorites_listview_row, data);
        this.context = context;
        this.activity = activity;

    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.custom_categories_listview_row, parent, false);

        Category category = getItem(position);

        TextView categoryNameTextView = listItem.findViewById(R.id.category_textview);

        categoryNameTextView.setText(category.getCategoryVisibleName());

        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EditCustomCategoryActivity.class);
                intent.putExtra("category-id", position);
                intent.putExtra("category-name", category.getCategoryVisibleName());
                activity.startActivity(intent);
            }
        });
        return listItem;
    }


}
