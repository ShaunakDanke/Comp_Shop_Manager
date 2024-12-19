package com.example.comp_shop;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText txtInno, txtCid, txtName, txtContactNo, txtProductId, txtProductName, txtCategory, txtPrice, txtQty, txtAmount, txtTotalAmt, txtGst, txtFinalAmt;
    private Spinner cmbCname, cmbPname;
    private TextView lblDate, lblTime;
    private Button btnadditem,btnsave;
    private int amount = 0;
    private BillingCustomerAdapter billingCustomerAdapter;
    private ArrayList<Customer> billingcustomerList;
    private Map<String, String> customerMap;
    private BillingProductAdapter billingProductAdapter;
    private ArrayList<Product> billingproductList;
    private Map<String,String> productMap;
    private InvoiceTempAdapter invoiceTempAdapter;
    private ArrayList<InvoiceTemp> invoiceTempList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        db = FirebaseFirestore.getInstance();
        // Initialize UI components
        initializeUIComponents();
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCustomer);// Replace with your RecyclerView ID
        RecyclerView recyclerView1=findViewById(R.id.recyclerViewProduct);
        RecyclerView recyclerView2=findViewById(R.id.recyclercart);
        // Initialize the customer list and adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        billingcustomerList = new ArrayList<>();
        billingCustomerAdapter = new BillingCustomerAdapter(billingcustomerList, this::onCustomerSelected);
        recyclerView.setAdapter(billingCustomerAdapter);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        billingproductList = new ArrayList<>();
        billingProductAdapter = new BillingProductAdapter(billingproductList,this::onProductSelected); // Note the lowercase 'o'
        recyclerView1.setAdapter(billingProductAdapter);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        invoiceTempList = new ArrayList<>();
        invoiceTempAdapter = new InvoiceTempAdapter(invoiceTempList, this);
        recyclerView2.setAdapter(invoiceTempAdapter);

        lblDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", new java.util.Date()));

        disabledElements();
        // Load initial data
        loadInitialData();
        // Generate invoice ID
        generateInvoiceId();
        cmbCname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (customerMap != null) {
                    String selectedCustomerName = cmbCname.getSelectedItem().toString();
                    String customerId = customerMap.get(selectedCustomerName);

                    if (customerId != null) {
                        loadCustomerData(customerId);
                    } else {
                        Toast.makeText(BillingActivity.this, "Customer ID not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BillingActivity.this, "Customer map is not initialized", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle case where nothing is selected, if necessary
            }
        });
        cmbPname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCategory = cmbPname.getSelectedItem().toString();
                loadproductdata(selectedCategory);  // Load data based on selected category
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle case where nothing is selected, if necessary
            }
        });
        btnadditem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddItemClick(view);
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveClick(view);
            }
        });
txtQty.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        try {
            int x = Integer.parseInt(txtPrice.getText().toString());
            int bill = x * Integer.parseInt(txtQty.getText().toString());
            // Convert the integer to a string before setting it to the TextView
            txtAmount.setText(String.valueOf(bill));
            // Assuming `amount` is calculated somewhere before this line
            txtTotalAmt.setText(String.valueOf(amount));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Handle the case where the input is not a valid number
        }
    }
    @Override
    public void afterTextChanged(Editable editable) {

    }
});
    }

    public void disabledElements()
    {
        txtInno.setEnabled(false);
        txtCid.setEnabled(false);
        txtName.setEnabled(false);
        txtContactNo.setEnabled(false);
        txtProductId.setEnabled(false);
        txtProductId.setEnabled(false);
        txtCategory.setEnabled(false);
        txtPrice.setEnabled(false);
        txtAmount.setEnabled(false);
        txtTotalAmt.setEnabled(false);
        txtGst.setEnabled(false);
        txtFinalAmt.setEnabled(false);
    }

    private void initializeUIComponents() {
        txtInno = findViewById(R.id.txtinno);
        txtCid = findViewById(R.id.txtcid);
        txtName = findViewById(R.id.txtname);
        txtContactNo = findViewById(R.id.txtcontact_no);
        txtProductId = findViewById(R.id.txtProductId);
        txtProductName = findViewById(R.id.txtProductName);
        txtCategory = findViewById(R.id.txtP_category);
        txtPrice = findViewById(R.id.txtprice);
        txtQty = findViewById(R.id.txtqty);
        txtAmount = findViewById(R.id.txtamount);
        txtTotalAmt = findViewById(R.id.txttotalamt);
        txtGst = findViewById(R.id.txtgst);
        txtFinalAmt = findViewById(R.id.txtfinalamt);
        cmbCname = findViewById(R.id.cmbCname);
        cmbPname = findViewById(R.id.cmbPname);
        lblDate = findViewById(R.id.lbldate);
        lblTime = findViewById(R.id.lbltime);
        btnadditem=findViewById(R.id.btnAddItem);
        btnsave=findViewById(R.id.btnSave);
        RecyclerView recyclerView=findViewById(R.id.recyclerViewCustomer);
        RecyclerView recyclerView1=findViewById(R.id.recyclerViewProduct);
    }
    private void loadInitialData() {
        fillCustomerSpinner();
        fillProductSpinner();
        fillCustomerDataGrid();
        fillProductDataGrid();
    }
    private void loadCustomerData(String customerId) {
        db.collection("Customer_Registration").document(customerId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Customer customer = task.getResult().toObject(Customer.class);
                        if (customer != null) {
                            customer.setId(customerId);  // Explicitly set the ID here
                            billingcustomerList.clear();
                            billingcustomerList.add(customer);
                            billingCustomerAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(BillingActivity.this, "No customer found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(BillingActivity.this, "Failed to load customer data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadproductdata(String category) {
        db.collection("Products").whereEqualTo("category", category).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> filteredProducts = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            filteredProducts.add(product);
                        }
                        billingProductAdapter.updateData(filteredProducts);  // Update adapter with filtered data
                    } else {
                        Toast.makeText(BillingActivity.this, "Failed to load products: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void generateInvoiceId() {
        db.collection("Invoice_Master")
                .orderBy("Invoice_no", com.google.firebase.firestore.Query.Direction.DESCENDING) // Order by Invoice_no field
                .limit(1) // Get the latest document based on Invoice_no
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String newInvoiceNo = "001"; // Default value with padding if no records exist

                        if (!task.getResult().isEmpty()) {
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0); // Fetch the first document
                            String invoiceNo = document.getString("Invoice_no"); // Fetch the Invoice_no field

                            Log.d("FirestoreDebug", "Fetched Invoice_no: " + invoiceNo); // Log the fetched Invoice_no

                            if (invoiceNo != null && !invoiceNo.isEmpty()) {
                                try {
                                    int invoiceNumber = Integer.parseInt(invoiceNo); // Convert Invoice_no to a number
                                    invoiceNumber += 1; // Increment the invoice number
                                    // Pad with leading zeros
                                    newInvoiceNo = String.format("%03d", invoiceNumber); // Adjust number of zeros based on max invoice number expected
                                } catch (NumberFormatException e) {
                                    Log.e("FirestoreError", "Error parsing Invoice_no", e);
                                }
                            }
                        } else {
                            Log.d("FirestoreDebug", "No documents found");
                        }
                        txtInno.setText(newInvoiceNo); // Set the new invoice number
                    } else {
                        // Handle the error case
                        Log.e("FirestoreError", "Error fetching the last invoice number", task.getException());
                        Toast.makeText(getApplicationContext(), "Error fetching invoice number", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fillCustomerSpinner() {
        customerMap = new HashMap<>();
        db.collection("Customer_Registration").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> customerNames = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String customerId = document.getId(); // Assuming the document ID is the customer ID
                    String customerName = document.getString("name");
                    customerNames.add(customerName);
                    customerMap.put(customerName, customerId);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, customerNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cmbCname.setAdapter(adapter);
            }
        });
    }
    private void fillProductSpinner() {
        db.collection("Product_Categories").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> productCategories = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    productCategories.add(document.getString("name"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productCategories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cmbPname.setAdapter(adapter);
            }
        });
    }
    private void fillCustomerDataGrid() {
        billingcustomerList.clear();
        db.collection("Customer_Registration").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Populate data grid logic here
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Customer customer = document.toObject(Customer.class);
                    billingcustomerList.add(customer);
                }
                billingCustomerAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to load customers: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fillProductDataGrid() {
        billingproductList.clear();
        db.collection("Products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Populate data grid logic here
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Product product = document.toObject(Product.class);
                    billingproductList.add(product);
                }
                billingProductAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to load Product: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateTotalBill() {
        try {
            double currentTotalAmount = 0;
            double currentGstAmount = 0;
            double currentFinalAmount = 0;

            // Check if fields are not empty before parsing
            if (!txtTotalAmt.getText().toString().isEmpty()) {
                currentTotalAmount = Double.parseDouble(txtTotalAmt.getText().toString());
            }

            if (!txtGst.getText().toString().isEmpty()) {
                currentGstAmount = Double.parseDouble(txtGst.getText().toString());
            }

            if (!txtFinalAmt.getText().toString().isEmpty()) {
                currentFinalAmount = Double.parseDouble(txtFinalAmt.getText().toString());
            }

            // Get the values for the current item being added
            double itemRate = Double.parseDouble(txtPrice.getText().toString());
            double itemQuantity = Double.parseDouble(txtQty.getText().toString());

            if (itemRate <= 0 || itemQuantity <= 0) {
                throw new Exception("Rate or Quantity cannot be zero or negative");
            }

            double itemAmount = itemRate * itemQuantity;

            // Calculate GST for the current item (assuming 18% GST)
            double itemGst = itemAmount * 0.18;
            double itemFinalAmount = itemAmount + itemGst;

            // Accumulate the values
            currentTotalAmount += itemAmount;
            currentGstAmount += itemGst;
            currentFinalAmount += itemFinalAmount;

            // Update UI fields
            txtTotalAmt.setText(String.format("%.2f", currentTotalAmount));
            txtGst.setText(String.format("%.2f", currentGstAmount));
            txtFinalAmt.setText(String.format("%.2f", currentFinalAmount));

        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(BillingActivity.this, "Invalid number format in price or quantity", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(BillingActivity.this, "Error calculating total bill: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void onAddItemClick(View view) {
        try {
            // Get the invoice number
            String invoiceNo = txtInno.getText().toString();

            // Reference the document for the current invoice number
            DocumentReference invoiceRef = db.collection("Invoice_temp").document(invoiceNo);

            // Create a product map to store item details
            Map<String, Object> productData = new HashMap<>();
            productData.put("id", txtProductId.getText().toString());
            productData.put("Product_Name", txtProductName.getText().toString());
            productData.put("Product_Category", txtCategory.getText().toString());
            productData.put("Rate", txtPrice.getText().toString());
            productData.put("Quantity", txtQty.getText().toString());

            // Calculate and set total amount for the current product
            double itemRate = Double.parseDouble(txtPrice.getText().toString());
            double itemQuantity = Double.parseDouble(txtQty.getText().toString());
            double itemAmount = itemRate * itemQuantity;
            productData.put("Total_Amount", String.format("%.2f", itemAmount));

            // Check if the invoice document exists, and add/update products accordingly
            invoiceRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        // Retrieve the 'products' list
                        List<Map<String, Object>> products = (List<Map<String, Object>>) document.get("products");

                        if (products == null) {
                            // Initialize an empty product list if no products exist
                            products = new ArrayList<>();
                        }

                        // Add the current product to the list
                        products.add(productData);

                        // Update the document with the new list of products
                        invoiceRef.update("products", products)
                                .addOnSuccessListener(aVoid -> {
                                    calculateTotalBill();// Ensure total bill, GST, and final amount are recalculated

                                    clearProductFields();

                                    fillInvoiceTempGrid();  // Refresh the invoice grid
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(BillingActivity.this, "Error updating item", Toast.LENGTH_SHORT).show());
                    } else {
                        // Create a new document with the first product if no document exists
                        Map<String, Object> invoiceData = new HashMap<>();
                        List<Map<String, Object>> products = new ArrayList<>();
                        products.add(productData);

                        invoiceData.put("Invoice_no", invoiceNo);
                        invoiceData.put("products", products);

                        invoiceRef.set(invoiceData)
                                .addOnSuccessListener(aVoid -> {
                                    calculateTotalBill(); // Ensure total bill, GST, and final amount are recalculated

                                    clearProductFields();
                                    fillInvoiceTempGrid();  // Refresh the invoice grid
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(BillingActivity.this, "Error adding invoice", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Toast.makeText(BillingActivity.this, "Error fetching invoice", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(BillingActivity.this, "Error processing the item", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearProductFields() {
        txtProductId.setText("");
        txtProductName.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtQty.setText("");
        txtAmount.setText("");
    }

    private void fillInvoiceTempGrid() {
        // Ensure you pass the correct invoice number here
        String invoiceNo = txtInno.getText().toString();

        // Fetch the invoice document based on the invoice number
        db.collection("Invoice_temp").document(invoiceNo).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    // Clear the existing list
                    invoiceTempList.clear();

                    // Retrieve the products list from the document
                    List<Map<String, Object>> products = (List<Map<String, Object>>) document.get("products");

                    if (products != null) {
                        // Iterate through each product in the list
                        for (Map<String, Object> product : products) {
                            try {
                                // Extract product details safely
                                String productName = product.get("Product_Name") != null ?
                                        product.get("Product_Name").toString() : "";
                                String productCategory = product.get("Product_Category") != null ?
                                        product.get("Product_Category").toString() : "";

                                // Parse quantity, rate, and total amount safely (since all are strings in Firebase)
                                int quantity = Integer.parseInt(product.get("Quantity").toString());
                                double rate = Double.parseDouble(product.get("Rate").toString());
                                double totalAmount = Double.parseDouble(product.get("Total_Amount").toString());

                                // Log product details for debugging
                                Log.d("Firestore", "Product: " + productName + ", Quantity: " + quantity);

                                // Create and add the InvoiceTemp object to the list
                                InvoiceTemp invoiceTemp = new InvoiceTemp(productName, productCategory, quantity, rate, totalAmount);
                                invoiceTempList.add(invoiceTemp);

                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                Log.e("Firestore", "Error parsing product data: " + e.getMessage());
                            }
                        }
                    } else {
                        // No products found in the document
                        Log.d("Firestore", "No products found for document: " + document.getId());
                    }

                    // Notify the adapter of the data change to update the RecyclerView
                    invoiceTempAdapter.notifyDataSetChanged();

                } else {
                    Log.d("Firestore", "No such document exists with invoiceNo: " + invoiceNo);
                }
            } else {
                Log.e("Firestore", "Error fetching data: ", task.getException());
                Toast.makeText(BillingActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void onSaveClick(android.view.View view) {
        try {
            String invoiceNo = txtInno.getText().toString(); // Get the invoice number

            // Create a map to store only the invoice details in Invoice_Master (without products)
            java.util.Map<String, Object> invoiceMasterData = new java.util.HashMap<>();
            invoiceMasterData.put("Invoice_no", invoiceNo);
            invoiceMasterData.put("cid", txtCid.getText().toString());
            invoiceMasterData.put("name", txtName.getText().toString());
            invoiceMasterData.put("contact", txtContactNo.getText().toString());
            invoiceMasterData.put("Dop", lblDate.getText().toString());
            invoiceMasterData.put("GST", txtGst.getText().toString());
            invoiceMasterData.put("Final_Amount", txtFinalAmt.getText().toString());

            // Save only invoice details to Invoice_Master
            db.collection("Invoice_Master").document(invoiceNo)
                    .set(invoiceMasterData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Save product details to Invoice_Details
                            saveDetails(invoiceNo);
                            updateStock();
                            Intent intent = new Intent(BillingActivity.this, ReceiptActivity.class);
                            intent.putExtra("INVOICE_NO", invoiceNo);
                            startActivity(intent);
                        } else {
                            Toast.makeText(BillingActivity.this, "Error saving invoice", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void saveDetails(String invoiceNo) {
        // Fetch all products associated with the current invoice number from Invoice_temp collection
        db.collection("Invoice_temp")
                .whereEqualTo("Invoice_no", invoiceNo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> products = new ArrayList<>();

                        // Loop through each document in Invoice_temp and extract product details
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            products.addAll((List<Map<String, Object>>) document.get("products"));
                        }

                        // Create an InvoiceDetails document for storing products
                        Map<String, Object> invoiceDetailsData = new HashMap<>();
                        invoiceDetailsData.put("Invoice_no", invoiceNo);
                        invoiceDetailsData.put("products", products);

                        // Save the product details in the Invoice_Details collection
                        db.collection("Invoice_Details").document(invoiceNo)
                                .set(invoiceDetailsData)
                                .addOnSuccessListener(aVoid -> {
                                    // After saving details, clean up temp data
                                    deleteTempInvoice(invoiceNo);
                                    Toast.makeText(BillingActivity.this, "Invoice saved successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(BillingActivity.this, "Error saving product details", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(BillingActivity.this, "Error retrieving temp data", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void updateStock() {
        try {
            // Step 1: Retrieve all documents from Invoice_Temp
            db.collection("Invoice_temp").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    WriteBatch batch = db.batch(); // Start a batch operation
                    List<Task<Void>> updateTasks = new ArrayList<>(); // To track tasks for all updates

                    // Step 2: Iterate through each document in Invoice_Temp
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        List<Map<String, Object>> products = (List<Map<String, Object>>) document.get("products");

                        // Step 3: Iterate through each product in the invoice
                        for (Map<String, Object> product : products) {
                            String productId = (String) product.get("id");
                            String quantityStr = (String) product.get("Quantity");

                            // Convert quantity from string to long
                            Long quantity = null;
                            try {
                                quantity = Long.parseLong(quantityStr);
                            } catch (NumberFormatException e) {
                                Log.e("UpdateStock", "Invalid quantity format: " + quantityStr);
                                continue; // Skip this product or handle the error as needed
                            }

                            // Fetch the current quantity from the product document
                            DocumentReference productRef = db.collection("Products").document(productId);
                            Long finalQuantity = quantity; // Declare finalQuantity here
                            updateTasks.add(productRef.get().continueWith(taskQuantity -> {
                                if (taskQuantity.isSuccessful()) {
                                    DocumentSnapshot quantityDocument = taskQuantity.getResult();
                                    if (quantityDocument.exists()) {
                                        // Retrieve the current quantity as a string
                                        String currentQuantityStr = quantityDocument.getString("quantity");

                                        // Convert current quantity to Long safely
                                        Long currentQuantity = 0L; // Default value in case of conversion failure
                                        try {
                                            currentQuantity = Long.parseLong(currentQuantityStr);
                                        } catch (NumberFormatException e) {
                                            Log.e("UpdateStock", "Invalid current quantity format: " + currentQuantityStr);
                                            return null; // Skip this product or handle the error as needed
                                        }

                                        // Step 4: Update the quantity as a string in the Products collection
                                        Long newQuantity = currentQuantity - finalQuantity; // Calculate new quantity
                                        String newQuantityStr = String.valueOf(newQuantity); // Convert new quantity back to string

                                        // Perform the update in batch
                                        batch.update(productRef, "quantity", newQuantityStr);
                                    }
                                } else {
                                    Log.e("UpdateStock", "Error getting current quantity for product: " + productId);
                                }
                                return null; // Return null to indicate no result is needed
                            }));
                        }
                    }

                    // Step 5: Commit all stock updates in a single batch after all updates are ready
                    Tasks.whenAllComplete(updateTasks).addOnCompleteListener(taskComplete -> {
                        batch.commit().addOnSuccessListener(aVoid ->
                                        Toast.makeText(BillingActivity.this, "Stock updated successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(BillingActivity.this, "Error updating stock", Toast.LENGTH_SHORT).show());
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteTempInvoice(String invoiceNo) {
        // Delete all temp invoice items for the current invoice number
        db.collection("Invoice_temp")
                .whereEqualTo("Invoice_no", invoiceNo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Delete each document
                            document.getReference().delete();
                        }
                        Toast.makeText(BillingActivity.this, "Temp invoice deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BillingActivity.this, "Error deleting temp invoice", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void onCustomerSelected(Customer customer) {
        if (customer != null) {
            // Set the name, contact, and ID fields when a customer is selected
            txtName.setText(customer.getName());
            txtContactNo.setText(customer.getContact());
            txtCid.setText(customer.getId());  // Set the Customer ID field
        } else {
            // Handle if customer is null
            Toast.makeText(this, "Customer data is invalid", Toast.LENGTH_SHORT).show();
        }
    }
    private  void onProductSelected(Product product){
        txtProductId.setText(product.getId());
        txtProductName.setText(product.getName());
        txtCategory.setText(product.getCategory());
        txtPrice.setText(product.getRate());

    }
}

