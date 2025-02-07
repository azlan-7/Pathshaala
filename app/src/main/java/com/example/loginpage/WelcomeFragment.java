package com.example.loginpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

public class WelcomeFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private AppCompatButton nextButton;


    public static WelcomeFragment newInstance(String title, String description) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, description);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        if (getArguments() != null) {
            String title = getArguments().getString(ARG_TITLE);
            String description = getArguments().getString(ARG_DESCRIPTION);

            // Inflate the correct layout based on title or position
            if ("Welcome to Pathshaala".equals(title)) {
                view = inflater.inflate(R.layout.fragment_welcome_3, container, false); // Last Fragment
            } else {
                view = inflater.inflate(R.layout.fragment_welcome_2, container, false); // Other Fragments
            }

            // Set up the views after inflating the correct layout
            TextView titleText = view.findViewById(R.id.textView22);
            TextView descriptionText = view.findViewById(R.id.textView23);
            nextButton = view.findViewById(R.id.button8); // For last fragment

            if (title != null && description != null) {
                titleText.setText(title);
                descriptionText.setText(description);
            }

            // Set up button click listener if it's the last fragment
            if (nextButton != null) {
                nextButton.setOnClickListener(v -> {
                    // Handle button click, for example, go to the next page
                });
            }

        }

        return view;
    }
}

