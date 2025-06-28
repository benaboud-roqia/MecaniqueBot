package com.todolist.mechanique;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.animation.LayoutTransition;

import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private LinearLayout chatLayout;
    private EditText editTextMessage;
    private ScrollView scrollView;
    private String selectedDomaine = "Général";

    // Remplace par l'URL de ton webhook Java (ex: https://ton-serveur.com/webhook)
    private static final String WEBHOOK_URL = "https://ton-serveur.com/webhook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatLayout = findViewById(R.id.chatLayout);
        chatLayout.setLayoutTransition(new LayoutTransition());
        editTextMessage = findViewById(R.id.editTextMessage);
        scrollView = findViewById(R.id.scrollView);

        Spinner spinnerDomaines = findViewById(R.id.spinnerDomaines);
        String[] domaines = {"Général", "Résistance des matériaux", "Fluides", "Thermodynamique", "Matériaux"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, domaines);
        spinnerDomaines.setAdapter(adapter);
        spinnerDomaines.setSelection(0);
        spinnerDomaines.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedDomaine = domaines[position];
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        ImageButton buttonSend = findViewById(R.id.buttonSend);
        buttonSend.setScaleX(1f);
        buttonSend.setScaleY(1f);
        editTextMessage.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                buttonSend.animate().scaleX(1.2f).scaleY(1.2f).setDuration(150).start();
            } else {
                buttonSend.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    addMessage(message, true);
                    editTextMessage.setText("");
                    sendMessageToWebhook(message, selectedDomaine);
                }
            }
        });
    }

    private void addMessage(String message, boolean isUser) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setTextSize(16);
        textView.setPadding(24, 16, 24, 16);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 8, 8, 8);
        if (isUser) {
            params.gravity = android.view.Gravity.END;
            textView.setBackgroundResource(R.drawable.bg_bubble_user);
            textView.setTextColor(0xFFFFFFFF);
        } else {
            params.gravity = android.view.Gravity.START;
            textView.setBackgroundResource(R.drawable.bg_bubble_bot);
            textView.setTextColor(0xFF263238);
        }
        textView.setLayoutParams(params);
        chatLayout.addView(textView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void sendMessageToWebhook(String message, String domaine) {
        new Thread(() -> {
            try {
                URL url = new URL(WEBHOOK_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("query", message);
                body.put("domaine", domaine);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                Scanner in = new Scanner(conn.getInputStream());
                StringBuilder response = new StringBuilder();
                while (in.hasNext()) response.append(in.nextLine());
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                String botReply = jsonResponse.getString("reply");

                runOnUiThread(() -> addMessage(botReply, false));

            } catch (Exception e) {
                runOnUiThread(() -> addMessage("Erreur de connexion au serveur.", false));
                e.printStackTrace();
            }
        }).start();
    }
}