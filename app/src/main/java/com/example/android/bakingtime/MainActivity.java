package com.example.android.bakingtime;

import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.android.bakingtime.data.RecipesDataContract;
import com.example.android.bakingtime.sync.SyncRecipesDataIntentService;
import com.example.android.bakingtime.ui.RecipeDetailsFragment;
import com.example.android.bakingtime.ui.RecipeListFragment;

import static com.example.android.bakingtime.RecipeDetailsActivity.END_ID_TAG;
import static com.example.android.bakingtime.RecipeDetailsActivity.ID_TAG;
import static com.example.android.bakingtime.RecipeDetailsActivity.START_ID_TAG;
import static com.example.android.bakingtime.ui.RecipeListFragment.layoutManager;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        RecipeListFragment.OnRecipeSelectedListener,RecipeDetailsFragment.OnStepSelectedListener,
        RecipeDetailsFragment.OnWidgetButtonClickListener{
    private static final int RECIPES_LOADER_ID = 29;
    public static final String[] RECIPES_PROJECTION = {
            RecipesDataContract.RecipeEntry.COLUMN_RECIPE_NAME,
            RecipesDataContract.RecipeEntry.COLUMN_RECIPE_SERVINGS,
            RecipesDataContract.RecipeEntry.COLUMN_RECIPE_INGREDIENTS,
            RecipesDataContract.RecipeEntry.COLUMN_RECIPE_IMAGE
    };
    public static final String INTENT_TAG = "recipe_info";
    private static final String RECYCLER_VIEW_POSITION = "rv_position";
    private Parcelable recipesSavedState;
    private static final String DETAILS_STATE = "details_state";
    private Parcelable detailsSavedState;
    private static boolean mTwoPane;
    public static final int STEPS_LOADER_ID = 776;
    public static final String[] STEPS_PROJECTION = {
            RecipesDataContract.StepEntry._ID,
            RecipesDataContract.StepEntry.COLUMN_RECIPE_NAME,
            RecipesDataContract.StepEntry.COLUMN_SHORT_DESCRIPTION,
            RecipesDataContract.StepEntry.COLUMN_DESCRIPTION,
            RecipesDataContract.StepEntry.COLUMN_VIDEO_URL,
            RecipesDataContract.StepEntry.COLUMN_THUMBNAIL_URL
    };
    private static String recipeName;
    private static String[] mRecipeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intentToSyncImmediately = new Intent(this, SyncRecipesDataIntentService.class);
        startService(intentToSyncImmediately);
        if(findViewById(R.id.divider) != null){
            mTwoPane = true; //Two pane case
            if(savedInstanceState == null) {
                RecipeDetailsFragment detailsFragment = new RecipeDetailsFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.details_fragment_container, detailsFragment).commit();
            }
        }else{
            mTwoPane = false; //One pane case
        }
        getLoaderManager().initLoader(RECIPES_LOADER_ID,null,this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, final Bundle loaderArgs) {
        switch(loaderId){
            case RECIPES_LOADER_ID:
                Uri recipesUri = RecipesDataContract.RecipeEntry.RECIPES_CONTENT_URI;
                return new CursorLoader(this,recipesUri,RECIPES_PROJECTION,null,null,null);
            case STEPS_LOADER_ID:
                Uri stepsUri = RecipesDataContract.StepEntry.STEPS_CONTENT_URI;
                String selection = "recipe=?";
                String[] selectionArgs = {recipeName};
                return new CursorLoader(this,stepsUri,STEPS_PROJECTION,selection,selectionArgs,null);
            default:
                throw new RuntimeException("Loader not implemented" + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int loaderId = loader.getId();
        switch (loaderId){
            case RECIPES_LOADER_ID:
                if(cursor != null && cursor.getCount() != 0){
                    RecipeListFragment.mRecipesAdapter.setRecipeDataCursor(cursor);
                    layoutManager.onRestoreInstanceState(recipesSavedState);
                    if(MainActivity.mTwoPane){
                        cursor.moveToFirst();
                        recipeName = cursor.getString(0);
                        String servings = cursor.getString(1);
                        String ingredients = cursor.getString(2);
                        mRecipeInfo = new String[]{recipeName,servings,ingredients};
                        getLoaderManager().initLoader(STEPS_LOADER_ID,null,this);
                    }
                }
                break;
            case STEPS_LOADER_ID:
                if(cursor != null && cursor.getCount() != 0) {
                    RecipeDetailsFragment.setRecipeDetailsContents(mRecipeInfo,cursor);
                    RecipeDetailsFragment.layoutManager.onRestoreInstanceState(detailsSavedState);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int loaderId = loader.getId();
        switch (loaderId){
            case RECIPES_LOADER_ID:
                RecipeListFragment.mRecipesAdapter.setRecipeDataCursor(null);
            case STEPS_LOADER_ID:
                RecipeDetailsFragment.mStepsAdapter.setStepsCursor(null);
            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECYCLER_VIEW_POSITION, layoutManager.onSaveInstanceState());
        if(RecipeDetailsFragment.layoutManager != null) {
            outState.putParcelable(DETAILS_STATE, RecipeDetailsFragment.layoutManager.onSaveInstanceState());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            recipesSavedState = savedInstanceState.getParcelable(RECYCLER_VIEW_POSITION);
            detailsSavedState = savedInstanceState.getParcelable(DETAILS_STATE);
        }
    }


    @Override
    public void onRecipeSelected(int position, String[] recipeInfo) {
        mRecipeInfo = recipeInfo;
        recipeName = recipeInfo[0];
        if(mTwoPane){
            getLoaderManager().restartLoader(STEPS_LOADER_ID,null,this);
        }else {
            Intent startDetailsActivityIntent = new Intent(this, RecipeDetailsActivity.class);
            startDetailsActivityIntent.putExtra(MainActivity.INTENT_TAG, recipeInfo);
            startActivity(startDetailsActivityIntent);
        }
    }

    @Override
    public void onStepSelected(int position, int step, int firstId, int lastId) {
        Intent startStepDetailsActivityIntent = new Intent(this,StepDetailsActivity.class);
        startStepDetailsActivityIntent.putExtra(ID_TAG,step);
        startStepDetailsActivityIntent.putExtra(START_ID_TAG,firstId);
        startStepDetailsActivityIntent.putExtra(END_ID_TAG,lastId);
        startStepDetailsActivityIntent.putExtra("recipe_name", recipeName);
        startActivity(startStepDetailsActivityIntent);
    }

    @Override
    public void widgetButtonSelected(View view) {
        SetWidgetService.startActionSetWidget(this,recipeName,mRecipeInfo[2]);
    }
}
