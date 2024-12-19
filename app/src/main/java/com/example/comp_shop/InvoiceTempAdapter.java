package com.example.comp_shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class InvoiceTempAdapter extends RecyclerView.Adapter<InvoiceTempAdapter.ViewHolder> {

    private ArrayList<InvoiceTemp> invoiceTempList;
    private Context context;

    // Constructor
    public InvoiceTempAdapter(ArrayList<InvoiceTemp> invoiceTempList, Context context) {
        this.invoiceTempList = invoiceTempList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_invoice_temp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InvoiceTemp invoiceTemp = invoiceTempList.get(position);
        holder.productName.setText(invoiceTemp.getProductName());
        holder.productCatageory.setText(invoiceTemp.getproductCatageory());
        holder.quantity.setText(String.valueOf(invoiceTemp.getQuantity()));
        holder.price.setText(String.valueOf(invoiceTemp.getPrice()));
        holder.amount.setText(String.valueOf(invoiceTemp.getAmount()));
    }

    @Override
    public int getItemCount() {
        return invoiceTempList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName,productCatageory, quantity, price, amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productCatageory=itemView.findViewById(R.id.productCatageory);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
            amount = itemView.findViewById(R.id.amount);
        }
    }
}
