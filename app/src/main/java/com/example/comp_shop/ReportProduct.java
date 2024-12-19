package com.example.comp_shop;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReportProduct extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReportProductAdapter reportProductAdapter;
    private List<Product> productList;
    private List<Product> originalProductList; // Holds the unfiltered original list of products

    private Spinner spinnerCategory, spinnerProduct;


    private FirebaseFirestore db;
    private Set<String> categories;
    private List<String> productNames;
    private String selectedCategory = "";
    private String selectedProduct = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_product);

        db = FirebaseFirestore.getInstance();


        recyclerView = findViewById(R.id.recyclerproductreport);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        originalProductList = new ArrayList<>(); // Initialize the original product list
        reportProductAdapter = new ReportProductAdapter(this, productList);
        recyclerView.setAdapter(reportProductAdapter);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerProduct = findViewById(R.id.spinnerProduct);

        categories = new HashSet<>();
        productNames = new ArrayList<>();

        fetchProductDetails();

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedCategory = parentView.getItemAtPosition(position).toString();

                // Reset product selection and update spinner based on selected category
                spinnerProduct.setSelection(0);  // Reset product spinner selection
                selectedProduct = "";
                filterProductsByCategory();

                // Apply filters to update RecyclerView as soon as a new category is selected
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedProduct = parentView.getItemAtPosition(position).toString();
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

    }

    private void fetchProductDetails() {
        CollectionReference productRef = db.collection("Products");

        productRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    productList.clear();
                    originalProductList.clear(); // Clear original list before adding new items
                    categories.clear();
                    productNames.clear();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        productList.add(product);
                        originalProductList.add(product); // Add each product to the original list

                        categories.add(product.getCategory());
                        productNames.add(product.getName());

                        // Log each product for debugging
                        Log.d("ReportProduct", "Product: " + product.getName() + ", Category: " + product.getCategory());
                    }

                    populateCategorySpinner();
                    reportProductAdapter.notifyDataSetChanged();
                } else {
                    Log.e("ReportProduct", "Error fetching product data", task.getException());
                }
            }
        });
    }

    private void populateCategorySpinner() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(categories));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    private void filterProductsByCategory() {
        // Clear product list for the spinner
        List<String> filteredProducts = new ArrayList<>();

        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            for (Product product : originalProductList) { // Use original list to filter
                if (product.getCategory().equals(selectedCategory)) {
                    filteredProducts.add(product.getName());
                }
            }
        }

        if (filteredProducts.isEmpty()) {
            filteredProducts.add("No products available");
            Log.d("ReportProduct", "No products available for category: " + selectedCategory);
        }

        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filteredProducts);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProduct.setAdapter(productAdapter);

        // Reset RecyclerView and log message if no products found
        applyFilters();
    }

    private void applyFilters() {
        List<Product> filteredList = new ArrayList<>();

        if (selectedCategory.isEmpty()) {
            // Show all products if no category is selected
            filteredList.addAll(originalProductList);
        } else {
            // Filter products by selected category only
            for (Product product : originalProductList) {
                boolean matchesCategory = product.getCategory().equals(selectedCategory);

                // Add the product to the filtered list if it matches the selected category
                if (matchesCategory) {
                    filteredList.add(product);
                }
            }
        }

        if (filteredList.isEmpty()) {
            Log.d("ReportProduct", "No products match the selected filters.");
        }

        // Update RecyclerView with filtered products
        reportProductAdapter.updateProductList(filteredList);
    }

}
