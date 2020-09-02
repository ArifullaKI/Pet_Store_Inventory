package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class petDBHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = petDBHelper.class.getSimpleName();

    private static final int database_ver = 1;

    private static final String databaseName ="shelter.db";

    public petDBHelper(Context context){
        super(context,databaseName,null,database_ver);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " +  petsContract.petsEntry.TABLE_NAME +
        "(" + petsContract.petsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + petsContract.petsEntry.COLUMN_PET_NAME + " TEXT ," +
                petsContract.petsEntry.COLUMN_PET_BREED+" TEXT," +
                petsContract.petsEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL,"+
                 petsContract.petsEntry.COLUMN_PET_WEIGHT+" INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
}
