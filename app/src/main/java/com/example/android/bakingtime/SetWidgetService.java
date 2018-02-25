package com.example.android.bakingtime;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.bakingtime.data.RecipesDataContract;

/**
 * Created by Sylvana on 2/23/2018.
 */

public class SetWidgetService extends IntentService {
    public static final String ACTION_SET_WIDGET = "set_widget";
    public SetWidgetService(){super("SetWidgetService");}

    public static void startActionSetWidget(Context context){
        Intent intent = new Intent(context,SetWidgetService.class);
        intent.setAction(ACTION_SET_WIDGET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent != null){
            final String action = intent.getAction();
            if(action.equals(ACTION_SET_WIDGET)){
                handleActionSetWidget();
            }
        }
    }

    private void handleActionSetWidget(){
        String recipeName = null;
        String recipeIngredients = null;
        Uri recipeUri = RecipesDataContract.RecipeEntry.RECIPES_CONTENT_URI;
        Cursor cursor = getContentResolver().query(recipeUri,null,null,null,null);
        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int recipeNameIndex = cursor.getColumnIndex(RecipesDataContract.RecipeEntry.COLUMN_RECIPE_NAME);
            int recipeIngredientsIndex = cursor.getColumnIndex(RecipesDataContract.RecipeEntry.COLUMN_RECIPE_INGREDIENTS);
            recipeName = cursor.getString(recipeNameIndex);
            recipeIngredients = cursor.getString(recipeIngredientsIndex);
            cursor.close();
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,BakingTimeWidgetProvider.class));
        BakingTimeWidgetProvider.updateAppWidget(this,appWidgetManager,appWidgetIds,recipeName,recipeIngredients);
    }
}
