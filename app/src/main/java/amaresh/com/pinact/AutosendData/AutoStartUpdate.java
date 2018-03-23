package amaresh.com.pinact.AutosendData;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import amaresh.com.pinact.Home;

/**
 * Created by jagat on 2/22/2017.
 */
public class AutoStartUpdate extends Service implements android.location.LocationListener {
    //    MainActivity mainActivity;
    int user_id,server_status;
    Home obj;
    LocationManager locationManager;
    String latitude,longitude,provider,name,id,phone_number;





    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        //Toast.makeText(AutoStartUpdate.this,"hello",Toast.LENGTH_LONG).show();
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(provider);

            // Initialize the location fields
            if (location != null) {
                System.out.println("Provider " + provider + " has been selected.");
                onLocationChanged(location);
            } else {
                // latituteField.setText("Location not available");
                //longitudeField.setText("Location not available");
            }

        }
        else {
            Toast.makeText(AutoStartUpdate.this,"Allow to GPS",Toast.LENGTH_LONG).show();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(provider, 4000, 1, this);
        }
        else {
            Toast.makeText(AutoStartUpdate.this,"Allow to GPS",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // do your jobs here
        obj=new Home();
        obj.sendlatlongtoserver(latitude,longitude,obj.getImei());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            latitude = String.valueOf(lat);
            longitude = String.valueOf(lng);
            Calendar calander;
            SimpleDateFormat simpledateformat;
             calander = Calendar.getInstance();
            simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
           String time = simpledateformat.format(calander.getTime());
           // if(time.contentEquals())
            //  sendlatlongtoserver();
            String address="No Address found";

            obj=new Home();
            obj.sendlatlongtoserver(latitude,longitude,obj.getImei());
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String address="No Address found";
        obj=new Home();
        obj.sendlatlongtoserver(latitude,longitude,obj.getImei());
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

}
