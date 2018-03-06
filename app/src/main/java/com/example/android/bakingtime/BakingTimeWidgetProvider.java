package com.example.android.bakingtime;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class BakingTimeWidgetProvider extends AppWidgetProvider {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String recipeName) {
        Log.d(BakingTimeWidgetProvider.class.getSimpleName(),"GOT TO UPDATE WIDGET, SHOULD BE STARTING LIST WIDGET SERVICE: " + recipeName);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_time_widget);
        Intent intent = new Intent(context,ListWidgetService.class);
        intent.putExtra(SetWidgetService.WIDGET_RECIPE_EXTRA,recipeName);
        Log.d(BakingTimeWidgetProvider.class.getSimpleName(),"GOT TO UPDATE WIDGET, ABOUT TO SEND INTENT TO LIST WIDGET SERVICE: " + intent.getStringExtra("selection_args"));
        views.setRemoteAdapter(R.id.widget_list_view,intent);
        Intent openAppIntent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,openAppIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list_view,pendingIntent);
        views.setEmptyView(R.id.widget_list_view,R.id.empty_view);
        appWidgetManager.updateAppWidget(appWidgetId,views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SetWidgetService.startActionSetWidget(context,"");
    }

    public static void updateRecipeWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, String recipeName) {
        Log.d(BakingTimeWidgetProvider.class.getSimpleName(),"GOT TO UPDATE WIDGETS, SHOULD BE UPDATING WIDGET: " + recipeName);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeName);
        }
    }

    @Override
    public void onEnabled(Context context) {}

    @Override
    public void onDisabled(Context context) {}

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {}
}

