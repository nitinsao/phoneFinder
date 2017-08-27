package com.code.mygps_0;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class GPSService extends android.app.Service{

	public static final long INTERVAL=600;//variable for execute services every 1 minute
	private Handler mHandler=new Handler(); // run on another Thread to avoid crash
	private Timer mTimer=null; // timer handling
	
	
	private static String TAG = "ServiceTag";
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("unsupported Operation");
//		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.d(TAG, "GPSService started");
		Toast.makeText(getApplicationContext(), "GPS Service Started", Toast.LENGTH_LONG).show();

//		this.stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		if(mTimer!=null)
            mTimer.cancel();
    else
            mTimer=new Timer(); // recreate new timer
    mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(),0,INTERVAL);// schedule task

		super.onCreate();
	}
	

	boolean statusOfGPS = false;
	boolean suitablePermission = true;
	
	
	static LocationManager locationManager;// = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	static String msgBody;
	static String senderNumber = "";
	
	private static void setLM()
	{
		locationManager = MainActivity.locationManager;
		msgBody = MainActivity.msgBody;
	}
	
	LocationListener locationListener = new LocationListener() 
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
//                    double latitude = location.getLatitude();
//                    double longitude = location.getLongitude();
//                    double speed = location.getSpeed(); //spedd in meter/minute
//                    speed = (speed*3600)/1000;      // speed in km/minute               Toast.makeText(GraphViews.this, "Current speed:" + location.getSpeed(),Toast.LENGTH_SHORT).show();
                }
       };

       	
       
	
	private class TimeDisplayTimerTask extends TimerTask {
		

        public String checkGPS()
        {
        	try
			{
        		setLM();
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

				statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
				
				Toast.makeText(getApplicationContext(), "GPS Status: " + statusOfGPS, Toast.LENGTH_SHORT).show();
		        
			}catch(IllegalArgumentException iae)
            {
            	Toast.makeText(getApplicationContext(), "GPS provider doesn't exist on this device", Toast.LENGTH_LONG).show();
//            	
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
	        	msgBody = "GPS is disabled!!\nPlease turn it ON.";
//	        	return "GPS is disabled!!\nPlease turn it ON.";
	        }
			else if(suitablePermission)
	        {
	        	statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	        	if(statusOfGPS)
	        	{
	        		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	        		
//	        		lati = location.getLatitude();
//	        		longi = location.getLongitude();
//	        		k = lati + "," + longi;
	        		msgBody = "Lattitude, Longitude : " + location.getLatitude() + "," + location.getLongitude();
//	        		tv.setText(mesg);
	        	}
	        }
			else
			{
				msgBody = "No Suitable Permission for accessing GPS \nTry after sometime";
//        		tv.setText(mesg);
			}
        	return msgBody;
        }
        
        public boolean checkSMS()
        {
        	Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

			
			
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
			    		   if((Integer.parseInt(str) + 300000) <= SystemClock.elapsedRealtime())
			    			   return false;
			    	   }
			    	   else if(cursor.getColumnName(idx).equals("body"))
			    		   if(!cursor.getString(idx).equals("Location"))
			    			   return false;
			    		   else
			    		   {
			    			   msgBody = cursor.getString(idx);
			    			   break;
			    		   }
//			           msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
			       }
			       Toast.makeText(getApplicationContext(), msgData + " || " + SystemClock.elapsedRealtime(), Toast.LENGTH_LONG).show();
			       Toast.makeText(getApplicationContext(), senderNumber + " | " + msgBody, Toast.LENGTH_LONG).show();
			       
			   // } while (cursor.moveToNext());
			} else {
				return false;
			   // empty box, no SMS
			}
			return true;
        }
        
                
        
	    @Override
	    public void run() {
	        // run on another thread
	        mHandler.post(new Runnable() {
	            @Override
	            public void run() {
	            	String sendMsg = "";
	            	// Check SMS at every 1 minute
	            	/*if(checkSMS())
	            	{
	            		for(int i = 0; i < 3; i++)
	            		{
	            			try {
	            			java.lang.Thread.sleep(200);
	            			}catch(Exception e)
	            			{
	            				Toast.makeText(getApplicationContext(), "Sleaping Error", Toast.LENGTH_LONG).show();
	            			}
//		            		}
//	            			sendMsg = sendMsg + "\n" + checkGPS();
	            		}
//	            		sendSms(senderNumber, sendMsg);
	            		Toast.makeText(getApplicationContext(), senderNumber + " :: " + msgBody, Toast.LENGTH_LONG).show();
	            		stopSelf();
//	            		return;
	            	}
	                */
	            	
	            	    
	            }
	        });
	    }
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "GPS Service Destroyed", Toast.LENGTH_LONG).show();
        
		super.onDestroy();
		Log.d(TAG, "GPSService destroyed");
	}
	
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
