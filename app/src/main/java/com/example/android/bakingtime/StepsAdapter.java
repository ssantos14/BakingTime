package com.example.android.bakingtime;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sylvana on 2/20/2018.
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {
    private Cursor mStepsCursor;
    private final ListItemClickListener mClickListener;
    private static int firstId;
    private static int lastId;

    public void setStepsCursor(Cursor stepsCursor){
        if(stepsCursor != null && stepsCursor.getCount() > 0){
            mStepsCursor = stepsCursor;
            stepsCursor.moveToFirst();
            firstId = stepsCursor.getInt(0);
            stepsCursor.moveToLast();
            lastId = stepsCursor.getInt(0);
            notifyDataSetChanged();
        }
    }

    public StepsAdapter(ListItemClickListener listener) {
        mClickListener = listener;
    }

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.step_short_description_text_view) TextView mShortDescriptionTextView;
        public StepViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            int stepId = (int) mShortDescriptionTextView.getTag();
            mClickListener.onListItemClick(clickedPosition,stepId,firstId,lastId);
        }
    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForStepItem = R.layout.step_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForStepItem,parent,false);
        view.setFocusable(true);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        mStepsCursor.moveToPosition(position);
        String stepShortDescription = mStepsCursor.getString(2);
        int stepId = mStepsCursor.getInt(0);
        holder.mShortDescriptionTextView.setText(stepShortDescription);
        holder.mShortDescriptionTextView.setTag(stepId);
    }

    @Override
    public int getItemCount() {
        if(mStepsCursor == null)return 0;
        return mStepsCursor.getCount();
    }

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex, int StepId, int FirstId, int LastId);
    }
}
