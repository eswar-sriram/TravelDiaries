package com.example.traveldiaries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class Drawer extends AppCompatActivity {
    Button signout;
    TextView requests,buddies;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        firebaseAuth=FirebaseAuth.getInstance();
        setContent();
        requests=findViewById(R.id.p_requests);
        signout=findViewById(R.id.signout);
        buddies=findViewById(R.id.p_buddies);
        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RequestsView.class));
            }
        });
        buddies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),TripList.class));
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }

    private void setContent() {
        TextView name=findViewById(R.id.p_name);
        TextView mail=findViewById(R.id.p_email);
        String username=getCurrentUsername();
        String email=firebaseAuth.getCurrentUser().getEmail();
        name.setText(username);
        mail.setText(email);
        final long DATA=999999999;
        StorageReference storageReference= FirebaseStorage.getInstance().getReference(username+"/"+username+"_profilepic.jpg");
        storageReference.getBytes(DATA).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ImageView imageView=findViewById(R.id.p_pic);
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        });
    }
    private String getCurrentUsername(){
        FirebaseUser currentuser=firebaseAuth.getCurrentUser();
        final String mail=currentuser.getEmail();
        databaseReference= FirebaseDatabase.getInstance().getReference("keys");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SharedPreferences sharedPreferences=getSharedPreferences("names", Context.MODE_MULTI_PROCESS);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    if(i.getValue(String.class).equals(mail)){
                        editor.putString("username",i.getKey());

                    }
                }
                editor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        SharedPreferences sharedPreferences=getSharedPreferences("names",Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getString("username","oops");
    }
}
