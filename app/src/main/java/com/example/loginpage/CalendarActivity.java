package com.example.loginpage;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private List<TimeSlot> timeSlots = new ArrayList<>(); //  In real app, fetch from DB

    private WebView calendarWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarWebView = findViewById(R.id.calendarWebView);
        WebSettings webSettings = calendarWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true); //  <-- Add this line
        webSettings.setAllowContentAccess(true);
        calendarWebView.loadUrl("file:///android_asset/calendar.html");
    }

    // Sample Data Initialization
    private void initializeSampleData() {
        try {
            // Create sample TimeSlot objects
            TimeSlot slot1 = new TimeSlot(1, dateFormat.parse("2024-08-05T09:00:00"), dateFormat.parse("2024-08-05T10:00:00"), "Math", "Grade 1");
            TimeSlot slot2 = new TimeSlot(2, dateFormat.parse("2024-08-05T10:30:00"), dateFormat.parse("2024-08-05T11:30:00"), "Science", "Grade 1");
            TimeSlot slot3 = new TimeSlot(3, dateFormat.parse("2024-08-06T13:00:00"), dateFormat.parse("2024-08-06T14:00:00"), "History", "Grade 2");
            TimeSlot slot4 = new TimeSlot(4, dateFormat.parse("2024-08-07T11:00:00"), dateFormat.parse("2024-08-07T12:00:00"), "English", "Grade 3");
            TimeSlot slot5 = new TimeSlot(5, dateFormat.parse("2024-08-07T14:00:00"), dateFormat.parse("2024-08-07T15:00:00"), "Math", "Grade 3");

            // Add the TimeSlot objects to the list
            timeSlots.add(slot1);
            timeSlots.add(slot2);
            timeSlots.add(slot3);
            timeSlots.add(slot4);
            timeSlots.add(slot5);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is called from the JavaScript in calendar.html
    @JavascriptInterface
    public void showEventDialog(String start, String end, String eventId) {
        //  In a real app, you'd use a DialogFragment or a custom dialog here.
        //  For this example, we'll just show a toast.
        final String message;
        if (eventId == null || eventId.isEmpty()) {
            message = "Add Event: Start: " + start + ", End: " + end;
            //  You would launch a dialog here to get event details
            //  and then call addEvent()
            //  For now, add a dummy event and refresh
            try {
                //  Create a dummy TimeSlot
                TimeSlot newSlot = new TimeSlot(timeSlots.size() + 1, dateFormat.parse(start), dateFormat.parse(end), "New Subject", "New Grade");
                timeSlots.add(newSlot);
                loadEvents(); //  Reload events after adding
            } catch(Exception e) {
                e.printStackTrace();
            }

        } else {
            message = "Edit Event: ID: " + eventId + ", Start: " + start + ", End: " + end;
            //  Launch dialog to edit.
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CalendarActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Call this method from Android to load events into the calendar
    private void loadEvents() {
        JSONArray eventsJson = new JSONArray();
        try {
            for (TimeSlot slot : timeSlots) {
                JSONObject eventJson = new JSONObject();
                eventJson.put("id", slot.getId());
                eventJson.put("title", slot.getSubject() + " (" + slot.getGrade() + ")");
                eventJson.put("startTime", dateFormat.format(slot.getStartTime()));
                eventJson.put("endTime", dateFormat.format(slot.getEndTime()));
                eventJson.put("subject", slot.getSubject());
                eventJson.put("grade", slot.getGrade());
                eventsJson.put(eventJson);
            }
            // Call the JavaScript function in the WebView to add the events
            final String js = "javascript:addEventsToCalendar('" + eventsJson.toString() + "');";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    calendarWebView.evaluateJavascript(js, null);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Example of adding an event from Android (you'd call this after getting data from a dialog)
    public void addEvent(TimeSlot newSlot) {
        timeSlots.add(newSlot);
        loadEvents(); // Reload
    }

    public void updateEvent(int id, TimeSlot updatedSlot) {
        for (int i = 0; i < timeSlots.size(); i++) {
            TimeSlot slot = timeSlots.get(i);
            if (slot.getId() == id) {
                updatedSlot.setId(id);
                timeSlots.set(i, updatedSlot);
                loadEvents();
                return;
            }
        }
    }

    public void deleteEvent(int id) {
        timeSlots.removeIf(slot -> slot.getId() == id);
        loadEvents();
    }

    //  Call this from onCreate or whenever your data is ready
    @Override
    protected void onResume() {
        super.onResume();
        loadEvents(); // Load the events into the calendar when the activity is resumed.
    }

    // Inner class for TimeSlot
    private static class TimeSlot {
        private int id;
        private java.util.Date startTime;
        private java.util.Date endTime;
        private String subject;
        private String grade;

        public TimeSlot(int id, java.util.Date startTime, java.util.Date endTime, String subject, String grade) {
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

        public java.util.Date getStartTime() {
            return startTime;
        }

        public void setStartTime(java.util.Date startTime) {
            this.startTime = startTime;
        }

        public java.util.Date getEndTime() {
            return endTime;
        }

        public void setEndTime(java.util.Date endTime) {
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
