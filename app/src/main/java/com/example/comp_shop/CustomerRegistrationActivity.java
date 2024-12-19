package com.example.comp_shop;

import com.example.comp_shop.R;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerRegistrationActivity extends AppCompatActivity {

    // UI Elements
    private EditText txtName, txtAddress, txtPincode, txtContact, txtEmail;
    private Spinner spinnerCity;
    private TextView lblDate;
    private Button btnSave, btnUpdate, btnDelete, btnNew;
    private RecyclerView recyclerView;

    private CustomerAdapter customerAdapter;
    private ArrayList<Customer> customerList;
    private FirebaseFirestore db;
    private String selectedCustomerId;

    // City and Pincode mapping
    private Map<String, String> cityPincodeMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_registration);

        // Initialize UI Elements
        txtName = findViewById(R.id.txtName);
        txtAddress = findViewById(R.id.txtAddress);
        spinnerCity = findViewById(R.id.spinnerCity);
        txtPincode=findViewById(R.id.txtPincode);
        txtContact = findViewById(R.id.txtContact);
        txtEmail = findViewById(R.id.txtEmail);
        lblDate = findViewById(R.id.lblDate);
        btnSave = findViewById(R.id.btnSave);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnNew = findViewById(R.id.btnNew);
        recyclerView = findViewById(R.id.recyclerView);

        db = FirebaseFirestore.getInstance();

        // Set date for lblDate
        lblDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", new java.util.Date()));

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        customerList = new ArrayList<>();
        customerAdapter = new CustomerAdapter(customerList, this::onCustomerSelected);
        recyclerView.setAdapter(customerAdapter);

        // Populate city and pincode data
        populateCityPincodeData();
        setupCitySpinner();

        // Set button listeners
        btnSave.setOnClickListener(view -> saveCustomer());
        btnUpdate.setOnClickListener(view -> updateCustomer());
        btnDelete.setOnClickListener(view -> deleteCustomer());
        btnNew.setOnClickListener(view -> clearFields());

        // Load existing customers
        loadCustomerData();
    }

    private void populateCityPincodeData() {
        cityPincodeMap = new HashMap<>();
        cityPincodeMap.put("Baramati", "413133");
        cityPincodeMap.put("Solapur", "413001");
        cityPincodeMap.put("Pune", "411001");
        cityPincodeMap.put("Aurangabad", "431001");
        cityPincodeMap.put("Mumbai", "400001");
        cityPincodeMap.put("Delhi","110001");
        cityPincodeMap.put("Ahmedabad","380001");
        cityPincodeMap.put("Bengaluru","560001");
        cityPincodeMap.put("Chennai","600001");
        cityPincodeMap.put("Hyderabad","500001");
        cityPincodeMap.put("Kochi","682001");
        cityPincodeMap.put("Kolkata","700001");
        cityPincodeMap.put("Guwahati","781001");
        cityPincodeMap.put("Bhopal","462001");
        cityPincodeMap.put("Raipur","492001");
        cityPincodeMap.put("Jaipur","302001");
        cityPincodeMap.put("Jodhpur","342001");
        cityPincodeMap.put("Ajmer","305001");
        cityPincodeMap.put("Bikaner","334001");

    }

    private void setupCitySpinner() {
        // Get city list from the map
        ArrayList<String> cityList = new ArrayList<>(cityPincodeMap.keySet());

        // Set up Spinner adapter
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(cityAdapter);

        // Handle selection of city
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = cityList.get(position);
                String pincode = cityPincodeMap.get(selectedCity);

                if (txtPincode != null) { // Check if txtPincode is initialized
                    txtPincode.setText(pincode);  // Automatically update the pincode based on city
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (txtPincode != null) { // Check if txtPincode is initialized
                    txtPincode.setText("");
                }
            }
        });

    }

    private void loadCustomerData() {
        db.collection("Customer_Registration").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                customerList.clear();
                for (DocumentSnapshot document : task.getResult()) {
                    Customer customer = document.toObject(Customer.class);
                    customer.setId(document.getId());
                    customerList.add(customer);
                }
                customerAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(CustomerRegistrationActivity.this, "Failed to load customer data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCustomer() {
        if (validate()) {
            String name = txtName.getText().toString();
            String address = txtAddress.getText().toString();
            String city = spinnerCity.getSelectedItem().toString();
            String pincode = txtPincode.getText().toString();
            String contact = txtContact.getText().toString();
            String email = txtEmail.getText().toString();
            String date = lblDate.getText().toString();

            Map<String, Object> customerData = new HashMap<>();
            customerData.put("name", name);
            customerData.put("address", address);
            customerData.put("city", city);
            customerData.put("pincode", pincode);
            customerData.put("contact", contact);
            customerData.put("email", email);
            customerData.put("date", date);

            db.collection("Customer_Registration").add(customerData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(CustomerRegistrationActivity.this, "Customer saved", Toast.LENGTH_SHORT).show();
                        loadCustomerData();
                        clearFields();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreError", "Failed to save customer", e);
                        Toast.makeText(CustomerRegistrationActivity.this, "Failed to save customer", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private void updateCustomer() {
        if (selectedCustomerId != null) {
            if (validate()) {
                String name = txtName.getText().toString();
                String address = txtAddress.getText().toString();
                String city = spinnerCity.getSelectedItem().toString();
                String pincode = txtPincode.getText().toString();
                String contact = txtContact.getText().toString();
                String email = txtEmail.getText().toString();
                String date = lblDate.getText().toString();

                Map<String, Object> customerData = new HashMap<>();
                customerData.put("name", name);
                customerData.put("address", address);
                customerData.put("city", city);
                customerData.put("pincode", pincode);
                customerData.put("contact", contact);
                customerData.put("email", email);
                customerData.put("date", date);

                db.collection("Customer_Registration").document(selectedCustomerId).update(customerData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(CustomerRegistrationActivity.this, "Customer updated", Toast.LENGTH_SHORT).show();
                            loadCustomerData();
                            clearFields();
                        }).addOnFailureListener(e -> Toast.makeText(CustomerRegistrationActivity.this, "Failed to update customer", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void deleteCustomer() {
        if (selectedCustomerId != null) {
            db.collection("Customer_Registration").document(selectedCustomerId).delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(CustomerRegistrationActivity.this, "Customer deleted", Toast.LENGTH_SHORT).show();
                        loadCustomerData();
                        clearFields();
                    }).addOnFailureListener(e -> Toast.makeText(CustomerRegistrationActivity.this, "Failed to delete customer", Toast.LENGTH_SHORT).show());
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtAddress.setText("");
        txtPincode.setText("");
        txtContact.setText("");
        txtEmail.setText("");
        spinnerCity.setSelection(0);
        selectedCustomerId = null;
    }

    private boolean validate() {
        String name = txtName.getText().toString().trim();
        String address = txtAddress.getText().toString().trim();
        String pincode = txtPincode.getText().toString().trim();
        String contact = txtContact.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            txtName.setError("Please enter name");
            txtName.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(address)) {
            txtAddress.setError("Please enter address");
            txtAddress.requestFocus();
            return false;
        }  else if (!pincode.matches("\\d+")) {
            txtPincode.setError("Pincode must contain only digits");
            txtPincode.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(contact)) {
            txtContact.setError("Please enter contact");
            txtContact.requestFocus();
            return false;
        } else if (contact.length() != 10) {
            txtContact.setError("Contact must be 10 digits");
            txtContact.requestFocus();
            return false;
        } else if (!contact.matches("\\d+")) {
            txtContact.setError("Contact must contain only digits");
            txtContact.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Please enter email");
            txtEmail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError("Email not in correct format");
            txtEmail.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void onCustomerSelected(Customer customer) {
        selectedCustomerId = customer.getId();
        txtName.setText(customer.getName());
        txtAddress.setText(customer.getAddress());
        txtContact.setText(customer.getContact());
        txtEmail.setText(customer.getEmail());

        int cityPosition = ((ArrayAdapter<String>) spinnerCity.getAdapter()).getPosition(customer.getCity());
        spinnerCity.setSelection(cityPosition);
        txtPincode.setText(customer.getPincode());
    }
}
