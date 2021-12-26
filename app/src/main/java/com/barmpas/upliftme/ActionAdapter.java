package com.barmpas.upliftme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * The ActionAdapter to populate the recyclerView in HistoryActivity and DayActivity
 * @author Konstantinos Barmpas.
 */
public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ActionAdapterViewHolder> {

    /**
     * Array with the data
     */
    private String[] mActionData;
    /**
     * onClick Handler
     */
    private final ActionAdapterOnClickHandler mClickHandler;

    public interface ActionAdapterOnClickHandler {
        void onClick(String dataForThis);

    }

    public ActionAdapter(ActionAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class ActionAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mActionTxt;

        public ActionAdapterViewHolder(View view) {
            super(view);
            mActionTxt = (TextView) view.findViewById(R.id.action_saved);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public ActionAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.savedaction_card;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ActionAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ActionAdapterViewHolder actionAdapterViewHolder, int position) {
        String movieDetails = mActionData[position];
        actionAdapterViewHolder.mActionTxt.setText(movieDetails);
    }

    @Override
    public int getItemCount() {
        if (null == mActionData) return 0;
        return mActionData.length;
    }

    public void setData(String[] data) {
        mActionData = data;
        notifyDataSetChanged();
    }

}


