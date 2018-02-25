package com.example.android.bakingtime;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class BakingTimeWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetId, String recipeName, String recipeIngredients) {
        Intent intent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_time_widget);
        views.setOnClickPendingIntent(R.id.appwidget_text,pendingIntent);
        views.setTextViewText(R.id.appwidget_title,recipeName);
        views.setTextViewText(R.id.appwidget_text,recipeIngredients);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SetWidgetService.startActionSetWidget(context);
    }

    @Override
    public void onEnabled(Context context) {}

    @Override
    public void onDisabled(Context context) {}
}

