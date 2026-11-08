package com.example.personalexpensemanager.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.models.Category;
import com.example.personalexpensemanager.models.Transaction;
import com.example.personalexpensemanager.utils.DateUtils;
import com.example.personalexpensemanager.utils.MoneyFormatter;

import java.util.List;

public class TransactionAdapter extends ArrayAdapter<Transaction> {

    private Context context;
    private int resource;

    public TransactionAdapter(@NonNull Context context, int resource, @NonNull List<Transaction> objects) {
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

        Transaction transaction = getItem(position);
        if (transaction != null) {
            ImageView imgIcon = convertView.findViewById(R.id.imgCategoryIcon);
            TextView txtName = convertView.findViewById(R.id.txtCategoryName);
            TextView txtNote = convertView.findViewById(R.id.txtNote);
            TextView txtDate = convertView.findViewById(R.id.txtDate);
            TextView txtAmount = convertView.findViewById(R.id.txtAmount);
            ImageView imgAttachment = convertView.findViewById(R.id.imgAttachmentIndicator);

            txtName.setText(transaction.getCategoryName());
            txtDate.setText(DateUtils.formatToDisplayDate(transaction.getDate()));

            if (transaction.getNote() != null && !transaction.getNote().trim().isEmpty()) {
                txtNote.setText(transaction.getNote());
                txtNote.setVisibility(View.VISIBLE);
            } else {
                txtNote.setVisibility(View.GONE);
            }

            // Set icon
            String iconName = transaction.getCategoryIcon();
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

            // Amount formatting & color
            String formattedAmount = MoneyFormatter.formatMoney(transaction.getAmount());
            if (Category.TYPE_INCOME.equalsIgnoreCase(transaction.getCategoryType())) {
                txtAmount.setText("+ " + formattedAmount);
                txtAmount.setTextColor(ContextCompat.getColor(context, R.color.income_green));
            } else {
                txtAmount.setText("- " + formattedAmount);
                txtAmount.setTextColor(ContextCompat.getColor(context, R.color.expense_red));
            }

            // Attachment indicator
            if (imgAttachment != null) {
                if (transaction.getImageUri() != null && !transaction.getImageUri().isEmpty()) {
                    imgAttachment.setVisibility(View.VISIBLE);
                } else {
                    imgAttachment.setVisibility(View.GONE);
                }
            }
        }

        return convertView;
    }
}