package com.example.comp_shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Notifications extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAlertAdapter adapter;
    private List<Product> productList;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerAlerts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        adapter = new ProductAlertAdapter(productList,this);
        recyclerView.setAdapter(adapter);

        fetchLowStockProducts();
    }

    private void fetchLowStockProducts() {
        db.collection("Products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        productList.clear();

                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            // Convert string quantity to integer
                            String quantityStr = doc.getString("quantity");
                            int quantity = 0;

                            try {
                                quantity = Integer.parseInt(quantityStr);
                            } catch (NumberFormatException e) {
                                Toast.makeText(this, "Invalid quantity format: " + quantityStr, Toast.LENGTH_SHORT).show();
                                continue;
                            }

                            // Add product only if quantity is 10 or less
                            if (quantity <= 10) {
                                Product product = new Product(
                                        doc.getString("id"),
                                        doc.getString("name"),
                                        doc.getString("category"),
                                        quantityStr, // Store as string in the model
                                        doc.getString("rate")
                                );
                                productList.add(product);
                            }
                        }

                        // Show alert dialog if no products are found
                        if (productList.isEmpty()) {
                            new androidx.appcompat.app.AlertDialog.Builder(this)
                                    .setTitle("No Products Found")
                                    .setMessage("No low-stock products found!")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        dialog.dismiss();
                                        Intent i = new Intent(Notifications.this, MainActivity.class);
                                        startActivity(i);
                                        finish(); // Closes the current activity
                                    })
                                    .show();
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
