package com.example.comp_shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> customerList;
    private OnCustomerSelectedListener listener;

    public interface OnCustomerSelectedListener {
        void onCustomerSelected(Customer customer);
    }

    public CustomerAdapter(List<Customer> customerList, OnCustomerSelectedListener listener) {
        this.customerList = customerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item, parent, false);
        return new CustomerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.customerName.setText(customer.getName());
        holder.customerAddress.setText(customer.getAddress());
        holder.customerCity.setText(customer.getCity());
        holder.customerPincode.setText(customer.getPincode());
        holder.customerContact.setText(customer.getContact());
        holder.customerEmail.setText(customer.getEmail());
        holder.itemView.setOnClickListener(v -> listener.onCustomerSelected(customer));
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        public TextView customerName;
        public TextView customerAddress;
        public TextView customerCity;
        public TextView customerPincode;
        public TextView customerContact;
        public TextView customerEmail;


        public CustomerViewHolder(View view) {
            super(view);
            customerName = view.findViewById(R.id.txtname);
            customerAddress=view.findViewById(R.id.txtAddress);
            customerCity=view.findViewById(R.id.txtCity);
            customerPincode=view.findViewById(R.id.txtPincode);
            customerContact=view.findViewById(R.id.txtContact);
            customerEmail=view.findViewById(R.id.txtEmail);
        }
    }
}
