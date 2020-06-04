package com.example.traveldiaries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchDestination extends AppCompatActivity {
    DatabaseReference  databaseReference;
    FirebaseAuth firebaseAuth;
    ArrayList<String> city_list;
    SwipeRefreshLayout swipeRefreshLayout;
    Button search,profile;
    AutoCompleteTextView autoCompleteTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_destination);
        swipeRefreshLayout=findViewById(R.id.swiperefresh);
        city_list=new ArrayList<>();
        addlist(city_list);
        autoCompleteTextView = findViewById(R.id.search_bar);
        ArrayAdapter adapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,city_list);
        autoCompleteTextView.setAdapter(adapter);
        search=findViewById(R.id.search_button);
        profile=findViewById(R.id.viewprofile);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                startActivity(new Intent(getApplicationContext(),SearchDestination.class));
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences=getSharedPreferences("city",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("city",autoCompleteTextView.getText().toString().toLowerCase());
                editor.apply();
                startActivity(new Intent(getApplicationContext(),SameUsers.class));
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Drawer.class));
            }
        });
    }


    private ArrayList<String > addlist(final ArrayList<String > al) {
        databaseReference= FirebaseDatabase.getInstance().getReference("cities");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    al.add(i.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchDestination.this, "oops unable to load cities", Toast.LENGTH_SHORT).show();
            }
        });
        return al;
    }


}
