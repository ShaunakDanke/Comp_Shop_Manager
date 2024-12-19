package com.example.comp_shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CustomerAdapterReceipt extends RecyclerView.Adapter<CustomerAdapterReceipt.CustomerViewHolder> {

    private Context context;
    private List<Customer> customerList;

    public CustomerAdapterReceipt(Context context, List<Customer> customerList) {
        this.context = context;
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.report_customer_item, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);

        holder.txtCustName.setText(customer.getName());
        holder.txtCustAddress.setText(customer.getAddress());
        holder.txtCustCity.setText(customer.getCity());
        holder.txtCustPincode.setText(customer.getPincode());
        holder.txtCustContact.setText(customer.getContact());
        holder.txtCustEmail.setText(customer.getEmail());
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView txtCustName, txtCustAddress, txtCustCity, txtCustPincode, txtCustContact, txtCustEmail;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCustName = itemView.findViewById(R.id.txtCustName);
            txtCustAddress = itemView.findViewById(R.id.txtCustAddress);
            txtCustCity = itemView.findViewById(R.id.txtCustCity);
            txtCustPincode = itemView.findViewById(R.id.txtCustPincode);
            txtCustContact = itemView.findViewById(R.id.txtCustContact);
            txtCustEmail = itemView.findViewById(R.id.txtCustEmail);
        }
    }
}
