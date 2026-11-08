package com.example.personalexpensemanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.dao.UserDao;
import com.example.personalexpensemanager.models.User;
import com.example.personalexpensemanager.utils.SharedPrefManager;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private ImageView imgTogglePassword;
    private Button btnLogin;
    private TextView txtRegisterLink, txtError;
    private CheckBox chkRememberLogin;

    private UserDao userDao;
    private SharedPrefManager sharedPrefManager;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPrefManager = new SharedPrefManager(this);
        if (sharedPrefManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);

        if (sharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        userDao = new UserDao(this);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        imgTogglePassword = findViewById(R.id.imgTogglePassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegisterLink = findViewById(R.id.txtRegisterLink);
        txtError = findViewById(R.id.txtError);
        chkRememberLogin = findViewById(R.id.chkRememberLogin);
        String rememberedUsername = sharedPrefManager.getRememberedUsername();
        if (!rememberedUsername.isEmpty()) {
            edtUsername.setText(rememberedUsername);
            if ("diepnguyen".equals(rememberedUsername)) {
                edtPassword.setText("diepthanh01@");
            }
        } else {
            edtUsername.setText("diepnguyen");
            edtPassword.setText("diepthanh01@");
        }

        imgTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        btnLogin.setOnClickListener(v -> performLogin());

        txtRegisterLink.setText(android.text.Html.fromHtml("Chưa có tài khoản? <font color='#087887'><b>Đăng ký ngay</b></font>", android.text.Html.FROM_HTML_MODE_COMPACT));
        txtRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imgTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            isPasswordVisible = false;
        } else {
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imgTogglePassword.setImageResource(R.drawable.ic_visibility_on);
            isPasswordVisible = true;
        }
        edtPassword.setSelection(edtPassword.getText().length());
    }

    private void performLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            txtError.setText(R.string.err_empty_fields);
            txtError.setVisibility(View.VISIBLE);
            return;
        }

        User user = userDao.loginUser(username, password);
        if (user != null) {
            txtError.setVisibility(View.GONE);
            sharedPrefManager.setRememberedUsername(chkRememberLogin.isChecked() ? username : "");
            sharedPrefManager.saveUser(user.getId(), user.getUsername(), user.getFullName());
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            txtError.setText(R.string.err_login_failed);
            txtError.setVisibility(View.VISIBLE);
        }
    }
}
