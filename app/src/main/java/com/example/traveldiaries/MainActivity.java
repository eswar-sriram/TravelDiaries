package com.example.traveldiaries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView imageView;
    ArrayList<ArrayList<String>> al;
    Button signin;
    int a =10;
    String nu = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        al=new ArrayList<ArrayList<String>>();
        signin=findViewById(R.id.signin);
        imageView=findViewById(R.id.image);
        recyclerView=findViewById(R.id.destination_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter=new MyAdapter(getApplicationContext(),addArrayList(al));
        recyclerView.setAdapter(adapter);

          signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),Login.class));
                }
            });
    }
    private ArrayList<ArrayList<String>> addArrayList(final ArrayList<ArrayList<String>> ul) {
        ArrayList al=new ArrayList();
            al.add("Venice_beach");
            al.add(String.valueOf(R.drawable.venice_beach));
            ul.add(al);
            al = new ArrayList();
            al.add("Miami_beach");
            al.add(String.valueOf(R.drawable.miami_beach));
            ul.add(al);
            al = new ArrayList();
            al.add("Monarova_italy");
            al.add(String.valueOf(R.drawable.monarova_italy));
            ul.add(al);
            al = new ArrayList();
            al.add("Goa");
            al.add(String.valueOf(R.drawable.goa));
            ul.add(al);
            return ul;
    }
}


class MyAdapter extends RecyclerView.Adapter<MyAdapter.ImageViewHolder> {
    private Context context;
    private ArrayList<ArrayList<String>> uploads;

    public MyAdapter(Context context,ArrayList<ArrayList<String>> up){
        this.context=context;
        this.uploads=up;
        //added comments
        //added by branch
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.row_item,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, int position) {
        Picasso.with(context).load(Integer.parseInt(uploads.get(position).get(1))).into(holder.imageview);
        holder.textView.setText(uploads.get(position).get(0));
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageview;

        public ImageViewHolder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.cityname);
            imageview=itemView.findViewById(R.id.image);
        }
    }

}
