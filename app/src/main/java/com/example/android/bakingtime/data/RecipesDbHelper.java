package com.example.android.bakingtime.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sylvana on 2/14/2018.
 */

public class RecipesDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "recipes.db";
    private static final int DATABASE_VERSION = 5;
    public RecipesDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_RECIPES_TABLE = "CREATE TABLE " + RecipesDataContract.RecipeEntry.RECIPES_TABLE_NAME + " (" +
                RecipesDataContract.RecipeEntry.COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                RecipesDataContract.RecipeEntry.COLUMN_RECIPE_SERVINGS + " TEXT NOT NULL, " +
                RecipesDataContract.RecipeEntry.COLUMN_RECIPE_INGREDIENTS + " TEXT NOT NULL, " +
                RecipesDataContract.RecipeEntry.COLUMN_RECIPE_IMAGE + " TEXT NOT NULL);";
        final String SQL_CREATE_STEPS_TABLE = "CREATE TABLE " + RecipesDataContract.StepEntry.STEPS_TABLE_NAME + " (" +
                RecipesDataContract.StepEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RecipesDataContract.StepEntry.COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                RecipesDataContract.StepEntry.COLUMN_SHORT_DESCRIPTION + " TEXT NOT NULL, " +
                RecipesDataContract.StepEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                RecipesDataContract.StepEntry.COLUMN_VIDEO_URL + " TEXT NOT NULL, " +
                RecipesDataContract.StepEntry.COLUMN_THUMBNAIL_URL + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(SQL_CREATE_RECIPES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STEPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipesDataContract.RecipeEntry.RECIPES_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipesDataContract.StepEntry.STEPS_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
