package com.example.loginpage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.loginpage.R; // Replace with your actual R class

public class RoundTextCheckbox extends CompoundButton {

    private Paint paint;
    private String text = "";
    private int radius;
    private int strokeWidth;
    private int textColor;
    private int checkedColor;
    private int uncheckedColor;
    private int strokeColor;

    public RoundTextCheckbox(Context context) {
        super(context);
        init();
    }

    public RoundTextCheckbox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundTextCheckbox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokeWidth = dpToPx(2); // Define your stroke width
        radius = dpToPx(12);    // Define your radius
        textColor = ContextCompat.getColor(getContext(), android.R.color.white); // Default text color
        checkedColor = ContextCompat.getColor(getContext(), R.color.blueGradientEnd); // Your checked color
        uncheckedColor = ContextCompat.getColor(getContext(), android.R.color.transparent); // Your unchecked color
        strokeColor = ContextCompat.getColor(getContext(), R.color.blueGradientStart); // Your stroke color

        // Remove the default button drawable
        setButtonDrawable(new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                // Do nothing - we'll draw our own
            }

            @Override
            public void setAlpha(int alpha) {
                // Do nothing
            }

            @Override
            public void setColorFilter(@Nullable android.graphics.ColorFilter colorFilter) {
                // Do nothing
            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSPARENT;
            }
        });
    }

    public void setRoundText(String text) {
        this.text = text;
        invalidate(); // Request a redraw
    }
    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) {
            // Handle the case where the view has no size (e.g., due to layout issues)
            return; // Or draw a placeholder, or log an error
        }

        int centerX = width / 2;
        int centerY = height / 2;

        // Draw the outer circle (stroke)
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerX, centerY, radius, paint);

        // Draw the inner circle (fill)
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX, centerY, radius - strokeWidth, paint);

        // Draw the text
        paint.setColor(isChecked() ? textColor : strokeColor);
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        // More efficient text centering
        canvas.drawText(text, centerX, centerY + textBounds.height() / 2f, paint);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    private int spToPx(int sp) {
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scaledDensity + 0.5f);
    }
}