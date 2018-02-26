package com.example.android.bakingtime.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingtime.MainActivity;
import com.example.android.bakingtime.R;
import com.example.android.bakingtime.RecipeDetailsActivity;
import com.example.android.bakingtime.RecipesAdapter;
import com.example.android.bakingtime.StepDetailsActivity;
import com.example.android.bakingtime.StepsAdapter;

/**
 * Created by Sylvana on 2/25/2018.
 */

public class RecipeDetailsFragment extends Fragment implements StepsAdapter.ListItemClickListener{
    public static StepsAdapter mStepsAdapter;
    private GridLayoutManager layoutManager;
    public static final String ID_TAG = "_id";
    public static final String START_ID_TAG = "start";
    public static final String END_ID_TAG = "end";
    public static TextView mRecipeNameTextView;
    public static TextView mRecipeServingsTextView;
    public static TextView mRecipeIngredientsTextView;
    public static NestedScrollView mDetailsScrollView;
    public RecyclerView recyclerView;
    private static int firstId;
    private static int lastId;
    public RecipeDetailsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_details,container,false);
        mRecipeNameTextView = rootView.findViewById(R.id.recipe_details_name);
        mRecipeServingsTextView = rootView.findViewById(R.id.recipe_details_servings);
        mRecipeIngredientsTextView = rootView.findViewById(R.id.recipe_details_ingredients);
        mDetailsScrollView = rootView.findViewById(R.id.details_scroll_view);
        recyclerView = rootView.findViewById(R.id.recipe_steps_recycler_view);
        layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mStepsAdapter = new StepsAdapter(this);
        recyclerView.setAdapter(mStepsAdapter);
        return rootView;
    }

    @Override
    public void onListItemClick(int adapterPosition, int stepId) {
        Intent startStepDetailsActivityIntent = new Intent(getActivity(),StepDetailsActivity.class);
        startStepDetailsActivityIntent.putExtra(ID_TAG,stepId);
        startStepDetailsActivityIntent.putExtra(START_ID_TAG,firstId);
        startStepDetailsActivityIntent.putExtra(END_ID_TAG,lastId);
        startActivity(startStepDetailsActivityIntent);
    }

    public static void setRecipeDetailsContents(String[] recipeInfo, Cursor cursor, int FirstId, int LastId){
        firstId = FirstId;
        lastId = LastId;
        mRecipeNameTextView.setText(recipeInfo[0]);
        mRecipeServingsTextView.setText(recipeInfo[1]);
        mRecipeIngredientsTextView.setText(recipeInfo[2]);
        mStepsAdapter.setStepsCursor(cursor);
    }

}
