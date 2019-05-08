package com.example.dell.jsh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {



    private TextView tv1 , tv2 , tv3 ,tv4 ,tv5 , tv6,tv7  ;
    CircleImageView circleImageView;

    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth=FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);


        tv1 = findViewById(R.id.my_profile_full_name);
        tv2 = findViewById(R.id.my_username);
        tv3 = findViewById(R.id.my_profile_status);
        tv4 = findViewById(R.id.my_country);
        tv5 = findViewById(R.id.my_dob);
        tv6 = findViewById(R.id.my_gender);
        tv7 = findViewById(R.id.my_rxn);

        circleImageView=findViewById(R.id.my_profile_pic);




        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {


                    String myprofileimage = dataSnapshot.child("profileimage").getValue().toString();
                    String myusername = dataSnapshot.child("userName").getValue().toString();
                    String myfullname = dataSnapshot.child("fullName").getValue().toString();
                    String mystatus = dataSnapshot.child("Status").getValue().toString();
                    String mydob = dataSnapshot.child("DOB").getValue().toString();
                    String mycountry = dataSnapshot.child("Country").getValue().toString();
                    String mygender = dataSnapshot.child("Gender").getValue().toString();
                    String myrxnstat = dataSnapshot.child("Relation Ship").getValue().toString();





                    Picasso.with(ProfileActivity.this).load(myprofileimage).placeholder(R.drawable.profile).into(circleImageView);

                    tv1.setText(myfullname);//
                    tv2.setText("@"+myusername);
                    tv3.setText("Work: "+mystatus);
                    tv4.setText("Country: "+mycountry);//
                    tv5.setText("DOB: "+mydob);//
                    tv6.setText("Gender: "+mygender);//
                    tv7.setText("Branch: "+myrxnstat);




                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        tv1=findViewById(R.id.my_profile_full_name);
        tv2=findViewById(R.id.my_username);
        tv3=findViewById(R.id.my_profile_status);
        tv4=findViewById(R.id.my_country);
        tv5=findViewById(R.id.my_dob);
        tv6=findViewById(R.id.my_gender);
        tv7=findViewById(R.id.my_rxn);

    }
}
