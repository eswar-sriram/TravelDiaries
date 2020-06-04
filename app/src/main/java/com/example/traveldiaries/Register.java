package com.example.traveldiaries;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class Register extends AppCompatActivity {
    private ArrayList<String> userslist;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    ImageView profilepic;
    Button signup;
    EditText usernameEditext,emailEditext,ageEditext,genderEditext,mobileEditext,stateEditext,passwordEditext,cpasswordEditext;
    private Uri imageuri;
    private static final int GALLERY_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        profilepic=findViewById(R.id.profilepic);
        profilepic.setClickable(true);
        getUsersList();
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);intent.setType("image/*");
                startActivityForResult(intent,GALLERY_CODE);
            }
        });
        usernameEditext=findViewById(R.id.username);
        emailEditext=findViewById(R.id.email);
        ageEditext=findViewById(R.id.age);
        genderEditext=findViewById(R.id.gender);
        mobileEditext=findViewById(R.id.mobile);
        stateEditext=findViewById(R.id.state);
        passwordEditext=findViewById(R.id.password);
        cpasswordEditext=findViewById(R.id.confirm_password);
        signup=findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reguser();
            }
        });

    }

    private void getUsersList() {
        userslist=new ArrayList<>();
        databaseReference=FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot i:dataSnapshot.getChildren()){
                    userslist.add(i.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void reguser() {
        final String username=usernameEditext.getText().toString().trim();
        String email=emailEditext.getText().toString().trim();
        String gender=genderEditext.getText().toString().trim().toLowerCase();
        String age =ageEditext.getText().toString().trim();
        String mobile=mobileEditext.getText().toString().trim();
        String state=stateEditext.getText().toString().trim().toLowerCase();
        String pass=passwordEditext.getText().toString().trim();
        String cpass=cpasswordEditext.getText().toString().trim();

        databaseReference=FirebaseDatabase.getInstance().getReference("users");
        boolean val = validate(mobile, age, email, gender, pass, cpass);
        if(alreadyuser(username)){
            Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
        }else {
            if (val) {
                Handler handler=new Handler();
                addmail(email,pass);
                User user = new User(username,email, age, gender, mobile, state);
                databaseReference.child(username).setValue(user);
                databaseReference=FirebaseDatabase.getInstance().getReference("keys");
                databaseReference.child(username).setValue(email);
                getUsersList();
                Toast.makeText(this, "done reg", Toast.LENGTH_SHORT).show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        uploadimage(username);
                    }
                },4000);
            }
        }
    }

    private void addmail(String email, String pass) {
    firebaseAuth=FirebaseAuth.getInstance();
    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){
                Toast.makeText(Register.this, "Login credentials created", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Register.this, "Login credentials failee", Toast.LENGTH_SHORT).show();
            }
        }
    });
    }

    private String getFileExtension(Uri imguri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imguri));

    }

    private boolean alreadyuser(final String username) {
        for(String u:userslist){
            if(u.equals(username))return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_CODE && resultCode==RESULT_OK && data!=null){
            imageuri=(Uri)data.getData();
            profilepic.setImageURI(imageuri);
        }
    }

    private boolean validate(String phno,String age,String mail,String gender,String pass,String cpass){
        boolean flag=true;
        if(phno.isEmpty() || age.isEmpty() || mail.isEmpty() || gender.isEmpty() || pass.isEmpty() || cpass.isEmpty()){
            Toast.makeText(this, "empty values are not allowed", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!pass.equals(cpass) || pass.length()<8){
            flag=false;
            Toast.makeText(this, "passwords", Toast.LENGTH_SHORT).show();
        } else if(phno.length() != 10){
            flag=false;
            Toast.makeText(this, "phon", Toast.LENGTH_SHORT).show();
        } else if(Integer.parseInt(age) < 18){
            flag=false;
            Toast.makeText(this, "age", Toast.LENGTH_SHORT).show();
        } else if(!(gender.equals("male") || gender.equals("female"))) {
            flag=false;
            Toast.makeText(this, "gender", Toast.LENGTH_SHORT).show();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            flag=false;
            Toast.makeText(this, "mail", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }

    private void uploadimage(final String user){
        if(imageuri!=null){
            StorageReference fileReference=FirebaseStorage.getInstance().getReference(user).child(user+"_profilepic."+getFileExtension(imageuri));
            fileReference.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String uploaduri=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                    databaseReference.child(user).child("imageuri").setValue(uploaduri);
                    Toast.makeText(Register.this, "success uploading", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Register.this, "failed uploading", Toast.LENGTH_SHORT).show();
                }
            });


        }else{
            Toast.makeText(this, "no image selected", Toast.LENGTH_SHORT).show();
        }


    }
}
