package com.example.personalexpensemanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.models.Category;

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<Category> {

    private Context context;
    private int resource;
    private long selectedCategoryId = -1;

    public CategoryAdapter(@NonNull Context context, int resource, @NonNull List<Category> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    public void setSelectedCategoryId(long categoryId) {
        this.selectedCategoryId = categoryId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        Category category = getItem(position);
        if (category != null) {
            ImageView imgIcon = convertView.findViewById(R.id.imgCategoryIcon);
            TextView txtName = convertView.findViewById(R.id.txtCategoryName);
            TextView txtType = convertView.findViewById(R.id.txtCategoryType);

            txtName.setText(category.getName());
            if (txtType != null) {
                txtType.setText(Category.TYPE_INCOME.equalsIgnoreCase(category.getType()) ? "[Thu]" : "[Chi]");
            }

            String iconName = category.getIconName();
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

            // Selection background
            if (category.getId() == selectedCategoryId) {
                convertView.setBackgroundResource(R.drawable.bg_search_bar);
            } else {
                convertView.setBackgroundResource(R.drawable.bg_card_rounded);
            }
        }

        return convertView;
    }
}
