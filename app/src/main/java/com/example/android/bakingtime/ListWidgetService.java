package com.example.android.bakingtime;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import static com.example.android.bakingtime.data.RecipesDataContract.BASE_CONTENT_URI;
import static com.example.android.bakingtime.data.RecipesDataContract.PATH_RECIPES;


public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(ListWidgetService.class.getSimpleName(),"GOT TO LIST WIDGET SERVICE: " + intent.getStringExtra(SetWidgetService.WIDGET_RECIPE_EXTRA) );
        return new ListRemoteViewsFactory(this.getApplicationContext(),intent.getStringExtra(SetWidgetService.WIDGET_RECIPE_EXTRA));
    }
}
