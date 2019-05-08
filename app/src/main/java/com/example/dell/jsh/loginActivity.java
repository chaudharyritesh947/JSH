package com.example.dell.jsh;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginActivity extends AppCompatActivity {



    DatabaseHelper mydb;

    private ProgressDialog loadinBar;
    private Button loginButton;
    private EditText userEmail,userPassword;
    private TextView needNewAccountLink;
    private ImageButton i1;
    //private FloatingActionButton fabtn;
    private FirebaseAuth mauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        needNewAccountLink = findViewById(R.id.register_account_link);
        userEmail= findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);
        loginButton =findViewById(R.id.login_button);
        loadinBar =new ProgressDialog(this);
        mydb = new DatabaseHelper(this);

        mauth=FirebaseAuth.getInstance();

        needNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToRegisterActivity();
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowingUserToLogin();

            }
        });
       // fabtn=findViewById(R.id.floatingActionButton1);

    }

    private void allowingUserToLogin() {

    String email = userEmail.getText().toString();
    String password= userPassword.getText().toString();
    if(TextUtils.isEmpty(email)){
        Toast.makeText(loginActivity.this,"PLease write ur email" , Toast.LENGTH_SHORT).show();
    }
    else if(TextUtils.isEmpty(password)){
            Toast.makeText(loginActivity.this,"PLease write ur passwrd" , Toast.LENGTH_SHORT).show();
        }
    else {

        loadinBar.setTitle("Login");
        loadinBar.setMessage("Please wait.... while we are allowing you to enter into your  Account");
        loadinBar.show();
        loadinBar.setCanceledOnTouchOutside(true);


        mauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){
                sendUserToMainActivity();
                addData();
                Toast.makeText(loginActivity.this,"Logged in successsfully" , Toast.LENGTH_SHORT).show();
                loadinBar.dismiss();

            }
            else{
                String message = task.getException().getMessage();
                Toast.makeText(loginActivity.this,"error occured : "+message , Toast.LENGTH_SHORT).show();
                loadinBar.dismiss();
            }
        }
    });
    }
    }
    private void sendUserToMainActivity() {
        Intent mainIntenet= new Intent(loginActivity.this , MainActivity.class);
        mainIntenet.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntenet);
        finish();
    }

    private  void sendUserToRegisterActivity() {
        Intent registerIntent = new Intent(loginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
        //finish();
    }



    public void addData() {
        boolean isInserted =  mydb.insertData(userEmail.getText().toString());
        if(isInserted==true){
            Toast.makeText(loginActivity.this,"Data Inserted " , Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(loginActivity.this,"Some error has occured " , Toast.LENGTH_SHORT).show();
        }
    }
    public void seeMyData(View view) {
        Cursor res = mydb.getAlldata();
        if(res.getCount()==0){
            showMessage("Error" , "Error 404 nothing Found !!!");
            //iska mtlb ek v row nahi hai.
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()){
            //index column ke adhar pr hongi like jo index phele aaega uska uska index 0 hoga
            buffer.append("Login Mail : "+ res.getString(0)+"\n");
                  }
        showMessage(" History " , buffer.toString());
    }



    public void showMessage(String title , String dataAll){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(dataAll);
        builder.show();
    }

    public void AdminView(View view) {
        Intent registerIntent = new Intent(loginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);

    }
}
