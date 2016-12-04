package screancasttest;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.gemswin.screencastrecevertest.R;
import com.example.gemswin.screencastrecevertest.ScreenCastLib.VideoChunk;
import com.example.gemswin.screencastrecevertest.PrefManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class MyService extends Service  {

   public PrefManager pref;
    public SenderAsyncTask mSenderAsyncTask;
    JSONParser jparser = new JSONParser();
    JSONParser jparser2 = new JSONParser();
    JSONObject json2;
    List<String> ipArray ;
    // private static final String LOG_TAG = SenderAsyncTask.class.getSimpleName();
    // public static   List<String> mIp;
    Context context;
    // public static DataOutputStream[] dataOut;
    LinkedBlockingDeque<VideoChunk> mVideoChunks = new LinkedBlockingDeque<VideoChunk>();

    public MyService() {
	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	@Override
    public void onCreate() {
     //8   Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();
        pref	= new PrefManager(getApplication());
    }
 
    @Override
    public void onStart(Intent intent, int startId) {
    	// For time consuming an long tasks you can launch a new thread here...
       // Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();

        try {



            new getNewIP().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_LONG).show();

        }
       
   
    }
 
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        super.onDestroy();
        
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void triggerNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

        int icon = R.drawable.image;
        System.currentTimeMillis();
        getApplicationContext();
        CharSequence contentTitle = "Hurray!!!";
        CharSequence contentText = "Master privileges have been granted.";
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

       new reset().execute();

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(icon)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE);


        Notification notification = new Notification.BigTextStyle(builder).bigText(contentText).build();

        // and this
        final int HELLO_ID = 1;
        mNotificationManager.notify(HELLO_ID, notification);


    }
    private class reset extends AsyncTask<String, Void, String> {



        @Override
        protected String doInBackground(String... urls)
        {


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // params.add(new BasicNameValuePair("ip", downloadupdate));

            params.add(new BasicNameValuePair("class", pref.getBatch()));
            //  params.add(new BasicNameValuePair("name", nameString));

            String log1 = "http://176.32.230.250/anshuli.com/ScreenCast/resetStatus.php";


            json2 = jparser2.makeHttpRequest(log1, "POST", params);






            //visible
            return null;



        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result)
        {  //gone
            // //System.out.println("RESULT : " + result);




     }
    }
    public class getNewIP extends AsyncTask<String, Void, String> {
        JSONObject json;




        /** progress dialog to show user that the backup is processing. */
        /** application context. */

        protected void onPreExecute() {

        }



        @Override
        protected String doInBackground(String... urls)
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("roll", pref.getSerialNo()));

            String url = "http://176.32.230.250/anshuli.com/ScreenCast/getMessage.php";



            json = jparser.makeHttpRequest(url, "POST", params);


            //JSONArray jsonArray;


            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result)
        {


            try {
                // Checking for SUCCESS TAG

                //forjson.clear();

                String	heading="";
                String message="";
                //Toast.makeText(MainActivity.this, (CharSequence) json, 1).show();

                JSONArray account= json.getJSONArray("client");
                for(int i = 0; i < account.length(); i++)
                {
                    json =account.getJSONObject(i);

                    heading=json.getString("STATUS");
                }
                if((heading.equals("1")))
                    triggerNotification();


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }



}
