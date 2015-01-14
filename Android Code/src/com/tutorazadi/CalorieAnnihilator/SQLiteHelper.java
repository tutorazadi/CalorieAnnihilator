package com.tutorazadi.CalorieAnnihilator;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

// Taken from http://www.vogella.com/tutorials/AndroidSQLite/article.html

public class SQLiteHelper extends SQLiteOpenHelper
{
    public static final String TABLE_COMMENTS = "comments";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COMMENT = "comment";

    private static final String DATABASE_NAME = "commments.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table Comments(_id integer primary key autoincrement, "
            + "comment text not null);";

    public SQLiteHelper(Context context)
    {
        super(context, "comments.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS Comments");
        onCreate(db);
    }
}