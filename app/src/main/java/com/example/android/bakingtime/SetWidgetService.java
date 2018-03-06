package com.example.android.bakingtime;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.bakingtime.data.RecipesDataContract;

import static com.example.android.bakingtime.data.RecipesDataContract.BASE_CONTENT_URI;
import static com.example.android.bakingtime.data.RecipesDataContract.PATH_RECIPES;

/**
 * Created by Sylvana on 2/23/2018.
 */

public class SetWidgetService extends IntentService {
    private static final String ACTION_SET_WIDGET = "set_widget";
    private static final String ACTION_UPDATE_WIDGET = "update_widget";
    public static final String WIDGET_RECIPE_EXTRA = "widget_recipe";
    public SetWidgetService(){super("SetWidgetService");}

    public static void startActionSetWidget(Context context, String recipeName){
        Intent intent = new Intent(context,SetWidgetService.class);
        intent.setAction(ACTION_SET_WIDGET);
        intent.putExtra(WIDGET_RECIPE_EXTRA, recipeName);
        context.startService(intent);
    }

    public static void startActionUpdateWidget(Context context){
        Intent intent = new Intent(context,SetWidgetService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent != null){
            final String action = intent.getAction();
            if(action.equals(ACTION_SET_WIDGET)){
                final String recipe = intent. getStringExtra(WIDGET_RECIPE_EXTRA);
                handleActionSetWidget(recipe);
            }else if(action.equals(ACTION_UPDATE_WIDGET)){
                handleActionUpdateWidget();
            }
        }
    }

    private void handleActionSetWidget(String recipe){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,BakingTimeWidgetProvider.class));
        BakingTimeWidgetProvider.updateRecipeWidgets(this,appWidgetManager,appWidgetIds,recipe);
    }

    private void handleActionUpdateWidget(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName thisAppWidget = new ComponentName(this, BakingTimeWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,  R.id.widget_list_view);
    }

}
