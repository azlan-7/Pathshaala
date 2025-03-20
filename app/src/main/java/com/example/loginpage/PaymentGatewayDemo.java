package com.example.loginpage;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.razorpay.Checkout;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentGatewayDemo extends AppCompatActivity {
    private Button payNowBtn;
    private EditText amountText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_gateway_demo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        payNowBtn = findViewById(R.id.payNowBtn);
        amountText = findViewById(R.id.amountText);
        Checkout.preload(PaymentGatewayDemo.this);
        payNowBtn.setOnClickListener(v -> {
            startPayment(Integer.parseInt(amountText.getText().toString()));
        });
    }

    public void startPayment(int Amount){
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_4ZtM3uCcmSeeED");

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Pathshaala");
            options.put("description", "Payment is to be done here.");
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", Amount * 100);

            JSONObject retryObject = new JSONObject();
            retryObject.put("enabled", true);
            retryObject.put("max_count", 4);
            options.put("retry", retryObject);

            checkout.open(PaymentGatewayDemo.this, options);

        } catch (JSONException e) {
            Toast.makeText(PaymentGatewayDemo.this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
    }
}