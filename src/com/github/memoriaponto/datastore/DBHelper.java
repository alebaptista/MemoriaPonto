package com.github.memoriaponto.datastore;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.xml.sax.DTDHandler;

import com.github.memoriaponto.model.Evento;


import android.content.Context;
import android.database.Cursor;

public class DBHelper extends SQLiteOpenHelper {
	private final SimpleDateFormat df = new SimpleDateFormat("dd/MM HH:mm");

    private SQLiteDatabase db;
    private static final int DATABASE_VERSION = 1;
    private static final String DB_NAME = "sample.db";
    private static final String TABLE_NAME = "registro";

    /**
     * Constructor
     * @param context the application context
     */
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }
    
    /**
     * Called at the time to create the DB.
     * The create DB statement
     * @param the SQLite DB
     */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
                "create table " + TABLE_NAME + " (_data text not null primary key , " + " descricao text not null) ");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
    /**
     * The Insert DB statement
     * @param id the friends id to insert
     * @param name the friend's name to insert
     */
    public void insert(Date dataHora , String descricao) {
        db.execSQL("INSERT INTO "+ TABLE_NAME +"('_data', 'descricao') values ('"
                + df.format(dataHora) + "', '"
                + descricao + "')");
    }
    
    /**
     * Select All that returns an ArrayList
     * @return the ArrayList for the DB selection
     */
    public ArrayList<Evento> listSelectAll() {
        ArrayList<Evento> list = new ArrayList<Evento>();
        // "strftime('%Y-%m-%d %H:%M', _data ) "
        Cursor cursor = this.db.query(TABLE_NAME, new String[] { "_data", "descricao" }, 
null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Evento f = new Evento(null, null);
                try {
                	String strDH = cursor.getString(0) ;
					f.dataHora =   df.parse( strDH );
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                f.descricao = cursor.getString(1);
                list.add(f);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }
}
