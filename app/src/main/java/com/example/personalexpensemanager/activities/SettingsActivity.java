package com.example.personalexpensemanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.dao.TransactionDao;
import com.example.personalexpensemanager.dao.UserDao;
import com.example.personalexpensemanager.utils.SharedPrefManager;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView txtProfileName, txtProfileUsername, txtProfileInitials;
    private SwitchMaterial switchDarkMode;
    private LinearLayout layoutChangePassword, layoutLogout, layoutDeleteData;

    private SharedPrefManager sharedPrefManager;
    private UserDao userDao;
    private TransactionDao transactionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPrefManager = new SharedPrefManager(this);
        userDao = new UserDao(this);
        transactionDao = new TransactionDao(this);

        btnBack = findViewById(R.id.btnBack);
        txtProfileName = findViewById(R.id.txtProfileName);
        txtProfileUsername = findViewById(R.id.txtProfileUsername);
        txtProfileInitials = findViewById(R.id.txtProfileInitials);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        layoutChangePassword = findViewById(R.id.layoutChangePassword);
        layoutLogout = findViewById(R.id.layoutLogout);
        layoutDeleteData = findViewById(R.id.layoutDeleteData);

        btnBack.setOnClickListener(v -> finish());

        txtProfileName.setText(sharedPrefManager.getFullName());
        txtProfileUsername.setText("@" + sharedPrefManager.getUsername());
        txtProfileInitials.setText(getInitials(sharedPrefManager.getFullName()));

        switchDarkMode.setChecked(sharedPrefManager.isDarkMode());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPrefManager.setDarkMode(isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        layoutChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        layoutDeleteData.setOnClickListener(v -> confirmDeleteData());

        layoutLogout.setOnClickListener(v -> performLogout());
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "";
        String[] parts = name.trim().split("\\s+");
        String first = parts[0].substring(0, 1);
        String last = parts.length > 1 ? parts[parts.length - 1].substring(0, 1) : "";
        return (first + last).toUpperCase();
    }

    private void confirmDeleteData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_delete_data, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button btnDelete = dialogView.findViewById(R.id.btnDelete);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnDelete.setOnClickListener(v -> {
            int removed = transactionDao.deleteTransactionsByUser(sharedPrefManager.getUserId());
            Toast.makeText(this, "Đã xóa " + removed + " giao dịch", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        EditText edtOldPassword = dialogView.findViewById(R.id.edtOldPassword);
        EditText edtNewPassword = dialogView.findViewById(R.id.edtNewPassword);
        EditText edtConfirmPassword = dialogView.findViewById(R.id.edtConfirmPassword);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String oldPass = edtOldPassword.getText().toString().trim();
            String newPass = edtNewPassword.getText().toString().trim();
            String confirmPass = edtConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
                Toast.makeText(this, R.string.err_empty_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, R.string.err_pass_mismatch, Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = userDao.changePassword(sharedPrefManager.getUserId(), oldPass, newPass);
            if (success) {
                Toast.makeText(this, R.string.msg_change_pass_success, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, R.string.err_wrong_old_pass, Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void performLogout() {
        sharedPrefManager.logout();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
