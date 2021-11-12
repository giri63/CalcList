package com.aks.calclist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBListItem extends SQLiteOpenHelper {
    private static final String TAG = "DBListItem";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CalcKirani.db";
    private static final String TABLE_NAME = "ListOfItemList";


    private static final String KEY_LIST_ID = "list_id";
    private static final String KEY_LIST_NAME = "list_name";
    private static final String KEY_LIST_TIME = "list_time";
    private static final String KEY_LIST_COUNT = "list_count";
    private static final String KEY_LIST_AMOUNT = "list_amount";
    private static final String KEY_LIST_ITEMS = "list_items"; //comma seperated

    private int INDEX_KEY_LIST_ID = 0;
    private int INDEX_KEY_LIST_NAME = 0;
    private int INDEX_KEY_LIST_TIME = 0;
    private int INDEX_KEY_LIST_COUNT = 0;
    private int INDEX_KEY_LIST_AMOUNT = 0;
    private int INDEX_KEY_LIST_ITEMS = 0;

    public DBListItem(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_LIST_ID + " TEXT PRIMARY KEY,"
                + KEY_LIST_NAME + " TEXT,"
                + KEY_LIST_COUNT + " INTEGER,"
                + KEY_LIST_AMOUNT + " INTEGER,"
                + KEY_LIST_TIME + " TEXT,"
                + KEY_LIST_ITEMS + " TEXT)";
        Log.d(TAG, "CREATE_TABLE_QUERY:" + CREATE_TABLE_QUERY);
        db.execSQL(CREATE_TABLE_QUERY);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    Boolean addItem(ListItemEntry item) {
        Log.d(TAG, "addItem item:" + item.getListID());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LIST_ID, item.getListID());

        values.put(KEY_LIST_NAME, item.getListName());

        values.put(KEY_LIST_COUNT, item.getListCount());

        values.put(KEY_LIST_AMOUNT, item.getListAmount());

        values.put(KEY_LIST_TIME, item.getListTime());

        List<String> items = item.getListItems();

        StringBuilder str = new StringBuilder("");

        for (String eachstring : items) {
            str.append(eachstring).append(" , ");
        }
        values.put(KEY_LIST_ITEMS, str.toString());

        long row_id = db.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addContact values:" + values + " status:" + row_id);
        db.close();
        if(row_id == -1) {
           return false;
        }
        return true;
    }

    Boolean index_init = false;
    void initIndex(Cursor cursor) {
        if(index_init == false) {
            index_init = true;

            INDEX_KEY_LIST_ID = cursor.getColumnIndex(KEY_LIST_ID);
            INDEX_KEY_LIST_NAME = cursor.getColumnIndex(KEY_LIST_NAME);
            INDEX_KEY_LIST_TIME = cursor.getColumnIndex(KEY_LIST_TIME);
            INDEX_KEY_LIST_COUNT = cursor.getColumnIndex(KEY_LIST_COUNT);
            INDEX_KEY_LIST_AMOUNT = cursor.getColumnIndex(KEY_LIST_AMOUNT);
            INDEX_KEY_LIST_ITEMS = cursor.getColumnIndex(KEY_LIST_ITEMS);
        }
    }

    ListItemEntry getItem(String listId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, KEY_LIST_ID + "=?",
                new String[] { listId }, null, null, null, null);
        if (cursor.moveToFirst()) {
            return getListEntry(cursor);
        }
        return null;
    }

    private ListItemEntry getListEntry(Cursor cursor) {
        initIndex(cursor);

        ListItemEntry item = new ListItemEntry();
        item.setListID(cursor.getString(INDEX_KEY_LIST_ID));
        item.setListName(cursor.getString(INDEX_KEY_LIST_NAME));
        item.setListCount(cursor.getInt(INDEX_KEY_LIST_COUNT));
        item.setListAmount(cursor.getInt(INDEX_KEY_LIST_AMOUNT));

        item.setListTime(cursor.getString(INDEX_KEY_LIST_TIME));

        String items = cursor.getString(INDEX_KEY_LIST_ITEMS);
        List<String> list = new ArrayList<String>(Arrays.asList(items.split(" , ")));
        item.setListItems(list);

        Log.d(TAG, "getListEntry list:" + list.toString());

        return item;
    }

    // code to get all contacts in a list view
    public List<ListItemEntry> getAllItems() {
        List<ListItemEntry> contactList = new ArrayList<ListItemEntry>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                contactList.add(getListEntry(cursor));
            } while (cursor.moveToNext());
        }
        return contactList;
    }

    // code to update the single contact
    public Boolean updateItem(ListItemEntry item) {
        Log.d(TAG, "updateItem item:" + item.getListID());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LIST_ID, item.getListID());

        values.put(KEY_LIST_NAME, item.getListName());

        values.put(KEY_LIST_COUNT, item.getListCount());

        values.put(KEY_LIST_AMOUNT, item.getListAmount());

        values.put(KEY_LIST_TIME, item.getListTime());

        List<String> items = item.getListItems();

        StringBuilder str = new StringBuilder("");

        for (String eachstring : items) {
            str.append(eachstring).append(" , ");
        }
        values.put(KEY_LIST_ITEMS, str.toString());

        int result = db.update(TABLE_NAME, values, KEY_LIST_ID + " = ?",
                new String[] { item.getListID() });

        Log.d(TAG, "updateItem values:" + values + " result:" + result);
        db.close();
        if(result == -1) {
            return false;
        }
        return true;
    }

    public void deleteListEntry(String listID) {
        SQLiteDatabase db = this.getWritableDatabase();
        int status = db.delete(TABLE_NAME, KEY_LIST_ID + " = ?",
                new String[] { listID });
        Log.d(TAG, "deleteListEntry listID:" + listID + " status:" + status);
        db.close();
    }

    public int getEntriesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }
}
