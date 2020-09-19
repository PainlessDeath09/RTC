package com.example.rtc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class chatScreen extends AppCompatActivity {

    private DatabaseReference myDatabase;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        myDatabase = FirebaseDatabase.getInstance().getReference("Message");

        final TextView myText = findViewById(R.id.textView2);
        myText.setScroller(new Scroller(getApplicationContext()));
        myText.setVerticalScrollBarEnabled(true);

        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String[] message = snapshot.getValue().toString().split(",");
                myText.setText("");

                String a = snapshot.getValue().toString();
                Log.i("Raw",a);

                String[] finalMessage = new String[0];
               for(int i=0; i<message.length; i++)
                {
                    finalMessage = message[i].split("=");
                    myText.append(finalMessage[1] + "\n");
                }
                //myText.setText(snapshot.getValue().toString());
               // myText.setText(finalMessage);
                Log.i("String value",message.toString());

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
        username = user.getUid();
        Log.i("Username:",username);

        myDatabase.child(username+"__"+Long.toString(date.getTime())).setValue(textbox.getText().toString());
        //myDatabase.child(username).setValue(textbox.getText().toString());



        textbox.setText("");

    }
}