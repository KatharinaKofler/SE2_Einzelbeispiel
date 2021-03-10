package com.example.se2_einzelbeispiel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    TextView Ausgabe_server;
    TextView Ausgabe_berechnung;
    Socket client;
    DataOutputStream writeStream;
    BufferedReader readStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Ausgabe_server = new TextView(this);
        Ausgabe_server = (TextView)findViewById(R.id.ausgabe_server);

        Ausgabe_berechnung = new TextView(this);
        Ausgabe_berechnung = (TextView)findViewById(R.id.ausgabe_berechnung);

        Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Thread thread = new Thread(new Runnable() {

                    // IN EXTRA THREAD BECAUSE NETWORK CONNECTIONS ARE NOT ALLOWED IN THE MAIN THREAD BY DEFAULT
                    // https://stackoverflow.com/questions/6343166/how-to-fix-android-os-networkonmainthreadexception
                    @Override
                    public void run() {
                        try {
                            EditText eingabe_matrikelnummer = (EditText) findViewById(R.id.eingabe);
                            String eingabe = eingabe_matrikelnummer.getText().toString();

                            client = new Socket("se2-isys.aau.at", 53212);
                            writeStream = new DataOutputStream(client.getOutputStream());
                            readStream = new BufferedReader(new InputStreamReader(client.getInputStream()));

                            writeStream.writeBytes(eingabe + "\n");
                            Ausgabe_server.setText(readStream.readLine());

                            int quersumme = 0;
                            int zwSpeicher;
                            int count = 0;

                            for(int i = 0; i < eingabe.length();i++) {
                                char aktuel = eingabe.charAt(i);
                                int akt_num = Integer.parseInt(String.valueOf(aktuel));

                                if(akt_num<0||akt_num>9) return;

                                quersumme = quersumme  + akt_num;
                            }

                            zwSpeicher = quersumme;
                            while(zwSpeicher!=0){
                                zwSpeicher = zwSpeicher/2;
                                count++;
                            }

                            int arr[] = new int[count];

                            for(int i = 0; i < count; i++){
                                arr[i] = quersumme%2;
                                quersumme = quersumme/2;
                            }

                            String ausgabe = "";
                            for(int i = count-1; i>=0; i--){
                                ausgabe += arr[i];

                            }
                            Ausgabe_berechnung.setText(ausgabe);

                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();

            }
        });
    }
}