package com.example.gemswin.screencastrecevertest;





import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;


@SuppressWarnings("deprecation")
public class RegActivity extends Activity {


    Button login;
    EditText user,class_reg;
    String username;
    String nameString;
    String password;
    EditText pass;
    EditText name;
    JSONParser jParser5 = new JSONParser();
    String	userget,classget;
    String	passget;
    String	portget;
    JSONObject json;
    private PrefManager pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_reciever);



        pref = new PrefManager(getApplicationContext());


        if(pref.getLogin() != null)
        {
    	   /*Intent reload = new Intent(LoginActivity.this, LoginActivity.class);
			startActivity(reload);
			finish();*/

            if( pref.getLogin().equals("client") )
            {


                // Toast.makeText(getApplicationContext(), "You are succesfully logged in as ADMIN.",Toast.LENGTH_SHORT).show();

                Intent admin = new Intent(RegActivity.this, MainActivity_Reciever.class);
                startActivity(admin);
                finish();

            }


        }


        user = (EditText)findViewById(R.id.roll_reg);
        pass = (EditText)findViewById(R.id.pass_reg);
        name = (EditText)findViewById(R.id.name);
        class_reg = (EditText)findViewById(R.id.class_reg);



        login = (Button) findViewById(R.id.Register);


        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialogManager alert = new AlertDialogManager();
                ConnectionDetector    cd = new ConnectionDetector(getApplicationContext());

                // Check if Internet present
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(RegActivity.this,
                            "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    // stop executing code by return
                    return;
                }



                username = user.getText().toString();
                password = pass.getText().toString();
                nameString = 	name.getText().toString();
                classget = 	class_reg .getText().toString();

                if(!username.equals("") && !password.equals("") && !nameString.equals("") && !classget.equals(""))
                    new login().execute();
                else
                    Toast.makeText(RegActivity.this,"Please enter all the fileds.",Toast.LENGTH_SHORT).show();

              //  new login().execute();
					



            }
        });




    }


    private class login extends AsyncTask<String, Void, String> {


        SweetAlertDialog dialog = new SweetAlertDialog(RegActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        protected void onPreExecute() {
            // this.dialog.setMessage("Uploading file...");
            //this.dialog.show();
            dialog.getProgressHelper().setBarColor(Color.parseColor("#7f7fff"));
            dialog.setTitleText("Registering...");
            dialog.setCancelable(false);
            this.dialog.show();
        }


        @Override
        protected String doInBackground(String... urls)
        {


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("roll_number", username));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("name", nameString));
            params.add(new BasicNameValuePair("class", classget));
            String log = "http://176.32.230.250/anshuli.com/ScreenCast/regClient.php";


            json = jParser5.makeHttpRequest(log, "POST", params);






            //visible
            return null;



        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result)
        {  //gone
            // //System.out.println("RESULT : " + result);

            this.dialog.dismiss();
            try {
                // Checking for SUCCESS TAG




                //Toast.makeText(MainActivity_Reciever.this, (CharSequence) json, 1).show();

                String account= json.getString("client");
                if(account.equals("FAILED"))
                    Toast.makeText(RegActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                else if(account.equals("SUCCESS")) {
                    Toast.makeText(RegActivity.this, "Successfully Registered.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegActivity.this, LoginActivity.class);

                    startActivity(intent);
                    //finish();
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Network connection Error!!!",Toast.LENGTH_LONG).show();
            }






        }
    }




}
