package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class PetProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private petDBHelper mDbHelper;

    private static final int PET = 100;
    private static  final int PET_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //Static intializer. this is run first time anything is called from this class.
    static {
        // calls to addURI() go here, for all the content uri patterns that this provider
        //should recognize . all paths added to the urimatcher have a corresponding code return
        //when a match is found

        //add 2 contents uri.
        sUriMatcher.addURI(petsContract.CONTENT_AUTHORITY,petsContract.PATH_PETS,PET);

        sUriMatcher.addURI(petsContract.CONTENT_AUTHORITY,petsContract.PATH_PETS+"/#",PET_ID);

    }



    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
         mDbHelper = new petDBHelper(getContext());

        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();


        //hold the result of the query
        Cursor cursor = null;

        // figure out if the uri matcher can match the uri to specify code
        int match = sUriMatcher.match(uri);

        switch (match){
            case PET:

                cursor = database.query(petsContract.petsEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);

                break;

            case PET_ID:
                selection = petsContract.petsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(petsContract.petsEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;

            default:
                throw new IllegalArgumentException("cannot query unknown URI" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;

    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PET:
                return insertPet(uri,contentValues);

            default:throw new IllegalArgumentException("INsertion not supported"+uri);
        }

    }
    private Uri insertPet(Uri uri,ContentValues values){

        String name = values.getAsString(petsContract.petsEntry.COLUMN_PET_NAME);
        if(name == null){
            throw new IllegalArgumentException("Pet require a name");
        }
        Integer gender = values.getAsInteger(petsContract.petsEntry.COLUMN_PET_GENDER);
        if(gender == null || !petsContract.petsEntry.isValidGender(gender)){
            throw new IllegalArgumentException("pet require valid gender");
        }

        Integer weight = values.getAsInteger(petsContract.petsEntry.COLUMN_PET_WEIGHT);
        if(weight != null && weight<0){
            throw new IllegalArgumentException("Pet require valid weight");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(petsContract.petsEntry.TABLE_NAME,null,values);

        if(id == -1){
            Log.e(LOG_TAG,"Failed to insert row for"+uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PET:
                return updatePet(uri,contentValues,selection,selectionArgs);

            case PET_ID:
                selection = petsContract.petsEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri,contentValues,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update not supported for"+ uri);
        }

    }

    private int updatePet(Uri uri,ContentValues contentValues,String selection,String[] selectionArgs){

        if(contentValues.containsKey(petsContract.petsEntry.COLUMN_PET_NAME)) {
            String name = contentValues.getAsString(petsContract.petsEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet Name Requried");
            }
        }

        if(contentValues.containsKey(petsContract.petsEntry.COLUMN_PET_GENDER)) {
            Integer gender = contentValues.getAsInteger(petsContract.petsEntry.COLUMN_PET_GENDER);
            if (gender == null || !petsContract.petsEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet Grender Requried");
            }
        }


        if (contentValues.containsKey(petsContract.petsEntry.COLUMN_PET_WEIGHT)) {
            Integer weight = contentValues.getAsInteger(petsContract.petsEntry.COLUMN_PET_WEIGHT);
            if (weight == null || weight < 0) {
                throw new IllegalArgumentException("Enter Valid weight");
            }
        }

        if(contentValues.size() == 0){
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(petsContract.petsEntry.TABLE_NAME,contentValues,selection,selectionArgs);

        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri,null);

        }

        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted ;



        final int match = sUriMatcher.match(uri);
        switch (match){
            case PET:
                //delete all the selection;
                rowsDeleted= database.delete(petsContract.petsEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case PET_ID:

                //delete a single row given vy id in uri
                selection = petsContract.petsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted =  database.delete(petsContract.petsEntry.TABLE_NAME,selection,selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported"+ uri);
        }

        if (rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsDeleted;

    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match){
            case PET:
                return petsContract.petsEntry.CONTENT_LIST_TYPE;

            case PET_ID:
                return petsContract.petsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unkown Uri" + uri + "with match" +match);
        }
    }









}
