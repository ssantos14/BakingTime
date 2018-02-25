package com.example.android.bakingtime;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.android.bakingtime.data.RecipesDataContract;
import com.example.android.bakingtime.sync.SyncRecipesDataIntentService;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,RecipesAdapter.ListItemClickListener{

    @BindView(R.id.recipes_recycler_view) RecyclerView mRecipesRecyclerView;
    private RecipesAdapter mRecipesAdapter;
    private static final int RECIPES_LOADER_ID = 29;
    public static final String[] RECIPES_PROJECTION = {
            RecipesDataContract.RecipeEntry.COLUMN_RECIPE_NAME,
            RecipesDataContract.RecipeEntry.COLUMN_RECIPE_SERVINGS,
            RecipesDataContract.RecipeEntry.COLUMN_RECIPE_INGREDIENTS
    };
    public static final String INTENT_TAG = "recipe_info";
    private static final String RECYCLER_VIEW_POSITION = "rv_position";
    private LinearLayoutManager layoutManager;
    private Parcelable recipesSavedState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            layoutManager = new GridLayoutManager(this, 1);
        }
        else{
            layoutManager = new GridLayoutManager(this, 2);
        }
        mRecipesRecyclerView.setLayoutManager(layoutManager);
        mRecipesRecyclerView.setHasFixedSize(true);
        mRecipesAdapter = new RecipesAdapter(this,this);
        mRecipesRecyclerView.setAdapter(mRecipesAdapter);
        Intent intentToSyncImmediately = new Intent(this, SyncRecipesDataIntentService.class);
        startService(intentToSyncImmediately);
        getLoaderManager().initLoader(RECIPES_LOADER_ID,null,this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, final Bundle loaderArgs) {
        switch(loaderId){
            case RECIPES_LOADER_ID:
                Uri recipesUri = RecipesDataContract.RecipeEntry.RECIPES_CONTENT_URI;
                return new CursorLoader(this,recipesUri,RECIPES_PROJECTION,null,null,null);
            default:
                throw new RuntimeException("Loader not implemented" + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mRecipesAdapter.setRecipeDataCursor(cursor);
        layoutManager.onRestoreInstanceState(recipesSavedState);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onListItemClick(int clickedItemIndex,String[] recipeInfo) {
        Intent startDetailsActivityIntent = new Intent(this, RecipeDetailsActivity.class);
        startDetailsActivityIntent.putExtra(INTENT_TAG,recipeInfo);
        startActivity(startDetailsActivityIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECYCLER_VIEW_POSITION, layoutManager.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            recipesSavedState = savedInstanceState.getParcelable(RECYCLER_VIEW_POSITION);
        }
    }

}
