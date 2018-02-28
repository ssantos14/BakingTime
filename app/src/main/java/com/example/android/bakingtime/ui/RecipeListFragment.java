package com.example.android.bakingtime.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingtime.MainActivity;
import com.example.android.bakingtime.R;
import com.example.android.bakingtime.RecipeDetailsActivity;
import com.example.android.bakingtime.RecipesAdapter;
import com.example.android.bakingtime.data.RecipesDataContract;

/**
 * Created by Sylvana on 2/25/2018.
 */

public class RecipeListFragment extends Fragment implements RecipesAdapter.ListItemClickListener{
    public static RecipesAdapter mRecipesAdapter;
    public static GridLayoutManager layoutManager;
    public RecipeListFragment(){}

    OnRecipeSelectedListener mCallback;

    public interface OnRecipeSelectedListener {
        void onRecipeSelected(int position,String[] info);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnRecipeSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnRecipeSelectedListener");
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex,String[] recipeInfo) {
        mCallback.onRecipeSelected(clickedItemIndex,recipeInfo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_list,container,false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recipes_recycler_view);
        layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mRecipesAdapter = new RecipesAdapter(this);
        recyclerView.setAdapter(mRecipesAdapter);
        return rootView;
    }

}
