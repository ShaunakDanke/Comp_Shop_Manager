package com.example.comp_shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final OnProductSelectedListener listener;

    // Define the interface for product selection
    public interface OnProductSelectedListener {
        void onProductSelected(Product product);
    }

    // Constructor that initializes productList and listener
    public ProductAdapter(List<Product> productList, OnProductSelectedListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Method to update data in the adapter
    public void updateData(List<Product> newProductList) {
        productList.clear();
        productList.addAll(newProductList);
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView txtProductName, txtCategory, txtQuantity, txtRate;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtRate = itemView.findViewById(R.id.txtRate);
        }

        public void bind(Product product, OnProductSelectedListener listener) {
            txtProductName.setText(product.getName());
            txtCategory.setText(product.getCategory());
            txtQuantity.setText(product.getQuantity());
            txtRate.setText(product.getRate());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductSelected(product);
                }
            });
        }
    }
}
