package com.example.android.bakingtime.sync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Sylvana on 2/17/2018.
 */

public class SyncRecipesDataIntentService extends IntentService{
    public SyncRecipesDataIntentService() {super("SyncMovieDataIntentService");}

    @Override
    protected void onHandleIntent(Intent intent) {
        SyncRecipesDataTask.syncRecipesData(this);
    }
}
