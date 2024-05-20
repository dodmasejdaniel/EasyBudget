package smart.budget.diary.ui.records;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import smart.budget.diary.R;
import smart.budget.diary.models.IncomeExpenseType;

public class IncomeExpenseTypeAdapter extends ArrayAdapter<String> {
    private final List<IncomeExpenseType> items;
    private final Context context;

    public IncomeExpenseTypeAdapter(Context context, int resource,
                                    List objects) {
        super(context, resource, 0, objects);
        this.context = context;
        items = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.new_entry_type_spinner_row, parent, false);

        TextView textView = view.findViewById(R.id.item_category);
        ImageView iconImageView = view.findViewById(R.id.icon_imageview);

        iconImageView.setImageResource(items.get(position).iconID);
        iconImageView.setBackgroundTintList(ColorStateList.valueOf(items.get(position).color));
        textView.setText(items.get(position).name);

        return view;
    }

}