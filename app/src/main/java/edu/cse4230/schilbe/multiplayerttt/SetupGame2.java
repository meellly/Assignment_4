package edu.cse4230.schilbe.multiplayerttt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetupGame2 extends AppCompatActivity {

    IntentFilter filterSetup = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
    EditText phoneNumber = null, messageText = null;
    TextView pNumber = null, mText = null;
    Button smsButton = null;
    BroadcastReceiver brSetup = null;
    Button startButton = null;
    String playerNumber = null;
//    String player0Number = "";
//    String player1Number = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_game2);

        startButton = (Button) findViewById(R.id.start);
        phoneNumber = (EditText) findViewById(R.id.editTextNumber);
        messageText = (EditText) findViewById(R.id.editTextMessage);
        pNumber = (TextView) findViewById(R.id.textViewNumber);
        mText = (TextView) findViewById(R.id.textViewMessage);
        smsButton = (Button) findViewById(R.id.buttonSend);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                playerNumber = phoneNumber.getText().toString();
//                if (player0Number.length() > 2) {
//                    player0Number = phoneNumber.getText().toString();
//                } else {
//                    player1Number = phoneNumber.getText().toString();
//                }

                Intent intent = new Intent(view.getContext(), PlayGame.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("PlayerNumber", playerNumber);
//                intent.putExtra("Player0Number", player0Number);
//                intent.putExtra("Player1Number", player1Number);
                startActivity(intent);
            }
        });

        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = messageText.getText().toString();
                String number = phoneNumber.getText().toString();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, msg, null, null); //Line #41
            }
        });

        brSetup = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

                    for (SmsMessage m : messages) {
                        String number = m.getDisplayOriginatingAddress();
                        String text = m.getDisplayMessageBody();

                        pNumber.setText(number);
                        mText.setText(text);
                    }
                }
            }
        };

        registerReceiver(brSetup, filterSetup);
    }
}
