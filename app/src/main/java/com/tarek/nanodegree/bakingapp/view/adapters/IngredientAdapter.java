package com.tarek.nanodegree.bakingapp.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tarek.nanodegree.bakingapp.R;
import com.tarek.nanodegree.bakingapp.model.pojo.Ingredient;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by tarek.abdulkader on 2/4/2018.
 */

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private Context context;
    private ArrayList<Ingredient> dataSet;

    public IngredientAdapter(Context context, ArrayList<Ingredient> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        IngredientViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.editText);
        }
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        IngredientViewHolder ingredientViewHolder = new IngredientViewHolder(v);
        return ingredientViewHolder;
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, final int position) {

        double quantity = dataSet.get(position).getQuantity();
        DecimalFormat df = new DecimalFormat("###.#");

        try {
            holder.title.setText(
                    "-  " +
                            df.format(quantity)
                            + " " +
                            dataSet.get(position).getMeasure()
                            + " " +
                            dataSet.get(position).getIngredient());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}