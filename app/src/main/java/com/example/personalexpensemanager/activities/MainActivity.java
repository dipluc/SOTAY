package com.example.personalexpensemanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.adapters.TransactionAdapter;
import com.example.personalexpensemanager.dao.TransactionDao;
import com.example.personalexpensemanager.models.Category;
import com.example.personalexpensemanager.models.Transaction;
import com.example.personalexpensemanager.utils.DateUtils;
import com.example.personalexpensemanager.utils.MoneyFormatter;
import com.example.personalexpensemanager.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView txtGreeting, txtTotalBalance, txtTotalIncome, txtTotalExpense, txtCurrentMonth, txtTransactionCount;
    private ImageButton btnMenuMore, btnPrevMonth, btnNextMonth;
    private EditText edtSearch;
    private RadioGroup rgTypeFilter;
    private ListView listViewTransactions;
    private LinearLayout layoutEmptyState;
    private View fabAddTransaction;

    private TransactionDao transactionDao;
    private SharedPrefManager sharedPrefManager;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList = new ArrayList<>();

    private Calendar currentCalendar = Calendar.getInstance();
    private String currentTypeFilter = "ALL";
    private long currentUserId;

    private ActivityResultLauncher<Intent> addEditLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadData();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPrefManager = new SharedPrefManager(this);
        if (sharedPrefManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        if (!sharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        currentUserId = sharedPrefManager.getUserId();
        transactionDao = new TransactionDao(this);

        txtGreeting = findViewById(R.id.txtGreeting);
        txtTotalBalance = findViewById(R.id.txtTotalBalance);
        txtTotalIncome = findViewById(R.id.txtTotalIncome);
        txtTotalExpense = findViewById(R.id.txtTotalExpense);
        txtCurrentMonth = findViewById(R.id.txtCurrentMonth);
        txtTransactionCount = findViewById(R.id.txtTransactionCount);
        btnMenuMore = findViewById(R.id.btnMenuMore);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        edtSearch = findViewById(R.id.edtSearch);
        rgTypeFilter = findViewById(R.id.rgTypeFilter);
        listViewTransactions = findViewById(R.id.listViewTransactions);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        fabAddTransaction = findViewById(R.id.navAdd);

        txtGreeting.setText(sharedPrefManager.getFullName());

        transactionAdapter = new TransactionAdapter(this, R.layout.item_transaction, transactionList);
        listViewTransactions.setAdapter(transactionAdapter);
        registerForContextMenu(listViewTransactions);

        listViewTransactions.setOnItemClickListener((parent, view, position, id) -> {
            if (position < transactionList.size()) {
                Transaction selectedTrans = transactionList.get(position);
                Intent intent = new Intent(MainActivity.this, AddEditTransactionActivity.class);
                intent.putExtra("EXTRA_TRANS_ID", selectedTrans.getId());
                addEditLauncher.launch(intent);
            }
        });

        btnMenuMore.setOnClickListener(this::showPopupMenu);

        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            loadData();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            loadData();
        });

        rgTypeFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbFilterExpense) {
                currentTypeFilter = Category.TYPE_EXPENSE;
            } else if (checkedId == R.id.rbFilterIncome) {
                currentTypeFilter = Category.TYPE_INCOME;
            } else {
                currentTypeFilter = "ALL";
            }
            loadData();
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fabAddTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTransactionActivity.class);
            addEditLauncher.launch(intent);
        });

        findViewById(R.id.navCategories).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CategoryActivity.class)));
        findViewById(R.id.navStatistics).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, StatisticsActivity.class)));
        findViewById(R.id.navSettings).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        loadData();
    }

    private String getMonthYearString() {
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH) + 1;
        return String.format(Locale.getDefault(), "%04d-%02d", year, month);
    }

    private void loadData() {
        String monthYear = getMonthYearString();
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH) + 1;
        txtCurrentMonth.setText(String.format(Locale.getDefault(), "%02d/%04d", month, year));

        long totalIncome = transactionDao.getTotalAmountByType(currentUserId, Category.TYPE_INCOME, monthYear);
        long totalExpense = transactionDao.getTotalAmountByType(currentUserId, Category.TYPE_EXPENSE, monthYear);
        long balance = totalIncome - totalExpense;

        txtTotalIncome.setText("+ " + MoneyFormatter.formatMoney(totalIncome));
        txtTotalExpense.setText("- " + MoneyFormatter.formatMoney(totalExpense));
        txtTotalBalance.setText(MoneyFormatter.formatMoney(balance));

        transactionList.clear();
        transactionList.addAll(transactionDao.getTransactionsByUser(currentUserId, currentTypeFilter, monthYear));
        transactionAdapter.notifyDataSetChanged();
        updateTransactionCount();

        if (transactionList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            listViewTransactions.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            listViewTransactions.setVisibility(View.VISIBLE);
        }
    }

    private void performSearch(String keyword) {
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        transactionList.clear();
        transactionList.addAll(transactionDao.searchTransactions(currentUserId, keyword));
        transactionAdapter.notifyDataSetChanged();
        updateTransactionCount();

        if (transactionList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            listViewTransactions.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            listViewTransactions.setVisibility(View.VISIBLE);
        }
    }

    private void updateTransactionCount() {
        txtTransactionCount.setText(transactionList.size() + " giao dịch");
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_category) {
                startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                return true;
            } else if (id == R.id.menu_statistics) {
                startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
                return true;
            } else if (id == R.id.menu_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            } else if (id == R.id.menu_logout) {
                sharedPrefManager.logout();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
        popup.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context_transaction, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (info != null && info.position < transactionList.size()) {
            Transaction selectedTrans = transactionList.get(info.position);
            int id = item.getItemId();
            if (id == R.id.menu_edit) {
                Intent intent = new Intent(MainActivity.this, AddEditTransactionActivity.class);
                intent.putExtra("EXTRA_TRANS_ID", selectedTrans.getId());
                addEditLauncher.launch(intent);
                return true;
            } else if (id == R.id.menu_delete) {
                new AlertDialog.Builder(this)
                        .setTitle("Xóa giao dịch")
                        .setMessage(R.string.confirm_delete_trans)
                        .setPositiveButton(R.string.delete, (dialog, which) -> {
                            transactionDao.deleteTransaction(selectedTrans.getId());
                            Toast.makeText(MainActivity.this, "Đã xóa giao dịch", Toast.LENGTH_SHORT).show();
                            loadData();
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
