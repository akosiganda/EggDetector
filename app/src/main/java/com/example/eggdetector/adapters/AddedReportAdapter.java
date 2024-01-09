package com.example.eggdetector.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eggdetector.R;
import com.example.eggdetector.models.AddedReportModel;

import java.util.ArrayList;

public class AddedReportAdapter extends RecyclerView.Adapter<AddedReportAdapter.AddedReportViewHolder> {
    Context context;
    private ArrayList<AddedReportModel> addedReportModels;
    private OnItemClickListener listener;

    public ArrayList<AddedReportModel> getReports() {
        return addedReportModels;
    }

    public interface OnItemClickListener {
        void increaseCount(int position);
        void decreaseCount(int position);
        void removeItem(int position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        listener = clickListener;
    }

    public AddedReportAdapter(Context context, ArrayList<AddedReportModel> addedReportModels) {
        this.context = context;
        this.addedReportModels = addedReportModels;
    }

    @NonNull
    @Override
    public AddedReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_report_layout, parent, false);
        return new AddedReportViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AddedReportViewHolder holder, int position) {
        holder.reportAddedEggQuality.setText(addedReportModels.get(position).getEggQuality());
        holder.addReportCount.setText(String.valueOf(addedReportModels.get(position).getCount()));
    }

    @Override
    public int getItemCount() {
        return addedReportModels.size();
    }

    class AddedReportViewHolder extends RecyclerView.ViewHolder {
        private TextView reportAddedEggQuality, addReportCount;
        private ImageButton removeAddedEggQualityBtn;
        private ImageView minusBtn, plusBtn;
        public AddedReportViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            reportAddedEggQuality = itemView.findViewById(R.id.reportAddedEggQuality);
            addReportCount = itemView.findViewById(R.id.addReportCount);
            removeAddedEggQualityBtn = itemView.findViewById(R.id.removeAddedEggQualityBtn);
            minusBtn = itemView.findViewById(R.id.minusBtn);
            plusBtn = itemView.findViewById(R.id.plusBtn);

            plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.increaseCount(getAdapterPosition());
                }
            });

            minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.decreaseCount(getAdapterPosition());
                }
            });

            removeAddedEggQualityBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.removeItem(getAdapterPosition());
                }
            });
        }
    }
}
