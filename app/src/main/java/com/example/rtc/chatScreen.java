package com.example.rtc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class chatScreen extends AppCompatActivity {

    private DatabaseReference myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        myDatabase = FirebaseDatabase.getInstance().getReference("Message");

       // final TextView myText = findViewById(R.id.textBox);
        myText.setScroller(new Scroller(getApplicationContext()));
        myText.setVerticalScrollBarEnabled(true);

        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String[] message = snapshot.getValue().toString().split(",");
                myText.setText("");

                String[] finalMessage = new String[0];
               for(int i=0; i<message.length; i++)
                {
                    finalMessage = message[i].split("=");
                    myText.append(finalMessage[1] + "\n");
                }
                //myText.setText(snapshot.getValue().toString());
               // myText.setText(finalMessage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                myText.setText("CANCELLED");

            }
        });
    }

    public void sendMessage(View view)
    {
        EditText textbox = findViewById(R.id.messageBox);
        Date date = new Date();
        myDatabase.child(Long.toString(date.getTime())).setValue(textbox.getText().toString());
        textbox.setText("");
    }
}