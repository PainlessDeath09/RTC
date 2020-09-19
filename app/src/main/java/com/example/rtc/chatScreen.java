package com.example.rtc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class chatScreen extends AppCompatActivity {

    private DatabaseReference myDatabase;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();;
    StorageReference storageReference = storage.getReference();;


    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;

    private String username;
    //Initialize Views

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        myDatabase = FirebaseDatabase.getInstance().getReference("Message");

        final TextView myText = findViewById(R.id.textView2);
        Button upload = findViewById(R.id.Upload);
        ImageButton btnChoose = findViewById(R.id.Choose);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        /*btnUpload = (Button) findViewById(R.id.Upload);*/


        myText.setScroller(new Scroller(getApplicationContext()));
        myText.setVerticalScrollBarEnabled(true);

        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String[] message = snapshot.getValue().toString().split(",");
                myText.setText("");

                String a = snapshot.getValue().toString();
                a = a.substring(1, a.length() - 1);
                String[] arr = a.split(",");


                /*
                String[] finalMessage = new String[0];
               for(int i=0; i<message.length; i++)
                {
                    finalMessage = message[i].split("=");
                    myText.append(finalMessage[1] + "\n");
                }
                //myText.setText(snapshot.getValue().toString());
               // myText.setText(finalMessage);
                Log.i("String value",message.toString());
                */
                username = user.getUid();
                String msg, temp;
                int flag=0, tflag = 0;
                for(String i : arr)
                {
                    Log.i("RAW",i);
                    if(i.contains(username))
                    {
                        //temp = getColoredSpanned(i.substring(i.lastIndexOf('=') + 1), "#008000" );
                        temp = i.substring(i.lastIndexOf('=') + 1);
                        if(flag == 0)
                        {
                            myText.append( "\n\nYou:\n"+temp+"\n");
                            flag = 1;
                        }
                        else
                        {
                            myText.append(temp+"\n");
                        }
                    }
                    else if(!(i.contains("00=")))
                    {
                        temp = i.substring(i.lastIndexOf('=') + 1);
                        if(tflag == 0)
                        {
                            myText.append("\n\nRecipient:\n" + temp + "\n");
                            tflag = 1;
                        }
                        else
                        {
                            myText.append(temp+"\n");
                        }
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                myText.setText("CANCELLED");

            }
        });

    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView imageView = (ImageView) findViewById(R.id.imgView);
        Button cancel = findViewById(R.id.Cancel);
        Button upload = findViewById(R.id.Upload);

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                upload.setVisibility(View.VISIBLE);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(View view)
    {
        EditText textbox = findViewById(R.id.messageBox);
        Date date = new Date();
        username = user.getUid();
        Log.i("Username:",username);

        if(!TextUtils.isEmpty(textbox.getText()))
            myDatabase.child(username+"__"+Long.toString(date.getTime())).setValue(textbox.getText().toString());
        //myDatabase.child(username).setValue(textbox.getText().toString());
        textbox.setText("");
    }

    private void uploadImage() {

        final ImageView imageView = (ImageView) findViewById(R.id.imgView);
        final Button cancel = findViewById(R.id.Cancel);
        final Button upload = findViewById(R.id.Upload);
        final Date date = new Date();
        username = user.getUid();
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(chatScreen.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(chatScreen.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                            imageView.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                            upload.setVisibility(View.GONE);
                            myDatabase.child(username+"__"+Long.toString(date.getTime())).setValue("<<IMAGE>>");
                        }
                    });
        }
    }

}