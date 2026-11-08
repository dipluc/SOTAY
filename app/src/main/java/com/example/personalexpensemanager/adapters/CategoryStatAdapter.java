package com.example.personalexpensemanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.models.CategoryStat;
import com.example.personalexpensemanager.utils.MoneyFormatter;

import java.util.List;
import java.util.Locale;

public class CategoryStatAdapter extends ArrayAdapter<CategoryStat> {

    private Context context;
    private int resource;

    public CategoryStatAdapter(@NonNull Context context, int resource, @NonNull List<CategoryStat> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        CategoryStat stat = getItem(position);
        if (stat != null) {
            ImageView imgIcon = convertView.findViewById(R.id.imgCategoryIcon);
            TextView txtName = convertView.findViewById(R.id.txtCategoryName);
            TextView txtAmount = convertView.findViewById(R.id.txtAmount);
            TextView txtPercentage = convertView.findViewById(R.id.txtPercentage);
            ProgressBar progressBar = convertView.findViewById(R.id.progressBarCategory);

            txtName.setText(stat.getCategoryName());
            txtAmount.setText(MoneyFormatter.formatMoney(stat.getTotalAmount()));

            String pctText = String.format(Locale.getDefault(), "%.1f%%", stat.getPercentage());
            txtPercentage.setText(pctText);

            if (progressBar != null) {
                progressBar.setProgress(Math.round(stat.getPercentage()));
            }

            String iconName = stat.getIconName();
            if (iconName != null && !iconName.isEmpty()) {
                int resId = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
                if (resId != 0) {
                    imgIcon.setImageResource(resId);
                } else {
                    imgIcon.setImageResource(R.drawable.ic_other);
                }
            } else {
                imgIcon.setImageResource(R.drawable.ic_other);
            }
        }

        return convertView;
    }
}