package sergey.zhuravel.faceverification.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper implements IDatabaseHandler {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "usersManager";
    private static final String TABLE_CONTACTS = "users";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LIST = "list";
    private static final String KEY_BASE_PATH = "basePath";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_LIST + " TEXT," + KEY_BASE_PATH + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        onCreate(db);
    }

    @Override
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LIST, String.valueOf(user.getFeuter()));
        values.put(KEY_NAME, user.getName());
        values.put(KEY_BASE_PATH, user.getBasePath());
        Log.e("TEST_DB", String.valueOf(user.getFeuter()));
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    @Override
    public User getUser(int id) {

        return null;
    }

    @Override
    public List<User> getAllUser() {
        List<User> contactList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(0)));
                String fe = cursor.getString(cursor.getColumnIndex(KEY_LIST));
                fe = fe.replace("[", "");
                fe = fe.replace("]", "");
                List<String> myList = new ArrayList<String>(Arrays.asList(fe.split(",")));
                List<Float> list = new ArrayList<>();
                for (String s : myList) {
                    list.add(Float.valueOf(s));
                }
                user.setFeuter(list);
                user.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                user.setBasePath(cursor.getString(cursor.getColumnIndex(KEY_BASE_PATH)));
                contactList.add(user);
            } while (cursor.moveToNext());
        }

        return contactList;
    }

    @Override
    public boolean deleteAllUser() {

        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            db.delete(TABLE_CONTACTS, null, null);
            db.close();
            return true;
        }
        return false;
    }
}
