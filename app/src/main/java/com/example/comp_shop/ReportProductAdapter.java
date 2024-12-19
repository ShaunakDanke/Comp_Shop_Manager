package com.example.comp_shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReportProductAdapter extends RecyclerView.Adapter<ReportProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    // Constructor to initialize the context and product list
    public ReportProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.report_product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Get the current product from the list
        Product product = productList.get(position);

        // Bind the data to the ViewHolder's views
        holder.txtProdCategory.setText(product.getCategory());
        holder.txtProdName.setText(product.getName());
        holder.txtProdQuantity.setText(product.getQuantity());
        holder.txtProdPrice.setText(product.getRate());
    }

    @Override
    public int getItemCount() {
        return productList.size(); // Return the total number of products
    }

    // Static inner class to hold the views for each item
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtProdCategory, txtProdName, txtProdQuantity, txtProdPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find and assign views
            txtProdCategory = itemView.findViewById(R.id.txtProdCatageory);
            txtProdName = itemView.findViewById(R.id.txtProdName);
            txtProdQuantity = itemView.findViewById(R.id.txtProdQuantity);
            txtProdPrice = itemView.findViewById(R.id.txtProdPrice);
        }
    }

    // Method to update the product list (useful for filtering)
    public void updateProductList(List<Product> newProductList) {
        productList.clear();
        productList.addAll(newProductList);
        notifyDataSetChanged();  // Notify the adapter to refresh the view
    }
}
