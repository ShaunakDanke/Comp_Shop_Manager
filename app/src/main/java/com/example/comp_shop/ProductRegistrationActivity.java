package com.example.comp_shop;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class ProductRegistrationActivity extends AppCompatActivity {

    private Spinner spinnerCategories;
    private EditText txtProductName, txtQuantity, txtRate;
    private Button btnSave, btnUpdate, btnDelete, btnNew;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ArrayList<Product> productList;

    private FirebaseFirestore firestore;
    private CollectionReference categoriesRef, productsRef;
    private ArrayList<String> categoriesList;
    private String selectedProductId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_registration);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        categoriesRef = firestore.collection("Product_Categories");
        productsRef = firestore.collection("Products");

        spinnerCategories = findViewById(R.id.spinnerCategories);
        txtProductName = findViewById(R.id.txtProductName);
        txtQuantity = findViewById(R.id.txtQuantity);
        txtRate = findViewById(R.id.txtRate);
        btnSave = findViewById(R.id.btnSave);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnNew = findViewById(R.id.btnNew);
        recyclerView = findViewById(R.id.recyclerView);

        categoriesList = new ArrayList<>();
        productList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(productList, this::loadProductDetails);
        recyclerView.setAdapter(productAdapter);

        loadCategoriesIntoSpinner();
        loadProductsIntoRecyclerView();

        btnSave.setOnClickListener(v -> saveProduct());
        btnUpdate.setOnClickListener(v -> updateProduct());
        btnDelete.setOnClickListener(v -> deleteProduct());
        btnNew.setOnClickListener(v -> clearFields());
    }

    private void loadCategoriesIntoSpinner() {
        categoriesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                categoriesList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String categoryName = document.getString("name");
                    if (categoryName != null) {
                        categoriesList.add(categoryName);
                    }
                }

                if (!categoriesList.isEmpty()) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ProductRegistrationActivity.this, android.R.layout.simple_spinner_item, categoriesList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategories.setAdapter(adapter);
                } else {
                    Toast.makeText(ProductRegistrationActivity.this, "No categories found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProductRegistrationActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductsIntoRecyclerView() {
        productsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Product product = document.toObject(Product.class);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ProductRegistrationActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProduct() {
        String categoryName = spinnerCategories.getSelectedItem().toString();
        String productName = txtProductName.getText().toString();
        String quantity = txtQuantity.getText().toString();
        String rate = txtRate.getText().toString();

        if (categoryName.isEmpty() || productName.isEmpty() || quantity.isEmpty() || rate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String productId = productsRef.document().getId();
        Product product = new Product(productId, categoryName, productName, quantity, rate);
        productsRef.document(productId).set(product).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Product saved", Toast.LENGTH_SHORT).show();
                clearFields();
                loadProductsIntoRecyclerView();
            } else {
                Toast.makeText(this, "Failed to save product", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProduct() {
        if (selectedProductId == null) {
            Toast.makeText(this, "No product selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String categoryName = spinnerCategories.getSelectedItem().toString();
        String productName = txtProductName.getText().toString();
        String quantity = txtQuantity.getText().toString();
        String rate = txtRate.getText().toString();

        if (categoryName.isEmpty() || productName.isEmpty() || quantity.isEmpty() || rate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Product product = new Product(selectedProductId, categoryName, productName, quantity, rate);
        productsRef.document(selectedProductId).set(product).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show();
                clearFields();
                loadProductsIntoRecyclerView();
            } else {
                Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteProduct() {
        if (selectedProductId == null) {
            Toast.makeText(this, "No product selected", Toast.LENGTH_SHORT).show();
            return;
        }

        productsRef.document(selectedProductId).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
                clearFields();
                loadProductsIntoRecyclerView();
            } else {
                Toast.makeText(this, "Failed to delete product", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductDetails(Product product) {
        selectedProductId = product.getId();
        spinnerCategories.setSelection(categoriesList.indexOf(product.getCategory()));
        txtProductName.setText(product.getName());
        txtQuantity.setText(product.getQuantity());
        txtRate.setText(product.getRate());
    }

    private void clearFields() {
        txtProductName.setText("");
        txtQuantity.setText("");
        txtRate.setText("");
        spinnerCategories.setSelection(0);
        selectedProductId = null;
    }
}
