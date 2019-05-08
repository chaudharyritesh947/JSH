package com.example.dell.jsh;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsAvtivity extends AppCompatActivity {



    private Uri resultUri ;
    private DatabaseReference userRef;


    private ProgressDialog loadinBar;
//    loadinBar =new ProgressDialog(this);
private StorageReference userProfileImageRef;

    private Toolbar mtoolbar;
    private EditText  ed1 , ed2 , ed3 , ed4 , ed5 , ed6,ed7;
    private Button b1;
    private CircleImageView circleImageView;

    final static int Gallery_Pick=1;
    private String currentUserId;
    private FirebaseAuth mAuth ;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_avtivity);


     //   userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

      //  resultUri = data.getData();
        mtoolbar=findViewById(R.id.settings_toolbar);
       setSupportActionBar(mtoolbar);
       getSupportActionBar().setTitle("Account settings");
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       mAuth= FirebaseAuth.getInstance();
       currentUserId=mAuth.getCurrentUser().getUid();
       databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);


        userProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");


        ed1=findViewById(R.id.settings_status);
        ed2=findViewById(R.id.settings_username);
        ed3=findViewById(R.id.settings_fullname);
        ed4=findViewById(R.id.settings_Country);
        ed5=findViewById(R.id.settings_dob);
        ed6=findViewById(R.id.settings_genders);
        ed7=findViewById(R.id.settings_branch);

        loadinBar =new ProgressDialog(this);
        circleImageView = findViewById(R.id.settings_profile_image);

        b1=(Button)findViewById(R.id.settings_button);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String profileImge = dataSnapshot.child("profileimage").getValue().toString();
                    String username = dataSnapshot.child("userName").getValue().toString();
                    String fullname = dataSnapshot.child("fullName").getValue().toString();
                    String dob = dataSnapshot.child("DOB").getValue().toString();
                    String country = dataSnapshot.child("Country").getValue().toString();
                    String status = dataSnapshot.child("Status").getValue().toString();
                    String rXn = dataSnapshot.child("Relation Ship").getValue().toString();
                    String gender = dataSnapshot.child("Gender").getValue().toString();


                    Picasso.with(SettingsAvtivity.this).load(profileImge).placeholder(R.drawable.profile).into(circleImageView);

                    ed2.setText(username);//
                    ed3.setText(fullname);
                    ed5.setText(dob);
                    ed1.setText(status);//
                    ed7.setText(rXn);//
                    ed4.setText(country);//

                    ed6.setText(gender);




                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAccountInfo();
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galaryIntent= new Intent();//;.ACTION_GET_CONTENT();
                galaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galaryIntent.setType("image/*");
                startActivityForResult(galaryIntent,Gallery_Pick);

            }
        });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            loadinBar.setTitle("Saving Your DP");
            loadinBar.setMessage("Please wait while we are saving your your profile image ");
            loadinBar.show();
            loadinBar.setCanceledOnTouchOutside(true);
            resultUri = data.getData();
            final StorageReference filePath = userProfileImageRef.child(currentUserId +".jpg");
            filePath.putFile(resultUri).continueWithTask(  new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downUri = task.getResult();
                        Toast.makeText(SettingsAvtivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();

                        final String downloadUrl = downUri.toString();
                        databaseReference.child("profileimage").setValue(downloadUrl)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent selfIntent = new Intent(SettingsAvtivity.this, SettingsAvtivity.class);
                                            startActivity(selfIntent);

                                            Toast.makeText(SettingsAvtivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                            loadinBar.dismiss();
                                        } else {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(SettingsAvtivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                            loadinBar.dismiss();
                                        }
                                    }
                                });
                    }
                }
            });
        }
        else{
            Toast.makeText(SettingsAvtivity.this,"Some error has occured : ",Toast.LENGTH_SHORT).show();
            loadinBar.dismiss();
        }
    }





    private void validateAccountInfo() {


        String username =ed2.getText().toString();
        String fullname =ed3.getText().toString();
        String gender =ed6.getText().toString();
        String status =ed1.getText().toString();
        String relationsship =ed7.getText().toString();
        String country =ed4.getText().toString();
        String dob =ed5.getText().toString();


        if(TextUtils.isEmpty(username)){
            Toast.makeText(SettingsAvtivity.this,"username is empty" ,Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(fullname)){
            Toast.makeText(SettingsAvtivity.this,"fullname is empty" ,Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(gender)){
            Toast.makeText(SettingsAvtivity.this,"gender is empty" ,Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(status)){
            Toast.makeText(SettingsAvtivity.this,"status is empty" ,Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(relationsship)){
            Toast.makeText(SettingsAvtivity.this,"Branch is empty" ,Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(country)){
            Toast.makeText(SettingsAvtivity.this,"country is empty" ,Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(dob)){
            Toast.makeText(SettingsAvtivity.this,"dob is empty" ,Toast.LENGTH_SHORT).show();
        }
        else{
            UpdateAccountInfo(username , fullname,gender ,status,relationsship,country,dob);
        }

    }

    private void UpdateAccountInfo(String username, String fullname, String gender, String status, String relationsship, String country, String dob) {

        HashMap hm = new HashMap();
        hm.put("userName" ,username);
        hm.put("fullName" ,fullname);
        hm.put("Country" ,country);
        hm.put("Relation Ship" ,relationsship);
        hm.put("DOB" ,dob);
        hm.put("Status" ,status);
        hm.put("Gender" ,gender);


    databaseReference.updateChildren(hm).addOnCompleteListener(new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
       if(task.isSuccessful()){
           sendUserToMainActivity();
           Toast.makeText(SettingsAvtivity.this,"Updated Sucessfully",Toast.LENGTH_SHORT).show();
       }
       else{
           Toast.makeText(SettingsAvtivity.this,"Updation  Unsucessfull",Toast.LENGTH_SHORT).show();

       }
        }
    });



    }


    private void sendUserToMainActivity() {
        Intent mainIntenet= new Intent(SettingsAvtivity.this , MainActivity.class);
        mainIntenet.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntenet);
        finish();
    }
}
