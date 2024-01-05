package com.example.eggdetector;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eggdetector.adapters.AddedReportAdapter;
import com.example.eggdetector.models.AddedReportModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private String[] eggQualityTypes = {"Good", "Crack", "Dirty", "Blood Spot", "Deformed"};
    private Button reportBtn;
    private RecyclerView reportLists;
    private AutoCompleteTextView eggQualityATV;
    private ImageButton addReportEgg;
    private ArrayList<AddedReportModel> reports;
    String eggQualitySelected = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reportLists = view.findViewById(R.id.reportLists);
        eggQualityATV = view.findViewById(R.id.eggQuality);

        reportBtn = view.findViewById(R.id.reportBtn);
        addReportEgg = view.findViewById(R.id.addReportEgg);

        loadDataFromSharedPref();

        ArrayAdapter<String> eggQualityArray = new ArrayAdapter<>(view.getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, eggQualityTypes);
        eggQualityATV.setAdapter(eggQualityArray);

        eggQualityATV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                eggQualitySelected = parent.getItemAtPosition(position).toString();
            }
        });

        reportLists.setLayoutManager(new LinearLayoutManager(getContext()));
        AddedReportAdapter adapter = new AddedReportAdapter(getContext(), reports);
        reportLists.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        addReportEgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eggQualitySelected == null) {
                    Toast.makeText(view.getContext(), "Please select an egg quality", Toast.LENGTH_SHORT).show();
                } else {
                    if (reports.stream().filter(obj -> obj.getEggQuality().equals(eggQualitySelected)).findFirst().isPresent()) {
                        Toast.makeText(view.getContext(), "Egg Quality is already on the list", Toast.LENGTH_SHORT).show();
                    } else {
                        reports.add(new AddedReportModel(eggQualitySelected, 1));
                        adapter.notifyDataSetChanged();
                        eggQualitySelected = null;
                        saveToSharedPref();
                    }
                }
            }
        });

        adapter.setOnItemClickListener(new AddedReportAdapter.OnItemClickListener() {
            @Override
            public void increaseCount(int position) {
                reports.get(position).increaseCount();
                adapter.notifyItemChanged(position);
                saveToSharedPref();
            }

            @Override
            public void decreaseCount(int position) {
                if (reports.get(position).getCount() == 1) {
                    reports.remove(position);
                    adapter.notifyItemRemoved(position);
                    saveToSharedPref();
                } else {
                    reports.get(position).decreaseCount();
                    adapter.notifyItemChanged(position);
                    saveToSharedPref();
                }

            }

            @Override
            public void removeItem(int position) {
                reports.remove(position);
                adapter.notifyItemRemoved(position);
                saveToSharedPref();
            }
        });
        
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(), "Reports saved", Toast.LENGTH_SHORT).show();
                clearDataFromSharedPref();
                dismiss();
            }
        });
    }
    private void saveToSharedPref() {
        SharedPreferences preferences = getContext().getSharedPreferences("shared pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(reports);
        editor.putString("items", json);
        editor.apply();
    }
    private void loadDataFromSharedPref() {
        SharedPreferences preferences = getContext().getSharedPreferences("shared pref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("items", null);
        Type type = new TypeToken<ArrayList<AddedReportModel>>() {}.getType();
        reports = gson.fromJson(json, type);

        if (reports == null || reports.isEmpty()) {
            reports = new ArrayList<>();
        }
    }
    private void clearDataFromSharedPref() {
        SharedPreferences preferences = getContext().getSharedPreferences("shared pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("items");
        editor.apply();
    }
}
