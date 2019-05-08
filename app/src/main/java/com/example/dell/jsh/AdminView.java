package com.example.dell.jsh;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminView extends AppCompatActivity {

    EditText ed1 , ed2;
    Button b1 ;
    private  String admin;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

         admin =    "AOADMINpu2005";
         password = "AOADMINlpu@2005";

    ed1 = findViewById(R.id.name);
    ed2=findViewById(R.id.psd);


    }

    public void gotoAdmin(View view) {


        String name1 = ed1.getText().toString();
        String psd = ed2.getText().toString();

        if(TextUtils.isEmpty(name1)){
            Toast.makeText(this, "Name field is empty !!!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(psd)){
            Toast.makeText(this, "Password field is empty !!!", Toast.LENGTH_SHORT).show();
        }
        else {


            if(name1.equals(admin)  && psd.equals(password)){
                Intent i = new Intent(AdminView.this , MainActivity.class);
                i.putExtra("flag" , "1");
                startActivity(i);
                Toast.makeText(this,"Welcome Admin!!! "  ,Toast.LENGTH_SHORT).show();
            }

        }


    }
}
