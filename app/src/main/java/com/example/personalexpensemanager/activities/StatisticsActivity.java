package com.example.personalexpensemanager.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.adapters.CategoryStatAdapter;
import com.example.personalexpensemanager.dao.BudgetDao;
import com.example.personalexpensemanager.dao.TransactionDao;
import com.example.personalexpensemanager.models.Budget;
import com.example.personalexpensemanager.models.Category;
import com.example.personalexpensemanager.models.CategoryStat;
import com.example.personalexpensemanager.utils.MoneyFormatter;
import com.example.personalexpensemanager.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView txtCurrentMonth, txtSummaryTitle, txtSpentAmount, txtLimitAmount, txtBudgetWarningMsg;
    private TextView txtStatIncome, txtStatExpense, txtStatBalance, txtBudgetPercent, txtBudgetTitle;
    private TextView txtCategoryStatsSummary;
    private Button btnSetBudget;
    private ProgressBar progressBarBudget;
    private LinearLayout layoutBudgetWarning;
    private LinearLayout cardBudget;
    private Spinner spinnerPeriod;
    private ListView listViewCategoryStats;

    private TransactionDao transactionDao;
    private BudgetDao budgetDao;
    private SharedPrefManager sharedPrefManager;

    private CategoryStatAdapter statAdapter;
    private List<CategoryStat> statList = new ArrayList<>();
    private Calendar currentCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        sharedPrefManager = new SharedPrefManager(this);
        transactionDao = new TransactionDao(this);
        budgetDao = new BudgetDao(this);

        btnBack = findViewById(R.id.btnBack);
        spinnerPeriod = findViewById(R.id.spinnerPeriod);
        txtCurrentMonth = findViewById(R.id.txtCurrentMonth);
        txtSummaryTitle = findViewById(R.id.txtSummaryTitle);
        cardBudget = findViewById(R.id.cardBudget);
        txtSpentAmount = findViewById(R.id.txtSpentAmount);
        txtLimitAmount = findViewById(R.id.txtLimitAmount);
        txtBudgetWarningMsg = findViewById(R.id.txtBudgetWarningMsg);
        txtStatIncome = findViewById(R.id.txtStatIncome);
        txtStatExpense = findViewById(R.id.txtStatExpense);
        txtStatBalance = findViewById(R.id.txtStatBalance);
        txtBudgetPercent = findViewById(R.id.txtBudgetPercent);
        txtBudgetTitle = findViewById(R.id.txtBudgetTitle);
        txtCategoryStatsSummary = findViewById(R.id.txtCategoryStatsSummary);
        btnSetBudget = findViewById(R.id.btnSetBudget);
        progressBarBudget = findViewById(R.id.progressBarBudget);
        layoutBudgetWarning = findViewById(R.id.layoutBudgetWarning);
        listViewCategoryStats = findViewById(R.id.listViewCategoryStats);

        statAdapter = new CategoryStatAdapter(this, R.layout.item_stat_category, statList);
        listViewCategoryStats.setAdapter(statAdapter);

        btnBack.setOnClickListener(v -> finish());

        spinnerPeriod.setSelection(0, false);
        spinnerPeriod.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                loadStatistics();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        txtCurrentMonth.setOnClickListener(v -> showPeriodDatePicker());

        btnSetBudget.setOnClickListener(v -> showSetBudgetDialog());

        loadStatistics();
    }

    private String getMonthYearString() {
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH) + 1;
        return String.format(Locale.getDefault(), "%04d-%02d", year, month);
    }

    private String getPeriodFilter() {
        int period = spinnerPeriod.getSelectedItemPosition();
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH) + 1;
        if (period == 1) {
            return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month,
                    currentCalendar.get(Calendar.DAY_OF_MONTH));
        }
        if (period == 2) {
            return String.format(Locale.getDefault(), "%04d", year);
        }
        return getMonthYearString();
    }

    private void showPeriodDatePicker() {
        new DatePickerDialog(this, (picker, year, month, day) -> {
            currentCalendar.set(year, month, day);
            loadStatistics();
        }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH),
                currentCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadStatistics() {
        String monthYear = getMonthYearString();
        String periodFilter = getPeriodFilter();
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH) + 1;
        int period = spinnerPeriod.getSelectedItemPosition();
        String selectedLabel;
        String summaryLabel;
        if (period == 1) {
            selectedLabel = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                    currentCalendar.get(Calendar.DAY_OF_MONTH), month, year);
            summaryLabel = "Tổng quan ngày " + selectedLabel;
        } else if (period == 2) {
            selectedLabel = String.format(Locale.getDefault(), "%04d", year);
            summaryLabel = "Tổng quan năm " + selectedLabel;
        } else {
            selectedLabel = String.format(Locale.getDefault(), "%02d/%04d", month, year);
            summaryLabel = "Tổng quan tháng " + selectedLabel;
        }
        txtCurrentMonth.setText(selectedLabel);
        txtSummaryTitle.setText(summaryLabel);
        cardBudget.setVisibility(period == 0 ? View.VISIBLE : View.GONE);

        long userId = sharedPrefManager.getUserId();

        long totalIncome = transactionDao.getTotalAmountByType(userId, Category.TYPE_INCOME, periodFilter);
        long totalExpense = transactionDao.getTotalAmountByType(userId, Category.TYPE_EXPENSE, periodFilter);
        txtStatIncome.setText(MoneyFormatter.formatMoney(totalIncome));
        txtStatExpense.setText(MoneyFormatter.formatMoney(totalExpense));
        txtStatBalance.setText(MoneyFormatter.formatMoney(totalIncome - totalExpense));
        txtBudgetTitle.setText(String.format(Locale.getDefault(), "Hạn mức tháng %02d-%04d", month, year));
        txtSpentAmount.setText(MoneyFormatter.formatMoney(totalExpense));

        Budget budget = budgetDao.getBudget(userId, monthYear);
        long budgetLimit = budget != null ? budget.getLimitAmount() : 0;
        txtLimitAmount.setText(MoneyFormatter.formatMoney(budgetLimit));

        if (budgetLimit > 0) {
            float percentSpent = ((float) totalExpense / budgetLimit) * 100f;
            int progress = Math.min(100, Math.round(percentSpent));
            progressBarBudget.setProgress(progress);
            txtBudgetPercent.setText(progress + "%");

            if (percentSpent > 100f) {
                progressBarBudget.setProgressTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.budget_exceeded)));
                cardBudget.setBackgroundResource(R.drawable.bg_rounded_activity_statistics_budget_exceeded);
                btnSetBudget.setBackgroundResource(R.drawable.bg_rounded_btncancel);
                btnSetBudget.setTextColor(getColor(R.color.budget_exceeded));
                layoutBudgetWarning.setVisibility(View.VISIBLE);
                layoutBudgetWarning.setBackgroundResource(R.drawable.bg_exceeded_badge);
                txtBudgetWarningMsg.setText(R.string.budget_exceeded);
                txtBudgetWarningMsg.setTextColor(getColor(R.color.budget_exceeded));
            } else if (percentSpent >= 80f) {
                progressBarBudget.setProgressTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.warning_orange)));
                cardBudget.setBackgroundResource(R.drawable.bg_rounded_activity_statistics_budget_warning);
                btnSetBudget.setBackgroundResource(R.drawable.bg_rounded_btncancel);
                btnSetBudget.setTextColor(getColor(R.color.warning_orange));
                layoutBudgetWarning.setVisibility(View.VISIBLE);
                layoutBudgetWarning.setBackgroundResource(R.drawable.bg_warning_badge);
                txtBudgetWarningMsg.setText(R.string.budget_warning);
                txtBudgetWarningMsg.setTextColor(getColor(R.color.warning_orange));
            } else {
                progressBarBudget.setProgressTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.primary)));
                cardBudget.setBackgroundResource(R.drawable.bg_rounded_budgetpanel);
                btnSetBudget.setBackgroundResource(R.drawable.bg_button_primary);
                btnSetBudget.setTextColor(getColor(R.color.color_16_62_71));
                layoutBudgetWarning.setVisibility(View.GONE);
            }
        } else {
            progressBarBudget.setProgress(0);
            progressBarBudget.setProgressTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.primary)));
            cardBudget.setBackgroundResource(R.drawable.bg_rounded_budgetpanel);
            btnSetBudget.setBackgroundResource(R.drawable.bg_button_primary);
            btnSetBudget.setTextColor(getColor(R.color.color_16_62_71));
            txtBudgetPercent.setText("0%");
            layoutBudgetWarning.setVisibility(View.GONE);
        }

        statList.clear();
        statList.addAll(transactionDao.getCategoryStats(userId, Category.TYPE_EXPENSE, periodFilter));
        statAdapter.notifyDataSetChanged();
        txtCategoryStatsSummary.setText(statList.size() + " danh mục · " + MoneyFormatter.formatMoney(totalExpense));
    }

    private void showSetBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_set_budget, null);
        builder.setView(dialogView);

        TextView txtMonthYear = dialogView.findViewById(R.id.txtMonthYear);
        EditText edtBudgetAmount = dialogView.findViewById(R.id.edtBudgetAmount);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        String monthYear = getMonthYearString();
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH) + 1;
        txtMonthYear.setText(String.format(Locale.getDefault(), "Tháng %02d/%04d", month, year));

        Budget currentBudget = budgetDao.getBudget(sharedPrefManager.getUserId(), monthYear);
        if (currentBudget != null) {
            edtBudgetAmount.setText(String.valueOf(currentBudget.getLimitAmount()));
        }

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String input = edtBudgetAmount.getText().toString().trim();
            long amount = MoneyFormatter.parseMoney(input);
            if (amount <= 0) {
                Toast.makeText(this, R.string.err_invalid_amount, Toast.LENGTH_SHORT).show();
                return;
            }

            budgetDao.setBudget(sharedPrefManager.getUserId(), monthYear, amount);
            Toast.makeText(this, "Đã thiết lập ngân sách thành công!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadStatistics();
        });

        dialog.show();
    }
}
