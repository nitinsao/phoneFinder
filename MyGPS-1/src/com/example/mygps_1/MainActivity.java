package com.example.mygps_1;


import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {


	double longi, lati;
	String mesg = "";
	String k = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final TextView tv = (TextView) findViewById(R.id.textView1);
		Button b1 = (Button) findViewById(R.id.button1);
		
		tv.setText("GPS Co-Ordinates are : ");
		final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		final LocationListener locationListener = new LocationListener() 
        {

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }

                    @Override
                    public void onLocationChanged(Location location) {
                        // TODO Auto-generated method stub
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        double speed = location.getSpeed(); //spedd in meter/minute
                        speed = (speed*3600)/1000;      // speed in km/minute               Toast.makeText(GraphViews.this, "Current speed:" + location.getSpeed(),Toast.LENGTH_SHORT).show();
                    }
                };

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

//        ----------------------------------------------------------------------------
        
                b1.setOnClickListener(new OnClickListener() {
        			
        			@Override
        			public void onClick(View v) {

        				
        				try {
        					Thread.sleep(2000);
        				} catch (InterruptedException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        				
        				boolean statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        				
        				Toast.makeText(getApplicationContext(), "GPS Status: " + statusOfGPS, Toast.LENGTH_LONG).show();
        		        if(!statusOfGPS) //LocationManager.GPS_PROVIDER)
        		        {
        		        	mesg = "GPS is disabled!!\nPlease turn it ON.";
        		        	tv.setText(mesg);
        		        	k = "";
        		        }
        		        else
        		        {
        		        	statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        		        	if(statusOfGPS)
        		        	{
        		        		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        		        		
        		        		lati = location.getLatitude();
        		        		longi = location.getLongitude();
        		        		k = lati + "," + longi;
        		        		mesg = "Lattitude, Longitude : " + lati + "," + longi;
        		        		tv.setText(mesg);
        		        	}
        		        }
        			}
        		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
