
package com.example.loginpage;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class OTPService {


    public static void sendOTP(String mobileNumber, final Context context) {


        if (mobileNumber == null || mobileNumber.isEmpty()) {
            Log.e("OTPService", "Mobile number is null or empty.");
            Toast.makeText(context, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("OTPService", "Sending OTP to: " + mobileNumber);

        // Generate OTP
        String otp = generateOTP();

        SharedPreferences sharedPreferences = context.getSharedPreferences("OTPDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("generatedOTP", otp);  // Save the OTP
        editor.apply();

        // Construct the message
        String message = "Your OTP is " + otp + " MagnusIT";


        Log.d("OTPService", "Phone Number: " + mobileNumber);
        Log.d("OTPService", "Generated OTP: " + otp);

        final String url = String.format("https://api2.nexgplatforms.com/sms/1/text/query?username=MAGNUSREST&password=Viram@2024&from=MGNSIT&to=%s&indiaDltContentTemplateId=1407161526643587607&indiaDltTelemarketerId=1202163221429156518&indiaDltPrincipalEntityId=1401470050000018185&text=Your OTP is %s MagnusIT", mobileNumber, otp);


        Log.d("OTPService", "API Request URL: " + url);

        // Send API request using Volley
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url, null,


                response -> {
                    Log.d("OTPService", "OTP Sent Successfully! Response: " + response.toString());
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(context, "OTP sent to: " + mobileNumber, Toast.LENGTH_SHORT).show()
                    );
                },
                error -> {
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        String errorResponse = new String(error.networkResponse.data);

                        Log.e("OTPService", "HTTP Status Code: " + statusCode);
                        Log.e("OTPService", "Error Response: " + errorResponse);

                        // Handle error based on status code (optional)
                        if (statusCode == 400) {
                            Log.e("OTPService", "Bad Request: Check API parameters.");
                        } else if (statusCode == 401) {
                            Log.e("OTPService", "Unauthorized: Check API credentials.");
                        } else if (statusCode == 500) {
                            Log.e("OTPService", "Internal Server Error: Try again later.");
                        }
                    }
                    else if (error.getCause() != null) {
                        Log.e("OTPService", "Network error: " + error.getCause().getMessage());
                    }
                    else {
                        Log.e(TAG, "Unknown error occurred.");
                    }

                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(context, "Error sending OTP", Toast.LENGTH_SHORT).show()
                    );
                });

        Volley.newRequestQueue(context).add(postRequest);
    }

    // Function to generate a random 6-digit OTP
    private static String generateOTP() {
        return String.valueOf((int) (Math.random() * 900000) + 100000); // Generates a 6-digit OTP
    }
}















//package com.example.loginpage;
//
//import android.content.Context;
//import android.net.Network;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.widget.Toast;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import org.json.JSONObject;
//import android.net.ConnectivityManager;
//import android.net.NetworkCapabilities;
//import android.content.Context;
//
//public class OTPService {
//
//    public static boolean isNetworkAvailable(Context context) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager != null) {
//            Network network = connectivityManager.getActiveNetwork();
//            if (network != null) {
//                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
//                return capabilities != null &&
//                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
//                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
//            }
//        }
//        return false;
//    }
//
//
//
//    public static void sendOTP(String phoneNumber, String otp, final Context context) {
//        // Message format for the OTP SMS
//        String message = otp + ",is,Your,Verification,code,to,Verify,Your,Mobile,On,Pathshaala";
//
//        //Harcoded OTP
//        //final String url = "https://api2.nexgplatforms.com/sms/1/text/query?username=MAGNUSREST&password=Viram@2024&from=MGNSIT&to=8004257064&indiaDltContentTemplateId=1407161526643587607&indiaDltTelemarketerId=1202163221429156518&indiaDltPrincipalEntityId=1401470050000018185&text=Your OTP is 123456 MagnusIT";
//
//
//
//        // Your Infobip or SMS API URL
////        final String url = "http://45.64.104.219/PUSHSMS/api/push?accesskey=h9tjHdgkgaWtbcY6LFnXOsntzJRfFQ&to="
////                + phoneNumber + "&text=" + message + "&from=BE-MAGNUS";
//
//        //final String url = "https://api2.nexgplatforms.com/sms/1/text/query?username=MAGNUSREST&password=Viram@2024&from=MGNSIT&to=918004257064&indiaDltContentTemplateId=1407161526643587607&indiaDltTelemarketerId=1202163221429156518&indiaDltPrincipalEntityId=1401470050000018185&text=Your OTP is 123456 MagnusIT";
//
//        // New API URL with dynamic phone number and OTP
//        final String url = "https://api2.nexgplatforms.com/sms/1/text/query?"
//                + "username=MAGNUSREST"
//                + "&password=Viram@2024"
//                + "&from=MGNSIT"
//                + "&to=91" + phoneNumber
//                + "&indiaDltContentTemplateId=1407161526643587607"
//                + "&indiaDltTelemarketerId=1202163221429156518"
//                + "&indiaDltPrincipalEntityId=1401470050000018185"
//                + "&text=" + message;
//
//        Log.d("OTPService", "API Request URL: " + url); // Log for debugging
//
//        // Check if network is available before sending request
//        if (!isNetworkAvailable(context)) {
//            Log.e("OTPService", "No network connection available.");
//            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
//            return; // Exit function if no internet
//        }
//
//
//
//        // New API URL (replacing {phno} with phoneNumber and {varOTP} with otp)
////        final String url = "https://api2.nexgplatforms.com/sms/1/text/query?"
////                + "username=MAGNUSREST"
////                + "&password=Viram@2024"
////                + "&from=MGNSIT"
////                + "&to=91" + phoneNumber  // Append country code
////                + "&indiaDltContentTemplateId=1407161526643587607"
////                + "&indiaDltTelemarketerId=1202163221429156518"
////                + "&indiaDltPrincipalEntityId=1401470050000018185"
////                + "&text=Your OTP is " + otp + " MagnusIT";
//
//
//        // Create a new Volley request
//        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(final JSONObject response) {
//                        Log.d("OTPService", "OTP Sent Successfully! Response: " + response.toString());
//
//                        // Update UI on the main thread (showing a Toast)
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(context, "OTP Sent Successfully!", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                },
//                error -> {
//
//
//                    // More detailed error logging
//                    if (error.networkResponse != null) {
//                        // Log HTTP status code and the response body (if available)
//                        int statusCode = error.networkResponse.statusCode;
//                        String errorResponse = new String(error.networkResponse.data);
//
//                        Log.e("OTPService", "HTTP Status Code: " + statusCode);
//                        Log.e("OTPService", "Error Response: " + errorResponse);
//
//                        // Handle specific error based on status code (optional)
//                        if (statusCode == 400) {
//                            Log.e("OTPService", "Bad Request: Check API parameters.");
//                        } else if (statusCode == 401) {
//                            Log.e("OTPService", "Unauthorized: Check API credentials.");
//                        } else if (statusCode == 500) {
//                            Log.e("OTPService", "Internal Server Error: Try again later.");
//                        }
//
//                    } else if (error.getCause() != null) {
//                        Log.e("OTPService", "Network error: " + error.getCause().getMessage());
//                    } else {
//                        Log.e("OTPService", "Unknown error occurred.");
//                    }
//
////                    // More detailed error logging
////                    if (error.networkResponse != null) {
////                        // Capture HTTP status code
////                        String errorMsg = "Error sending OTP: " + error.networkResponse.statusCode;
////                        Log.e("OTPService", errorMsg); // Log status code from the response
////
////                        // Log the full error response body (if available)
////                        if (error.networkResponse.data != null) {
////                            String errorResponse = new String(error.networkResponse.data);
////                            Log.e("OTPService", "Error Response: " + errorResponse);
////                        }
////                    } else if (error.getCause() != null) {
////                        Log.e("OTPService", "Network error: " + error.getCause().getMessage());
////                    } else {
////                        Log.e("OTPService", "Unknown error occurred.");
////                    }
//
////                    // More detailed error logging
////                    if (error.networkResponse != null) {
////                        String errorMsg = "Error sending OTP: " + error.networkResponse.statusCode;
////                        Log.e("OTPService", errorMsg); // Log status code from the response
////                    } else if (error.getCause() != null) {
////                        Log.e("OTPService", "Network error: " + error.getCause().getMessage());
////                    } else {
////                        Log.e("OTPService", "Unknown error occurred.");
////                    }
////                    Log.e("OTPService", "Error sending OTP: " + error.getMessage());
//
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(context, "Error sending OTP", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                });
//
//        // Add the request to the RequestQueue
//        Volley.newRequestQueue(context).add(getRequest);
//    }
//}




// Post Method



//package com.example.loginpage;
//
//import android.content.Context;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.widget.Toast;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import org.json.JSONObject;
//
//public class OTPService {
//
//    public static void sendOTP(String phoneNumber, String otp, final Context context) {
//        String message = otp + ",is,Your,Verification,code,to,Verify,Your,Mobile,On,UPPCL";
//
//        // New API URL with dynamic phone number and OTP
//        final String url = "https://api2.nexgplatforms.com/sms/1/text/query";
//
//        JSONObject params = new JSONObject();
//        try {
//            params.put("username", "MAGNUSREST");
//            params.put("password", "Viram@2024");
//            params.put("from", "MGNSIT");
//            params.put("to", "91" + phoneNumber);  // Add country code
//            params.put("indiaDltContentTemplateId", "1407161526643587607");
//            params.put("indiaDltTelemarketerId", "1202163221429156518");
//            params.put("indiaDltPrincipalEntityId", "1401470050000018185");
//            params.put("text", "Your OTP is " + otp + " MagnusIT");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Log.d("OTPService", "API Request URL: " + url); // Log URL for debugging
//
//        // Create a new Volley POST request
//        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, params,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(final JSONObject response) {
//                        Log.d("OTPService", "OTP Sent Successfully! Response: " + response.toString());
//
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(context, "OTP Sent Successfully!", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                },
//                error -> {
//                    // More detailed error logging
//                    if (error.networkResponse != null) {
//                        // Log HTTP status code and the response body (if available)
//                        int statusCode = error.networkResponse.statusCode;
//                        String errorResponse = new String(error.networkResponse.data);
//
//                        Log.e("OTPService", "HTTP Status Code: " + statusCode);
//                        Log.e("OTPService", "Error Response: " + errorResponse);
//
//                        // Handle specific error based on status code (optional)
//                        if (statusCode == 400) {
//                            Log.e("OTPService", "Bad Request: Check API parameters.");
//                        } else if (statusCode == 401) {
//                            Log.e("OTPService", "Unauthorized: Check API credentials.");
//                        } else if (statusCode == 500) {
//                            Log.e("OTPService", "Internal Server Error: Try again later.");
//                        }
//
//                    } else if (error.getCause() != null) {
//                        Log.e("OTPService", "Network error: " + error.getCause().getMessage());
//                    } else {
//                        Log.e("OTPService", "Unknown error occurred.");
//                    }
//
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(context, "Error sending OTP", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                });
//
//        // Add the request to the RequestQueue
//        Volley.newRequestQueue(context).add(postRequest);
//    }
//}




//    private static final String TAG = "OTPService";


//    public static void sendOTP(String mobileNumber, final Context context) {
//        if (mobileNumber == null || mobileNumber.isEmpty()) {
//            Log.e("OTPService", "Mobile number is null or empty.");
//            Toast.makeText(context, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        Log.d("OTPService", "Sending OTP to: " + mobileNumber);
//
//        // Construct the message (API will generate OTP)
//        String message = "Your OTP is [Generated by API] MagnusIT"; // The API will handle OTP generation
//
//        // API URL with dynamic phone number
//        final String url = "https://api2.nexgplatforms.com/sms/1/text/query?"
//                + "username=MAGNUSREST"
//                + "&password=Viram@2024"
//                + "&from=MGNSIT"
//                + "&to=91" + mobileNumber  // Include country code (91 for India)
//                + "&indiaDltContentTemplateId=1407161526643587607"
//                + "&indiaDltTelemarketerId=1202163221429156518"
//                + "&indiaDltPrincipalEntityId=1401470050000018185"
//                + "&text=" + message;
//
//        Log.d("OTPService", "API Request URL: " + url);  // Log for debugging
//
//        // Send API request using Volley
//        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                response -> {
//                    Log.d("OTPService", "OTP Sent Successfully! Response: " + response.toString());
//                    Toast.makeText(context, "OTP Sent Successfully!", Toast.LENGTH_SHORT).show();
//                },
//                error -> {
//                    Log.e("OTPService", "Error sending OTP: " + error.getMessage());
//                    Toast.makeText(context, "Error sending OTP", Toast.LENGTH_SHORT).show();
//                });
//
//        // Add the request to the RequestQueue
//        Volley.newRequestQueue(context).add(postRequest);
//    }
//}

