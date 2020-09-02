/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.pets.data.PetProvider;
import com.example.android.pets.data.petDBHelper;
import com.example.android.pets.data.petsContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    PetCursorAdaptor mCursorAdapter;
    private  petDBHelper mDbHelper;;

    int PET_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);


        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView petListView = (ListView) findViewById(R.id.List);

        View emptyView = findViewById(R.id.empty_view);

        petListView.setEmptyView(emptyView);

        mCursorAdapter = new PetCursorAdaptor(this,null);
                petListView.setAdapter(mCursorAdapter);

        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intentForView = new Intent(CatalogActivity.this,EditorActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(petsContract.petsEntry.CONTENT_URI,id);

                intentForView.setData(currentPetUri);

                startActivity(intentForView);
            }
        });



        getLoaderManager().initLoader(PET_LOADER,null,this);
    }



    /*
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

     */


    /*
    private void displayDatabaseInfo() {

        //Perform this raw SQl query "selsect * from pets"
        String[] projection = {
                petsContract.petsEntry._ID,
                petsContract.petsEntry.COLUMN_PET_NAME,
                petsContract.petsEntry.COLUMN_PET_BREED,
                petsContract.petsEntry.COLUMN_PET_GENDER,
                petsContract.petsEntry.COLUMN_PET_WEIGHT
        };


        Cursor cursor = getContentResolver().query(
                petsContract.petsEntry.CONTENT_URI, projection, null, null, null);

        ListView petListView = (ListView) findViewById(R.id.List);

        PetCursorAdaptor adaptor = new PetCursorAdaptor(this, cursor);

        petListView.setAdapter(adaptor);
        */




      /*  try{

            displayView.setText("Number of rows in pets database table: " + cursor.getCount()+"\n"+"\n");

            displayView.append(petsContract.petsEntry._ID +" - " + petsContract.petsEntry.COLUMN_PET_NAME+" - "
            + petsContract.petsEntry.COLUMN_PET_BREED+ " - " + petsContract.petsEntry.COLUMN_PET_GENDER + " - " +petsContract.petsEntry.COLUMN_PET_WEIGHT+"\n");

            int idColumnIndex = cursor.getColumnIndex(petsContract.petsEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(petsContract.petsEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(petsContract.petsEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(petsContract.petsEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(petsContract.petsEntry.COLUMN_PET_WEIGHT);

            while(cursor.moveToNext()){

                int currentID= cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentBreed =  cursor.getString(breedColumnIndex);
                int currentGender = cursor.getInt(genderColumnIndex);
                int currentWeight = cursor.getInt(weightColumnIndex);

                displayView.append("\n"+currentID+" - "+currentName+" - "+currentBreed+" - "+currentGender+" - "+currentWeight+"\n");


            }
        }finally {
            cursor.close();
        }*/



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertPet() {

        ContentValues values = new ContentValues();
        values.put(petsContract.petsEntry.COLUMN_PET_NAME, "Toto");
        values.put(petsContract.petsEntry.COLUMN_PET_BREED, "Terrier");
        values.put(petsContract.petsEntry.COLUMN_PET_GENDER, 1);
        values.put(petsContract.petsEntry.COLUMN_PET_WEIGHT, 7);

        Uri newUri = getContentResolver().insert(petsContract.petsEntry.CONTENT_URI, values);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                // Do nothing for now
                insertPet();
                //displayDatabaseInfo();

                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Defines a projection that specifies the column from the table we care about
        String[] projection = {
                petsContract.petsEntry._ID,
                petsContract.petsEntry.COLUMN_PET_NAME,
                petsContract.petsEntry.COLUMN_PET_BREED};
        //Loader will execute the Content Provider a query method on background
        return new CursorLoader(this,
                petsContract.petsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

    }




    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    mCursorAdapter.swapCursor(null);
    }

    private void deleteAllPets(){
        int rowsDeleted = getContentResolver().delete(petsContract.petsEntry.CONTENT_URI,null,null);
        Log.v("CatlogActivity",rowsDeleted + "rows Deleted from pet database");
    }
}