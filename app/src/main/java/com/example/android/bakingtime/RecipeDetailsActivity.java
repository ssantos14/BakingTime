package com.example.android.bakingtime;

import android.app.FragmentManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.bakingtime.data.RecipesDataContract;
import com.example.android.bakingtime.ui.RecipeDetailsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,RecipeDetailsFragment.OnStepSelectedListener{
    private static final int STEPS_LOADER_ID = 776;
    public static final String[] STEPS_PROJECTION = {
            RecipesDataContract.StepEntry._ID,
        RecipesDataContract.StepEntry.COLUMN_RECIPE_NAME,
        RecipesDataContract.StepEntry.COLUMN_SHORT_DESCRIPTION,
        RecipesDataContract.StepEntry.COLUMN_DESCRIPTION,
        RecipesDataContract.StepEntry.COLUMN_VIDEO_URL,
        RecipesDataContract.StepEntry.COLUMN_THUMBNAIL_URL
    };
    private String recipeName;
    public static final String ID_TAG = "_id";
    public static final String START_ID_TAG = "start";
    public static final String END_ID_TAG = "end";
    private String[] recipeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        if(savedInstanceState == null) {
            RecipeDetailsFragment detailsFragment = new RecipeDetailsFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().add(R.id.details_fragment_container, detailsFragment).commit();
        }
        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity.hasExtra(MainActivity.INTENT_TAG)){
            recipeInfo = intentThatStartedThisActivity.getStringArrayExtra(MainActivity.INTENT_TAG);
            recipeName = recipeInfo[0];
            getLoaderManager().initLoader(STEPS_LOADER_ID,null,this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch(loaderId){
            case STEPS_LOADER_ID:
                Uri stepsUri = RecipesDataContract.StepEntry.STEPS_CONTENT_URI;
                String selection = "recipe=?";
                String[] selectionArgs = {recipeName};
                return new CursorLoader(this,stepsUri,STEPS_PROJECTION,selection,selectionArgs,null);
            default:
                throw new RuntimeException("Loader not implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor != null && cursor.getCount() != 0) {
            RecipeDetailsFragment.setRecipeDetailsContents(recipeInfo,cursor);
        }else{
            Log.d(RecipeDetailsActivity.class.getSimpleName(),"cursor is empty");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {RecipeDetailsFragment.mStepsAdapter.setStepsCursor(null);}

    @Override
    public void onStepSelected(int position, int step, int firstId, int lastId) {
        Intent startStepDetailsActivityIntent = new Intent(this,StepDetailsActivity.class);
        startStepDetailsActivityIntent.putExtra(ID_TAG,step);
        startStepDetailsActivityIntent.putExtra(START_ID_TAG,firstId);
        startStepDetailsActivityIntent.putExtra(END_ID_TAG,lastId);
        startActivity(startStepDetailsActivityIntent);
    }

    public void makeWidget(View view){
        SetWidgetService.startActionSetWidget(this,recipeName,recipeInfo[2]);
        Log.d(RecipeDetailsActivity.class.getSimpleName(),"MAKE WIDGET CLICKED, SHOULD BE STARTING ACTION SET WIDGET: " + recipeName);
    }

}
