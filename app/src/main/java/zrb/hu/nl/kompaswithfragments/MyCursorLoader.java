package zrb.hu.nl.kompaswithfragments;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

import java.util.Observable;
import java.util.Observer;

import zrb.hu.nl.kompaswithfragments.data.MyDBHelper;

/**
 * Created by JZuurbier on 22-5-2015.
 */
public class MyCursorLoader extends CursorLoader implements Observer {

    private MyDBHelper mdbh;

    public MyCursorLoader(Context c){
        super(c);
        mdbh = MyDBHelper.getInstance( c);
        mdbh.addObserver(this);
    }

    @Override
    public Cursor loadInBackground() {
        return mdbh.getLocaties();
    }

    @Override
    public void update(Observable observable, Object data) {
        onContentChanged();
    }
}