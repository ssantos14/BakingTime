package com.example.android.bakingtime;
import android.content.Intent;
import android.widget.RemoteViewsService;


public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(),intent.getStringExtra(SetWidgetService.WIDGET_RECIPE_EXTRA));
    }
}
