
package com.example.gemswin.screencastrecevertest;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.util.Pair;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.gemswin.screencastrecevertest.ScreenCastLib.DecoderAsyncTask;
import com.example.gemswin.screencastrecevertest.ScreenCastLib.MediaCodecFactory;
import com.example.gemswin.screencastrecevertest.ScreenCastLib.OnVideoSizeChangedListener;
import com.example.gemswin.screencastrecevertest.ScreenCastLib.VideoChunk;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import screancasttest.MainActivity;
import screancasttest.MyBroadcastReceiver;


public class MainActivity_Reciever extends Activity implements ReceiverAsyncTask.ReceiverListener, TextureView.SurfaceTextureListener, OnVideoSizeChangedListener {

	private static final String LOG_TAG = MainActivity_Reciever.class.getSimpleName();
	private ReceiverAsyncTask mTask;
	private final MediaCodecFactory mMediaCodecFactory = new MediaCodecFactory(0,0);
	private DecoderAsyncTask mDecoderAsyncTask;
	TextureView mTextureView;
	private PrefManager pref;
	Button logout;
	Button doubtbutton, viewbutton;
	NiftyDialogBuilder dialogDoubt;
	String doubt;
	String message = "";
	Button submit1,cast;
	EditText name1,class1, doubt1;
	String doubtstring  ;
	String nameString,itemValue;
	ArrayList<String> planetList;
	private ListView mainListView ;
	private ArrayAdapter<String> listAdapter ;
	NiftyDialogBuilder dialogDoubt1;
	JSONParser jParser5 = new JSONParser();
	String	userget,classget;
	String	passget;
	String	portget;
	JSONObject json;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_reciever);


		AlertDialogManager alert = new AlertDialogManager();
		ConnectionDetector    cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity_Reciever.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
		String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
		//TextView text = (TextView) findViewById(R.id.textView);


		planetList = new ArrayList<String>();

		mTextureView = (TextureView)findViewById(R.id.textureView);
		mTextureView.setSurfaceTextureListener(this);
		mTextureView.requestLayout();
		mTextureView.invalidate();
		mTextureView.setOpaque(false);
		logout = (Button) findViewById(R.id.logout);
		//cast = (Button) findViewById(R.id.castAsmaster);


		doubtbutton = (Button) findViewById(R.id.doubt);
		viewbutton = (Button) findViewById(R.id.viewdoubt);

		pref = new PrefManager(getApplicationContext());
		//text.setText(ip);
		logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pref.logout();
				Intent i = new Intent(MainActivity_Reciever.this, LoginActivity.class);
				startActivity(i);
				finish();
			}
		});
		/*cast.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				new allowcast().execute();
			}
		});*/
		viewbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				new viewdoubttask().execute();
			}
		});

		doubtbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//pref.logout(); 															 // LOOK HERE ASH
				/*Intent i = new Intent(MainActivity_Reciever.this,DoubtActivity.class);
				startActivity(i);*/
				DoubtBox();

			}
		});

		mTask = new ReceiverAsyncTask(this);
		mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		Intent intent1 = new Intent(this, MyBroadcastReceiver.class);
		intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 234324243, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (10 * 1000), pendingIntent);


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTask.cancel(true);
	}


	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
		Log.i(LOG_TAG, "onSurfaceTextureAvailable (" + width + "/" + height + ")");
		try {
			final Surface surface = new Surface(surfaceTexture);
			mDecoderAsyncTask = new DecoderAsyncTask(mMediaCodecFactory, surface, this);
			mDecoderAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}  //skip

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

	} //skip

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		return false;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
	}

	@Override
	public void onVideoChunk(VideoChunk chunk) {

        if(mDecoderAsyncTask!=null) {
			mDecoderAsyncTask.addChunk(chunk);
		}
	}//skip

    @Override

    public void onConnectionLost() {
        if(!isDestroyed()){
            mTask = new ReceiverAsyncTask(this);
            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }//skip

    @Override
	public void onVideoSizeChanged(int videoWidth, int videoHeight) {
		// Get the SurfaceView layout parameters
		Log.i(LOG_TAG, "onVideoSizeChange (" + videoWidth + "/" + videoHeight + ")");
		android.view.ViewGroup.LayoutParams lp = mTextureView.getLayoutParams();
		float videoProportion = (float) videoWidth / (float) videoHeight;
		// Get the width of the screen

		int screenWidth = mTextureView.getWidth();
		int screenHeight = mTextureView.getHeight();
		float screenProportion = (float) screenWidth / (float) screenHeight;
		if (videoProportion > screenProportion) {
			//video is wider than our screen
			lp.width = screenWidth;
			lp.height = (int) ((float) screenWidth / videoProportion);
		} else {
			lp.width = (int) (videoProportion * (float) screenHeight);
			lp.height = screenHeight;
		}
		// Commit the layout parameters
		mTextureView.setLayoutParams(lp);

	} // end of videosize  //skip

    @Override

    public void onVideoEnded(List<Pair<Long, Integer>> chunksTimeSeries) {

    }





//jo bhi ho raha idhar ho raha
	protected void DoubtBox () {

		dialogDoubt= NiftyDialogBuilder.getInstance(MainActivity_Reciever.this);
		// dialogReg = new Dialog(FileBrowser.this);
        dialogDoubt
				.withTitle("Ask Doubts")                                  //.withTitle(null)  no title
				.withTitleColor("#FFFFFF")                                  //def
				.withDividerColor("#11000000")
				.withDialogColor("#7f7fff")
				.isCancelableOnTouchOutside(false)
				.withDuration(700)
				.withEffect(Effectstype.Newspager)
				.withButton1Text("SUBMIT")
				.withButton2Text("Cancel")
				.setCustomView(R.layout.activity_doubt_reciever, dialogDoubt.getContext())
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						AlertDialogManager alert = new AlertDialogManager();
						ConnectionDetector    cd = new ConnectionDetector(getApplicationContext());

						// Check if Internet present
						if (!cd.isConnectingToInternet()) {
							// Internet Connection is not present
							alert.showAlertDialog(MainActivity_Reciever.this,
									"Internet Connection Error",
									"Please connect to working Internet connection", false);
							// stop executing code by return
							return;
						}
						doubtstring = doubt1.getText().toString();  //username
						//password = pass.getText().toString();
						//	nameString = 	name1.getText().toString();
						//classget = class1.getText().toString();
						classget = pref.getClassGET();

						new login().execute();

						dialogDoubt.dismiss();


					}
				})
				.setButton2Click(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						dialogDoubt.dismiss();
					}
				})
				.show();
		doubt1 = (EditText)dialogDoubt.findViewById(R.id.doubt_reg1);

	}     //end of doubt box


	protected void DoubtBox1 () {
		// TODO Auto-generated method stub

		dialogDoubt1= NiftyDialogBuilder.getInstance(MainActivity_Reciever.this);
        dialogDoubt1
				.withTitle("View & Delete Doubts")                                  //.withTitle(null)  no title
				.withTitleColor("#FFFFFF")                                  //def
				.withDividerColor("#11000000")
				.withDialogColor("#7f7fff")
				.isCancelableOnTouchOutside(false)
				.withDuration(700)
				.withEffect(Effectstype.Slit)
				.withButton1Text("SUBMIT")
				.withButton2Text("Cancel")
				.setCustomView(R.layout.activity_doubtlist_reciever, dialogDoubt1.getContext())
				.setButton2Click(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						AlertDialogManager alert = new AlertDialogManager();
						ConnectionDetector    cd = new ConnectionDetector(getApplicationContext());

						// Check if Internet present
						if (!cd.isConnectingToInternet()) {
							// Internet Connection is not present
							alert.showAlertDialog(MainActivity_Reciever.this,
									"Internet Connection Error",
									"Please connect to working Internet connection", false);
							// stop executing code by return
							return;
						}
						dialogDoubt1.dismiss();
					}
				})
				.show();
		mainListView = (ListView)dialogDoubt1.findViewById(R.id.mainListView);
		listAdapter = new ArrayAdapter<String>(MainActivity_Reciever.this, R.layout.simplerow_reciever, planetList);
		mainListView.setAdapter(listAdapter);

		mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
										   final int pos, long id) {
				// TODO Auto-generated method stub
				itemValue = (String) mainListView.getItemAtPosition(pos);
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case DialogInterface.BUTTON_POSITIVE:

								new delete().execute();
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								//No button clicked
								break;
						}
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(arg1.getContext());
				builder.setMessage("Are you sure to delete this doubt ?").setPositiveButton("Yes", dialogClickListener)
						.setNegativeButton("No", dialogClickListener).show();

				return true;

			}
		});



	}     //end of doubt box








//start of async task

	private class delete extends AsyncTask<String, Void, String> {

			SweetAlertDialog dialog = new SweetAlertDialog(MainActivity_Reciever.this, SweetAlertDialog.PROGRESS_TYPE);
		protected void onPreExecute() {
			// this.dialog.setMessage("Uploading file...");
			//this.dialog.show();
			dialog.getProgressHelper().setBarColor(Color.parseColor("#7f7fff"));
			dialog.setTitleText("Deleting Doubt...");
			dialog.setCancelable(false);
			this.dialog.show();
		}



		@Override
		protected String doInBackground(String... urls)
		{


			List<NameValuePair> params = new ArrayList<NameValuePair>();
			//  params.add(new BasicNameValuePair("roll_number", username));

			params.add(new BasicNameValuePair("doubtValue", itemValue));
			params.add(new BasicNameValuePair("roll", pref.getSerialNo()));

			String log = "http://176.32.230.250/anshuli.com/ScreenCast/deleteDoubt.php";


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
					Toast.makeText(MainActivity_Reciever.this, "Doubt deleting Failed", Toast.LENGTH_SHORT).show();
				else if(account.equals("SUCCESS")) {
					Toast.makeText(MainActivity_Reciever.this, "Doubt Successfully Deleted.", Toast.LENGTH_SHORT).show();
					//Intent intent = new Intent(DoubtActivity.this, LoginActivity.class);

					//startActivity(intent);
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
	private class allowcast extends AsyncTask<String, Void, String> {

		SweetAlertDialog dialog = new SweetAlertDialog(MainActivity_Reciever.this, SweetAlertDialog.PROGRESS_TYPE);
		protected void onPreExecute() {
			// this.dialog.setMessage("Uploading file...");
			//this.dialog.show();
			dialog.getProgressHelper().setBarColor(Color.parseColor("#7f7fff"));
			dialog.setTitleText("Loading...");
			dialog.setCancelable(false);
			this.dialog.show();
		}






		@Override
		protected String doInBackground(String... urls)
		{


			List<NameValuePair> params = new ArrayList<NameValuePair>();
			//  params.add(new BasicNameValuePair("roll_number", username));


			params.add(new BasicNameValuePair("roll_number", pref.getSerialNo()));

			String log = "http://176.32.230.250/anshuli.com/ScreenCast/allowPermissions.php";


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

				JSONArray account= json.getJSONArray("client");
				for(int i = 0; i < account.length(); i++)
				{
					json =account.getJSONObject(i);



					 doubt = json.getString("STATUS");


				}

				if(doubt.equals("1"))
				{
					Toast.makeText(MainActivity_Reciever.this,"You have permissions to cast your screen.",Toast.LENGTH_LONG).show();
					Intent i = new Intent(MainActivity_Reciever.this, MainActivity.class);
					startActivity(i);

				}else
				{
					Toast.makeText(MainActivity_Reciever.this,"You don't have permissions to cast your screen.",Toast.LENGTH_LONG).show();

				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),"Network connection Error!!!",Toast.LENGTH_LONG).show();
			}






		}
	}
	private class login extends AsyncTask<String, Void, String> {

		SweetAlertDialog dialog = new SweetAlertDialog(MainActivity_Reciever.this, SweetAlertDialog.PROGRESS_TYPE);
		protected void onPreExecute() {
			// this.dialog.setMessage("Uploading file...");
			//this.dialog.show();
			dialog.getProgressHelper().setBarColor(Color.parseColor("#7f7fff"));
			dialog.setTitleText("Sending Doubt...");
			dialog.setCancelable(false);
			this.dialog.show();
		}






		@Override
		protected String doInBackground(String... urls)
		{


			List<NameValuePair> params = new ArrayList<NameValuePair>();
			//  params.add(new BasicNameValuePair("roll_number", username));
			params.add(new BasicNameValuePair("doubt", doubtstring));
			params.add(new BasicNameValuePair("name", pref.getName()));
			params.add(new BasicNameValuePair("class", classget));
			params.add(new BasicNameValuePair("roll", pref.getSerialNo()));

			String log = "http://176.32.230.250/anshuli.com/ScreenCast/setDoubt.php";


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
					Toast.makeText(MainActivity_Reciever.this, "Doubt Sending Failed", Toast.LENGTH_SHORT).show();
				else if(account.equals("SUCCESS")) {
					Toast.makeText(MainActivity_Reciever.this, "Successfully Submitted.", Toast.LENGTH_SHORT).show();
					//Intent intent = new Intent(DoubtActivity.this, LoginActivity.class);

					//startActivity(intent);
					//finish();
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),"Network connection Error!!!",Toast.LENGTH_LONG).show();
			}






		}
	}    // end of async

//start of async task 2

	private class viewdoubttask extends AsyncTask<String, Void, String> {

		SweetAlertDialog dialog = new SweetAlertDialog(MainActivity_Reciever.this, SweetAlertDialog.PROGRESS_TYPE);
		protected void onPreExecute() {
			// this.dialog.setMessage("Uploading file...");
			//this.dialog.show();
			dialog.getProgressHelper().setBarColor(Color.parseColor("#7f7fff"));
			dialog.setTitleText("Opening Doubt List...");
			dialog.setCancelable(false);
			this.dialog.show();
		}






		@Override
		protected String doInBackground(String... urls)
		{


			List<NameValuePair> params = new ArrayList<NameValuePair>();
			//  params.add(new BasicNameValuePair("roll_number", username));
			//params.add(new BasicNameValuePair("doubt", doubtstring));
			params.add(new BasicNameValuePair("name", pref.getName()));
			params.add(new BasicNameValuePair("roll", pref.getSerialNo()));

			String log = "http://176.32.230.250/anshuli.com/ScreenCast/seeDoubtList.php";  // change php file name


			json = jParser5.makeHttpRequest(log, "POST", params);



			try {
				// Checking for SUCCESS TAG
				planetList.clear();
				JSONArray account= json.getJSONArray("client");
				for(int i = 0; i < account.length(); i++)
				{
					json =account.getJSONObject(i);



					String doubt = json.getString("DOUBT");

					planetList.add(doubt);
				}



			}
			catch (Exception e)
			{
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),"Network connection Error!!!",Toast.LENGTH_LONG).show();
			}


			//visible
			return null;



		}
		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result)
		{  //gone
			// //System.out.println("RESULT : " + result);


			this.dialog.dismiss();

               DoubtBox1();






		}
	}








}
