package com.example.comp_shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SalesReportAdapter extends RecyclerView.Adapter<SalesReportAdapter.SalesReportViewHolder> {

    private List<SalesReport> salesReportList;

    // Constructor
    public SalesReportAdapter(List<SalesReport> salesReportList) {
        this.salesReportList = salesReportList;
    }

    @NonNull
    @Override
    public SalesReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_salesmaster_item, parent, false);
        return new SalesReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesReportViewHolder holder, int position) {
        SalesReport salesReport = salesReportList.get(position);

        holder.txtCustomerId.setText(salesReport.getCustomerId());
        holder.txtCustomerName.setText(salesReport.getCustomerName());
        holder.txtCustomerContact.setText(salesReport.getCustomerContact());
        holder.txtDateofPurchase.setText(salesReport.getDateOfPurchase());
        holder.txtGST.setText(salesReport.getGst());
        holder.txtFinalAmount.setText(salesReport.getFinalAmount());
    }

    @Override
    public int getItemCount() {
        return salesReportList.size();
    }

    public static class SalesReportViewHolder extends RecyclerView.ViewHolder {
        TextView txtCustomerId, txtCustomerName, txtCustomerContact, txtDateofPurchase, txtGST, txtFinalAmount;

        public SalesReportViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCustomerId = itemView.findViewById(R.id.txtCustomerId);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtCustomerContact = itemView.findViewById(R.id.txtCustomerContact);
            txtDateofPurchase = itemView.findViewById(R.id.txtDateofPurchase);
            txtGST = itemView.findViewById(R.id.txtGST);
            txtFinalAmount = itemView.findViewById(R.id.txtFinalAmount);
        }
    }
}
