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
    private static final String ACTION_SET_WIDGET = "set_widget";
    private static String RecipeName;
    private static String RecipeIngredients;
    public SetWidgetService(){super("SetWidgetService");}

    public static void startActionSetWidget(Context context, String recipeName,String recipeIngredients){
        RecipeName = recipeName;
        RecipeIngredients = recipeIngredients;
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
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,BakingTimeWidgetProvider.class));
        BakingTimeWidgetProvider.updateAppWidget(this,appWidgetManager,appWidgetIds,RecipeName,RecipeIngredients);
    }
}
