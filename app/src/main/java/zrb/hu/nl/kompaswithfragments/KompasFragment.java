package zrb.hu.nl.kompaswithfragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;

import zrb.hu.nl.kompaswithfragments.KompasView;
import zrb.hu.nl.kompaswithfragments.R;

public class KompasFragment extends Fragment implements LocationListener, SensorEventListener  {
	
	LocationManager lmanager;
	SensorManager smanager;
	PowerManager.WakeLock wl;
	
	String locationprovider = "gps";
	//private Sensor mAccelerometer;
	//private Sensor mMagnetometer;
	private Sensor orientatiesensor; 
	
	String targetNaam;
    Location targetLocation;
	float targetRichting;
	float deviceRichting;
	
	KompasView kv;
	TextView tv;
	TextView rv;
    TextView nv;
	
	NumberFormat nf ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.kompas, null);
        kv = (KompasView) v.findViewById(R.id.viewkompas);
        tv = (TextView) v.findViewById(R.id.textViewDistance);
        rv = (TextView) v.findViewById(R.id.textViewDirection);
        nv = (TextView) v.findViewById(R.id.textViewLocation);
        if(savedInstanceState != null) {
            targetNaam = savedInstanceState.getString("naam");
            targetLocation = savedInstanceState.getParcelable("targetlocation");
        }
        if(targetNaam != null)nv.setText(targetNaam);
        if (targetLocation != null)  onLocationChanged(lmanager.getLastKnownLocation(locationprovider));
        return v;
    }
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        

        
        nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        
        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Kompas");
        
        lmanager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        locationprovider = lmanager.getBestProvider(criteria, true);
        
        smanager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        orientatiesensor = smanager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

	 }

    public void setNaam(String nm){
        targetNaam = nm;
        if (nv != null) nv.setText(nm);

    }

    public void setTargetLocation(double lon, double lat){
        targetLocation = new Location(locationprovider);
        targetLocation.setLongitude(lon);
        targetLocation.setLatitude(lat);
        //if (lmanager != null) onLocationChanged(lmanager.getLastKnownLocation(locationprovider));

    }
	 
    public void onResume(){
    	super.onResume();
    	wl.acquire();
    	lmanager.requestLocationUpdates(locationprovider, 0,0, this);
    	//smanager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL); 
    	//smanager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    	smanager.registerListener(this, orientatiesensor, SensorManager.SENSOR_DELAY_NORMAL);
        nv.setText(targetNaam);
        onLocationChanged(lmanager.getLastKnownLocation(locationprovider));
    }
    
    public void onPause(){
    	lmanager.removeUpdates(this);
    	smanager.unregisterListener(this);
    	wl.release();
    	super.onPause();
    }

	@Override
	public void onLocationChanged(Location currentLocation) {
        try {
            targetRichting = currentLocation.bearingTo(targetLocation);
            kv.setRichting(targetRichting - deviceRichting);

            float afstand = currentLocation.distanceTo(targetLocation);
            if (afstand < 1000)
                tv.setText(" " + nf.format(afstand) + " m");
            else {
                afstand /= 1000f;
                tv.setText(" " + afstand + " km");
            }


            rv.setText("koers  " + nf.format(targetRichting) + "�");
        }
        catch(NullPointerException e){
            if(currentLocation == null)Log.e("onLocationChanged", "currentLocation is null");
            if(targetLocation == null)Log.e("onLocationChanged", "targetLocation is null");
        }
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		int type = event.sensor.getType();
		/* werkt niet op Samsung Galaxy Ace
		float[] mags = {0f,0f,0f};		
		if(type == Sensor.TYPE_MAGNETIC_FIELD){
			for(int i = 0; i < 3; i++){mags[i] = event.values[i];}
		}
		float[] accels = {0f,0f,0f};
		if(type == Sensor.TYPE_ACCELEROMETER){
			for(int i = 0; i < 3; i++){accels[i] = event.values[i];}
		}
		float[] R = new float[9];
		float[] I = new float[9];
		boolean b = SensorManager.getRotationMatrix(R, I, accels, mags);
		Log.d("Kompas", "getRotationMatrixSucces: " + b); //false
		float[] attitude = {0f, 0f, 0f};
		SensorManager.getOrientation(R, attitude);
		deviceRichting = attitude[0];
		kv.setRichting(targetRichting - deviceRichting);
		*/
		if (type == Sensor.TYPE_ORIENTATION){
			float[] x = event.values;
			deviceRichting = x[0];
			kv.setRichting(targetRichting - deviceRichting);	
		}
	}

    @Override
    public void onSaveInstanceState(Bundle state){
        state.putString("naam", targetNaam);
        state.putParcelable("targetlocation", targetLocation);
    }

}
