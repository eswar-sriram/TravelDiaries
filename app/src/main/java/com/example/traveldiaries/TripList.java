package com.example.traveldiaries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TripList extends AppCompatActivity {
    ListView listView;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        firebaseAuth=FirebaseAuth.getInstance();
        listView=findViewById(R.id.tourslist);
        databaseReference= FirebaseDatabase.getInstance().getReference("buddieslist").child(getCurrentUsername());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> al = new ArrayList<>();
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    al.add(i.getKey());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, al);
                listView.setAdapter(adapter);
                final ArrayList<String > list=new ArrayList<String >(al);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(TripList.this, list.get(position), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Buddies.class);
                        intent.putExtra("selected", list.get(position));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private String getCurrentUsername(){
        FirebaseUser currentuser=firebaseAuth.getCurrentUser();
        final String mail=currentuser.getEmail();
        databaseReference=FirebaseDatabase.getInstance().getReference("keys");
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



















