package com.example.mia_hometest.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());

    public static Date dateToString (String date) {
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Comparator<ListItem> orderDate = (item1, item2) -> {
        Date date = dateToString(item1.getDate());
        Date date2 = dateToString(item2.getDate());

        if (date != null && date2 != null) {
            return date2.compareTo(date);
        } else {
            return 0;
        }
    };
}
