package com.example.personalexpensemanager.activities;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.dao.UserDao;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtUsername, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private TextView txtLoginLink, txtError;
    private ImageView imgTogglePassword, imgToggleConfirmPassword;
    private boolean passwordVisible, confirmPasswordVisible;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDao = new UserDao(this);

        edtFullName = findViewById(R.id.edtFullName);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtLoginLink = findViewById(R.id.txtLoginLink);
        txtError = findViewById(R.id.txtError);
        imgTogglePassword = findViewById(R.id.imgTogglePassword);
        imgToggleConfirmPassword = findViewById(R.id.imgToggleConfirmPassword);

        btnRegister.setOnClickListener(v -> performRegister());

        txtLoginLink.setOnClickListener(v -> finish());
        imgTogglePassword.setOnClickListener(v -> togglePassword(edtPassword, imgTogglePassword, false));
        imgToggleConfirmPassword.setOnClickListener(v -> togglePassword(edtConfirmPassword, imgToggleConfirmPassword, true));
    }

    private void togglePassword(EditText field, ImageView icon, boolean confirm) {
        boolean visible = confirm ? confirmPasswordVisible : passwordVisible;
        if (visible) {
            field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            icon.setImageResource(R.drawable.ic_visibility_off);
        } else {
            field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            icon.setImageResource(R.drawable.ic_visibility_on);
        }
        field.setSelection(field.getText().length());
        if (confirm) confirmPasswordVisible = !visible;
        else passwordVisible = !visible;
    }

    private void performRegister() {
        String fullName = edtFullName.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(username)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            txtError.setText(R.string.err_empty_fields);
            txtError.setVisibility(View.VISIBLE);
            return;
        }

        if (username.matches(".*\\s+.*")) {
            txtError.setText(R.string.err_username_spaces);
            txtError.setVisibility(View.VISIBLE);
            return;
        }

        if (password.length() < 8) {
            txtError.setText(R.string.err_password_short);
            txtError.setVisibility(View.VISIBLE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            txtError.setText(R.string.err_pass_mismatch);
            txtError.setVisibility(View.VISIBLE);
            return;
        }

        long userId = userDao.registerUser(username, password, fullName);
        if (userId == -1) {
            txtError.setText(R.string.err_user_exists);
            txtError.setVisibility(View.VISIBLE);
        } else {
            txtError.setVisibility(View.GONE);
            Toast.makeText(this, R.string.msg_register_success, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
