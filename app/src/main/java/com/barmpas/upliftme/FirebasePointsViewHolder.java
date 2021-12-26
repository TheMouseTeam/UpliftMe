package com.barmpas.upliftme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Vector;


/**
 * The viewholder for objects retrieved from the firebase.
 * @author Konstantinos Barmpas.
 */
public class FirebasePointsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    /**
     * View for the ViewHolder
     */
    private View mView;
    /**
     * Context of activity calling
     */
    private Context mContext;
    /**
     * Public vector to populate
     */
    public static Vector<String> v;
    /**
     * String array from vector
     */
    private String [] s;

    public FirebasePointsViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    static public void createVector(){
        v = new Vector<String>();
    }

    public void bindData(final String data) {
        String[] seperated=data.split("#");
        TextView points=(TextView) mView.findViewById(R.id.points_display_recycler);
        points.setText(mContext.getResources().getString(R.string.points)+" "+seperated[0]);
        TextView action_text=(TextView) mView.findViewById(R.id.action_display_recycler);
        action_text.setText(seperated[1]);
        v.add(data);
    }

    @Override
    public void onClick(final View view) {
        s = v.toArray(new String[v.size()]);
        PointsActivity.onClickItem(s[getAdapterPosition()],mView);
    }

}