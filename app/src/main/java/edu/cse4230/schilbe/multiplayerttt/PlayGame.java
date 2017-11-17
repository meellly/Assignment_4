package edu.cse4230.schilbe.multiplayerttt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PlayGame extends AppCompatActivity implements View.OnClickListener {
    IntentFilter filterGame = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
    Intent setupIntent = null;
    BroadcastReceiver brGame = null;

    String currentPlayerId = null;
    String currentPlayerName = null;
    String currentPlayerIconColor = " ";
    String otherPlayerId = null;
    String otherPlayerName = null;
    String otherPlayerNumber = null;
    String otherPlayerIconColor = " ";

    String currentPlayerTurn = null;
    String firstPlayerWin = null;
    String secondPlayerWin = null;

    String playersTurnName = null;
    String playersIconColor = " ";
    String firstPlayer = null;
    String secondPlayer = null;
    String firstPlayerColor = " ";
    String secondPlayerColor = " ";

    Button A1, A2, A3, B1, B2, B3, C1, C2, C3, reset;
    TextView currentPlayer;
    ImageView profileIcon;
    int count = 0;
    int playersTurn = 0;
    ImageView imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        setupIntent = getIntent();

        //***** get values from START_GAME *****//
        currentPlayerId = setupIntent.getStringExtra("CurrentPlayerId");
        currentPlayerName = setupIntent.getStringExtra("CurrentPlayerName");
        currentPlayerIconColor = setupIntent.getStringExtra("CurrentPlayerIconColor");
        otherPlayerId = setupIntent.getStringExtra("OtherPlayerId");
        otherPlayerName = setupIntent.getStringExtra("OtherPlayerName");
        otherPlayerNumber = setupIntent.getStringExtra("OtherPlayerNumber");
        otherPlayerIconColor = setupIntent.getStringExtra("OtherPlayerIconColor");

        //Setup who goes first
        if (currentPlayerId.equals("0")) {
            firstPlayer = currentPlayerName;
            secondPlayer = otherPlayerName;
            firstPlayerColor = currentPlayerIconColor;
            secondPlayerColor = otherPlayerIconColor;
            playersTurn = 0;
        } else if (currentPlayerId.equals("1")) {
            firstPlayer = otherPlayerName;
            secondPlayer = currentPlayerName;
            firstPlayerColor = otherPlayerIconColor;
            secondPlayerColor = currentPlayerIconColor;
            playersTurn = 1;
        }

        playersTurnName = firstPlayer;
        playersIconColor = firstPlayerColor;

        //Setup strings to display
        firstPlayerWin = firstPlayer + " wins!";
        secondPlayerWin = secondPlayer + " wins!";
        currentPlayerTurn = playersTurnName + "'s Turn";

        //DEBUG
        //Toast.makeText(getApplicationContext(), "CurrentID: " + currentPlayerId + ", CurrentName: " + currentPlayerName, Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), "OtherID: " + otherPlayerId + ", OtherName: " + otherPlayerName + ", OtherNumber" + otherPlayerNumber, Toast.LENGTH_LONG).show();

        //link the buttons to the layout file
        currentPlayer = (TextView) findViewById(R.id.text);
        profileIcon = (ImageView) findViewById(R.id.profileIcon);
        A1 = (Button) findViewById(R.id.A1);
        A2 = (Button) findViewById(R.id.A2);
        A3 = (Button) findViewById(R.id.A3);
        B1 = (Button) findViewById(R.id.B1);
        B2 = (Button) findViewById(R.id.B2);
        B3 = (Button) findViewById(R.id.B3);
        C1 = (Button) findViewById(R.id.C1);
        C2 = (Button) findViewById(R.id.C2);
        C3 = (Button) findViewById(R.id.C3);
        reset = (Button) findViewById(R.id.reset);
        imageView = (ImageView) findViewById(R.id.cat);

        currentPlayer.setText(firstPlayer + "'s Turn");

        //Set player's icon
        switch (firstPlayerColor) {
            case "orange":
                DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.profileOrange));
                break;
            case "purple":
                DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.profilePurple));
                break;
            case "green":
                DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.profileGreen));
                break;
            case "red":
                DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.profileRed));
                break;
            default:
                DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.colorText));
        }

        //Set the clickListener for the buttons
        A1.setOnClickListener(this);
        A2.setOnClickListener(this);
        A3.setOnClickListener(this);
        B1.setOnClickListener(this);
        B2.setOnClickListener(this);
        B3.setOnClickListener(this);
        C1.setOnClickListener(this);
        C2.setOnClickListener(this);
        C3.setOnClickListener(this);
        reset.setOnClickListener(this);

        //Disable player 1 cells to allow player 0 to go first
        if (playersTurn == 1) {
            A1.setEnabled(false);
            A2.setEnabled(false);
            A3.setEnabled(false);
            B1.setEnabled(false);
            B2.setEnabled(false);
            B3.setEnabled(false);
            C1.setEnabled(false);
            C2.setEnabled(false);
            C3.setEnabled(false);
        }

        brGame = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

                    for (SmsMessage m : messages) {

                        String number = m.getDisplayOriginatingAddress();
                        String message = m.getDisplayMessageBody();

                        //Extract elements from message: [0] broadcast receiver type, [1] player's turn name, [2] symbol to set cell, [3] id of cell (button) that was clicked, [4] player's icon color
                        String[] string_to_array = message.split(",");

                        String br_type = string_to_array[0];

                        // Check to make sure correct message
                        if (br_type.equals("$#$#UPDATE_GAME")) {
                            String players_turn_name = string_to_array[1];
                            String cell_clicked_symbol = string_to_array[2];
                            String cell_clicked_id = string_to_array[3];
                            String players_icon_color = string_to_array[4];

                            playersTurnName = players_turn_name;
                            currentPlayerTurn = playersTurnName + "'s Turn";
                            currentPlayer.setText(currentPlayerTurn);
                            playersIconColor = players_icon_color;

                            //Set player's icon
                            switch (playersIconColor) {
                                case "orange":
                                    DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.profileOrange));
                                    break;
                                case "purple":
                                    DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.profilePurple));
                                    break;
                                case "green":
                                    DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.profileGreen));
                                    break;
                                case "red":
                                    DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.profileRed));
                                    break;
                                default:
                                    DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.colorText));
                            }

                            // Enable cells
                            A1.setEnabled(true);
                            A2.setEnabled(true);
                            A3.setEnabled(true);
                            B1.setEnabled(true);
                            B2.setEnabled(true);
                            B3.setEnabled(true);
                            C1.setEnabled(true);
                            C2.setEnabled(true);
                            C3.setEnabled(true);

                            //Update Board
                            switch (cell_clicked_id) {
                                case "A1":
                                    A1.setText(cell_clicked_symbol);
                                    break;
                                case "A2":
                                    A2.setText(cell_clicked_symbol);
                                    break;
                                case "A3":
                                    A3.setText(cell_clicked_symbol);
                                    break;
                                case "B1":
                                    B1.setText(cell_clicked_symbol);
                                    break;
                                case "B2":
                                    B2.setText(cell_clicked_symbol);
                                    break;
                                case "B3":
                                    B3.setText(cell_clicked_symbol);
                                    break;
                                case "C1":
                                    C1.setText(cell_clicked_symbol);
                                    break;
                                case "C2":
                                    C2.setText(cell_clicked_symbol);
                                    break;
                                case "C3":
                                    C3.setText(cell_clicked_symbol);
                                    break;
                            }
                            //Toast.makeText(getBaseContext(), "Player 1 made move (" + number + ")", Toast.LENGTH_SHORT).show();
                            result();
                        }
                        if (br_type.equals("$#$#RESET_GAME")) {
                            reset();
                        }
                    }
                }
            }
        };
        registerReceiver(brGame, filterGame);
    }

    @Override
    public void onClick(View view) {

        //Set symbol on board and check for win
        switch (view.getId()) {

            case R.id.start:
                reset();
                break;

            case R.id.reset:
                //***** SEND_RESET *****//
                String brType = "$#$#RESET_GAME";
                String[] message_array = {brType};

                StringBuilder strBuilder = new StringBuilder();
                for (int i = 0; i < message_array.length; i++) {
                    strBuilder.append(message_array[i]);
                    strBuilder.append(",");
                }
                String message_array_string = strBuilder.toString();

                String message = message_array_string;

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(otherPlayerNumber, null, message, null, null);
                reset();
                break;

            case R.id.A1:
                players_turn(A1);
                break;

            case R.id.A2:
                players_turn(A2);
                break;

            case R.id.A3:
                players_turn(A3);
                break;

            case R.id.B1:
                players_turn(B1);
                break;

            case R.id.B2:
                players_turn(B2);
                break;

            case R.id.B3:
                players_turn(B3);
                break;

            case R.id.C1:
                players_turn(C1);
                break;

            case R.id.C2:
                players_turn(C2);
                break;

            case R.id.C3:
                players_turn(C3);
                break;
        }
    }

    public void result() {
        if (A1.getText().toString().equals("X") &&
                A2.getText().toString().equals("X") &&
                A3.getText().toString().equals("X")) {
            Toast.makeText(this, firstPlayerWin, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                reset.callOnClick();
            }
        } else if (B1.getText().toString().equals("X") &&
                B2.getText().toString().equals("X") &&
                B3.getText().toString().equals("X")) {
            Toast.makeText(this, firstPlayerWin, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                reset.callOnClick();
            }
        } else if (C1.getText().toString().equals("X") &&
                C2.getText().toString().equals("X") &&
                C3.getText().toString().equals("X")) {
            Toast.makeText(this, firstPlayerWin, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                reset.callOnClick();
            }
        } else if (A1.getText().toString().equals("X") &&
                B2.getText().toString().equals("X") &&
                C3.getText().toString().equals("X")) {
            Toast.makeText(this, firstPlayerWin, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                reset.callOnClick();
            }
        } else if (A3.getText().toString().equals("X") &&
                B2.getText().toString().equals("X") &&
                C1.getText().toString().equals("X")) {
            Toast.makeText(this, firstPlayerWin, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                reset.callOnClick();
            }
        } else if (A1.getText().toString().equals("X") &&
                B1.getText().toString().equals("X") &&
                C1.getText().toString().equals("X")) {
            Toast.makeText(this, firstPlayerWin, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                reset.callOnClick();
            }
        } else if (A2.getText().toString().equals("X") &&
                B2.getText().toString().equals("X") &&
                C2.getText().toString().equals("X")) {
            Toast.makeText(this, firstPlayerWin, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                reset.callOnClick();
            }
        } else if (A3.getText().toString().equals("X") &&
                B3.getText().toString().equals("X") &&
                C3.getText().toString().equals("X")) {
            Toast.makeText(this, firstPlayerWin, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                reset.callOnClick();
            }
        } else if (A1.getText().toString().equals("O") &&
                A2.getText().toString().equals("O") &&
                A3.getText().toString().equals("O")) {
            Toast.makeText(this, secondPlayerWin, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                reset.callOnClick();
            }
        } else if (B1.getText().toString().equals("O") &&
                B2.getText().toString().equals("O") &&
                B3.getText().toString().equals("O")) {
            Toast.makeText(this, secondPlayerWin, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                reset.callOnClick();
            }
        } else if (C1.getText().toString().equals("O") &&
                C2.getText().toString().equals("O") &&
                C3.getText().toString().equals("O")) {
            Toast.makeText(this, secondPlayerWin, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                reset.callOnClick();
            }
        } else if (A1.getText().toString().equals("O") &&
                B2.getText().toString().equals("O") &&
                C3.getText().toString().equals("O")) {
            Toast.makeText(this, secondPlayerWin, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                reset.callOnClick();
            }
        } else if (A3.getText().toString().equals("O") &&
                B2.getText().toString().equals("O") &&
                C1.getText().toString().equals("O")) {
            Toast.makeText(this, secondPlayerWin, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                reset.callOnClick();
            }
        } else if (A1.getText().toString().equals("O") &&
                B1.getText().toString().equals("O") &&
                C1.getText().toString().equals("O")) {
            Toast.makeText(this, secondPlayerWin, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                reset.callOnClick();
            }
        } else if (A2.getText().toString().equals("O") &&
                B2.getText().toString().equals("O") &&
                C2.getText().toString().equals("O")) {
            Toast.makeText(this, secondPlayerWin, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                reset.callOnClick();
            }
        } else if (A3.getText().toString().equals("O") &&
                B3.getText().toString().equals("O") &&
                C3.getText().toString().equals("O")) {
            Toast.makeText(this, secondPlayerWin, Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                reset.callOnClick();
            }
        } else {
            if (!(A1.getText().toString().equals("")) &&
                    !(A2.getText().toString().equals("")) &&
                    !(A3.getText().toString().equals("")) &&
                    !(B1.getText().toString().equals("")) &&
                    !(B2.getText().toString().equals("")) &&
                    !(B3.getText().toString().equals("")) &&
                    !(C1.getText().toString().equals("")) &&
                    !(C2.getText().toString().equals("")) &&
                    !(C3.getText().toString().equals(""))) {

                imageView.setVisibility(View.VISIBLE);
                imageView.bringToFront();

                Toast.makeText(this, "Cats Game!", Toast.LENGTH_SHORT).show();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                    reset.callOnClick();
//                }
            }
        }
    }

    public void players_turn(Button game_button) {
        if (game_button.getText().toString().equals("")) {
            count += 1;

            //Disable cells of player that just took a turn
            A1.setEnabled(false);
            A2.setEnabled(false);
            A3.setEnabled(false);
            B1.setEnabled(false);
            B2.setEnabled(false);
            B3.setEnabled(false);
            C1.setEnabled(false);
            C2.setEnabled(false);
            C3.setEnabled(false);

            //Update board cell of player that just took turn and switch players
            if (playersTurnName.equals(firstPlayer)) {
                game_button.setText("X");
                playersTurnName = secondPlayer;
                playersIconColor = secondPlayerColor;

            } else if (playersTurnName.equals(secondPlayer)) {
                game_button.setText("O");
                playersTurnName = firstPlayer;
                playersIconColor = firstPlayerColor;
            }

            currentPlayerTurn = playersTurnName + "'s Turn";
            currentPlayer.setText(currentPlayerTurn);

            //Set player's icon
            switch (playersIconColor) {
                case "orange":
                    DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.profileOrange));
                    break;
                case "purple":
                    DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.profilePurple));
                    break;
                case "green":
                    DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.profileGreen));
                    break;
                case "red":
                    DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.profileRed));
                    break;
                default:
                    DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.colorText));
            }

            //***** SEND_UPDATE *****//
            String brType = "$#$#UPDATE_GAME";
            String players_turn_name = playersTurnName;
            String button_clicked_symbol = game_button.getText().toString();
            String button_clicked_id = String.valueOf(game_button.getTag());
            String players_icon_color = playersIconColor;
            String[] message_array = {brType, players_turn_name, button_clicked_symbol, button_clicked_id, players_icon_color};

            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i < message_array.length; i++) {
                strBuilder.append(message_array[i]);
                strBuilder.append(",");
            }
            String message_array_string = strBuilder.toString();

            String message = message_array_string;

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(otherPlayerNumber, null, message, null, null);

            result();
        }
    }

    public void reset() {
        A1.setText("");
        A2.setText("");
        A3.setText("");
        B1.setText("");
        B2.setText("");
        B3.setText("");
        C1.setText("");
        C2.setText("");
        C3.setText("");
        playersTurnName = firstPlayer;
        if (playersTurn == 1) {
            // Disable second player's cells
            A1.setEnabled(false);
            A2.setEnabled(false);
            A3.setEnabled(false);
            B1.setEnabled(false);
            B2.setEnabled(false);
            B3.setEnabled(false);
            C1.setEnabled(false);
            C2.setEnabled(false);
            C3.setEnabled(false);
        } else if (playersTurn == 0) {
            // Enable first player's cells
            A1.setEnabled(true);
            A2.setEnabled(true);
            A3.setEnabled(true);
            B1.setEnabled(true);
            B2.setEnabled(true);
            B3.setEnabled(true);
            C1.setEnabled(true);
            C2.setEnabled(true);
            C3.setEnabled(true);
        }
        count = 0;
        currentPlayer.setText(firstPlayer + "'s Turn");

        //Set player's icon
        switch (firstPlayerColor) {
            case "orange":
                DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.profileOrange));
                break;
            case "purple":
                DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.profilePurple));
                break;
            case "green":
                DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.profileGreen));
                break;
            case "red":
                DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.profileRed));
                break;
            default:
                DrawableCompat.setTint(profileIcon.getDrawable(), ContextCompat.getColor(getBaseContext(), R.color.colorText));
        }

        imageView.setVisibility(View.GONE);

    }
}
