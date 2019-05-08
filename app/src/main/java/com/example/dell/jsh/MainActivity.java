package com.example.dell.jsh;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private ImageButton addNewPostButton;
    private NavigationView navigationView;
    private CircleImageView NavProfileImage ;
    private TextView NavProfileUserName;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    private FirebaseAuth mauth ;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseReference userRef,postRef,likesRef;
    String currentUserId;
    Boolean likeChecker=false;
    String flag="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mauth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mauth.getCurrentUser();
        if (currentUser == null){
            sendUserToLoginActivity();
        }else{
            currentUserId = mauth.getCurrentUser().getUid();
        }
        userRef =FirebaseDatabase.getInstance().getReference().child("Users");//database base ka ref lega jaha hmstore krega sabbb
        postRef =FirebaseDatabase.getInstance().getReference().child("Posts");
        likesRef=FirebaseDatabase.getInstance().getReference().child("Likes");

        addNewPostButton=findViewById(R.id.add_new_post_button);

        mtoolbar = findViewById(R.id.main_page_toolbar);

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Home");

        drawerLayout = findViewById(R.id.drawable_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById( R.id.nav_profile_image);
        NavProfileUserName =(TextView) navView.findViewById(R.id.nav_user_full_name);
        //Hamburger icon
        actionBarDrawerToggle=new ActionBarDrawerToggle(MainActivity.this, drawerLayout , R.string.drawer_open,R.string.drawer_close);
        //for accesiblity reso...
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//back <- ke liyehota hai .

        postList=(RecyclerView)findViewById(R.id.all_user_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelecter(item);
                return false;
            }
        });

        addNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToPostActiviy();
            }
        });



//currently  jo user online hai ... uska  kaam  hai ye .
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    if(dataSnapshot.hasChild("fullName")){
                        String fullname  = dataSnapshot.child("fullName").getValue().toString();
                        NavProfileUserName.setText(fullname);

                    }
                    if(dataSnapshot.hasChild("profileimage")){
                        String profilimage  = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(MainActivity.this).load(profilimage).placeholder(R.drawable.profile).into(NavProfileImage);

                    }

                    else{
                        Toast.makeText(MainActivity.this,"profile name not presnt" , Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DisplayAllUserPost();
    }

    private void DisplayAllUserPost() {
        FirebaseRecyclerAdapter<Posts,PostViewHolder> firebaseRecyclerAdapter =new FirebaseRecyclerAdapter<Posts, PostViewHolder>(
                Posts.class,
                R.layout.all_post_layout,
                PostViewHolder.class,
                postRef

        ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Posts model, int position) {
                final String PostKey = getRef(position).getKey();
//                Toast.makeText(MainActivity.this,PostKey,Toast.LENGTH_SHORT).show();
                viewHolder.setFullname(model.getFullname());
                viewHolder.setTime(model.getTime());
                viewHolder.setDate(model.getDate());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                viewHolder.setPostimage(getApplicationContext(), model.getPostimage());
                viewHolder.setLikeButtonStatus(PostKey);
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent ClickPostIntent = new Intent(MainActivity.this,ClickPost.class);
                        ClickPostIntent.putExtra("PostKey",PostKey);
                        startActivity(ClickPostIntent);

                    }
                });

                viewHolder.likePostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeChecker=true;
                        likesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                            if(likeChecker.equals(true)){
                                if(dataSnapshot.child(PostKey).hasChild(currentUserId)){
                                    likesRef.child(PostKey).child(currentUserId).removeValue();
                                    likeChecker=false;
                                }
                                else{
                                    likesRef.child(PostKey).child(currentUserId).setValue(true);//Value();
                                    likeChecker=false;
                                }

                            }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });


                viewHolder.fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {



                        Intent clickPostIntent = new Intent(MainActivity.this,CommentsActivity.class);
                       clickPostIntent.putExtra("PostKey",PostKey);
                        startActivity(clickPostIntent);



                    }
                });
            }
        };

        postList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{



        ImageButton likePostButton , commentPostButton;
        TextView displayNumberOfLikes;


        FloatingActionButton fab;
        int countLikes;
        String currentUserId;
        DatabaseReference LikeRef;

        View mview;
        public PostViewHolder(View itemView) {
            super(itemView);
        mview = itemView;

        likePostButton =mview.findViewById(R.id.like_button);
        fab=mview.findViewById(R.id.floatingActionButton);
        displayNumberOfLikes=mview.findViewById(R.id.display_number_of_likes);

        LikeRef =FirebaseDatabase.getInstance().getReference().child("Likes");
        currentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();

        }

        public void setLikeButtonStatus(final String PostKey) {

            LikeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(PostKey).hasChild(currentUserId)){
                        countLikes =(int)dataSnapshot.child(PostKey).getChildrenCount();
                        likePostButton.setImageResource(R.drawable.like);
                        displayNumberOfLikes.setText(Integer.toString(countLikes)+" Like");
                    }
                    else{

                        countLikes =(int)dataSnapshot.child(PostKey).getChildrenCount();
                        likePostButton.setImageResource(R.drawable.dislike);
                        displayNumberOfLikes.setText(Integer.toString(countLikes)+" Like");




                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setFullname(String fullname) {
        TextView username  = mview.findViewById(R.id.post_user_name);
        username.setText(fullname);
        }


        public void setProfileimage(Context ctx ,String profileimage){
            CircleImageView  image = mview.findViewById(R.id.post_profile_image);
            Picasso.with(ctx).load(profileimage).into(image);

        }


        public void setTime(String time){
            TextView PostTime  = mview.findViewById(R.id.post_time);
            PostTime.setText(time);
        }


        public void setDate(String date){
            TextView PostDate  = mview.findViewById(R.id.post_date);
            PostDate.setText(date);

        }
        public void setDescription(String description){
            TextView PostDescription  = mview.findViewById(R.id.post_description);
            PostDescription.setText(description);

        }

        public void setPostimage(Context ctx , String postimage) {
            ImageView image = mview.findViewById(R.id.post_image);
            Picasso.with(ctx).load(postimage).into(image);
        }


    }

    private void sendUserToPostActiviy() {
    Intent postIt = new Intent (MainActivity.this , PostActivity.class);
    startActivity(postIt);
    }





    @Override
    protected void onStart() {
        super.onStart();//whene app runs called auto. to chk user auth.
        FirebaseUser currentUser=mauth.getCurrentUser();
        if(currentUser==null){
            //means user is not auth ;
            sendUserToLoginActivity();
        }
        else{
            checkUserExistence();
        }




    }

    private void checkUserExistence() {
    final String current_user_id=mauth.getCurrentUser().getUid();//id of the user who iis online.
    userRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //user is authenticated but uska recor nhi hai setup pr bhej do .
               if(!dataSnapshot.hasChild(current_user_id)) {
                   sendUserToSetUpActivity();
               }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
    }

    private void sendUserToSetUpActivity() {
        Intent setUpIntent = new Intent(MainActivity.this,SetUpActivity.class);
        setUpIntent .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//for validation bck buttonn dbaogey to piche nahi  jaega.
        startActivity(setUpIntent );
        finish();

    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this,loginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//for validation bck buttonn dbaogey to piche nahi  jaega.
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelecter(MenuItem item) {

    switch(item.getItemId()){
        case R.id.nav_post:
            sendUserToPostActiviy();
            break;
        case R.id.nav_profile:
            SendUserToProfileActivity();
            break;
        case R.id.nav_home:
            Intent i = new Intent(this , MainActivity.class);
            startActivity(i);
            break;
        case R.id.nav_settings:
            sendUserToSSettingsActivity();
            break;
        case R.id.nav_logout:
            mauth.signOut();
            sendUserToLoginActivity();
            break;
}
}

    private void SendUserToProfileActivity() {

    Intent i = new Intent(MainActivity.this,ProfileActivity.class);
    startActivity(i);

    }

    private void sendUserToSSettingsActivity() {

    Intent i  = new Intent(MainActivity.this,SettingsAvtivity.class);
    startActivity(i);
    }
}
