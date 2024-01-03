package com.example.eggdetector.Helper;

import com.example.eggdetector.R;

import java.util.ArrayList;

public class Constants {

    public static int SELECTED_TAB = 0;
    public static int SELECTED_TAB_STATS = 0;

    public static int DAILY = 0;
    public static int WEEKLY = 1;
    public static int MONTHLY = 2;
    public static String GOOD = "GOOD";
    public static String CRACKED = "CRACKED";
    public static String UNKNOWN = "UNKNOWN";
    public static String DIRTY = "DIRTY";
    public static String BLOOD_SPOT = "BLOODSPOT";
    public static String NO_BLOOD_SPOT = "NO BLOODSPOT";

    public static final String[] CLASSES = {"Good Egg", "Crack Egg", "Dirty Egg", "Unknown"};

    public static ArrayList<EggType> categories;

    public static void setType() {
        categories = new ArrayList<>();
        categories.add(new EggType("Good", R.drawable.icon_good, R.color.white));
        categories.add(new EggType("Crack", R.drawable.icon_cracked, R.color.white));
        categories.add(new EggType("Dirty", R.drawable.icon_dirty, R.color.white));
        categories.add(new EggType("BloodSpot", R.drawable.icon_blood_spot, R.color.white));
    }

    public static EggType getTypeCategory(String categoryName) {
        for (EggType cat :
                categories) {
            if (cat.getCategoryName().equals(categoryName)) {
                return cat;
            }
        }
        return null;
    }

}
