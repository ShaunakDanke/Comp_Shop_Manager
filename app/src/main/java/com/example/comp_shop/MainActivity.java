package com.example.comp_shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    Button CustomerButton, ProductButton, BillingButton, CustomerReportbtn, ProductReportbtn, SalesReportbtn,Alertsbtn;
    TextView userName, userEmail;
    ImageView userImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Initialize views
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userImage = findViewById(R.id.userImage);

        // Get current user from Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Set user details
            userName.setText(currentUser.getDisplayName());
            userEmail.setText(currentUser.getEmail());

            // Fetch and load user profile image from Gmail if available
            if (currentUser.getPhotoUrl() != null) {
                // Load the image using Picasso
                Picasso.get().load(currentUser.getPhotoUrl()).into(userImage);
            } else {
                // Set default placeholder image if no photo is available
                userImage.setImageResource(R.drawable.splashlogo);
            }
        }

        // Initialize buttons
        CustomerButton = findViewById(R.id.btnCustomerRegistration);
        ProductButton = findViewById(R.id.btnProductRegistration);
        BillingButton = findViewById(R.id.btnBilling);
        Alertsbtn=findViewById(R.id.btnAlerts);

        CustomerReportbtn = findViewById(R.id.btnCustomerReports);
        ProductReportbtn = findViewById(R.id.btnProductReports);
        SalesReportbtn = findViewById(R.id.btnSalesReport);
        // Set up button listeners
        SalesReportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ReportSalesMaster.class);
                startActivity(i);
            }
        });

        ProductReportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ReportProduct.class);
                startActivity(i);
            }
        });

        CustomerReportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ReportCustomer.class);
                startActivity(i);
            }
        });

        CustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(MainActivity.this, CustomerRegistrationActivity.class);
                    startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        ProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ProductRegistrationActivity.class);
                startActivity(i);
            }
        });

        BillingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(MainActivity.this, BillingActivity.class);
                    startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error" + e, Toast.LENGTH_LONG).show();
                }
            }
        });

        Alertsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this, Notifications.class);
                startActivity(i);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        // Check if the selected item is the "Settings" option
        if (id == R.id.action_settings) {
            // Handle the settings option click
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), AdminLogin.class));
            finish();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
