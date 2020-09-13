package com.example.rtc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Date;

public class chatScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
<<<<<<< HEAD

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
=======
>>>>>>> parent of 630aef3... Base Chat Config
    }
}