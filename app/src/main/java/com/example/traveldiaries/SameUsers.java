package com.example.traveldiaries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SameUsers extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    RecyclerView recyclerView;
    Button fixDestination;
    SwipeRefreshLayout swipe;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_same_users);
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        recyclerView=findViewById(R.id.userslist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String > al=new ArrayList<>();
        setpeople();
        fixDestination=findViewById(R.id.fixdest);
        fixDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences=getSharedPreferences("city",MODE_PRIVATE);
                String city=sharedPreferences.getString("city","no city");
                addDataToDB(city);
                Toast.makeText(SameUsers.this, "added", Toast.LENGTH_SHORT).show();
            }
        });
        swipe=findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                startActivity(new Intent(getApplicationContext(),SameUsers.class));
            }
        });
    }

    private void addDataToDB(final String destination) {
        final String mail=firebaseAuth.getCurrentUser().getEmail();
        databaseReference=FirebaseDatabase.getInstance().getReference("keys");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name="";
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    if(i.getValue().equals(mail)){
                        name=i.getKey();
                    }
                }
                databaseReference=FirebaseDatabase.getInstance().getReference("userdestinations/"+destination);
                databaseReference.child(name).setValue(mail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setpeople() {
        SharedPreferences sharedPreferences=getSharedPreferences("city",MODE_PRIVATE);
        String dest=sharedPreferences.getString("city","no city");
        databaseReference= FirebaseDatabase.getInstance().getReference("userdestinations/"+dest+"/");
        recyclerView=findViewById(R.id.userslist);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String > users=new ArrayList<>();
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    if(i!=null){
                    users.add(i.getKey());
                    }
                }
                final ArrayList<String> al=new ArrayList<>(users);
                CustomAdapter adapter=new CustomAdapter(getApplicationContext(),users);
                adapter.setItemClickListener(new CustomAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        userDetailView(al.get(position));
                    }
                });
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void userDetailView(final String name) {
        databaseReference=firebaseDatabase.getReference("keys/"+name);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = getLayoutInflater().inflate(R.layout.alert_dialog, null);
        builder.setView(view);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextView textView1 = view.findViewById(R.id.email);
                textView1.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        TextView textView = view.findViewById(R.id.name);
        final ImageView imageView = view.findViewById(R.id.img);
        textView.setText(name);
        final long DATA = 999999999;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(name + "/" + name + "_profilepic.jpg");
        storageReference.getBytes(DATA).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        });
        Button notify=view.findViewById(R.id.notify);
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyuser(name);
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    private void notifyuser(String name) {
        SharedPreferences sharedPreferences=getSharedPreferences("city",MODE_PRIVATE);
        String city=sharedPreferences.getString("city","no city");
        databaseReference=firebaseDatabase.getReference("requestnode/"+name+"/");
        databaseReference.child(getCurrentUsername()).setValue(city);
    }

    private String getCurrentUsername(){
        FirebaseUser currentuser=firebaseAuth.getCurrentUser();
        final String mail=currentuser.getEmail();
        databaseReference=FirebaseDatabase.getInstance().getReference("keys");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SharedPreferences sharedPreferences=getSharedPreferences("names",Context.MODE_MULTI_PROCESS);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    if(i.getValue(String.class).equals(mail)){
                        Toast.makeText(SameUsers.this, i.getKey(), Toast.LENGTH_SHORT).show();
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

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ImageViewHolder>{
    private Context context;
    private ArrayList<String > arrayList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    public CustomAdapter(Context context,ArrayList<String> arrayList) {
        this.context=context;
        this.arrayList=arrayList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(context).inflate(R.layout.cityrecycler,parent,false);
        return new ImageViewHolder(v,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, int position) {
        String name=arrayList.get(position);
        holder.textView.setText(name);
        StorageReference ref= FirebaseStorage.getInstance().getReference(name+"/"+name+"_profilepic.jpg");
        final long DATA=999999999;
        ref.getBytes(DATA).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                holder.imageView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textView=itemView.findViewById(R.id.users);
            imageView=itemView.findViewById(R.id.profilepic);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
}