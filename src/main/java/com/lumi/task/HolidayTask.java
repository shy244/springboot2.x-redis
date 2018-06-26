package com.lumi.task;

import com.alibaba.fastjson.JSONObject;
import com.lumi.utils.HttpClientUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolidayTask {
    public static String dateListString(List<Date> dateList) {
        if (dateList == null || dateList.isEmpty()) {
            throw new RuntimeException("dataList can't be null or empty");
        }
        StringBuilder dateListString = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        for (Date date : dateList) {
            dateListString.append(sdf.format(date));
            dateListString.append(",");
        }
        dateListString.deleteCharAt(dateListString.length() - 1);
        System.out.println(dateListString);
        return dateListString.toString();
    }

    private static List<Date> getDateList(int queryDays) {
        List<Date> dateList = new ArrayList<Date>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < queryDays; i++) {
            dateList.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        System.out.println(dateList);
        return dateList;
    }

    public static String getHolidayTimeStampList(String dateListString) throws Exception {
        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("d", dateListString);
        String response = HttpClientUtils.requestString(
                "http://tool.bitefu.net/jiari/", null, queryParameters, "get", false);
        System.out.println(response);
        return response;
    }

    public static void main(String[] args) throws Exception {
        List<Date> dateList = getDateList(100);
        String dateListString = dateListString(dateList);

        String result = getHolidayTimeStampList(dateListString);

        Map<String, Object> map = JSONObject.parseObject(result, Map.class);
        System.out.println(map);



        System.out.println(map.values().size());
        List<Long> timeStampList = new ArrayList<>();
//		List<String> values = new ArrayList<>(map.values());
        String[] dateArr = dateListString.split(",");
        for (int i = 0; i < dateArr.length; i++) {
            String day = String.valueOf(map.get(dateArr[i]));
            if ("1".equals(day) || "2".equals(day)) {
                timeStampList.add(dateList.get(i).getTime() / 1000);
            }
        }

        System.out.println(timeStampList);

    }

    private Map<String, Object> getHolidayList() {

        return null;
    }
}
