package edu.cse4230.schilbe.multiplayerttt;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Html;
import android.widget.Toast;

public class dialogbox extends AppCompatActivity {

    Intent setupIntent = null;
    String currrentPlayerName = null;
    String otherPlayerNumber = null;
    String otherPlayerName = null;
    String currentPlayerIconColor = " ";
    String otherPlayerIconColor = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupIntent = getIntent();

        //***** get values from RECEIVE_INVITATION *****//
        currrentPlayerName = setupIntent.getStringExtra("CurrentPlayerName");
        currentPlayerIconColor = setupIntent.getStringExtra("CurrentPlayerIconColor");
        otherPlayerName = setupIntent.getStringExtra("InviterPlayerName");
        otherPlayerNumber = setupIntent.getStringExtra("InviterPlayerNumber");
        otherPlayerIconColor = setupIntent.getStringExtra("InviterPlayerColor");

        //DEBUG
        //Toast.makeText(getApplicationContext(), "Current: " + currrentPlayerName + ", Inviter: " + otherPlayerName + ", Inviter Number: " + otherPlayerNumber, Toast.LENGTH_LONG).show();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(dialogbox.this);

        alertDialog.setTitle("Invitation from " + otherPlayerName);

        // Set Dialog Message
        alertDialog.setMessage("Do you want to play TicTacToe?");

        // SetPositive "Yes" Button
        alertDialog.setPositiveButton(Html.fromHtml("<font color='#000000'>Accept</font>"), new DialogInterface.OnClickListener() {

            //***** SEND_ACCEPT *****//
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Starting game with " + otherPlayerName, Toast.LENGTH_SHORT).show();

                //Build array for all messages that need to be sent to Broadcast Receiver
                String brType = "$#$#ACCEPT_INVITATION";
                String currentPlayer = otherPlayerName;
                String otherPlayer = currrentPlayerName;
                String otherPlayerColor = currentPlayerIconColor;
                String[] message_array = {brType, currentPlayer, otherPlayer, otherPlayerColor};

                //Convert array to string
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < message_array.length; i++) {
                    stringBuilder.append(message_array[i]);
                    stringBuilder.append(",");
                }
                String message_array_string = stringBuilder.toString();

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(otherPlayerNumber, null, message_array_string, null, null);

                //***** START_GAME *****//
                String currentPlayerId = "1";
                String otherPlayerId = "0";

                Toast.makeText(getApplicationContext(), "Starting game with " + otherPlayerName, Toast.LENGTH_SHORT).show();

                Intent startGame = new Intent(getBaseContext(), PlayGame.class);
                startGame.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startGame.putExtra("CurrentPlayerId", currentPlayerId);
                startGame.putExtra("CurrentPlayerName", currrentPlayerName);
                startGame.putExtra("CurrentPlayerIconColor", currentPlayerIconColor);
                startGame.putExtra("OtherPlayerId", otherPlayerId);
                startGame.putExtra("OtherPlayerName", otherPlayerName);
                startGame.putExtra("OtherPlayerNumber", otherPlayerNumber);
                startGame.putExtra("OtherPlayerIconColor", otherPlayerIconColor);
                startActivity(startGame);
            }
        });

        // Set Negative "NO" Button
        alertDialog.setNegativeButton(Html.fromHtml("<font color='#000000'>Decline</font>"), new DialogInterface.OnClickListener() {

            //***** SEND_DECLINE *****//
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "You have declined the invitation", Toast.LENGTH_SHORT).show();
                dialog.cancel();

                //Build array for all messages that need to be sent to Broadcast Receiver
                String brType = "$#$#DECLINE_INVITATION";
                String[] message_array = {brType, currrentPlayerName};

                //Convert array to string
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < message_array.length; i++) {
                    stringBuilder.append(message_array[i]);
                    stringBuilder.append(",");
                }
                String message_array_string = stringBuilder.toString();

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(otherPlayerNumber, null, message_array_string, null, null);

                Intent startOver = new Intent(getBaseContext(), SetupGame.class);
                startOver.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(startOver);
            }
        });

        // Display Alert Message
        alertDialog.show();
    }
}
