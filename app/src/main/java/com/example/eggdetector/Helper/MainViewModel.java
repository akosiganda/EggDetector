package com.example.eggdetector.Helper;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainViewModel extends AndroidViewModel {
    public MutableLiveData<RealmResults<Transaction>> transaction = new MutableLiveData<>();
    public MutableLiveData<Integer> totalGood = new MutableLiveData<>();
    public MutableLiveData<Integer> totalCracked = new MutableLiveData<>();
    public MutableLiveData<Integer> totalDirty = new MutableLiveData<>();
    public MutableLiveData<Integer> totalBloodspot = new MutableLiveData<>();
    public MutableLiveData<Integer> totalNoBloodspot = new MutableLiveData<>();
    public MutableLiveData<Integer> totalAmount = new MutableLiveData<>();

    private Realm realm;
    private Calendar calendar;

    public MainViewModel(@NonNull Application application) {
        super(application);
        Realm.init(application);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(new MyRealmMigration())
                .build();

        Realm.setDefaultConfiguration(realmConfig);
        setupDatabase();
    }

    public void getTransaction(Calendar calendar) {
        this.calendar = calendar;
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int good = 0;
        int cracked = 0;
        int dirty = 0;
        int bloodspot = 0;
        int nobloodspot = 0;
        int total = 0;

        RealmResults<Transaction> newTransactions = null;
        if(Constants.SELECTED_TAB == Constants.DAILY) {

            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .findAll();

            good = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .equalTo("type", Constants.GOOD)
                    .sum("count")
                    .intValue();

            cracked = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .equalTo("type", Constants.CRACKED)
                    .sum("count")
                    .intValue();

            dirty = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .equalTo("type", Constants.DIRTY)
                    .sum("count")
                    .intValue();

            bloodspot = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .equalTo("type", Constants.BLOOD_SPOT)
                    .sum("count")
                    .intValue();

            nobloodspot = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .equalTo("type", Constants.NO_BLOOD_SPOT)
                    .sum("count")
                    .intValue();

            total = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", calendar.getTime())
                    .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                    .sum("count")
                    .intValue();

        }  else if(Constants.SELECTED_TAB == Constants.WEEKLY) {

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            Date startDate = calendar.getTime();

            calendar.add(Calendar.DAY_OF_WEEK, 6);
            Date endDate = calendar.getTime();

            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startDate)
                    .lessThan("date", endDate)
                    .findAll();

            good = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startDate)
                    .lessThan("date", endDate)
                    .equalTo("type", Constants.GOOD)
                    .sum("count")
                    .intValue();

            cracked = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startDate)
                    .lessThan("date", endDate)
                    .equalTo("type", Constants.CRACKED)
                    .sum("count")
                    .intValue();

            dirty = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startDate)
                    .lessThan("date", endDate)
                    .equalTo("type", Constants.DIRTY)
                    .sum("count")
                    .intValue();

            bloodspot = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startDate)
                    .lessThan("date", endDate)
                    .equalTo("type", Constants.BLOOD_SPOT)
                    .sum("count")
                    .intValue();

            nobloodspot = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startDate)
                    .lessThan("date", endDate)
                    .equalTo("type", Constants.NO_BLOOD_SPOT)
                    .sum("count")
                    .intValue();

            total = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startDate)
                    .lessThan("date", endDate)
                    .sum("count")
                    .intValue();

        } else if(Constants.SELECTED_TAB == Constants.MONTHLY) {

            calendar.set(Calendar.DAY_OF_MONTH,0);

            Date startTime = calendar.getTime();

            calendar.add(Calendar.MONTH,1);
            Date endTime = calendar.getTime();

            newTransactions = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .findAll();

            good = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .equalTo("type", Constants.GOOD)
                    .sum("count")
                    .intValue();

            cracked = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .equalTo("type", Constants.CRACKED)
                    .sum("count")
                    .intValue();

            dirty = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .equalTo("type", Constants.DIRTY)
                    .sum("count")
                    .intValue();

            bloodspot = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .equalTo("type", Constants.BLOOD_SPOT)
                    .sum("count")
                    .intValue();

            nobloodspot = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .equalTo("type", Constants.NO_BLOOD_SPOT)
                    .sum("count")
                    .intValue();

            total = realm.where(Transaction.class)
                    .greaterThanOrEqualTo("date", startTime)
                    .lessThan("date", endTime)
                    .sum("count")
                    .intValue();
        }

        totalGood.setValue((int) good);
        totalCracked.setValue((int) cracked);
        totalDirty.setValue((int) dirty);
        totalBloodspot.setValue((int) bloodspot);
        totalNoBloodspot.setValue((int) nobloodspot);
        totalAmount.setValue((int) total);
        transaction.setValue(newTransactions);
    }
    public void createTransaction(String type) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Transaction existingTransaction = realm.where(Transaction.class)
                .equalTo("type", type)
                .greaterThanOrEqualTo("date", calendar.getTime())
                .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                .findFirst();

        if (existingTransaction == null) {
            Transaction newTransaction = new Transaction();
            newTransaction.setDate(new Date());
            newTransaction.setType(type);
            newTransaction.setCount(1); // Increment the count for each type
            newTransaction.setDate(calendar.getTime());
            newTransaction.setId(calendar.getTime().getTime());
            addTransaction(newTransaction);
        } else {
            realm.beginTransaction();
            existingTransaction.setCount(existingTransaction.getCount() + 1); // Increment the count
            realm.commitTransaction();
        }

        // Fetch the transactions again
        getTransaction(calendar);
    }

    public void addTransaction(Transaction transaction) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(transaction);
        realm.commitTransaction();
    }

    public void deleteTransaction(Transaction transaction) {
        realm.beginTransaction();
        transaction.deleteFromRealm();
        realm.commitTransaction();
        getTransaction(calendar);
    }
    private void setupDatabase() {
        realm = Realm.getDefaultInstance();
    }
}
