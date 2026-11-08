package com.example.personalexpensemanager.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.personalexpensemanager.R;
import com.example.personalexpensemanager.adapters.CategoryAdapter;
import com.example.personalexpensemanager.dao.CategoryDao;
import com.example.personalexpensemanager.models.Category;
import com.example.personalexpensemanager.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private RadioGroup rgType;
    private GridView gridCategories;
    private Button btnAddCategory;
    private TextView txtCategorySection, txtCategoryCount;

    private CategoryDao categoryDao;
    private SharedPrefManager sharedPrefManager;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList = new ArrayList<>();

    private String currentType = Category.TYPE_EXPENSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        sharedPrefManager = new SharedPrefManager(this);
        categoryDao = new CategoryDao(this);

        btnBack = findViewById(R.id.btnBack);
        rgType = findViewById(R.id.rgType);
        gridCategories = findViewById(R.id.gridCategories);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        txtCategorySection = findViewById(R.id.txtCategorySection);
        txtCategoryCount = findViewById(R.id.txtCategoryCount);

        categoryAdapter = new CategoryAdapter(this, R.layout.item_category, categoryList);
        gridCategories.setAdapter(categoryAdapter);

        btnBack.setOnClickListener(v -> finish());

        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbIncome) {
                currentType = Category.TYPE_INCOME;
            } else {
                currentType = Category.TYPE_EXPENSE;
            }
            loadCategories();
        });

        btnAddCategory.setOnClickListener(v -> showAddCategoryDialog(null));

        gridCategories.setOnItemClickListener((parent, view, position, id) -> {
            Category selectedCat = categoryList.get(position);
            showAddCategoryDialog(selectedCat);
        });

        gridCategories.setOnItemLongClickListener((parent, view, position, id) -> {
            Category selectedCat = categoryList.get(position);
            showCategoryOptionsDialog(selectedCat);
            return true;
        });

        loadCategories();
    }

    private void loadCategories() {
        categoryList.clear();
        categoryList.addAll(categoryDao.getCategoriesByUser(sharedPrefManager.getUserId(), currentType));
        categoryAdapter.notifyDataSetChanged();
        txtCategorySection.setText(Category.TYPE_INCOME.equals(currentType) ? "Danh mục thu" : "Danh mục chi");
        txtCategoryCount.setText(categoryList.size() + " danh mục");
    }

    private void showCategoryOptionsDialog(Category category) {
        String[] options = {"Sửa danh mục", "Xóa danh mục"};
        new AlertDialog.Builder(this)
                .setTitle(category.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showAddCategoryDialog(category);
                    } else if (which == 1) {
                        confirmDeleteCategory(category);
                    }
                })
                .show();
    }

    private void showAddCategoryDialog(Category categoryToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_category, null);
        builder.setView(dialogView);

        EditText edtCategoryName = dialogView.findViewById(R.id.edtCategoryName);
        RadioGroup rgCategoryType = dialogView.findViewById(R.id.rgCategoryType);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        if (categoryToEdit != null) {
            edtCategoryName.setText(categoryToEdit.getName());
            if (Category.TYPE_INCOME.equalsIgnoreCase(categoryToEdit.getType())) {
                rgCategoryType.check(R.id.rbIncome);
            } else {
                rgCategoryType.check(R.id.rbExpense);
            }
        }

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = edtCategoryName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Vui lòng nhập tên danh mục!", Toast.LENGTH_SHORT).show();
                return;
            }

            String type = rgCategoryType.getCheckedRadioButtonId() == R.id.rbIncome ? Category.TYPE_INCOME : Category.TYPE_EXPENSE;
            String iconName = type.equals(Category.TYPE_INCOME) ? "ic_other_income" : "ic_other";

            if (categoryToEdit != null) {
                categoryToEdit.setName(name);
                categoryToEdit.setType(type);
                categoryToEdit.setIconName(iconName);
                categoryDao.updateCategory(categoryToEdit);
                Toast.makeText(this, "Đã cập nhật danh mục", Toast.LENGTH_SHORT).show();
            } else {
                Category cat = new Category();
                cat.setUserId(sharedPrefManager.getUserId());
                cat.setName(name);
                cat.setType(type);
                cat.setIconName(iconName);
                categoryDao.insertCategory(cat);
                Toast.makeText(this, "Đã thêm danh mục mới", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
            loadCategories();
        });

        dialog.show();
    }

    private void confirmDeleteCategory(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa danh mục")
                .setMessage(R.string.confirm_delete_cat)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    categoryDao.deleteCategory(category.getId());
                    Toast.makeText(CategoryActivity.this, "Đã xóa danh mục", Toast.LENGTH_SHORT).show();
                    loadCategories();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
