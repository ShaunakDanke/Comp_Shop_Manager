package com.example.comp_shop;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReportSalesMaster extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SalesReportAdapter salesReportAdapter;
    private List<SalesReport> salesReportList;

    // Firestore instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference invoiceMasterRef = db.collection("Invoice_Master");

    private Button btnStartDate, btnEndDate, btnFetchRecords, btnFetchAllRecords;
    private String selectedStartDate, selectedEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_sales_master);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerSalesreport);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        salesReportList = new ArrayList<>();
        salesReportAdapter = new SalesReportAdapter(salesReportList);
        recyclerView.setAdapter(salesReportAdapter);

        // Initialize Buttons for date pickers and fetch records
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnFetchRecords = findViewById(R.id.btnFetchRecords);
        btnFetchAllRecords = findViewById(R.id.btnFetchAllRecords);

        // Fetch all records when the "Fetch All" button is clicked
        btnFetchAllRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchSalesData();  // Fetch all records without date filtering
            }
        });

        // Set OnClickListener for the start date button
        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(true);  // Show the date picker for start date
            }
        });

        // Set OnClickListener for the end date button
        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(false);  // Show the date picker for end date
            }
        });

        // Set OnClickListener for the fetch records button (fetch with date range)
        btnFetchRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchSalesData(selectedStartDate, selectedEndDate);  // Fetch filtered records by date range
            }
        });

        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Fetch all sales data initially (on activity load)
        fetchSalesData();
    }

    // Show the date picker dialog
    private void showDatePickerDialog(final boolean isStartDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(selectedYear, selectedMonth, selectedDay);

                // Format the date to "yyyy-MM-dd"
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = sdf.format(selectedDate.getTime());

                // Set the selected date to the appropriate variable
                if (isStartDate) {
                    selectedStartDate = formattedDate;
                    btnStartDate.setText(formattedDate);  // Update button text with the selected start date
                } else {
                    selectedEndDate = formattedDate;
                    btnEndDate.setText(formattedDate);  // Update button text with the selected end date
                }
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    // Fetch sales data based on selected start and end dates
    private void fetchSalesData(String startDate, String endDate) {
        // Clear the existing list
        salesReportList.clear();

        if (startDate != null && endDate != null) {
            // Log the selected date range for debugging purposes
            Log.d("ReportSalesMaster", "Fetching records from " + startDate + " to " + endDate);

            // Query Firestore for documents where "Dop" is between startDate and endDate (both inclusive)
            invoiceMasterRef
                    .whereGreaterThanOrEqualTo("Dop", startDate)
                    .whereLessThanOrEqualTo("Dop", endDate)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Extract fields from Firestore document
                                    String customerId = document.getString("cid");
                                    String customerName = document.getString("name");
                                    String customerContact = document.getString("contact");
                                    String dateOfPurchase = document.getString("Dop");
                                    String gst = document.getString("GST");
                                    String finalAmount = document.getString("Final_Amount");

                                    // Create and add sales report object to the list
                                    SalesReport salesReport = new SalesReport(
                                            customerId, customerName, customerContact, dateOfPurchase, gst, finalAmount);
                                    salesReportList.add(salesReport);
                                }

                                // Notify the adapter that data has changed
                                salesReportAdapter.notifyDataSetChanged();
                            } else {
                                Log.w("ReportSalesMaster", "Error getting documents.", task.getException());
                            }
                        }
                    });
        } else {
            // If no date is provided, fetch all data (fallback case)
            fetchSalesData();
        }
    }

    // Fetch all sales data (without date filter)
    private void fetchSalesData() {
        // Clear the existing list
        salesReportList.clear();

        // Retrieve data from Firestore's Invoice_Master collection
        invoiceMasterRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Extract fields from Firestore document
                        String customerId = document.getString("cid");
                        String customerName = document.getString("name");
                        String customerContact = document.getString("contact");
                        String dateOfPurchase = document.getString("Dop");
                        String gst = document.getString("GST");
                        String finalAmount = document.getString("Final_Amount");

                        // Create and add sales report object to the list
                        SalesReport salesReport = new SalesReport(
                                customerId, customerName, customerContact, dateOfPurchase, gst, finalAmount);
                        salesReportList.add(salesReport);
                    }

                    // Notify the adapter that data has changed
                    salesReportAdapter.notifyDataSetChanged();
                } else {
                    Log.w("ReportSalesMaster", "Error getting documents.", task.getException());
                }
            }
        });
    }
}
