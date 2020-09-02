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

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import com.example.android.pets.data.petDBHelper;
import com.example.android.pets.data.petsContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EXISTING_PET_LOADER = 0;

    private Uri mCurrentPetUri;

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = petsContract.petsEntry.GENDER_UNKNOWN;

    Cursor cursor;

    private boolean mPetHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentPetUri = intent.getData();


        if(mCurrentPetUri== null) {
            setTitle(getString(R.string.new_pet));

            // Invalidate the option menu, so the "delete" menu optioncan be nidden.
            //(It Doesnt make sense to delete a pet that hasnt been created yet.)
            invalidateOptionsMenu();
        }
            else  {
                setTitle(getString(R.string.Editing_Pet));


            getLoaderManager().initLoader(EXISTING_PET_LOADER,null,this);
        }



        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);


        setupSpinner();
    }



    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = petsContract.petsEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = petsContract.petsEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = petsContract.petsEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = petsContract.petsEntry.GENDER_UNKNOWN; // Unknown
            }
        });
    }



    private void savePet(){

        String nameString=mNameEditText.getText().toString().trim();
        String breedString = mBreedEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();


    if(mCurrentPetUri==null && TextUtils.isEmpty(nameString)&& TextUtils.isEmpty(breedString) &&
            TextUtils.isEmpty(weightString) && mGender == petsContract.petsEntry.GENDER_UNKNOWN)
    { return; }

        ContentValues values = new ContentValues();
        values.put(petsContract.petsEntry.COLUMN_PET_NAME,nameString);
        values.put(petsContract.petsEntry.COLUMN_PET_BREED,breedString);
        values.put(petsContract.petsEntry.COLUMN_PET_GENDER,mGender);
        //
        int weightInt = 0;
        if(!TextUtils.isEmpty(weightString)){
            weightInt=Integer.parseInt(weightString);
        }
        values.put(petsContract.petsEntry.COLUMN_PET_WEIGHT,weightInt);
        if(mCurrentPetUri==null)
    {

        Uri newUri = getContentResolver().insert(petsContract.petsEntry.CONTENT_URI,values);

        if(newUri==null){

            Toast.makeText(this, getString(R.string.editor_insert_pet_failed),Toast.LENGTH_SHORT).show();

    }else{
            Toast.makeText(this,"insert pet succesfull",Toast.LENGTH_SHORT).show();
        }

    } else {

        int rowsAffected = getContentResolver().update(mCurrentPetUri,values,null,null);

        if(rowsAffected == 0){

            Toast.makeText(this,getString(R.string.editor_update_pet_failed),Toast.LENGTH_SHORT).show();
        }else{

            Toast.makeText(this,getString(R.string.editor_update_pet_successful),Toast.LENGTH_SHORT).show();

        }

    }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * *This method is called after invalidateOptionMenu(), so that the
     * *menu can be updated(some menu Items can be hidden or made visible).

     */
   @Override
   public boolean onPrepareOptionsMenu(Menu menu){
       super.onPrepareOptionsMenu(menu);
       //if this is a new pet, hide the "delete" menu.
       if(mCurrentPetUri==null){
           MenuItem menuItem = menu.findItem(R.id.action_delete);
           menuItem.setVisible(false);
       }
       return true;
   }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                savePet();

                finish();

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Respond to a click on the "deleter" menu option
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                //If the pet hasnt changed, continue with navigating up to parent activity.
                // which is the {@link CatalogActivity}.
                if(!mPetHasChanged) {
                    // Navigate back to parent activity (CatalogActivity)
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                //Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed(){
        // If the pet hasn't changed, continue with handling back button press
        if(!mPetHasChanged){
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listner to handle the user confirming thatchanges should be discarded.

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "Discard" button,close the current activity.
                        finish();
                    }
                };
        //Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);

    }

   public Loader<Cursor> onCreateLoader(int i ,Bundle bundle)
    {
// Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                petsContract.petsEntry._ID,
                petsContract.petsEntry.COLUMN_PET_NAME,
                petsContract.petsEntry.COLUMN_PET_BREED,
                petsContract.petsEntry.COLUMN_PET_GENDER,
                petsContract.petsEntry.COLUMN_PET_WEIGHT
        };
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentPetUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {


            if (cursor == null || cursor.getCount() < 1) {
                return;
            }



        if(cursor.moveToFirst()){

            // Find the column s of all the pets attribte that we are intrested in
            int nameColumnIndex = cursor.getColumnIndex(petsContract.petsEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(petsContract.petsEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(petsContract.petsEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(petsContract.petsEntry.COLUMN_PET_WEIGHT);


            //Extrtact out of the value from the cursor for the given column index.
            String name = cursor.getString(nameColumnIndex);
            String breed= cursor.getString(breedColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);
            int weight = cursor.getInt(weightColumnIndex);

            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(Integer.toString(weight));

            switch (gender){
                case petsContract.petsEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;

                    case petsContract.petsEntry.GENDER_FEMALE:
                        mGenderSpinner.setSelection(2);
                        break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

        //If the loader is invalidated , clear out all the data from the input fields

        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener){
        // Create an AlertDialog.Builder and seet the messsage, and click listeners
        // for the positive and negative buttons on the dialogs.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard,discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // TODO: Implement this method
        if(mCurrentPetUri!=null){
            int rowsDeleted = getContentResolver().delete(mCurrentPetUri,null,null);

            if(rowsDeleted == 0){
                Toast.makeText(this,getString(R.string.editor_delete_pet_failed),Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this,getString(R.string.editor_delete_pet_successful),Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }


}