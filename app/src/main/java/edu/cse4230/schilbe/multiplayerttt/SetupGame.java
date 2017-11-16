package edu.cse4230.schilbe.multiplayerttt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SetupGame extends AppCompatActivity {

    IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
    Intent settingsIntent = null;
    BroadcastReceiver br = null;
    EditText phoneNumber = null, messageText = null;
    Button sendInvite = null;
    String currentPlayerName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_game);

        settingsIntent = getIntent();
        currentPlayerName = settingsIntent.getStringExtra("PlayerInputName");

        phoneNumber = (EditText) findViewById(R.id.editTextNumber);
        sendInvite = (Button) findViewById(R.id.buttonSendInvitation);


        //***** SEND_INVITATION *****//
        sendInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = phoneNumber.getText().toString();

                //Build array for all messages that need to be sent to Broadcast Receiver
                String brType = "$#$#SEND_INVITE";
                String playerName = currentPlayerName;
                String[] message_array = {brType, playerName};

                //Convert array to string
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < message_array.length; i++) {
                    stringBuilder.append(message_array[i]);
                    stringBuilder.append(",");
                }
                String message_array_string = stringBuilder.toString();

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, message_array_string, null, null);
            }
        });

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

                    for (SmsMessage m : messages) {
                        String number = m.getDisplayOriginatingAddress();
                        String message = m.getDisplayMessageBody();

                        //Extract elements from message: [0] broadcast receiver type, [1] current player, [2] current count, [3] button that was clicked
                        String[] message_string_array = message.split(",");
                        String brType = message_string_array[0];
                        String otherPlayerNumber = number;

                        //***** RECEIVE_ACCEPT *****//
                        if (brType.equals("$#$#ACCEPT_INVITATION")) {

                            String currentPlayerName = message_string_array[1];
                            String otherPlayerName = message_string_array[2];
                            String currentPlayerId = "0";
                            String otherPlayerId = "1";

                            Toast.makeText(getApplicationContext(), "Starting game with " + otherPlayerName, Toast.LENGTH_SHORT).show();

                            //***** START_GAME *****//
                            Intent startGame = new Intent(getBaseContext(), PlayGame.class);
                            startGame.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startGame.putExtra("CurrentPlayerId", currentPlayerId);
                            startGame.putExtra("CurrentPlayerName", currentPlayerName);
                            startGame.putExtra("OtherPlayerId", otherPlayerId);
                            startGame.putExtra("OtherPlayerName", otherPlayerName);
                            startGame.putExtra("OtherPlayerNumber", otherPlayerNumber);
                            startActivity(startGame);


                            //***** RECEIVE_INVITE *****//
                        } else if (brType.equals("$#$#SEND_INVITE")) {

                            String otherPlayerName = message_string_array[1];

                            Intent dialogIntent = new Intent(context, dialogbox.class);
                            dialogIntent.putExtra("CurrentPlayerName", currentPlayerName);
                            dialogIntent.putExtra("InviterPlayerName", otherPlayerName);
                            dialogIntent.putExtra("InviterPlayerNumber", number);
                            startActivity(dialogIntent);


                            //***** RECEIVE_DECLINE *****//
                        } else if (brType.equals("$#$#DECLINE_INVITATION")) {
                            String otherPlayerName = message_string_array[1];

                            Toast.makeText(getApplicationContext(), otherPlayerName + " has declined your invitation", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }

        ;

        registerReceiver(br, filter);
    }
}
