package com.ericsson.fms.utils;

import java.text.SimpleDateFormat;
import java.util.*;

public class IndexCalculationUtil {

    private static SimpleDateFormat indexFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    public final static String INDEX_PREFIX = "gis-";

    public static String getCurrentIndex() {
        return getIndex(0);
    }

    public static String getLastIndex() {
        return getIndex(-1);
    }

    public static String getIndex(int offset) {
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, offset);
        return INDEX_PREFIX + indexFormat.format(calendar.getTime());
    }

    public static String getIndexFromTimestamp(String timestamp) {
        return INDEX_PREFIX + timestamp.substring(0, 10);
    }

    public static List<Date> getBetweenDates(Date begin, Date end) {
        List<Date> result = new ArrayList<Date>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(begin);
        while(begin.getTime()<=end.getTime()){
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
            begin = tempStart.getTime();
        }
        return result;
    }

}
