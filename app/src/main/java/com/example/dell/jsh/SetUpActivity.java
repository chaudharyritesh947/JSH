package com.example.dell.jsh;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpActivity extends AppCompatActivity {

    private Uri ImageUri,resultUri;
    private EditText userName, fullName , countryName ;
    private Button saveInformationButton;
    private CircleImageView profileImage;
    private FirebaseAuth mauth ;
    private DatabaseReference userRef;
    private ProgressDialog loadinBar;
    private StorageReference userProfileImageRef;
    final  static int Gallery_Pick = 1;

    String  currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        userName = findViewById(R.id.setup_user_name);
        fullName = findViewById(R.id.setup_full_name);
        countryName =  findViewById(R.id.setup_country_name);
        saveInformationButton = findViewById(R.id.setup_information_button);
        profileImage =  findViewById(R.id.setup_profile_image);
//        private ProgressDialog loadinBar;
        loadinBar =new ProgressDialog(this);
        mauth=FirebaseAuth.getInstance();
        currentUserId = mauth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userProfileImageRef=FirebaseStorage.getInstance().getReference().child("Profile Images");

        saveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAccountSetupInformation();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galaryIntent= new Intent();//;.ACTION_GET_CONTENT();
                galaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galaryIntent.setType("image/*");
                startActivityForResult(galaryIntent,Gallery_Pick);
            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("profileimage")) {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(SetUpActivity.this).load(image).placeholder(R.drawable.profile).into(profileImage);
                    }else{
//                        Toast.makeText(SetUpActivity.this,"dya kuch toh gadbad hai" , Toast.LENGTH_SHORT);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        Toast.makeText(SetUpActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();

                        final String downloadUrl = downUri.toString();
                        userRef.child("profileimage").setValue(downloadUrl)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent selfIntent = new Intent(SetUpActivity.this, SetUpActivity.class);
                                            startActivity(selfIntent);

                                            Toast.makeText(SetUpActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                            loadinBar.dismiss();
                                        } else {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(SetUpActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                            loadinBar.dismiss();
                                        }
                                    }
                                });
                    }
        }
            });
        }
        else{
            Toast.makeText(SetUpActivity.this,"Some error has occured : ",Toast.LENGTH_SHORT).show();
            loadinBar.dismiss();
        }
    }


    private void saveAccountSetupInformation() {
        String username = userName.getText().toString();
        String fullname = fullName.getText().toString();
        String countryname = countryName.getText().toString();
        if(TextUtils.isEmpty(username)){
            Toast.makeText(SetUpActivity.this , "pls fill username"  , Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(fullname)){
            Toast.makeText(SetUpActivity.this , "pls fill fullname"  , Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(countryname)){
            Toast.makeText(SetUpActivity.this , "pls fill country name"  , Toast.LENGTH_SHORT);
        }
        else{
            loadinBar.setTitle("Saving Your Data");
            loadinBar.setMessage("Please wait while we are saveing your new data");
            loadinBar.show();
            loadinBar.setCanceledOnTouchOutside(true);
            HashMap userMap = new HashMap();
            userMap.put("userName",username);
            userMap.put("fullName",fullname);
            userMap.put("Country",countryname);
            userMap.put("Status","Software Development");
            userMap.put("Gender","Male");
            userMap.put("DOB","100999");
            userMap.put("Relation Ship","none");
            userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        sendUserToMainActivity();
                        Toast.makeText(SetUpActivity.this,"Data updated succesfully " , Toast.LENGTH_LONG).show();
                        loadinBar.dismiss();
                    }
                    else{
                        String message = task.getException().getMessage();
                        //  Toast.makeText(SetUpActivity.this,"Error Occured : "+message,Toast.LENGTH_LONG).show();
                   //     Toast.makeText(SetUpActivity.this,"chk point ",Toast.LENGTH_LONG).show();
                        loadinBar.dismiss();
                    }
                }
            });
        }
    }
    private void sendUserToMainActivity() {
        Intent mainIntenet= new Intent(SetUpActivity.this , MainActivity.class);
        mainIntenet.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntenet);
        finish();
    }
}