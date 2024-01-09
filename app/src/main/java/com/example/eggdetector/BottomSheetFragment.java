package com.example.eggdetector;

import android.content.Context;
import android.content.Intent;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eggdetector.Helper.Constants;
import com.example.eggdetector.Helper.MainViewModel;
import com.example.eggdetector.Helper.Transaction;
import com.example.eggdetector.adapters.AddedReportAdapter;
import com.example.eggdetector.models.AddedReportModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private String[] eggQualityTypes = {"Good", "Crack", "Dirty", "Blood Spot", "Deformed"};
    private Button reportBtn;
    private RecyclerView reportLists;
    private AutoCompleteTextView eggQualityATV;
    private ImageButton addReportEgg;
    private ArrayList<AddedReportModel> reports;
    String eggQualitySelected = null;
    MainViewModel viewModel;
    Transaction transaction;


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
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        transaction = new Transaction();

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
                Calendar calendar = Calendar.getInstance();

                // Retrieve data from the AddedReportAdapter
                AddedReportAdapter adapter = (AddedReportAdapter) reportLists.getAdapter();
                ArrayList<AddedReportModel> reportModels = adapter.getReports();

                // Create a map to store counts for each egg quality type
                Map<String, Integer> countMap = new HashMap<>();
                for (AddedReportModel model : reportModels) {
                    countMap.put(model.getEggQuality(), model.getCount());
                }

                // Retrieve data from the reportLists
                ArrayList<Transaction> finalReport = new ArrayList<>();

                // Iterate through all egg quality types
                long baseId = calendar.getTime().getTime();
                for (String eggQualityType : eggQualityTypes) {
                    Integer count = countMap.get(eggQualityType);
                    if (count != null && count > 0) {
                        Transaction transaction = new Transaction();

                        // Map egg quality to transaction type
                        switch (eggQualityType) {
                            case "Good":
                                transaction.setType(Constants.GOOD);
                                break;
                            case "Crack":
                                transaction.setType(Constants.CRACKED);
                                break;
                            case "Dirty":
                                transaction.setType(Constants.DIRTY);
                                break;
                            case "Blood Spot":
                                transaction.setType(Constants.BLOOD_SPOT);
                                break;
                            case "Deformed":
                                transaction.setType(Constants.DEFORMED);
                                break;
                        }

                        transaction.setDate(new Date());
                        transaction.setCount(count);
                        transaction.setId(baseId++);
                        finalReport.add(transaction);
                    }
                }

                // Save the final report to SharedPreferences
                saveToSharedPref(finalReport);

                // Save the final report to Realm
                saveToRealm(finalReport);
                Toast.makeText(view.getContext(), "Reports saved", Toast.LENGTH_SHORT).show();
                clearDataFromSharedPref();
                dismiss();

                // Assuming you want to add the transactions to the ViewModel and open RecordsActivity
                viewModel.addTransactionsFromReportList(finalReport);
                Intent intent = new Intent(getContext(), RecordsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveToSharedPref() {
        SharedPreferences preferences = getContext().getSharedPreferences("shared pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();

        // Convert the entire reports list to JSON
        String json = gson.toJson(reports);

        editor.putString("addedReports", json);
        editor.apply();
    }

    private void saveToSharedPref(ArrayList<Transaction> finalReport) {
        SharedPreferences preferences = getContext().getSharedPreferences("shared pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();

        // Convert the final report list to JSON
        String json = gson.toJson(finalReport);

        editor.putString("finalReport", json);
        editor.apply();
    }

    private void loadDataFromSharedPref() {
        SharedPreferences preferences = getContext().getSharedPreferences("shared pref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("addedReports", null);
        Type type = new TypeToken<ArrayList<AddedReportModel>>() {
        }.getType();
        reports = gson.fromJson(json, type);

        if (reports == null) {
            reports = new ArrayList<>();
        }
    }

    private void saveToRealm(ArrayList<Transaction> finalReport) {
        viewModel.addTransactionsFromReportList(finalReport);
    }
    private void clearDataFromSharedPref() {
        SharedPreferences preferences = getContext().getSharedPreferences("shared pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("addedReports");
        editor.apply();
    }
}
