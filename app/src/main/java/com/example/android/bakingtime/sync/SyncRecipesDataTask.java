package com.example.android.bakingtime.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.android.bakingtime.data.RecipesDataContract;
import com.example.android.bakingtime.utilities.NetworkUtils;
import com.example.android.bakingtime.utilities.OpenRecipesJsonUtils;

import java.net.URL;

/**
 * Created by Sylvana on 2/15/2018.
 */

public class SyncRecipesDataTask {

    synchronized public static void syncRecipesData(Context context){
        try{
            URL recipesUrl = NetworkUtils.buildUrl();
            String jsonRecipesResponse = NetworkUtils.getJsonResponseFromUrl(recipesUrl);
            ContentValues[] recipesData = OpenRecipesJsonUtils.getInfoFromJson(jsonRecipesResponse);
            if(recipesData != null && recipesData.length != 0){
                ContentResolver contentResolver = context.getContentResolver();
                contentResolver.delete(RecipesDataContract.RecipeEntry.RECIPES_CONTENT_URI, null,null);
                contentResolver.bulkInsert(RecipesDataContract.RecipeEntry.RECIPES_CONTENT_URI,recipesData);
            }
            ContentResolver contentResolver = context.getContentResolver();
            contentResolver.delete(RecipesDataContract.StepEntry.STEPS_CONTENT_URI, null, null);
            int stepsInserted = 0;
            for(int i = 0; i < recipesData.length; i++) {
                ContentValues[] stepsData = OpenRecipesJsonUtils.getStepsFromJSON(jsonRecipesResponse,i);
                if (stepsData != null && stepsData.length != 0) {
                    stepsInserted = stepsInserted + contentResolver.bulkInsert(RecipesDataContract.StepEntry.STEPS_CONTENT_URI, stepsData);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
