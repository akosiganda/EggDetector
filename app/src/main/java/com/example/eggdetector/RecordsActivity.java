package com.example.eggdetector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eggdetector.Helper.Constants;
import com.example.eggdetector.Helper.Helper;
import com.example.eggdetector.Helper.MainViewModel;
import com.example.eggdetector.Helper.Transaction;
import com.example.eggdetector.Helper.TransactionAdapter;
import com.example.eggdetector.databinding.ActivityRecordsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.Objects;

import io.realm.RealmResults;

public class RecordsActivity extends AppCompatActivity {

    ActivityRecordsBinding binding;
    Calendar calendar;
    int selectedDayOfWeek = Calendar.SUNDAY; // Initialize with Sunday or any default day
    public MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecordsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Records");

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        calendar = Calendar.getInstance();
        updateDate();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomMenu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_records);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.bottom_records) {
                return true;
            } else if (itemId == R.id.bottom_scanner) {
                startActivity(new Intent(getApplicationContext(), ScannerActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.bottom_info) {
                startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        binding.nextDateBtn.setOnClickListener(c -> {
            if (Constants.SELECTED_TAB == Constants.DAILY) {
                calendar.add(Calendar.DATE, 1);
            } else if (Constants.SELECTED_TAB == Constants.MONTHLY) {
                calendar.add(Calendar.MONTH, 1);
            } else if (Constants.SELECTED_TAB == Constants.WEEKLY) {
                calendar.add(Calendar.DAY_OF_WEEK, 1);
            }
            updateDate();
            viewModel.getTransaction(calendar);
        });

        binding.previousDateBtn.setOnClickListener(c -> {
            if (Constants.SELECTED_TAB == Constants.DAILY) {
                calendar.add(Calendar.DATE, -1);
            } else if (Constants.SELECTED_TAB == Constants.MONTHLY) {
                calendar.add(Calendar.MONTH, -1);
            } else if (Constants.SELECTED_TAB == Constants.WEEKLY) {
                calendar.add(Calendar.DAY_OF_WEEK, -1);
            }
            updateDate();
            viewModel.getTransaction(calendar);
        });

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                CharSequence text = tab.getText();
                if (text != null) {
                    if (text.equals("Monthly")) {
                        Constants.SELECTED_TAB = 2;
                        updateDate();
                    } else if (text.equals("Daily")) {
                        Constants.SELECTED_TAB = 0;
                        updateDate();
                    } else if (text.equals("Weekly")) {
                        Constants.SELECTED_TAB = 1;
                        updateDate();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel.transaction.observe(this, new Observer<RealmResults<Transaction>>() {
            @Override
            public void onChanged(RealmResults<Transaction> transactions) {
                TransactionAdapter transactionsAdapter = new TransactionAdapter(RecordsActivity.this, transactions);
                binding.recyclerView.setAdapter(transactionsAdapter);
                if (transactions.size() > 0) {
                    binding.emptyState.setVisibility(View.GONE);
                } else {
                    binding.emptyState.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.getTransaction(calendar);
    }

    void updateDate() {
        if(Constants.SELECTED_TAB == Constants.DAILY) {
            binding.currentDate.setText(Helper.formatDate(calendar.getTime()));
        } else if(Constants.SELECTED_TAB == Constants.MONTHLY) {
            binding.currentDate.setText(Helper.formatDateByMonth(calendar.getTime()));
        }else if(Constants.SELECTED_TAB == Constants.WEEKLY) {
            binding.currentDate.setText(Helper.formatDateWeekly(calendar.getTime()));
        }
        viewModel.getTransaction(calendar);
    }
}
