package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.petsContract;

public class PetCursorAdaptor extends CursorAdapter {


    public PetCursorAdaptor(Context context, Cursor c) {
        super(context, c,0/*flags*/);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView breedTextView = (TextView) view.findViewById(R.id.summary);

        int nameColumnIndex = cursor.getColumnIndex(petsContract.petsEntry.COLUMN_PET_NAME);
        int breedColumnIndex = cursor.getColumnIndex(petsContract.petsEntry.COLUMN_PET_BREED);

        String name = cursor.getString(nameColumnIndex);
        String breed = cursor.getString(breedColumnIndex);

        if (TextUtils.isEmpty(breed)){
            breed = context.getString(R.string.unknow_breed);
        }

        nameTextView.setText(name);
        breedTextView.setText(breed);

    }
}
