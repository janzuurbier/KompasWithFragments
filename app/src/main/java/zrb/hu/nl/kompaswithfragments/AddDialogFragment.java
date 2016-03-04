package zrb.hu.nl.kompaswithfragments;



import zrb.hu.nl.kompaswithfragments.data.MyDBHelper;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddDialogFragment extends DialogFragment implements LocationListener {

    MyDBHelper mdbh;
    EditText naamVeld;
    LocationManager manager;
    String locationprovider;
    Location location;
    //private RefreshListener parent;

    public AddDialogFragment() {

    }

   /* @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            parent = (RefreshListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement RefreshListener");
        }
    }*/


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mdbh = MyDBHelper.getInstance(getActivity());
        manager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        locationprovider = manager.getBestProvider(criteria, true);
        location = manager.getLastKnownLocation(locationprovider);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_location, container, false);
        naamVeld = (EditText) v.findViewById(R.id.editText1);

        Button save = (Button)v.findViewById(R.id.button1);
        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Editable naam = naamVeld.getText();
                final String nm = naam.toString();
                if(location == null){
                    Toast.makeText(getActivity(), "locatie niet gevonden", Toast.LENGTH_SHORT);
                }
                else {
                    Log.v("nieuwe locatie-OK", "location: " + location);
                    new Thread() { public void run() {
                        mdbh.insertLocatie(nm, location.getLongitude(), location.getLatitude());
                        dismiss();
                     }
                    }.start();

                }

            }
        });

        Button cancel = (Button) v.findViewById(R.id.button2);
        cancel.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;

    }


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void onResume(){
        super.onResume();
        manager.requestLocationUpdates(locationprovider, 0,0, this);
    }

    public void onPause(){
        manager.removeUpdates(this);
        super.onPause();
    }
}


