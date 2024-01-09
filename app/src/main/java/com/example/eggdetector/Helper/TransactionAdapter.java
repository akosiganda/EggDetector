package com.example.eggdetector.Helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eggdetector.RecordsActivity;
import com.example.eggdetector.R;
import com.example.eggdetector.databinding.RowTransactionBinding;

import io.realm.RealmResults;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    Context context;
    RealmResults<Transaction> transactions;

    public TransactionAdapter(Context context, RealmResults<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionViewHolder(LayoutInflater.from(context).inflate(R.layout.row_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.binding.transactionCategory.setText(transaction.getType());
        holder.binding.transactionDate.setText(Helper.formatDate(transaction.getDate()));
        holder.binding.transactionCount.setText(String.valueOf(transaction.getCount()));

        if (transaction.getType().equals(Constants.CRACKED)) {
            holder.binding.categoryIcon.setImageResource(R.drawable.crackegg);
            holder.binding.categoryIcon.setBackgroundTintList(context.getColorStateList(R.color.paleBlue));
            holder.binding.transactionCount.setTextColor(context.getColor(R.color.blue));
        } else if (transaction.getType().equals(Constants.GOOD)) {
            holder.binding.categoryIcon.setImageResource(R.drawable.goodegg);
            holder.binding.categoryIcon.setBackgroundTintList(context.getColorStateList(R.color.paleBlue));
            holder.binding.transactionCount.setTextColor(context.getColor(R.color.brightSapphireBlue));
        } else if (transaction.getType().equals(Constants.DIRTY)) {
            holder.binding.categoryIcon.setImageResource(R.drawable.dirtyegg);
            holder.binding.categoryIcon.setBackgroundTintList(context.getColorStateList(R.color.paleBlue));
            holder.binding.transactionCount.setTextColor(context.getColor(R.color.teal));
        } else if (transaction.getType().equals(Constants.BLOOD_SPOT)) {
            holder.binding.categoryIcon.setImageResource(R.drawable.bloodspot);
            holder.binding.categoryIcon.setBackgroundTintList(context.getColorStateList(R.color.paleBlue));
            holder.binding.transactionCount.setTextColor(context.getColor(R.color.ceruleanBlue));
        } else if (transaction.getType().equals(Constants.DEFORMED)) {
            holder.binding.categoryIcon.setImageResource(R.drawable.deformed);
            holder.binding.categoryIcon.setBackgroundTintList(context.getColorStateList(R.color.paleBlue));
            holder.binding.transactionCount.setTextColor(context.getColor(R.color.deep_aqua));
        } else {
            // Handle the else case if needed
        }


        holder.itemView.setOnLongClickListener(view -> {
            AlertDialog deleteDialog = new AlertDialog.Builder(context).create();
            deleteDialog.setTitle("Delete Transaction");
            deleteDialog.setMessage("Are you sure to delete this transaction?");
            deleteDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", (dialogInterface, i) -> ((RecordsActivity)context).viewModel.deleteTransaction(transaction));
            deleteDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", (dialogInterface, i) -> deleteDialog.dismiss());
            deleteDialog.show();
            return false;
        });

    }


    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder{

        RowTransactionBinding binding;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowTransactionBinding.bind(itemView);
        }
    }
}
