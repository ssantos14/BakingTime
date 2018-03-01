package com.example.android.bakingtime.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URL;

/**
 * Created by Sylvana on 2/14/2018.
 */

public class RecipesDataContract {

    public static final String AUTHORITY = "com.example.android.bakingtime";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_RECIPES = "recipes";
    public static final String PATH_STEPS = "steps";
    public static final class RecipeEntry implements BaseColumns{
        public static final Uri RECIPES_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();
        public static final String RECIPES_TABLE_NAME = "recipes";
        public static final String COLUMN_RECIPE_NAME = "name";
        public static final String COLUMN_RECIPE_SERVINGS = "servings";
        public static final String COLUMN_RECIPE_INGREDIENTS = "ingredients";
        public static final String COLUMN_RECIPE_IMAGE = "image";
    }
    public static final class StepEntry implements BaseColumns{
        public static final Uri STEPS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STEPS).build();
        public static final String STEPS_TABLE_NAME = "steps";
        public static final String COLUMN_RECIPE_NAME = "recipe";
        public static final String COLUMN_SHORT_DESCRIPTION = "shortDescrption";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_VIDEO_URL = "url";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnail";
    }
}
