package com.example.comp_shop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReceiptActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText txtInvoiceNo, txtCid, txtContactNo, txtCname, txtGst, txtFinalamt, txtPdate;
    private RecyclerView recyclerReceiptProducts;
    private ReceiptProductAdapter receiptProductAdapter;
    private List<ReceiptProduct> receiptProductList;
    private Button btnConvertToPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }


        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Find Views
        txtInvoiceNo = findViewById(R.id.txtinvoiceno);
        txtCid = findViewById(R.id.txtCid);
        txtContactNo = findViewById(R.id.txtContactNo);
        txtCname = findViewById(R.id.txtCname);
        txtGst = findViewById(R.id.txtGst);
        txtFinalamt = findViewById(R.id.txtFinalamt);
        txtPdate = findViewById(R.id.txtPdate);

        recyclerReceiptProducts = findViewById(R.id.recyclerReceiptProducts);
        btnConvertToPdf = findViewById(R.id.btnConvertToPdf);

        // Set up RecyclerView
        recyclerReceiptProducts.setLayoutManager(new LinearLayoutManager(this));
        receiptProductList = new ArrayList<>();
        receiptProductAdapter = new ReceiptProductAdapter(receiptProductList, this);
        recyclerReceiptProducts.setAdapter(receiptProductAdapter);

        // Retrieve and display receipt data
        retrieveReceiptData();

        // Set up PDF conversion button
        btnConvertToPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertToPdf();
            }
        });

        // Show order confirmation message after 3 seconds
        showConfirmationMessage();
    }

    private void retrieveReceiptData() {
        Intent intent = getIntent();
        String invoiceNo = intent.getStringExtra("INVOICE_NO");

        if (invoiceNo == null) {
            Toast.makeText(this, "Invalid invoice number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set the invoice number in the view
        txtInvoiceNo.setText(invoiceNo);

        // Retrieve invoice details
        db.collection("Invoice_Master").document(invoiceNo).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                txtCid.setText(document.getString("cid"));
                                txtContactNo.setText(document.getString("contact"));
                                txtCname.setText(document.getString("name"));
                                txtGst.setText(document.getString("GST"));
                                txtFinalamt.setText(document.getString("Final_Amount"));
                                txtPdate.setText(document.getString("Dop"));
                            } else {
                                Toast.makeText(ReceiptActivity.this, "Invoice not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ReceiptActivity.this, "Error retrieving invoice details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Retrieve product details
        db.collection("Invoice_Details")
                .whereEqualTo("Invoice_no", invoiceNo) // Searching by Invoice_no field
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Extract product details from the products array
                                    List<Map<String, Object>> products = (List<Map<String, Object>>) document.get("products");
                                    if (products != null) {
                                        for (Map<String, Object> productMap : products) {
                                            // Extract individual product fields
                                            String productName = (String) productMap.get("Product_Name");
                                            String productCategory = (String) productMap.get("Product_Category");
                                            String productId = (String) productMap.get("Product_id");

                                            // Handle the Quantity field based on its type (either Long or String)
                                            int quantity;
                                            try {
                                                quantity = ((Long) productMap.get("Quantity")).intValue(); // if stored as Long
                                            } catch (ClassCastException e) {
                                                quantity = Integer.parseInt((String) productMap.get("Quantity")); // if stored as String
                                            }

                                            // Parse Rate and Total_Amount fields safely
                                            double rate = Double.parseDouble(productMap.get("Rate").toString());
                                            double totalAmount = Double.parseDouble(productMap.get("Total_Amount").toString());

                                            // Create a ReceiptProduct object
                                            ReceiptProduct product = new ReceiptProduct(productName, productId, productCategory, quantity, rate, totalAmount);
                                            receiptProductList.add(product);
                                        }
                                        receiptProductAdapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
                                    } else {
                                        Toast.makeText(ReceiptActivity.this, "No products found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(ReceiptActivity.this, "Invoice not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ReceiptActivity.this, "Error retrieving product details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to convert the receipt details to a PDF
    private void convertToPdf() {
        // Create a new PdfDocument
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        // Start a page in the PDF
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Write text to the PDF (for example, invoice details)
        canvas.drawText("Invoice No: " + txtInvoiceNo.getText().toString(), 10, 25, paint);
        canvas.drawText("Customer ID: " + txtCid.getText().toString(), 10, 55, paint);
        canvas.drawText("Customer Name: " + txtCname.getText().toString(), 10, 85, paint);
        canvas.drawText("Contact No: " + txtContactNo.getText().toString(), 10, 115, paint);
        canvas.drawText("GST: " + txtGst.getText().toString(), 10, 145, paint);
        canvas.drawText("Final Amount: " + txtFinalamt.getText().toString(), 10, 175, paint);
        canvas.drawText("Purchase Date: " + txtPdate.getText().toString(), 10, 205, paint);

        // Loop through the products and add them to the PDF
        int yPos = 235; // Start position for product details
        for (ReceiptProduct product : receiptProductList) {
            canvas.drawText("Product Name: " + product.getProductName(), 10, yPos, paint);
            canvas.drawText("Quantity: " + product.getQuantity(), 200, yPos, paint);
            canvas.drawText("Price: " + product.getPrice(), 300, yPos, paint);
            canvas.drawText("Total: " + product.getTotalAmount(), 400, yPos, paint);
            yPos += 30; // Move to the next line
        }

        // Finish the page
        pdfDocument.finishPage(page);

        // Define a path in the app's external storage directory
        File pdfDir = new File(getExternalFilesDir(null), "PDFs");
        if (!pdfDir.exists()) {
            pdfDir.mkdir(); // Create directory if it does not exist
        }

        // Specify the file name and path
        File pdfFile = new File(pdfDir, "Receipt.pdf");

        // Write the PDF document to the file
        try {
            pdfDocument.writeTo(new FileOutputStream(pdfFile));
            Toast.makeText(this, "PDF saved to " + pdfFile.getPath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
        }

        // Close the document
        pdfDocument.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            // Check if the request was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with file operations
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied, inform the user
                Toast.makeText(this, "Permission denied. Cannot save PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showConfirmationMessage() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReceiptActivity.this);
                builder.setTitle("Order Confirmation")
                        .setMessage("Your order has been placed successfully!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(ReceiptActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                                dialog.dismiss();


                            }
                        })
                        .show();
            }
        }, 10000); // 3 seconds delay
    }
}
