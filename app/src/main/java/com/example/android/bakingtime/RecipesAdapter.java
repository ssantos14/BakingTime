package com.example.android.bakingtime;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sylvana on 2/17/2018.
 */

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder>{
    private Cursor mRecipesDataCursor;
    final private ListItemClickListener mOnClickListener;

    public void setRecipeDataCursor(Cursor recipesData){
        if(recipesData != null && recipesData.getCount() > 0){
            mRecipesDataCursor = recipesData;
            notifyDataSetChanged();
        }
    }

    public RecipesAdapter(ListItemClickListener listener){
        mOnClickListener = listener;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForRecipeItem = R.layout.recipe_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForRecipeItem,parent,false);
        view.setFocusable(true);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        mRecipesDataCursor.moveToPosition(position);
        String recipeName = mRecipesDataCursor.getString(0);
        String recipeServings = mRecipesDataCursor.getString(1);
        String recipeIngredients = mRecipesDataCursor.getString(2);
        String recipeImage = mRecipesDataCursor.getString(3);
        if(!TextUtils.isEmpty(recipeImage)){
            Picasso.with(holder.mRecipeImageView.getContext()).load(recipeImage).into(holder.mRecipeImageView);
        }
        String[] recipeArray = {recipeName,recipeServings,recipeIngredients};
        holder.mRecipeNameTextView.setText(recipeName);
        holder.mRecipeNameTextView.setTag(recipeArray);
    }

    @Override
    public int getItemCount() {
        if(mRecipesDataCursor == null) return 0;
        return mRecipesDataCursor.getCount();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.recipe_item_name) TextView mRecipeNameTextView;
        @BindView(R.id.recipe_image_view) ImageView mRecipeImageView;
        public RecipeViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            String[] recipeArray = (String[]) mRecipeNameTextView.getTag();
            mOnClickListener.onListItemClick(clickedPosition,recipeArray);
        }
    }

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex,String[] recipeArray);
    }

}
