package com.example.comp_shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp_shop.R;

import java.util.List;

public class ReceiptProductAdapter extends RecyclerView.Adapter<ReceiptProductAdapter.ReceiptProductViewHolder> {

    private List<ReceiptProduct> receiptProductList;
    private Context context;

    public ReceiptProductAdapter(List<ReceiptProduct> receiptProductList, Context context) {
        this.receiptProductList = receiptProductList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReceiptProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receipt_products, parent, false);
        return new ReceiptProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptProductViewHolder holder, int position) {
        ReceiptProduct product = receiptProductList.get(position);

        holder.txtProductName.setText(product.getProductName());
        holder.txtProductId.setText(product.getProductId());
        holder.txtProductCategory.setText(product.getProductCategory());
        holder.txtProductQuantity.setText(String.valueOf(product.getQuantity()));
        holder.txtProductPrice.setText(String.valueOf(product.getPrice()));
        holder.txtTotalPrice.setText(String.valueOf(product.getTotalAmount()));
    }

    @Override
    public int getItemCount() {
        return receiptProductList.size();
    }

    public static class ReceiptProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtProductId, txtProductCategory, txtProductQuantity, txtProductPrice, txtTotalPrice;

        public ReceiptProductViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductId = itemView.findViewById(R.id.txtProductId);
            txtProductCategory = itemView.findViewById(R.id.txtProductCategory);
            txtProductQuantity = itemView.findViewById(R.id.txtProductQuantity);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtTotalPrice = itemView.findViewById(R.id.txtTotalPrice);
        }
    }
}
