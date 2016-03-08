package zrb.hu.nl.kompaswithfragments.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;

public class MyDBHelper extends SQLiteOpenHelper {
	private static MyDBHelper theInstance;
    class DataObservable extends Observable {
        public void onDataChanged(){
            setChanged();
            notifyObservers();
        }
    };
    private DataObservable theObservable = new DataObservable();
	
	private static final String DATABASE_NAME = "LocatieDB";
	private static final int DATABASE_VERSION = 1;
	
	private static final String TABLE_NAME = "locaties";
	private static final String CREATE_TABLE = 
		"CREATE TABLE " + TABLE_NAME 
		+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
		+ "naam TEXT NOT NULL, "
		+ "longitude REAL NOT NULL, "
		+ "latitude REAL NOT NULL);";

	private MyDBHelper(Context context, String name, CursorFactory factory,
                       int version) {
		super(context, name, factory, version);
	}
	
	public static MyDBHelper getInstance(Context ctx) {
		if(theInstance == null){
			theInstance = new MyDBHelper(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		}
		return theInstance;
	}

   public void addObserver(Observer o){
       theObservable.addObserver(o);
   }

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v("MyDBHelper onCreate", "Creating tabel" + TABLE_NAME);
		try {
			db.execSQL(CREATE_TABLE);
			//insert some  locations
			db.execSQL("INSERT INTO " + TABLE_NAME + "(naam, longitude, latitude) VALUES('Utrecht CS', 5.1099, 52.0897)");
			db.execSQL("INSERT INTO " + TABLE_NAME + "(naam, longitude, latitude) VALUES('Nijenoord 1', 5.1058, 52.1035)");
			db.execSQL("INSERT INTO " + TABLE_NAME + "(naam, longitude, latitude) VALUES('Noordpool', 0, 90)");
			
			
		}
		catch(SQLiteException ex){
			Log.v("Create table exception", ex.getMessage());
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("TaskDBAdapter", "Upgrading from version " + oldVersion + " to " + newVersion + 
				" which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);

	}
	
	public long insertLocatie(String naam, double lon, double lat){
		try {
			SQLiteDatabase db = getWritableDatabase();
			Log.v("LocatieDB", "inserting " + naam + " " + lon + " " + lat);
			ContentValues newTaskValues = new ContentValues();
			newTaskValues.put("naam", naam);
			newTaskValues.put("longitude", lon);
			newTaskValues.put("latitude", lat);
			long l = db.insert("locaties", null, newTaskValues);
            theObservable.onDataChanged();
            return l;
		}
		catch(SQLiteException ex){
			Log.v("MyDBHelper insert", ex.getMessage());
			return -1;
		}
	}
	
	public Cursor getLocaties(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query("locaties",null, null, null, null, null, "naam");
		return c;
	}
	
	public Cursor findLocatiesByNaam(String naam){
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query("locaties", null, "naam = \'" + naam + "\'", null, null, null, null);
		return c;
	}
	
	public void verwijderLocatie(long itemId){
		SQLiteDatabase db = getWritableDatabase();
		db.delete("locaties", "_id="+itemId, null);
        theObservable.onDataChanged();
	}
	

}
