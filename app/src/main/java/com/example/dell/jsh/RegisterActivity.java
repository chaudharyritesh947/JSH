package com.example.dell.jsh;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
public class RegisterActivity extends AppCompatActivity {


    private ProgressDialog loadinBar;
    private FirebaseAuth mauth ;
    private EditText userEmail ,userPassword ,userConfirmPassword;
    private Button createAccountButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userEmail= findViewById(R.id.register_email);
        userPassword= findViewById(R.id.register_password);
        userConfirmPassword= findViewById(R.id.register_confirm_password);
        createAccountButton = findViewById(R.id.register_create_account);
        loadinBar =new ProgressDialog(this);

        mauth=FirebaseAuth.getInstance();

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });
    }
//**** ka repersentation kaise hoga .
    private void createNewAccount() {
        String email = userEmail.getText().toString();
        String password=userPassword.getText().toString();
        String confirmPassword = userConfirmPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            //utils will check for the nukll value of edittet.
            Toast.makeText(this,"please write your email ",Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"please write your password ",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(this,"please write your confirm password",Toast.LENGTH_SHORT).show();

        }
        else if(!password.equals(confirmPassword)){
            Toast.makeText(this," confirm password not in match wid confm password ",Toast.LENGTH_SHORT).show();
        }
        else{
            loadinBar.setTitle("Creating new Account");
            loadinBar.setMessage("Please wait while we are creating your new Account");
            loadinBar.show();
            loadinBar.setCanceledOnTouchOutside(true);

            mauth.createUserWithEmailAndPassword(email ,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        sendUserToSetUpActivity();
                        Toast.makeText(RegisterActivity.this,"You are Authenicated successfully ", Toast.LENGTH_SHORT).show();
                        loadinBar.dismiss();
                    }
                    else{

                        String message = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this,"error occured : "+message , Toast.LENGTH_SHORT).show();
                        loadinBar.dismiss();
                    }

                }
            });
        }
    }

    private void sendUserToSetUpActivity() {
        Intent setUpIntent = new Intent(RegisterActivity.this,SetUpActivity.class);
        setUpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //will not be aloud to got to register activity after the auth
        startActivity(setUpIntent);
        finish();
    }




}
