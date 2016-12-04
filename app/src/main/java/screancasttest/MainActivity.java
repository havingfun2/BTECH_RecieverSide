package screancasttest;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gemswin.screencastrecevertest.AlertDialogManager;
import com.example.gemswin.screencastrecevertest.ConnectionDetector;
import com.example.gemswin.screencastrecevertest.MainActivity_Reciever;
import com.example.gemswin.screencastrecevertest.PrefManager;
import com.example.gemswin.screencastrecevertest.R;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends Activity {
    Button button, canvas, download, sync, resetIP, doubt, openBrowser,attendence;
    String downloadIp, filename;
    String saveIP = "-1", saveRoll = "-1", flag = "0";
    NiftyDialogBuilder dialogRegupdate;
    String itemValue;
    int portFTP;
    JSONParser jParser5 = new JSONParser();
    JSONParser jParserStatus = new JSONParser();
    JSONObject json;
    JSONParser jParser6 = new JSONParser();
    JSONObject json1;
    JSONParser jParser7 = new JSONParser();
    JSONObject json2;
    JSONObject jsonStatus;
    PrefManager pref;

    public static String portString;
    ArrayList<String> planetList;
    ArrayList<String> planetList1;
    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;
    NiftyDialogBuilder dialogDoubt1;


    public static List<String> ipArray;

    GridView grid;
    String[] web = {
            "Browse",
            "Create Canvas",
            "FTP Server Files",
            "Go To Web"

    } ;
    int[] imageId = {
            R.drawable.browse,
            R.drawable.create,
            R.drawable.ftp,
            R.drawable.web
    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlertDialogManager alert = new AlertDialogManager();
        ConnectionDetector    cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(MainActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
        pref = new PrefManager(getApplicationContext());
        pref.setFlag("0");
        planetList = new ArrayList<String>();
        planetList1 = new ArrayList<String>();

        //doubt = (Button) findViewById(R.id.doubt);
       /* sync = (Button) findViewById(R.id.syncIP);*/

        CustomGridIcons adapter = new CustomGridIcons(MainActivity.this, web, imageId);
        grid=(GridView)findViewById(R.id.grid2);
        grid.setAdapter(adapter);
       grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

           @Override
           public void onItemClick(AdapterView<?> parent, View view,
                                   int position, long id) {

               if (web[+position].equals("Browse")) {
                   fileBrowser();
               }
               if (web[+position].equals("Create Canvas")) {
                   canvas();
               }

               if (web[+position].equals("FTP Server Files")) {
                   DialogBox();
               }
               if (web[+position].equals("Go To Web")) {
                   Intent search = new Intent(MainActivity.this, webview.class);
                   startActivity(search);
               }


           }
       });
    }

   /* @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        super.onDestroy();
        new resetIPs().execute();


    }*/

    private void canvas() {
        Intent intent = new Intent(this, CanvasMain.class);
        startActivity(intent);
    }

    private void fileBrowser() {
        Intent intent = new Intent(this, FileBrowser.class);
        startActivity(intent);

    }


    public class Download extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            FTPClient con = null;

            try {
                con = new FTPClient();
                con.connect(downloadIp, portFTP);

                if (con.login("FTPUser", "ankit")) {
                    con.enterLocalPassiveMode();
                    con.setFileType(FTP.BINARY_FILE_TYPE);
                    String data = "/sdcard/Download/" + filename;

                    OutputStream out = new FileOutputStream(new File(data));
                    boolean result = con.retrieveFile(filename, out);
                    out.close();
                    if (result) Log.v("download result", "succeeded");
                    con.logout();
                    con.disconnect();
                }
            } catch (Exception e) {
                Log.v("download result", "failed");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();

        }

        SweetAlertDialog dialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        protected void onPreExecute() {
            // this.dialog.setMessage("Uploading file...");
            //this.dialog.show();
            dialog.getProgressHelper().setBarColor(Color.parseColor("#FFF176"));
            dialog.setTitleText("Loading...");
            dialog.setCancelable(false);
            this.dialog.show();
        }


    }


    protected void DialogBox() {




        dialogRegupdate= NiftyDialogBuilder.getInstance(MainActivity.this);
        dialogRegupdate
                .withTitle("Download From FTP Server")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")
                .withDialogColor("#FFF176")
                .isCancelableOnTouchOutside(false)
                .withDuration(700)
                .withEffect(Effectstype.Flipv)
                .withButton1Text("SUBMIT")
                .withButton2Text("Cancel")
                .setCustomView(R.layout.dialog, dialogRegupdate.getContext())
                .setButton1Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        downloadIp = ((EditText) dialogRegupdate.findViewById(R.id.ip)).getText().toString();
                        portFTP = Integer.parseInt(((EditText) dialogRegupdate.findViewById(R.id.port)).getText().toString());
                        filename = ((EditText) dialogRegupdate.findViewById(R.id.filename)).getText().toString();
                        new Download().execute();
                        dialogRegupdate.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        dialogRegupdate.dismiss();
                    }
                })
                .show();
    }

    private class getIPs extends AsyncTask<String, Void, String> {

        SweetAlertDialog dialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        protected void onPreExecute() {
            // this.dialog.setMessage("Uploading file...");
            //this.dialog.show();
            dialog.getProgressHelper().setBarColor(Color.parseColor("#FFF176"));
            dialog.setTitleText("Loading...");
            dialog.setCancelable(false);
            this.dialog.show();
        }


        @Override
        protected String doInBackground(String... urls) {


            List<NameValuePair> params = new ArrayList<NameValuePair>();
             params.add(new BasicNameValuePair("class", pref.getclass()));
             String log1 = "http://176.32.230.250/anshuli.com/ScreenCast/getIPs.php";
            json1 = jParser6.makeHttpRequest(log1, "POST", params);


            //visible
            return null;


        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {  //gone
            // //System.out.println("RESULT : " + result);

            this.dialog.dismiss();
            try {
                // Checking for SUCCESS TAG


                //Toast.makeText(MainActivity.this, (CharSequence) json, 1).show();
                ipArray = new ArrayList<>();

                JSONArray account = json1.getJSONArray("IPs");
                for (int i = 0; i < account.length(); i++) {
                    json1 = account.getJSONObject(i);

                    String IpString = json1.getString("IP");

                    portString = json1.getString("PORT");

                    if (!IpString.equals("0.0.0.0"))
                        ipArray.add(IpString);

                }

                if (ipArray != null)
                    Toast.makeText(getApplicationContext(), "IPs are synchronised.", Toast.LENGTH_LONG).show();


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Network connection Error!!!", Toast.LENGTH_LONG).show();

            }


        }
    }

/*

    private class saveIP extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //  params.add(new BasicNameValuePair("roll_number", username));

            params.add(new BasicNameValuePair("roll", itemValue));

            String log = "http://176.32.230.250/anshuli.com/ScreenCast/saveIP.php";


            jsonStatus = jParserStatus.makeHttpRequest(log, "POST", params);


            //visible
            return null;


        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {  //gone
            // //System.out.println("RESULT : " + result);


            JSONArray account = null;
            try {
                account = jsonStatus.getJSONArray("IPs");

                for (int i = 0; i < account.length(); i++) {
                    jsonStatus = account.getJSONObject(i);

                    saveIP = jsonStatus.getString("IP");
                    saveRoll = jsonStatus.getString("ROLL_NUMBER");
                    pref.setIP(saveIP);
                    pref.setROLL(saveRoll);

                }
                new updatemasterIp().execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/


/*

    private class recoverIP extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {



            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //  params.add(new BasicNameValuePair("roll_number", username));

            params.add(new BasicNameValuePair("roll", pref.getROLL()));
            params.add(new BasicNameValuePair("ip", pref.getIP()));

            String log = "http://176.32.230.250/anshuli.com/ScreenCast/recoverIP.php";


            jsonStatus = jParserStatus.makeHttpRequest(log, "POST", params);


            //visible
            return null;


        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {  //gone
            // //System.out.println("RESULT : " + result);


            String account = null;
            try {
                account = jsonStatus.getString("client");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (account.equals("FAILED"))
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            else if (account.equals("SUCCESS")) {

                new saveIP().execute();
               // Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(DoubtActivity.this, LoginActivity.class);


            }
        }
    }


*/


    }

