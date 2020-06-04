package com.example.traveldiaries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Buddies extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String city;
    Button send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddies);
        firebaseAuth=FirebaseAuth.getInstance();
        recyclerView=findViewById(R.id.buddies_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();
        city=getIntent().getStringExtra("selected");
        setBuddies();
        setChat();
        send=findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChat();
            }
        });
    }

    private void setChat() {
        EditText editText=findViewById(R.id.data);
        final ListView listView=findViewById(R.id.chat);
        databaseReference=FirebaseDatabase.getInstance().getReference("chat").child("plan");
        databaseReference.child(System.currentTimeMillis()+"").setValue(getCurrentUsername()+":"+editText.getText().toString());
        databaseReference=FirebaseDatabase.getInstance().getReference("chat").child("plan");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> al=new ArrayList<>();
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    al.add(i.getValue(String.class));
                }
                ArrayAdapter<String> adapter=new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,al);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setBuddies() {
        databaseReference=FirebaseDatabase.getInstance().getReference("buddieslist").child(getCurrentUsername()).child(city);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> bud=new ArrayList<>();
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    bud.add(i.getKey());
                }
                final ArrayList<String > a=new ArrayList<>(bud);
                CustomAdapter customAdapter=new CustomAdapter(Buddies.this,bud);
                customAdapter.setItemClickListener(new CustomAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(final int position) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(Buddies.this);
                        builder.setTitle("choose");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReference=FirebaseDatabase.getInstance().getReference("buddieslist").child(getCurrentUsername()).child(city);
                                databaseReference.child(a.get(position)).removeValue();
                            }
                        });
                        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                });
                recyclerView.setAdapter(customAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
