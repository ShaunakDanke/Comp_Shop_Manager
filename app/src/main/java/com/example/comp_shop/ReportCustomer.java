package com.example.comp_shop;

import android.os.Bundle;
import android.util.Log;
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
import java.util.List;

public class ReportCustomer extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerAdapterReceipt customerAdapter;
    private List<Customer> customerList;

    // Firestore instance
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_customer);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclercustomerreport);  // Use correct ID from XML layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Customer List and Adapter
        customerList = new ArrayList<>();
        customerAdapter = new CustomerAdapterReceipt(this, customerList);  // Use correct adapter class
        recyclerView.setAdapter(customerAdapter);

        // Fetch customer details from Firestore
        fetchCustomerDetails();
    }

    private void fetchCustomerDetails() {
        // Firestore reference to the 'customers' collection
        CollectionReference customerRef = db.collection("Customer_Registration");

        // Fetch the documents from the 'customers' collection
        customerRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    customerList.clear();  // Clear the list before adding new data

                    // Loop through the query snapshot and retrieve customer data
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Customer customer = document.toObject(Customer.class);
                        customerList.add(customer);  // Add the customer to the list
                    }

                    // Notify the adapter of data change
                    customerAdapter.notifyDataSetChanged();
                } else {
                    Log.e("ReportCustomer", "Error fetching customer data", task.getException());
                }
            }
        });
    }
}
