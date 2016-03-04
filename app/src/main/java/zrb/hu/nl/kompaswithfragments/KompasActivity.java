package zrb.hu.nl.kompaswithfragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class KompasActivity extends Activity {
    KompasFragment kf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getString(R.string.orientation).equals("landscape")) finish();
        setContentView(R.layout.activity_kompas);
        kf = (KompasFragment) getFragmentManager().findFragmentById(R.id.kompasfragment);

    }

    @Override
    public void onResume(){
        super.onResume();
        Intent intent = getIntent();
        String naam = intent.getStringExtra("naam");
        kf.setNaam(naam);

        double lon = intent.getDoubleExtra("longitude", 0.0);
        double lat = intent.getDoubleExtra("latitude", 90.0);

        kf.setTargetLocation(lon, lat);
    }


}
