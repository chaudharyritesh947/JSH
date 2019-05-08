package com.example.dell.jsh;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView CommentList;
    private ImageButton postCommentButton;
    private EditText CommentInputText;
    private static String post_key,currentUserId;


    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    private static DatabaseReference posts1,posts2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");



        post_key= getIntent().getExtras().get("PostKey").toString();
        posts1=FirebaseDatabase.getInstance().getReference().child("Posts").child(post_key).child("comments");

        CommentList=(RecyclerView)findViewById(R.id.recyclerView);
        CommentList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentList.setLayoutManager(linearLayoutManager);




        postCommentButton=findViewById(R.id.post_comment_button);
        CommentInputText=findViewById(R.id.comment_input);


        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                        String username = dataSnapshot.child("userName").getValue().toString();

                        Toast.makeText(CommentsActivity.this,username , Toast.LENGTH_SHORT).show();
                        ValidatComment(username);

                        CommentInputText.setText(" ");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Comment,CommentsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Comment,CommentsViewHolder>(
                Comment.class,
                R.layout.all_comments_post,
                 CommentsViewHolder.class,
                posts1

        ){
            @Override
            protected void populateViewHolder(CommentsViewHolder viewHolder, Comment model, int position) {
                viewHolder.setUserName(model.getUserName());
                viewHolder.setComment(model.getComment());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setAdmin(model.getAdmin());
           //     viewHolder.setTimeAdmin(model.getTimeAdmin());
            }
        };
        CommentList.setAdapter(firebaseRecyclerAdapter);
    }
    public static class CommentsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public CommentsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setDate(String date) {
            TextView mydate=mView.findViewById(R.id.comment_data);
            mydate.setText(date);
        }
        String timeone;
        public void setTime(String time) {
            TextView mytime=mView.findViewById(R.id.comment_time);
            timeone=time;
            mytime.setText(time);
        }
        public void setUserName(String userName) {
            TextView myUserName=mView.findViewById(R.id.comment_username);
            myUserName.setText(userName);
        }
        public void setComment(String comment) {
            TextView mycomment=mView.findViewById(R.id.comment_text);
            mycomment.setText(comment);
        }
        static String add="";

        public void setAdmin(String admin) {
                  //  Admin = admin;
     //       final TextView myadmin=mView.findViewById(R.id.admin_comment_text);
       //     myadmin.setText("admin hi hm");
/*

            posts2=FirebaseDatabase.getInstance().getReference().child("Posts").child(post_key);//.child("comments");

            posts2.child("comments").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String admin = dataSnapshot.child("Admin").getValue().toString();
                    myadmin.setText(admin);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
  */        }

/*
        public void setTimeAdmin(String timeAdmin){
            TextView myadminTime = mView.findViewById(R.id.admin_comment_time);
            myadminTime.setText(timeone);
        }

*/


    }

    public void ValidatComment(String UserName){
        String commentText = CommentInputText.getText().toString();
        if(TextUtils.isEmpty(commentText)){
            Toast.makeText(this,"please fill the context before clicking it ",Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calFordDate.getTime());

            String Randam =currentUserId + saveCurrentDate+saveCurrentTime;
            HashMap commentMap=new HashMap();
            commentMap.put("UID" , currentUserId);
            commentMap.put("date" , saveCurrentDate);
            commentMap.put("time" , saveCurrentTime);
            commentMap.put("userName" , UserName);
            commentMap.put("comment" , commentText);
            commentMap.put("Admin"," we are on it");
            posts1.child(Randam).updateChildren(commentMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                Toast.makeText(CommentsActivity.this,"U have comented sucessfully " , Toast.LENGTH_SHORT).show();
                            }
                            else{

                                Toast.makeText(CommentsActivity.this,"error occured try again " , Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

        }



    }
}
