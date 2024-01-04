package com.example.eggdetector;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.eggdetector.Helper.Constants;
import com.example.eggdetector.Helper.Helper;
import com.example.eggdetector.Helper.MainViewModel;
import com.example.eggdetector.databinding.ActivityDashboardBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {

    ActivityDashboardBinding binding;

    MainViewModel viewModel;

    Calendar calendar;
    private BarChart barChart;
    private List<String> xAxisValues = Arrays.asList("Good", "Crack", "Dirty", "No Blood spot", "Blood Spot");
    private ArrayList <BarEntry> entries = new ArrayList<>();
    private float[] values = new float[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("EQMS");

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        barChart = findViewById(binding.barChart.getId());

        calendar = Calendar.getInstance();
        updateDate();

        ProgressBar goodProgressBar = findViewById(R.id.goodProgress);
        ProgressBar dirtyProgressBar = findViewById(R.id.dirtyProgress);
        ProgressBar crackProgressBar = findViewById(R.id.crackProgress);
        ProgressBar noBSProgressBar = findViewById(R.id.noBSProgress);
        ProgressBar bloodSpotProgressBar = findViewById(R.id.bloodSpotProgress);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomMenu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                return true;
            } else if (itemId == R.id.bottom_records) {
                startActivity(new Intent(getApplicationContext(), RecordsActivity.class));
                overridePendingTransition(0, 0);
                finish();
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

        binding.dashboardTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

        viewModel.totalGood.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer intVal) {
                values[0] = intVal;
                binding.totalGood.setText(String.valueOf(intVal));
                binding.totalGoodlbl.setText(String.valueOf(intVal));
                updateProgressBar(goodProgressBar, intVal);
            }

        });
        viewModel.totalCracked.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer intVal) {
                values[1] = intVal;
                binding.totalCracked.setText(String.valueOf(intVal));
                binding.totalCrackedlbl.setText(String.valueOf(intVal));
                updateProgressBar(crackProgressBar, intVal);
            }
        });
        viewModel.totalDirty.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer intVal) {
                values[2] = intVal;
                binding.totalDirty.setText(String.valueOf(intVal));
                binding.totalDirtylbl.setText(String.valueOf(intVal));
                updateProgressBar(dirtyProgressBar, intVal);
            }
        });
        viewModel.totalBloodspot.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer intVal) {
                values[3] = intVal;
                binding.totalBloodSpot.setText(String.valueOf(intVal));
                binding.totalBloodSpotlbl.setText(String.valueOf(intVal));
                updateProgressBar(bloodSpotProgressBar, intVal);
            }
        });

        viewModel.totalNoBloodspot.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer intVal) {
                values[4] = intVal;
                binding.totalNoBloodSpot.setText(String.valueOf(intVal));
                binding.totalNoBSlbl.setText(String.valueOf(intVal));
                updateProgressBar(noBSProgressBar, intVal);
            }
        });

        viewModel.totalAmount.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer intVal) {
                binding.totalLbl.setText(String.valueOf(intVal));
                binding.totalEgg.setText(String.valueOf(intVal));
            }
        });

        viewModel.getTransaction(calendar);

        // Bar Chart
        // change code the below if the database has values like this showBarChart(values[0], values[1], values[2], values[3], values[4])
        showBarChart(55f, 25f, 35f, 45f, 15f);
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

    private void updateProgressBar(ProgressBar progressBar, int count) {
        progressBar.setMax(150); // Set an appropriate max value
        progressBar.setProgress((count * 100) / progressBar.getMax()); // Calculate the progress
    }
    private void showBarChart(float good, float crack, float dirty, float noBloodSpot, float bloodSpot) {
        entries.add(new BarEntry(0, good));
        entries.add(new BarEntry(1, crack));
        entries.add(new BarEntry(2, dirty));
        entries.add(new BarEntry(3, noBloodSpot));
        entries.add(new BarEntry(4, bloodSpot));

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLUE);
        yAxis.setLabelCount(10);

        BarDataSet dataSet = new BarDataSet(entries, "Eggs");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData data = new BarData(dataSet);
        barChart.setData(data);

        barChart.getDescription().setEnabled(false);
        barChart.invalidate();

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.dashboard_logout) {

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            overridePendingTransition(0, 0);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}