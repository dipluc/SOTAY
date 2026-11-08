package com.example.personalexpensemanager.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.adapters.CategoryAdapter;
import com.example.personalexpensemanager.dao.CategoryDao;
import com.example.personalexpensemanager.dao.TransactionDao;
import com.example.personalexpensemanager.models.Category;
import com.example.personalexpensemanager.models.Transaction;
import com.example.personalexpensemanager.utils.DateUtils;
import com.example.personalexpensemanager.utils.MoneyFormatter;
import com.example.personalexpensemanager.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddEditTransactionActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView txtTitle;
    private RadioGroup rgType;
    private RadioButton rbExpense, rbIncome;
    private EditText edtAmount, edtDate, edtNote;
    private TextView txtSelectedCategory;
    private GridView gridCategories;
    private Button btnSelectImage, btnRemoveImage, btnSave;
    private ImageView imgBillPreview;
    private View layoutReceiptPlaceholder;

    private CategoryDao categoryDao;
    private TransactionDao transactionDao;
    private SharedPrefManager sharedPrefManager;

    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList = new ArrayList<>();
    private long selectedCategoryId = -1;
    private String selectedType = Category.TYPE_EXPENSE;
    private String imageUriString = null;
    private long transactionId = -1; // -1 for Add mode

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        try {
                            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (Exception ignored) {}
                        imageUriString = uri.toString();
                        imgBillPreview.setImageURI(uri);
                        imgBillPreview.setVisibility(View.VISIBLE);
                        layoutReceiptPlaceholder.setVisibility(View.GONE);
                        btnRemoveImage.setVisibility(View.VISIBLE);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_transaction);

        sharedPrefManager = new SharedPrefManager(this);
        categoryDao = new CategoryDao(this);
        transactionDao = new TransactionDao(this);

        btnBack = findViewById(R.id.btnBack);
        txtTitle = findViewById(R.id.txtTitle);
        rgType = findViewById(R.id.rgType);
        rbExpense = findViewById(R.id.rbExpense);
        rbIncome = findViewById(R.id.rbIncome);
        edtAmount = findViewById(R.id.edtAmount);
        edtDate = findViewById(R.id.edtDate);
        edtNote = findViewById(R.id.edtNote);
        txtSelectedCategory = findViewById(R.id.txtSelectedCategory);
        gridCategories = findViewById(R.id.gridCategories);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnRemoveImage = findViewById(R.id.btnRemoveImage);
        btnSave = findViewById(R.id.btnSave);
        imgBillPreview = findViewById(R.id.imgBillPreview);
        layoutReceiptPlaceholder = findViewById(R.id.layoutReceiptPlaceholder);

        categoryAdapter = new CategoryAdapter(this, R.layout.item_category, categoryList);
        gridCategories.setAdapter(categoryAdapter);

        btnBack.setOnClickListener(v -> finish());

        edtDate.setText(DateUtils.formatToDisplayDate(DateUtils.getTodayDbDate()));
        edtDate.setOnClickListener(v -> showDatePicker());

        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbIncome) {
                selectedType = Category.TYPE_INCOME;
            } else {
                selectedType = Category.TYPE_EXPENSE;
            }
            selectedCategoryId = -1;
            loadCategories();
        });

        txtSelectedCategory.setOnClickListener(v -> showCategoryPicker());

        gridCategories.setOnItemClickListener((parent, view, position, id) -> {
            Category cat = categoryList.get(position);
            selectedCategoryId = cat.getId();
            categoryAdapter.setSelectedCategoryId(selectedCategoryId);
        });

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            imagePickerLauncher.launch(intent);
        });

        btnRemoveImage.setOnClickListener(v -> {
            imageUriString = null;
            imgBillPreview.setImageURI(null);
            imgBillPreview.setVisibility(View.GONE);
            layoutReceiptPlaceholder.setVisibility(View.VISIBLE);
            btnRemoveImage.setVisibility(View.GONE);
        });

        btnSave.setOnClickListener(v -> saveTransaction());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

        transactionId = getIntent().getLongExtra("EXTRA_TRANS_ID", -1);
        if (transactionId != -1) {
            txtTitle.setText(R.string.edit_transaction);
            loadTransactionForEdit();
        } else {
            txtTitle.setText(R.string.add_transaction);
            loadCategories();
        }
    }

    private void loadCategories() {
        categoryList.clear();
        categoryList.addAll(categoryDao.getCategoriesByUser(sharedPrefManager.getUserId(), selectedType));
        categoryAdapter.notifyDataSetChanged();
        if (!categoryList.isEmpty() && selectedCategoryId == -1) {
            selectedCategoryId = categoryList.get(0).getId();
            categoryAdapter.setSelectedCategoryId(selectedCategoryId);
        }
        refreshSelectedCategoryLabel();
    }

    private void refreshSelectedCategoryLabel() {
        for (Category category : categoryList) {
            if (category.getId() == selectedCategoryId) {
                txtSelectedCategory.setText(category.getName());
                return;
            }
        }
        txtSelectedCategory.setText("Chọn danh mục");
    }

    private void showCategoryPicker() {
        if (categoryList.isEmpty()) return;
        String[] names = new String[categoryList.size()];
        int selectedIndex = 0;
        for (int i = 0; i < categoryList.size(); i++) {
            Category category = categoryList.get(i);
            names[i] = category.getName();
            if (category.getId() == selectedCategoryId) selectedIndex = i;
        }
        new AlertDialog.Builder(this)
                .setTitle("Danh mục")
                .setSingleChoiceItems(names, selectedIndex, (dialog, which) -> {
                    Category selected = categoryList.get(which);
                    selectedCategoryId = selected.getId();
                    categoryAdapter.setSelectedCategoryId(selectedCategoryId);
                    refreshSelectedCategoryLabel();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void loadTransactionForEdit() {
        List<Transaction> all = transactionDao.getTransactionsByUser(sharedPrefManager.getUserId(), "ALL", null);
        Transaction editTrans = null;
        for (Transaction t : all) {
            if (t.getId() == transactionId) {
                editTrans = t;
                break;
            }
        }

        if (editTrans != null) {
            edtAmount.setText(String.valueOf(editTrans.getAmount()));
            edtNote.setText(editTrans.getNote());
            edtDate.setText(DateUtils.formatToDisplayDate(editTrans.getDate()));
            edtDate.setTag(editTrans.getDate());
            selectedCategoryId = editTrans.getCategoryId();
            selectedType = editTrans.getCategoryType();

            if (Category.TYPE_INCOME.equalsIgnoreCase(selectedType)) {
                rbIncome.setChecked(true);
            } else {
                rbExpense.setChecked(true);
            }

            loadCategories();
            categoryAdapter.setSelectedCategoryId(selectedCategoryId);

            if (editTrans.getImageUri() != null && !editTrans.getImageUri().isEmpty()) {
                imageUriString = editTrans.getImageUri();
                try {
                    imgBillPreview.setImageURI(Uri.parse(imageUriString));
                    imgBillPreview.setVisibility(View.VISIBLE);
                    layoutReceiptPlaceholder.setVisibility(View.GONE);
                    btnRemoveImage.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    imgBillPreview.setVisibility(View.GONE);
                }
            }
        }
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String dbDate = DateUtils.formatToDbDate(year, month, dayOfMonth);
                    edtDate.setText(DateUtils.formatToDisplayDate(dbDate));
                    edtDate.setTag(dbDate);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void saveTransaction() {
        String amountStr = edtAmount.getText().toString().trim();
        long amount = MoneyFormatter.parseMoney(amountStr);

        if (amount <= 0) {
            Toast.makeText(this, R.string.err_invalid_amount, Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategoryId == -1) {
            Toast.makeText(this, "Vui lòng chọn danh mục!", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = edtNote.getText().toString().trim();
        String dateStr = edtDate.getText().toString().trim();
        String dbDate;
        if (edtDate.getTag() != null) {
            dbDate = (String) edtDate.getTag();
        } else {
            dbDate = DateUtils.getTodayDbDate();
        }

        Transaction t = new Transaction();
        t.setUserId(sharedPrefManager.getUserId());
        t.setCategoryId(selectedCategoryId);
        t.setAmount(amount);
        t.setNote(note);
        t.setDate(dbDate);
        t.setImageUri(imageUriString);

        if (transactionId != -1) {
            t.setId(transactionId);
            transactionDao.updateTransaction(t);
            Toast.makeText(this, "Đã cập nhật giao dịch", Toast.LENGTH_SHORT).show();
        } else {
            transactionDao.insertTransaction(t);
            Toast.makeText(this, "Đã thêm giao dịch thành công", Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }
}
