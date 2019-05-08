package com.example.dell.jsh;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
public class ClickPost extends AppCompatActivity {
    ImageView image ;
    Button updatePost , deletePost;
    TextView postDescription;
    FirebaseAuth mAuth;
    private String PostKey,currentUserId,databaseUserID,imageo, description;
    private DatabaseReference ClickPostRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);
        image =(ImageView)findViewById(R.id.click_post_image);

        mAuth=FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        postDescription=(TextView)findViewById(R.id.click_post_description);

        updatePost =findViewById(R.id.update_post_button);
        deletePost=findViewById(R.id.delete_post_button);


        updatePost.setVisibility(View.INVISIBLE);
        deletePost.setVisibility(View.INVISIBLE);


        PostKey = getIntent().getExtras().get("PostKey").toString();

        ClickPostRef= FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
        if(ClickPostRef!=null) {
         //   Toast.makeText(ClickPost.this, "ye bhi null  nhi hai  ", Toast.LENGTH_SHORT).show();
        }
        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                   description = dataSnapshot.child("description").getValue().toString();
                   postDescription.setText(description);
                   imageo = dataSnapshot.child("postimage").getValue().toString();
                   Picasso.with(ClickPost.this).load(imageo).into(image);

                   databaseUserID=dataSnapshot.child("uid").getValue().toString();
                   if(databaseUserID.equals(currentUserId)){


                       updatePost.setVisibility(View.VISIBLE);
                       deletePost.setVisibility(View.VISIBLE);
                   }
               }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        deletePost.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeletePost();
            }
        });
        updatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditCurrentPost(description);
            }
        });
    }
    private void EditCurrentPost(String description) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPost.this);
        builder.setTitle("Edit Post:");
        final EditText inputField = new EditText(ClickPost.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("update ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClickPostRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPost.this,"Updated sucessfully" , Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.cancel();
            }
        });

        Dialog dialog=builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);
    }

    private void DeletePost() {

        ClickPostRef.removeValue();
        Toast.makeText(ClickPost.this,"Post has been deleted sucessfully",Toast.LENGTH_SHORT).show();
        sendUserToMainActivity();

    }



    private void sendUserToMainActivity() {
        Intent mainIntenet= new Intent(ClickPost.this , MainActivity.class);
        mainIntenet.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntenet);
        finish();
    }
}