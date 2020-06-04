package com.example.traveldiaries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

public class RequestsView extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_view);
        recyclerView=findViewById(R.id.request_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();
        setRequestsArray(getCurrentUsername());

    }

    private void setRequestsArray(String name) {
        databaseReference=FirebaseDatabase.getInstance().getReference("requestnode/"+name+"/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ArrayList<String >> users=new ArrayList<ArrayList<String >>();
                ArrayList<String > dest;
                for(DataSnapshot i:dataSnapshot.getChildren()){
                    dest=new ArrayList<>();
                    dest.add(i.getKey());
                    dest.add(i.getValue(String.class));
                    users.add(dest);
                }
                final ArrayList<ArrayList<String>> al=new ArrayList<ArrayList<String>>(users);
                RequestAdapter adapter=new RequestAdapter(getApplicationContext(),users);
                adapter.setItemClickListener(new RequestAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        pendingRequest(al,position);
                    }
                });
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void pendingRequest(final ArrayList<ArrayList<String >> al,final int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(RequestsView.this);
        builder.setTitle("Accept request of "+al.get(position).get(0));
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference=FirebaseDatabase.getInstance().getReference("buddieslist").child(getCurrentUsername()).child(al.get(position).get(1));
                databaseReference.child(al.get(position).get(0)).setValue("friend");
                databaseReference=FirebaseDatabase.getInstance().getReference("buddieslist").child(al.get(position).get(0)).child(al.get(position).get(1));
                databaseReference.child(getCurrentUsername()).setValue("friend");
                databaseReference=FirebaseDatabase.getInstance().getReference("requestnode").child(getCurrentUsername());
                databaseReference.child(al.get(position).get(0)).removeValue();
                Toast.makeText(RequestsView.this, "Accepted", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference=FirebaseDatabase.getInstance().getReference("requestnode").child(getCurrentUsername());
                databaseReference.child(al.get(position).get(0)).removeValue();
                Toast.makeText(RequestsView.this, "Rejected", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
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
                        Toast.makeText(RequestsView.this, i.getKey(), Toast.LENGTH_SHORT).show();
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


class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ImageViewHolder>{
    private Context context;
    private ArrayList<ArrayList<String >> arrayList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    public RequestAdapter(Context context,ArrayList<ArrayList<String>> arrayList) {
        this.context=context;
        this.arrayList=arrayList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.request_row,parent,false);
        return new ImageViewHolder(v,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, int position) {
        String name=arrayList.get(position).get(0);
        holder.textView.setText(name);
        holder.destination.setText("request to: "+arrayList.get(position).get(1));
        StorageReference ref= FirebaseStorage.getInstance().getReference(name+"/"+name+"_profilepic.jpg");
        final long DATA=999999999;
        ref.getBytes(DATA).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                holder.imageView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView textView,destination;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textView=itemView.findViewById(R.id.rq_users);
            destination=itemView.findViewById(R.id.rq_users_dest);
            imageView=itemView.findViewById(R.id.rq_pic);
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