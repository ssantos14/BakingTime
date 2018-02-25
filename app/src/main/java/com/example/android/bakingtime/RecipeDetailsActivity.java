package com.example.android.bakingtime;

import android.app.LoaderManager;
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
import android.widget.TextView;

import com.example.android.bakingtime.data.RecipesDataContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,StepsAdapter.ListItemClickListener {
    @BindView(R.id.recipe_details_name) TextView mRecipeNameTextView;
    @BindView(R.id.recipe_details_servings) TextView mRecipeServingsTextView;
    @BindView(R.id.recipe_details_ingredients) TextView mRecipeIngredientsTextView;
    @BindView(R.id.recipe_steps_recycler_view) RecyclerView mStepsRecyclerView;
    @BindView(R.id.details_scroll_view) NestedScrollView mDetailsScrollView;
    private StepsAdapter mStepsAdapter;
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
    private int firstId;
    private int lastId;
    private static final String SCROLL_VIEW_STATE_KEY = "scroll_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        ButterKnife.bind(this);
        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity.hasExtra(MainActivity.INTENT_TAG)){
            String[] recipeInfo = intentThatStartedThisActivity.getStringArrayExtra(MainActivity.INTENT_TAG);
            recipeName = recipeInfo[0];
            mRecipeNameTextView.setText(recipeName);
            mRecipeServingsTextView.setText(recipeInfo[1]);
            mRecipeIngredientsTextView.setText(recipeInfo[2]);
        }
        mStepsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 1);
        mStepsRecyclerView.setLayoutManager(layoutManager);
        mStepsRecyclerView.setHasFixedSize(true);
        mStepsAdapter = new StepsAdapter(this,this);
        mStepsRecyclerView.setAdapter(mStepsAdapter);
        getLoaderManager().initLoader(STEPS_LOADER_ID,null,this);
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
        if(cursor != null) {
            cursor.moveToFirst();
            firstId = cursor.getInt(0);
            cursor.moveToLast();
            lastId = cursor.getInt(0);
            mStepsAdapter.setStepsCursor(cursor);
        }else{
            Log.d(RecipeDetailsActivity.class.getSimpleName(),"cursor is empty");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {mStepsAdapter.setStepsCursor(null);}

    @Override
    public void onListItemClick(int adapterPosition, int stepId) {
        Intent startStepDetailsActivityIntent = new Intent(this,StepDetailsActivity.class);
        startStepDetailsActivityIntent.putExtra(ID_TAG,stepId);
        startStepDetailsActivityIntent.putExtra(START_ID_TAG,firstId);
        startStepDetailsActivityIntent.putExtra(END_ID_TAG,lastId);
        startActivity(startStepDetailsActivityIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(SCROLL_VIEW_STATE_KEY, new int[]{ mDetailsScrollView.getScrollX(), mDetailsScrollView.getScrollY()});
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            final int[] position = savedInstanceState.getIntArray(SCROLL_VIEW_STATE_KEY);
            if(position != null)
                mDetailsScrollView.post(new Runnable() {
                    public void run() {
                        mDetailsScrollView.scrollTo(position[0], position[1]);
                    }
                });
        }
    }
}
