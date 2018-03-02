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
    private static String RecipeName;
    private static String RecipeIngredients;
    public SetWidgetService(){super("SetWidgetService");}

    public static void startActionSetWidget(Context context, String recipeName,String recipeIngredients){
        RecipeName = recipeName;
        RecipeIngredients = recipeIngredients;
        Log.d(SetWidgetService.class.getSimpleName(),"GOT TO START ACTION, SHOULD BE STARTING INTENT SET WIDGET: " + RecipeName);
        Intent intent = new Intent(context,SetWidgetService.class);
        intent.setAction(ACTION_SET_WIDGET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(SetWidgetService.class.getSimpleName(),"HANDLING INTENT ");
        if(intent != null){
            final String action = intent.getAction();
            if(action.equals(ACTION_SET_WIDGET)){
                handleActionSetWidget();
            }
        }
    }

    private void handleActionSetWidget(){
        Log.d(SetWidgetService.class.getSimpleName(),"HANDLED INTENT, SHOULD BE STARTING UPDATE WIDGETS: " + RecipeName);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,BakingTimeWidgetProvider.class));
        BakingTimeWidgetProvider.updateRecipeWidgets(this,appWidgetManager,appWidgetIds,RecipeName,RecipeIngredients);
    }
}
