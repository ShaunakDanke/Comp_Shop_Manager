package com.example.comp_shop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAlertAdapter extends RecyclerView.Adapter<ProductAlertAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;  // Declare context to use for starting the activity

    public ProductAlertAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;  // Initialize context
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.getName());
        holder.category.setText("Category: " + product.getCategory());
        holder.quantity.setText("Quantity: " + product.getQuantity());
        holder.rate.setText("Rate: Rs" + product.getRate());

        holder.btnUpdate.setOnClickListener(v -> {
            // Intent to navigate to ProductRegistration activity
            Intent intent = new Intent(context, ProductRegistrationActivity.class);
            context.startActivity(intent);

            // Close the current Notifications activity
            if (context instanceof Activity) {
                ((Activity) context).finish();  // This will close the current activity
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView name, category, quantity, rate;
        Button btnUpdate;  // Declare the button

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.productName);
            category = itemView.findViewById(R.id.productCategory);
            quantity = itemView.findViewById(R.id.productQuantity);
            rate = itemView.findViewById(R.id.productRate);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);  // Initialize the button
        }
    }
}
