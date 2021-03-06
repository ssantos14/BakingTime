package com.example.android.bakingtime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingtime.ui.RecipeDetailsFragment;

import static com.example.android.bakingtime.data.RecipesDataContract.BASE_CONTENT_URI;
import static com.example.android.bakingtime.data.RecipesDataContract.PATH_RECIPES;

/**
 * Created by Sylvana on 3/2/2018.
 */

public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;
    String mRecipeName;

    public ListRemoteViewsFactory(Context applicationContext, String name) {
        mContext = applicationContext;
        if(!TextUtils.isEmpty(name)) {
            mRecipeName = name;
        }else {
            mRecipeName = "Nutella Pie";
        }
    }

    @Override
    public void onCreate() {}

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        String prefKey = mContext.getResources().getString(R.string.preference_file_key);
        SharedPreferences sharedPref = mContext.getSharedPreferences(prefKey,Context.MODE_PRIVATE);
        String widgetRecipe = mContext.getResources().getString(R.string.widget_recipe);
        mRecipeName = sharedPref.getString(widgetRecipe,"Nutella Pie");
        Log.d(ListRemoteViewsFactory.class.getSimpleName(),"Desired recipe name: " + mRecipeName);
        Uri RECIPES_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();
        String selection = "name=?";
        String[] selectionArgs = {mRecipeName};
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(
                RECIPES_URI,
                null,
                selection,
                selectionArgs,
                null
        );

    }

    @Override
    public void onDestroy() {
        //mCursor.close();
    }

    @Override
    public int getCount() {
//        if (mCursor == null) return 0;
//        return mCursor.getCount();
        return 1;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) return null;
        mCursor.moveToPosition(position);
        String name = mCursor.getString(0);
        String ingredients = mCursor.getString(2);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_view_item);
        views.setTextViewText(R.id.widget_recipe_name, name);
        views.setTextViewText(R.id.widget_recipe_ingredients, ingredients);

        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
        Bundle extras = new Bundle();
        extras.putString("name", mRecipeName);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_recipe_ingredients, fillInIntent);
        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

