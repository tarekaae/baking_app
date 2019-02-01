package com.tarek.nanodegree.bakingapp.view.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tarek.nanodegree.bakingapp.R;
import com.tarek.nanodegree.bakingapp.model.pojo.Step;

import java.util.ArrayList;

/**
 * Created by tarek.abdulkader on 2/4/2018.
 */


public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {

    private Context context;
    private ArrayList<Step> dataSet;
    private OnItemClicked onClick;

    public StepAdapter(Context context, ArrayList<Step> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        LinearLayout stepLayout;
        CardView cardStep ;

        StepViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.step);
            stepLayout = (LinearLayout)view.findViewById(R.id.stepLayout);
            cardStep =(CardView) view.findViewById(R.id.cardStep);
         }
    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step, parent, false);
        StepViewHolder stepViewHolder = new StepViewHolder(v);
        return stepViewHolder;
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, final int position) {
        holder.title.setText(dataSet.get(position).getShortDescription());

        View.OnClickListener listener =  new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        };
        holder.title.setOnClickListener(listener);
        holder.cardStep.setOnClickListener(listener);
        holder.stepLayout.setOnClickListener(listener);


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

}
