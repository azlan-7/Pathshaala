package com.example.loginpage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loginpage.MySqliteDatabase.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private WebView calendarWebView;
    private int currentTeacherId; // To store the logged-in teacher's ID
    private final String HARDCODED_YEAR = "2025";
    private final int HARDCODED_MONTH = Calendar.MAY; // Month is 0-indexed (0 for January, 4 for May)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Log.d("CalendarActivity", "onCreate() called");

        calendarWebView = findViewById(R.id.calendarWebView);
        WebSettings webSettings = calendarWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        calendarWebView.addJavascriptInterface(this, "Android");
        calendarWebView.loadUrl("file:///android_asset/calendar.html");
        Log.d("CalendarActivity", "WebView loaded calendar.html");

        // Get the logged-in teacher's ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentTeacherId = sharedPreferences.getInt("USER_ID", -1);
        Log.d("CalendarActivity", "onCreate() - Retrieved Teacher ID: " + currentTeacherId);

        // Load events when the activity is created
        loadTeacherTimetable();
    }
    private void loadTeacherTimetable() {
        Log.d("CalendarActivity", "loadTeacherTimetable() called for Teacher ID: " + currentTeacherId);
        if (currentTeacherId != -1) {
            DatabaseHelper.getTimeTableByUserId(currentTeacherId, 0,0,new DatabaseHelper.ProcedureResultCallback<List<DatabaseHelper.TimeTableEntry>>() {
                @Override
                public void onSuccess(List<DatabaseHelper.TimeTableEntry> timeTableEntries) {
                    Log.d("CalendarActivity", "loadTeacherTimetable() - onSuccess: Retrieved " + timeTableEntries.size() + " timetable entries.");
                    JSONArray eventsJson = formatTimetableToCalendarEvents(timeTableEntries);
                    final String js = "javascript:addEventsToCalendar('" + eventsJson.toString() + "');";
                    runOnUiThread(() -> {
                        calendarWebView.evaluateJavascript(js, null);
                        Log.d("CalendarActivity", "loadTeacherTimetable() - onSuccess: Evaluated JavaScript: " + js);
                    });
                }

                @Override
                public void onError(String error) {
                    Log.e("CalendarActivity", "loadTeacherTimetable() - onError: Error fetching teacher timetable: " + error);
                    runOnUiThread(() -> Toast.makeText(CalendarActivity.this, "Error loading timetable", Toast.LENGTH_SHORT).show());
                }
            });
        } else {
            Log.e("CalendarActivity", "loadTeacherTimetable() - Error: Teacher ID not available.");
            runOnUiThread(() -> Toast.makeText(CalendarActivity.this, "Teacher ID not available", Toast.LENGTH_SHORT).show());
        }
    }

    private JSONArray formatTimetableToCalendarEvents(List<DatabaseHelper.TimeTableEntry> entries) {
        Log.d("CalendarActivity", "formatTimetableToCalendarEvents() called with " + entries.size() + " entries.");
        JSONArray eventsArray = new JSONArray();
        SimpleDateFormat timeParser = new SimpleDateFormat("hh:mm a", Locale.getDefault()); // Corrected format
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Calendar calendar = Calendar.getInstance(Locale.getDefault());

        for (DatabaseHelper.TimeTableEntry entry : entries) {
            Log.d("CalendarActivity", "formatTimetableToCalendarEvents() - Processing entry: Subject=" + entry.subjectName + ", StartTime=" + entry.startTime + ", EndTime=" + entry.endTime);
            try {
                // Process each day of the week
                processDay(eventsArray, entry, "Monday", entry.mon, timeParser, outputTimeFormat, 1);
                processDay(eventsArray, entry, "Tuesday", entry.tue, timeParser, outputTimeFormat, 2);
                processDay(eventsArray, entry, "Wednesday", entry.wed, timeParser, outputTimeFormat, 3);
                processDay(eventsArray, entry, "Thursday", entry.thur, timeParser, outputTimeFormat, 4);
                processDay(eventsArray, entry, "Friday", entry.fri, timeParser, outputTimeFormat, 5);
                processDay(eventsArray, entry, "Saturday", entry.sat, timeParser, outputTimeFormat, 6);
                processDay(eventsArray, entry, "Sunday", entry.sun, timeParser, outputTimeFormat, 7);

            } catch (ParseException | JSONException e) {
                Log.e("CalendarActivity", "formatTimetableToCalendarEvents() - Error formatting timetable entry: " + entry.subjectName, e);
            }
        }
        Log.d("CalendarActivity", "formatTimetableToCalendarEvents() - Returning JSON array: " + eventsArray.toString());
        return eventsArray;
    }

    private void processDay(JSONArray eventsArray, DatabaseHelper.TimeTableEntry entry, String dayName, String dayFlag,
                            SimpleDateFormat timeParser, SimpleDateFormat outputTimeFormat, int dayOfWeek)
            throws ParseException, JSONException {
        if (dayFlag != null && (dayFlag.equalsIgnoreCase(dayName.substring(0, 3)) || dayFlag.equalsIgnoreCase(dayName))) {
            Log.d("CalendarActivity", "processDay() - Processing day: " + dayName + " for subject: " + entry.subjectName);
            Date startTime = timeParser.parse(entry.startTime);
            Date endTime = timeParser.parse(entry.endTime);
            Log.d("CalendarActivity", "processDay() - Parsed Start Time: " + startTime + ", End Time: " + endTime);

            Calendar startCal = Calendar.getInstance();
            startCal.set(Calendar.YEAR, Integer.parseInt(HARDCODED_YEAR));
            startCal.set(Calendar.MONTH, HARDCODED_MONTH);
            startCal.set(Calendar.DAY_OF_WEEK, getCalendarDay(dayOfWeek));
            startCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(outputTimeFormat.format(startTime).split(":")[0]));
            startCal.set(Calendar.MINUTE, Integer.parseInt(outputTimeFormat.format(startTime).split(":")[1]));
            startCal.set(Calendar.SECOND, 0);
            Log.d("CalendarActivity", "processDay() - Start Calendar Time: " + dateFormat.format(startCal.getTime()));

            Calendar endCal = Calendar.getInstance();
            endCal.set(Calendar.YEAR, Integer.parseInt(HARDCODED_YEAR));
            endCal.set(Calendar.MONTH, HARDCODED_MONTH);
            endCal.set(Calendar.DAY_OF_WEEK, getCalendarDay(dayOfWeek));
            endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(outputTimeFormat.format(endTime).split(":")[0]));
            endCal.set(Calendar.MINUTE, Integer.parseInt(outputTimeFormat.format(endTime).split(":")[1]));
            endCal.set(Calendar.SECOND, 0);
            Log.d("CalendarActivity", "processDay() - End Calendar Time: " + dateFormat.format(endCal.getTime()));

            JSONObject eventJson = new JSONObject();
            eventJson.put("id", "timetable_" + entry.timeTableId + "_" + dayOfWeek); // Unique ID
            eventJson.put("title", entry.subjectName + " (" + entry.gradeName + ")");
            eventJson.put("startTime", dateFormat.format(startCal.getTime()));
            eventJson.put("endTime", dateFormat.format(endCal.getTime()));
            eventJson.put("subject", entry.subjectName);
            eventJson.put("grade", entry.gradeName);
            eventsArray.put(eventJson);
            Log.d("CalendarActivity", "processDay() - Added event to JSON array: " + eventJson.toString());
        } else {
            Log.d("CalendarActivity", "processDay() - Day " + dayName + " is not active for subject: " + entry.subjectName);
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
            default: return Calendar.MONDAY;
        }
    }

    @JavascriptInterface
    public void showEventDialog(String start, String end, String eventId) {
        Log.d("CalendarActivity", "showEventDialog() from JS - Start: " + start + ", End: " + end + ", Event ID: " + eventId);
        final String message;
        if (eventId == null || eventId.startsWith("timetable_")) {
            // Prevent editing of timetable events from the calendar for now
            message = "Timetable events cannot be edited here.";
        } else {
            message = "Edit Event: ID: " + eventId + ", Start: " + start + ", End: " + end;
        }
        runOnUiThread(() -> Toast.makeText(CalendarActivity.this, message, Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("CalendarActivity", "onResume() called - Reloading teacher timetable.");
        loadTeacherTimetable(); // Reload timetable when the activity resumes
    }
}