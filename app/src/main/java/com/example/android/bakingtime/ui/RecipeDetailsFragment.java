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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bakingtime.MainActivity;
import com.example.android.bakingtime.R;
import com.example.android.bakingtime.RecipeDetailsActivity;
import com.example.android.bakingtime.RecipesAdapter;
import com.example.android.bakingtime.SetWidgetService;
import com.example.android.bakingtime.StepDetailsActivity;
import com.example.android.bakingtime.StepsAdapter;

/**
 * Created by Sylvana on 2/25/2018.
 */

public class RecipeDetailsFragment extends Fragment implements StepsAdapter.ListItemClickListener{
    public static StepsAdapter mStepsAdapter;
    public static GridLayoutManager layoutManager;
    public static TextView mRecipeNameTextView;
    public static TextView mRecipeServingsTextView;
    public static TextView mRecipeIngredientsTextView;
    public static NestedScrollView mDetailsScrollView;
    public static TextView mRecipeIngredientsLabel;
    public static Button mWidgetButton;
    public RecyclerView recyclerView;
    public RecipeDetailsFragment(){}

    OnStepSelectedListener mCallback;

    public interface OnStepSelectedListener {
        void onStepSelected(int position,int step, int startId, int endId);
    }

    OnWidgetButtonClickListener mWidgetCallback;
    public interface OnWidgetButtonClickListener{
        void widgetButtonSelected(View view);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnStepSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnStepSelectedListener");
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex,int stepId, int firstId, int lastId) {
        mCallback.onStepSelected(clickedItemIndex,stepId, firstId, lastId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_details,container,false);
        mRecipeNameTextView = rootView.findViewById(R.id.recipe_details_name);
        mRecipeServingsTextView = rootView.findViewById(R.id.recipe_details_servings);
        mRecipeIngredientsTextView = rootView.findViewById(R.id.recipe_details_ingredients);
        mDetailsScrollView = rootView.findViewById(R.id.details_scroll_view);
        mRecipeIngredientsLabel = rootView.findViewById(R.id.recipe_details_ingredients_label);
        mWidgetButton = rootView.findViewById(R.id.widget_button);
        mWidgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWidgetCallback.widgetButtonSelected(view);
            }
        });
        recyclerView = rootView.findViewById(R.id.recipe_steps_recycler_view);
        layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mStepsAdapter = new StepsAdapter(this);
        recyclerView.setAdapter(mStepsAdapter);
        return rootView;
    }

    public static void setRecipeDetailsContents(String[] recipeInfo, Cursor cursor){
        mRecipeNameTextView.setText(recipeInfo[0]);
        mRecipeServingsTextView.setText(recipeInfo[1]);
        mRecipeIngredientsTextView.setText(recipeInfo[2]);
        mStepsAdapter.setStepsCursor(cursor);
    }


}
