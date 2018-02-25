package com.example.android.bakingtime.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.RecipesDataContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sylvana on 2/14/2018.
 */

public class OpenRecipesJsonUtils {

    private static ContentValues[] recipesData;
    private static ContentValues[] stepsData;

    public static ContentValues[] getInfoFromJson(String recipesJSON)
            throws JSONException{
        String recipeName;
        String recipeIngredients;
        String recipeServings;

        JSONArray recipesJSONArray = new JSONArray(recipesJSON);
        recipesData = new ContentValues[recipesJSONArray.length()];
        for(int i = 0; i < recipesJSONArray.length(); i++){
            JSONObject recipe = recipesJSONArray.getJSONObject(i);
            recipeName = recipe.getString("name");
            recipeServings = "Servings: " +recipe.getString("servings");
            recipeIngredients = getIngredients(recipe.getJSONArray("ingredients"));

            ContentValues recipesContentValues = new ContentValues();
            recipesContentValues.put(RecipesDataContract.RecipeEntry.COLUMN_RECIPE_NAME,recipeName);
            recipesContentValues.put(RecipesDataContract.RecipeEntry.COLUMN_RECIPE_SERVINGS,recipeServings);
            recipesContentValues.put(RecipesDataContract.RecipeEntry.COLUMN_RECIPE_INGREDIENTS,recipeIngredients);
            recipesData[i] = recipesContentValues;
        }
        return recipesData;
    }

    private static String getIngredients(JSONArray ingredientsJSONArray) throws JSONException{
        String ingredients = "";
        String quantity;
        String measure;
        String ingredient;

        int numberOfIngredients = ingredientsJSONArray.length();
        for(int i = 0; i < numberOfIngredients; i++){
            JSONObject ingredientInfo = ingredientsJSONArray.getJSONObject(i);
            quantity = ingredientInfo.getString("quantity");
            measure = ingredientInfo.getString("measure");
            ingredient = ingredientInfo.getString("ingredient");
            ingredients = ingredients + quantity + measure + " " + ingredient + "\n";
        }
        return ingredients;
    }

    public static ContentValues[] getStepsFromJSON(String recipesJson, int i) throws JSONException{
        String shortDescription;
        String description;
        String videoUrl;
        String thumbnailUrl;

        JSONArray recipesJsonArray = new JSONArray(recipesJson);
        JSONObject recipe = recipesJsonArray.getJSONObject(i);
        String recipeName = recipe.getString("name");
        JSONArray recipeSteps = recipe.getJSONArray("steps");
        int numberOfSteps = recipeSteps.length();
        stepsData = new ContentValues[numberOfSteps];
        for(int j = 0; j < numberOfSteps; j++){
            JSONObject stepsInfo = recipeSteps.getJSONObject(j);
            shortDescription = stepsInfo.getString("shortDescription");
            description = stepsInfo.getString("description");
            videoUrl = stepsInfo.getString("videoURL");
            thumbnailUrl = stepsInfo.getString("thumbnailURL");
            ContentValues stepsContentValue = new ContentValues();
            stepsContentValue.put(RecipesDataContract.StepEntry.COLUMN_RECIPE_NAME,recipeName);
            stepsContentValue.put(RecipesDataContract.StepEntry.COLUMN_SHORT_DESCRIPTION,shortDescription);
            stepsContentValue.put(RecipesDataContract.StepEntry.COLUMN_DESCRIPTION,description);
            stepsContentValue.put(RecipesDataContract.StepEntry.COLUMN_VIDEO_URL,videoUrl);
            stepsContentValue.put(RecipesDataContract.StepEntry.COLUMN_THUMBNAIL_URL,thumbnailUrl);
            stepsData[j] = stepsContentValue;
        }
        return stepsData;
    }

}
