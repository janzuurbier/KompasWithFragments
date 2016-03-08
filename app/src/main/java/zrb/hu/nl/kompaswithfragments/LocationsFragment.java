package zrb.hu.nl.kompaswithfragments;

import zrb.hu.nl.kompaswithfragments.data.MyDBHelper;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class LocationsFragment extends ListFragment implements OnItemClickListener,  LoaderManager.LoaderCallbacks<Cursor> {

    MyDBHelper mdbh;
    //Cursor theCursor;
    SimpleCursorAdapter adapter;



    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mdbh = MyDBHelper.getInstance(getActivity());

        ListView lv = getListView();
        lv.setOnItemClickListener(this);
        registerForContextMenu(lv);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v  = inflater.inflate(R.layout.buttonpaneltoonlocaties, null);
        lv.addFooterView(v);

        //theCursor = mdbh.getLocaties();
        String[] from = {"naam"};
        int[] to = { R.id.textView1};
        adapter = new SimpleCursorAdapter(getActivity(), R.layout.listitemlocatie, null, from , to, 0 );
        this.setListAdapter(adapter);

        Button b1 = (Button) v.findViewById(R.id.addbutton);
        b1.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                AddDialogFragment alf = new AddDialogFragment();
                alf.show(getFragmentManager(), "dialog");
            }
        });
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        open(arg2);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.locationmenu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.itemopen :
                open(info.position);
                return true;
            case R.id.itemremove:
                mdbh.verwijderLocatie(info.id);
                //refresh();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void open(int pos){
        Cursor theCursor = adapter.getCursor();
        if (theCursor.moveToPosition(pos)) {
            String naam = theCursor.getString(theCursor.getColumnIndex("naam"));
            double lon = theCursor.getDouble(theCursor.getColumnIndex("longitude"));
            double lat = theCursor.getDouble(theCursor.getColumnIndex("latitude"));
            MainActivity main = (MainActivity)getActivity();
            if (getResources().getString(R.string.orientation).equals("portrait")) {
                main.switchToKompas();
            }
            main.setKompasTarget(naam, lon, lat);
        }
        else{
            Log.w("ToonLocaties", "positie " + pos + " niet gevonden.");
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
