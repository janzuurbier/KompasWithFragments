package zrb.hu.nl.kompaswithfragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity  {

    Fragment fList;
    KompasFragment fKompas;

    @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    fList = getFragmentManager().findFragmentById(R.id.listfragment);
    fKompas = new KompasFragment();
    if(getResources().getString(R.string.orientation).equals("landscape")) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.kompascontainer, fKompas);
        transaction.commit();
    }
}



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void switchToKompas() {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        transaction.replace(R.id.maincontainer, fKompas);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setKompasTarget(String naam, double lon, double lat){
        fKompas.setNaam(naam);
        fKompas.setTargetLocation(lon, lat);
    }
}
