package com.example.gemswin.screencastrecevertest;
   	




import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;


@SuppressWarnings("deprecation")
public class LoginActivity extends Activity {
	
	
	Button login;
	EditText user;
	String username;
	String port;
	String password;
	EditText pass;
	EditText portedit;
	JSONParser jParser5 = new JSONParser();
	JSONObject json;
	JSONObject json1;
	String	userget,classget;
	String	passget;
	public static String	portget,studClass;

	private PrefManager pref;
	int flag=0;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_reciever);
    //    getActionBar().hide();


        pref = new PrefManager(getApplicationContext());


		 user = (EditText)findViewById(R.id.roll);
         pass = (EditText)findViewById(R.id.pass);
		 portedit = (EditText)findViewById(R.id.port);
        
 
         
        login = (Button) findViewById(R.id.login);
        
        
        login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialogManager alert = new AlertDialogManager();
				ConnectionDetector    cd = new ConnectionDetector(getApplicationContext());

				// Check if Internet present
				if (!cd.isConnectingToInternet()) {
					// Internet Connection is not present
					alert.showAlertDialog(LoginActivity.this,
							"Internet Connection Error",
							"Please connect to working Internet connection", false);
					// stop executing code by return
					return;
				}
				
				     username = user.getText().toString();
				     password = pass.getText().toString();
				     port = 	portedit.getText().toString();

				if(!username.equals("") && !password.equals("") && !port.equals(""))
				     new login().execute();
				else
					Toast.makeText(LoginActivity.this,"Please enter all the fileds.",Toast.LENGTH_SHORT).show();

			}
		});
        
        
        
        
    }
    
    


	private class login extends AsyncTask<String, Void, String> {


		SweetAlertDialog dialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
		protected void onPreExecute() {
			// this.dialog.setMessage("Uploading file...");
			//this.dialog.show();
			dialog.getProgressHelper().setBarColor(Color.parseColor("#7f7fff"));
			dialog.setTitleText("Logging In...");
			dialog.setCancelable(false);
			this.dialog.show();
		}



		@Override
		protected String doInBackground(String... urls)
		{
			WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
			//String ip1 = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

			String    ipPost = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());



			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("roll_number", username));
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("port", port));
			params.add(new BasicNameValuePair("ip", "0.0.0.0"));

			String log = "http://176.32.230.250/anshuli.com/ScreenCast/loginClient.php";
			json = jParser5.makeHttpRequest(log, "POST", params);

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

				JSONArray account= json.getJSONArray("client");
				for(int i = 0; i < account.length(); i++)
				{
					json =account.getJSONObject(i);

					userget=json.getString("ROLL_NUMBER");
					passget= json.getString("PASSWORD");
					portget= json.getString("PORT");

					studClass = json.getString("Class");
					String name = json.getString("NAME");
					pref.setName(name);
					pref.setSerialNo(userget);
					pref.setBatch(studClass);

				}



				if(username.equals(userget) && password.equals(passget) && port.equals(portget))
				{
					//pref.setLogin("client");

						new updateIP().execute();
					}
				else
				{

					Toast.makeText(getApplicationContext(),"Login failed:Check your credentials.",Toast.LENGTH_SHORT).show();

				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),"Network connection Error!!!",Toast.LENGTH_LONG).show();
			}






		}
	}



	private class updateIP extends AsyncTask<String, Void, String> {


		SweetAlertDialog dialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
		protected void onPreExecute() {
			// this.dialog.setMessage("Uploading file...");
			//this.dialog.show();
			dialog.getProgressHelper().setBarColor(Color.parseColor("#7f7fff"));
			dialog.setTitleText("Updating IP...");
			dialog.setCancelable(false);
			this.dialog.show();
		}



		@Override
		protected String doInBackground(String... urls)
		{
			WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
			//String ip1 = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

			String    ipPost = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());



			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("roll_number", username));
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("port", port));
			params.add(new BasicNameValuePair("ip", ipPost));

			String log = "http://176.32.230.250/anshuli.com/ScreenCast/loginClient.php";


			json1 = jParser5.makeHttpRequest(log, "POST", params);






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

				JSONArray account= json1.getJSONArray("client");
				for(int i = 0; i < account.length(); i++)
				{
					json1 =account.getJSONObject(i);
					classget=json1.getString("Class");
					userget=json1.getString("ROLL_NUMBER");
					passget= json1.getString("PASSWORD");
					portget= json1.getString("PORT");

					studClass = json1.getString("Class");
					String name = json1.getString("NAME");
					pref.setClassGET(classget);
					pref.setName(name);
					pref.setSerialNo(userget);
					pref.setBatch(studClass);

				}



				if(username.equals(userget) && password.equals(passget) && port.equals(portget))
				{

					Toast.makeText(getApplicationContext(), "You are succesfully logged in.", Toast.LENGTH_SHORT).show();

					Intent client = new Intent(LoginActivity.this, MainActivity_Reciever.class);

					startActivity(client);

				}
				else
				{

					Toast.makeText(getApplicationContext(),"Login failed:Error in connection...",Toast.LENGTH_SHORT).show();

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
