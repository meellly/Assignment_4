package edu.cse4230.schilbe.multiplayerttt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    TextView textView = null;
    EditText editText = null;
    Button button = null, buttonOrange = null, buttonPurple = null, buttonSilver = null, buttonRed = null;
    String initialPlayerName = null;
    String iconColor = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        buttonOrange = (Button) findViewById(R.id.orange);
        buttonPurple = (Button) findViewById(R.id.purple);
        buttonSilver = (Button) findViewById(R.id.green);
        buttonRed = (Button) findViewById(R.id.red);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialPlayerName = editText.getText().toString();
                Intent i = new Intent(view.getContext(), SetupGame.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                i.putExtra("PlayerInputName", initialPlayerName);
                i.putExtra("IconColor", iconColor);
                startActivity(i);
            }
        });

        buttonOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iconColor = "orange";
            }
        });

        buttonPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iconColor = "purple";
            }
        });

        buttonSilver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iconColor = "green";
            }
        });

        buttonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iconColor = "red";
            }
        });

    }
}
