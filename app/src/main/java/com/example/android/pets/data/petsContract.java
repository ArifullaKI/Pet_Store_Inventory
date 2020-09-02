package com.example.android.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class petsContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private petsContract(){ }

    public static final String CONTENT_AUTHORITY = "com.example.android.pets";

    public  static final Uri BASE_CONTENT_URI =Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_PETS ="pets";


    public static final class petsEntry implements BaseColumns{

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;


        public final static String TABLE_NAME = "pets";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PET_NAME ="Name";

        public static final String COLUMN_PET_BREED = "Breed";

        public final static String COLUMN_PET_GENDER = "Gender";

        public final static String COLUMN_PET_WEIGHT = "Weight";

        public static final int GENDER_UNKNOWN = 0;

        public final static int GENDER_MALE = 1;

        public final static int GENDER_FEMALE = 2;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PETS);

        public static boolean isValidGender(int gender){
            if(gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE){
                return true;
            }
            return false;
        }

    }

}
