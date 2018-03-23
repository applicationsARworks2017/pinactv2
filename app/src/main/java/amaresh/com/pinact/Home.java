package amaresh.com.pinact;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class Home extends AppCompatActivity implements android.location.LocationListener {
    LocationManager locationManager;
    String provider, latitude, longitude, server_response;
    int server_status;
    private TelephonyManager mTelephonyManager;
    TextView Latitude, Longitude;
    Button gomap;
    String deviceid;
    Boolean isGPSEnabled, isNetworkEnabled, canGetLocation;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 111;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 100;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getDeviceImei();

        Latitude = (TextView) findViewById(R.id.homelat);
        Longitude = (TextView) findViewById(R.id.homelng);
        gomap = (Button) findViewById(R.id.button);
        gomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, Trackme.class);
                startActivity(i);
            }
        });
        startService(new Intent(this, AutoStartUpdate.class));


        // Get the location manager

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_ACCESS_COARSE_LOCATION);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling


            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(provider);

            // Initialize the location fields
            if (location != null) {
                System.out.println("Provider " + provider + " has been selected.");
                onLocationChanged(location);
            } else {
                Latitude.setText("Device can't founf the loc");
                Longitude.setText("Device can't founf the loc");
            }

        } else {
          //  Toast.makeText(Home.this, "Allow to GPS", Toast.LENGTH_LONG).show();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(provider);

            // Initialize the location fields
            if (location != null) {
                System.out.println("Provider " + provider + " has been selected.");
                onLocationChanged(location);
            } else {
                Latitude.setText("Device can't founf the loc");
                Longitude.setText("Device can't founf the loc");
            }

        } else {
         //   Toast.makeText(Home.this, "Allow to GPS", Toast.LENGTH_LONG).show();
        }

        getgpsloc();


    }

    public void getgpsloc() {
        ///gps code start

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            // no GPS Provider and no network provider is enabled
        } else {   // Either GPS provider or network provider is enabled

            // First get location from Network Provider
            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        latitude = String.valueOf(lat);
                        longitude = String.valueOf(lon);
                        this.canGetLocation = true;
                        Latitude.setText("Network :" + latitude);
                        Longitude.setText("Network :" + longitude);
                        sendlatlongtoserver(latitude, longitude, deviceid);
                    }
                }
            }// End of IF network enabled

            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null)
                {
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null)
                    {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        latitude=String.valueOf(lat);
                        longitude=String.valueOf(lon);
                        this.canGetLocation = true;
                        Latitude.setText("Network :"+latitude);
                        Longitude.setText("Network :"+longitude);
                        sendlatlongtoserver(latitude,longitude,deviceid);
                    }
                }

            }// End of if GPS Enabled
        }// End of Either GPS provider or network provider is enabled



        //gps code end
    }

    private void getDeviceImei() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
            } else {

                mTelephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
                deviceid = mTelephonyManager.getDeviceId();

            }
        } else {
            mTelephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            deviceid = mTelephonyManager.getDeviceId();

        }
    }
    public String  getImei() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
            } else {

                mTelephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
                deviceid = mTelephonyManager.getDeviceId();

            }
        } else {
            mTelephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            deviceid = mTelephonyManager.getDeviceId();

        }
        return deviceid;
    }
    private void reeatcalltostatus() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        getgpsloc();
                        try {
                            if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            }
                            if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {
                                locationManager.requestLocationUpdates(provider, 800, 1,Home.this);
                            }
                            else {
                               // Toast.makeText(Home.this,"Allow to GPS",Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 1000);
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        reeatcalltostatus();
       /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(provider, 800, 1, this);
        }
        else {
            Toast.makeText(Home.this,"Allow to GPS",Toast.LENGTH_LONG).show();
        }*/
    }
    @Override
    public void onLocationChanged(Location location) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            latitude = String.valueOf(""+lat);
            longitude = String.valueOf(""+lng);
            Latitude.setText(latitude);
            Longitude.setText(longitude);
            sendlatlongtoserver(latitude,longitude,deviceid);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled provider " + provider,
                Toast.LENGTH_SHORT).show();
        // gps turnes on

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
        // gps turnes off
    }

    @Override
    public void onRequestPermissionsResult ( int requestCode, String[] permissions,
                                             int[] grantResults){
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getDeviceImei();
        }
    }
    public  void sendlatlongtoserver( String latitude, String longitude, String deviceid){
        this.latitude= latitude;
        this.longitude= longitude;
        this.deviceid=deviceid;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
       // DateFormat date = new SimpleDateFormat("HH:mm a");
        DateFormat date = new SimpleDateFormat("HH");
// you can get seconds by adding  "...:ss" to it
        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

        String localTime = date.format(currentLocalTime);
        int nowtime=Integer.parseInt(localTime);

        //oncomment this for timimg
       // if(nowtime >= 8 && nowtime <= 20)



        if (getNetworkConnectivityStatus(Home.this)) {
            uploadpos upload = new uploadpos();
            upload.execute( latitude, longitude, deviceid);
        }
        else{
            Toast.makeText(this,"No Internet",Toast.LENGTH_LONG).show();

        }


    }

    private boolean getNetworkConnectivityStatus(Context context ) {
        if(context == null){
            return false;
        }

        boolean isConnected = false;;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if((info != null) && (info.isConnected())){
            isConnected = true;
        }

        return isConnected;
    }

    public class uploadpos extends AsyncTask<String, Void, Void> {
        private static final String TAG = "Upload Lat Long";
        // private ProgressDialog progressDialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* if (progressDialog == null) {
                progressDialog = ProgressDialog.show(getApplication(), "Loading", "Please wait...");
            }*/
            // onPreExecuteTask();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                String _lat = params[0];
                String _lng = params[1];
                String _imei = params[2];

                InputStream in = null;
                int resCode = -1;

                String link ="http://citybuz.com/pinact/index.php/api/mobileapp/get_addresslat/format/json";
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setAllowUserInteraction(false);
                conn.setInstanceFollowRedirects(true);
                conn.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("lat", _lat)
                        .appendQueryParameter("lng", _lng)
                        .appendQueryParameter("imei", _imei);
                //.appendQueryParameter("deviceid", deviceid);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                resCode = conn.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    in = conn.getInputStream();
                }
                if (in == null) {
                    return null;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String response = "", data = "";

                while ((data = reader.readLine()) != null) {
                    response += data + "\n";
                }

                Log.i(TAG, "Response : " + response);

                /**
                 *
                 {
                 "response_status": "1",
                 "msg": "here is data is stored"
                 }
                 * */

                if (response != null && response.length() > 0) {
                    JSONObject res = new JSONObject(response.trim());
                    server_status = res.optInt("response_status");
                    if(server_status == 1){
                        server_response="Updated Successfully";
                    }
                    else{
                        server_response="Error in Uploading";

                    }

                    // server_response = res.optString("message");


                    // int status = res.optInt("login_status");
                    //  message = res.optString("message");
                }

                return null;
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void user) {
            super.onPostExecute(user);
            shownotification();
            if (server_status!=1){
                Toast.makeText(Home.this,server_response,Toast.LENGTH_LONG).show();
            }
            else{
               // String as=server_response;
            //    reeatcalltostatus();
               // Toast.makeText(Home.this,server_response,Toast.LENGTH_LONG).show();;

            }

        }
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void shownotification(){
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher) // notification icon
                .setContentTitle("Pinact is Running") // title for notification
                .setContentText("Kindely Be Online for update yourself") // message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(this, Home.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);

        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}



