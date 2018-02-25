package com.example.android.bakingtime.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Sylvana on 2/14/2018.
 */

public class RecipesContentProvider extends ContentProvider {
    private RecipesDbHelper mRecipesDbHelper;
    public static final int RECIPES = 229;
    public static final int STEPS = 230;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(RecipesDataContract.AUTHORITY, RecipesDataContract.PATH_RECIPES, RECIPES);
        uriMatcher.addURI(RecipesDataContract.AUTHORITY,RecipesDataContract.PATH_STEPS, STEPS);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mRecipesDbHelper = new RecipesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mRecipesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor returnCursor;
        switch (match){
            case RECIPES:
                returnCursor = db.query(RecipesDataContract.RecipeEntry.RECIPES_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case STEPS:
                returnCursor = db.query(RecipesDataContract.StepEntry.STEPS_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mRecipesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match){
            case RECIPES:
                long id = db.insert(RecipesDataContract.RecipeEntry.RECIPES_TABLE_NAME,null,contentValues);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(RecipesDataContract.RecipeEntry.RECIPES_CONTENT_URI,id);
                }else{
                    throw new android.database.SQLException("Failed to insert content values into: " + uri);
                }
                break;
            case STEPS:
                long id2 = db.insert(RecipesDataContract.StepEntry.STEPS_TABLE_NAME,null,contentValues);
                if(id2 > 0){
                    returnUri = ContentUris.withAppendedId(RecipesDataContract.StepEntry.STEPS_CONTENT_URI,id2);
                }else{
                    throw new android.database.SQLException("Failed to insert content values into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri,@NonNull ContentValues[] values){
        final SQLiteDatabase db = mRecipesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsInserted = 0;
        switch (match){
            case RECIPES:
                db.beginTransaction();
                try{
                    for(ContentValues value: values){
                        long id = db.insert(RecipesDataContract.RecipeEntry.RECIPES_TABLE_NAME,null,value);
                        if(id != -1){
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                if(rowsInserted > 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowsInserted;
            case STEPS:
                db.beginTransaction();
                try{
                    for(ContentValues value:values){
                        long id = db.insert(RecipesDataContract.StepEntry.STEPS_TABLE_NAME,null,value);
                        if(id != -1){
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                if(rowsInserted > 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowsInserted;
            default:
                return super.bulkInsert(uri,values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mRecipesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int recipesDeleted;
        switch(match){
            case RECIPES:
                recipesDeleted = db.delete(RecipesDataContract.RecipeEntry.RECIPES_TABLE_NAME,null,null);
                break;
            case STEPS:
                recipesDeleted = db.delete(RecipesDataContract.StepEntry.STEPS_TABLE_NAME,null,null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(recipesDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return recipesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

}
