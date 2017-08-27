package com.code.mygps_0;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	/*
	private static final String[] INITIAL_PERMS={
	    Manifest.permission.ACCESS_FINE_LOCATION,
	    
	  };
	  private static final String[] LOCATION_PERMS={
	      Manifest.permission.ACCESS_FINE_LOCATION
	  };
	  private static final int INITIAL_REQUEST=1337;
	  private static final int LOCATION_REQUEST=INITIAL_REQUEST+3;
	  private TextView location;
	*/
	
	double longi, lati;
	String mesg = "";
	static String msgBody = "";
	String k = "";
	static LocationManager locationManager;
	
	public static void send()
	{
		return;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final TextView tv = (TextView) findViewById(R.id.textView1);
		Button b1 = (Button) findViewById(R.id.button1);
		
		tv.setText("GPS Co-Ordinates are : ");
		
//		final LocationManager 
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
		startService(new Intent(this, GPSService.class));
		
		/*if (!canAccessLocation() || !canAccessContacts()) {
		      requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
		    }
		*/
		

        
//        --------------------------------------------------------------
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

                try
                {
                	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }catch(IllegalArgumentException iae)
                {
                	Toast.makeText(getApplicationContext(), "GPS provider doesn't exist on this device", Toast.LENGTH_LONG).show();
                }catch(SecurityException se)
                {
                	Toast.makeText(getApplicationContext(), "GPS has no Looper : Exception", Toast.LENGTH_LONG).show();
                }catch(RuntimeException rte)
                {
                	Toast.makeText(getApplicationContext(), "No suitable permission is present for GPS access", Toast.LENGTH_LONG).show();
                }

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
				
				boolean statusOfGPS = false;
				boolean suitablePermission = true;
				try
				{
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

					statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
					
					Toast.makeText(getApplicationContext(), "GPS Status: " + statusOfGPS, Toast.LENGTH_LONG).show();
			        
				}catch(IllegalArgumentException iae)
                {
                	Toast.makeText(getApplicationContext(), "GPS provider doesn't exist on this device", Toast.LENGTH_LONG).show();
                }catch(SecurityException se)
                {
                	Toast.makeText(getApplicationContext(), "GPS has no Looper : Exception", Toast.LENGTH_LONG).show();
                }catch(RuntimeException rte)
                {
                	Toast.makeText(getApplicationContext(), "No suitable permission is present for GPS access", Toast.LENGTH_LONG).show();
                	suitablePermission = false;
                }
				if(!statusOfGPS) //LocationManager.GPS_PROVIDER)
		        {
		        	msgBody = mesg = "GPS is disabled!!\nPlease turn it ON.";
		        	tv.setText(mesg);
		        	k = "";
		        }
				else if(suitablePermission)
		        {
		        	statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		        	if(statusOfGPS)
		        	{
		        		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		        		
		        		lati = location.getLatitude();
		        		longi = location.getLongitude();
		        		k = lati + "," + longi;
		        		msgBody = mesg = "Lattitude, Longitude : " + lati + "," + longi;
		        		tv.setText(mesg);
		        	}
		        }
				else
				{
					msgBody = mesg = "No Suitable Permission for accessing GPS \nTry after sometime";
	        		tv.setText(mesg);
				}
			}
		});
		
		
//		-----------------------------ClipBoard------------------------
		//ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		
		Button b2 = (Button) findViewById(R.id.button2);
		b2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!k.isEmpty())
				{
					ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					ClipData clip = ClipData.newPlainText("location",k);
					// Set the clipboard's primary clip.
					clipboard.setPrimaryClip(clip);
					Toast.makeText(getApplicationContext(), k + " Copied", Toast.LENGTH_LONG).show();
				}
				else
					Toast.makeText(getApplicationContext(), mesg, Toast.LENGTH_LONG).show();
			}
		});
		
//		---------------------Read SMS-------------------------
		
		Button b3 = (Button) findViewById(R.id.button3);
		
		
		b3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

				String senderNumber = "";
				
				if (cursor.moveToFirst()) { // must check the result to prevent exception
				    //do {
				       String msgData = "";
				       for(int idx=0;idx<cursor.getColumnCount();idx++)
				       {
				    	   if(cursor.getColumnName(idx).equals("address"))
				    		   senderNumber = "" + cursor.getString(idx);
				    	   else if(cursor.getColumnName(idx).equals("date"))
				    	   {
				    		   msgData = "Date: " + cursor.getString(idx);
				    		   String str = "" + cursor.getString(idx);
				    		   Toast.makeText(getApplicationContext(), cursor.getString(idx) + " || " + SystemClock.uptimeMillis(), Toast.LENGTH_LONG).show();
						       //----------------------------------------------------------------------Use GetTimeOfDay-----------------------------------------
//				    		   if((Integer.parseInt(str) + 300000) <= SystemClock.elapsedRealtime())
//				    			   return;
				    	   }
				    	   else if(cursor.getColumnName(idx).equals("body"))
				    		   if(!cursor.getString(idx).equals("Location"))
				    			   return;
				           msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
				       }
				       // use msgData
				       Toast.makeText(getApplicationContext(), msgData, Toast.LENGTH_LONG).show();
				       Toast.makeText(getApplicationContext(), senderNumber + " " + msgBody, Toast.LENGTH_LONG).show();
//				       sendSms(senderNumber, msgBody);
				   // } while (cursor.moveToNext());
				} else {
				   // empty box, no SMS
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
	/*
	 public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
	    //updateTable();

	    switch(requestCode) {
	      
	      case LOCATION_REQUEST:
	        if (canAccessLocation()) {
	          doLocationThing();
	        }
	        else {
	          bzzzt();
	        }
	        break;
	    }
	  }
	  

	  private boolean canAccessLocation() {
	    return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
	  }

	  private boolean hasPermission(String perm) {
		    return(PackageManager.PERMISSION_GRANTED==checkCallingOrSelfPermission(perm));
		  }
	  private void bzzzt() {
		    Toast.makeText(this, "bzzzt", Toast.LENGTH_LONG).show();
		  }

	  private void doLocationThing() {
		    Toast.makeText(this, "LocationThing", Toast.LENGTH_SHORT).show();
		  }
		  
		  */
	
	
	
	private void sendSms(String phonenumber,String message)
	{
		try {    
			Toast.makeText(this, "Sending Message", Toast.LENGTH_LONG).show();
		    SmsManager.getDefault().sendTextMessage(phonenumber, null,    
		    message, null, null);    
		    Toast.makeText(this, "Message sent", Toast.LENGTH_LONG).show();
		} catch (Exception e) {    
		    AlertDialog.Builder alertDialogBuilder = new    
		    AlertDialog.Builder(this);    
		    AlertDialog dialog = alertDialogBuilder.create();    
		    dialog.setMessage(e.getMessage());    
		    dialog.show();    
		}
	}
	
 
}
