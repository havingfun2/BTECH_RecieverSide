package com.example.gemswin.screencastrecevertest;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


@SuppressWarnings("deprecation")
public class HomeActivity extends Activity {

    Button loginHome;
    Button regHome;
    Button search;
    private PrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_reciever);
        pref = new PrefManager(getApplicationContext());

        //String pending_status =  DataHolderClass.getInstance().getDistributor_id();

        if (pref.getLogin() != null) {
    	   /*Intent reload = new Intent(LoginActivity.this, LoginActivity.class);
			startActivity(reload);
			finish();*/

            if (pref.getLogin().equals("client")) {


                // Toast.makeText(getApplicationContext(), "You are succesfully logged in as ADMIN.",Toast.LENGTH_SHORT).show();

                Intent admin = new Intent(HomeActivity.this, MainActivity_Reciever.class);
                startActivity(admin);
               // finish();

            }


        }


        loginHome = (Button) findViewById(R.id.loginHome);


        loginHome.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);

                startActivity(intent);
                //finish();

            }
        });


        regHome = (Button) findViewById(R.id.regHome);


        regHome.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Intent intent = new Intent(HomeActivity.this, RegActivity.class);

                startActivity(intent);
               // finish();


            }
        });


    }

}
