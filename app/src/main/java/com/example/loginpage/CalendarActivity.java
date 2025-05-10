package com.example.loginpage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private List<TimeSlot> timeSlots = new ArrayList<>(); // Not directly used now, using SharedPreferences

    private WebView calendarWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarWebView = findViewById(R.id.calendarWebView);
        WebSettings webSettings = calendarWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        calendarWebView.addJavascriptInterface(this, "Android"); // Make CalendarActivity methods accessible in JS
        calendarWebView.loadUrl("file:///android_asset/calendar.html");

        // No need to initialize sample data anymore
    }

    @JavascriptInterface
    public void showEventDialog(String start, String end, String eventId) {
        // For now, let's just toast the info. You'd build a proper UI for adding/editing.
        final String message;
        if (eventId == null || eventId.isEmpty()) {
            message = "Add Event: Start: " + start + ", End: " + end;
        } else {
            message = "Edit Event: ID: " + eventId + ", Start: " + start + ", End: " + end;
        }
        runOnUiThread(() -> Toast.makeText(CalendarActivity.this, message, Toast.LENGTH_LONG).show());
    }

    private void loadEvents() {
        JSONArray eventsJson = new JSONArray();
        SharedPreferences sp = getSharedPreferences("TimeTableData", MODE_PRIVATE);
        Map<String, ?> allEntries = sp.getAll();
        SimpleDateFormat timeParser = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        String hardcodedYear = "2025";
        int hardcodedMonth = Calendar.MAY;

        try {
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("subject_")) {
                    String[] parts = key.split("_");
                    if (parts.length == 4) {
                        try {
                            long timestamp = Long.parseLong(parts[1]);
                            int dayOfWeekInt = Integer.parseInt(parts[2]);
                            String startTimeStr = parts[3];
                            String grade = sp.getString("grade_" + timestamp + "_" + parts[2] + "_" + startTimeStr, "");
                            String subject = sp.getString(key, "");
                            String timeRange = sp.getString("time_" + timestamp + "_" + parts[2] + "_" + startTimeStr, "");
                            String savedYear = sp.getString("year_" + timestamp + "_" + parts[2] + "_" + startTimeStr, hardcodedYear);
                            int savedMonth = sp.getInt("month_" + timestamp + "_" + parts[2] + "_" + startTimeStr, hardcodedMonth);

                            if (timeRange != null && !timeRange.isEmpty()) {
                                String[] times = timeRange.split(" - ");
                                if (times.length == 2) {
                                    Date startTime = timeParser.parse(times[0]);
                                    Date endTime = timeParser.parse(times[1]);

                                    Calendar startCal = Calendar.getInstance();
                                    startCal.set(Calendar.YEAR, Integer.parseInt(savedYear));
                                    startCal.set(Calendar.MONTH, savedMonth);
                                    startCal.set(Calendar.DAY_OF_WEEK, getCalendarDay(dayOfWeekInt));
                                    startCal.set(Calendar.HOUR_OF_DAY, new SimpleDateFormat("HH", Locale.getDefault()).parse(times[0]).getHours());
                                    startCal.set(Calendar.MINUTE, new SimpleDateFormat("mm", Locale.getDefault()).parse(times[0]).getMinutes());
                                    startCal.set(Calendar.SECOND, 0);

                                    Calendar endCal = Calendar.getInstance();
                                    endCal.set(Calendar.YEAR, Integer.parseInt(savedYear));
                                    endCal.set(Calendar.MONTH, savedMonth);
                                    endCal.set(Calendar.DAY_OF_WEEK, getCalendarDay(dayOfWeekInt));
                                    endCal.set(Calendar.HOUR_OF_DAY, new SimpleDateFormat("HH", Locale.getDefault()).parse(times[1]).getHours());
                                    endCal.set(Calendar.MINUTE, new SimpleDateFormat("mm", Locale.getDefault()).parse(times[1]).getMinutes());
                                    endCal.set(Calendar.SECOND, 0);

                                    JSONObject eventJson = new JSONObject();
                                    eventJson.put("id", key);
                                    eventJson.put("title", subject + " (" + grade + ")");
                                    eventJson.put("startTime", dateFormat.format(startCal.getTime()));
                                    eventJson.put("endTime", dateFormat.format(endCal.getTime()));
                                    eventJson.put("subject", subject);
                                    eventJson.put("grade", grade);
                                    eventsJson.put(eventJson);
                                }
                            }
                        } catch (NumberFormatException | ParseException e) {
                            Log.e("CalendarActivity", "Error parsing SharedPreferences key: " + key, e);
                        }
                    }
                }
            }

            final String js = "javascript:addEventsToCalendar('" + eventsJson.toString() + "');";
            runOnUiThread(() -> calendarWebView.evaluateJavascript(js, null));

        } catch (JSONException e) {
            Log.e("CalendarActivity", "Error creating JSON for calendar events", e);
        }
    }

    private int getCalendarDay(int dayOfWeekInt) {
        switch (dayOfWeekInt) {
            case 1: return Calendar.MONDAY;
            case 2: return Calendar.TUESDAY;
            case 3: return Calendar.WEDNESDAY;
            case 4: return Calendar.THURSDAY;
            case 5: return Calendar.FRIDAY;
            case 6: return Calendar.SATURDAY;
            case 7: return Calendar.SUNDAY;
            default: return Calendar.MONDAY; // Default to Monday if the number is invalid
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    // The TimeSlot inner class remains the same
    private static class TimeSlot {
        private int id;
        private Date startTime;
        private Date endTime;
        private String subject;
        private String grade;

        public TimeSlot(int id, Date startTime, Date endTime, String subject, String grade) {
            this.id = id;
            this.startTime = startTime;
            this.endTime = endTime;
            this.subject = subject;
            this.grade = grade;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }
    }
}