package com.tutorazadi.CalorieAnnihilator;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataSource
{
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = { SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_CALORIES, SQLiteHelper.COLUMN_SUGAR };

    public DataSource(Context context)
    {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Calories createEntry(float calorieAmount, float sugarAmount)
    {
        Log.d("", "Sugar amount: " + sugarAmount);
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_CALORIES, calorieAmount);
        values.put(SQLiteHelper.COLUMN_SUGAR, sugarAmount);

        long insertId = database.insert(SQLiteHelper.TABLE_CALORIE_ANNIHILATOR, null,
                values);
        Cursor cursor = database.query(SQLiteHelper.TABLE_CALORIE_ANNIHILATOR,
                allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();

        Calories newAmount = cursorToComment(cursor);
        cursor.close();

        return newAmount;
    }

    public void deleteEntry(Calories comment)
    {
        long id = comment.getId();
        System.out.println("Deleted id: " + id);
        database.delete(SQLiteHelper.TABLE_CALORIE_ANNIHILATOR, SQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Calories> getAllEntries()
    {
        List<Calories> calories = new ArrayList<Calories>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_CALORIE_ANNIHILATOR,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Calories entry = cursorToComment(cursor);
            calories.add(entry);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return calories;
    }

    private Calories cursorToComment(Cursor cursor)
    {
        Calories entry = new Calories();
        entry.setId(cursor.getLong(0));
        entry.setCalories(cursor.getFloat(1));
        entry.setSugar(cursor.getFloat(2));
        return entry;
    }
}