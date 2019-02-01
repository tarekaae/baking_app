package com.tarek.nanodegree.bakingapp.view.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tarek.nanodegree.bakingapp.R;
import com.tarek.nanodegree.bakingapp.model.pojo.Recipe;

import java.util.ArrayList;

/**
 * Created by tarek.abdulkader on 1/31/2018.
 */

public class AllRecipesAdapter extends RecyclerView.Adapter<AllRecipesAdapter.AllRecipesViewHolder> {

    private Context context;
    private ArrayList<Recipe> dataSet;
    private OnItemClicked onClick;

    public AllRecipesAdapter(Context context, ArrayList<Recipe> allRecipes) {
        this.context = context;
        this.dataSet = allRecipes;
    }

    public static class AllRecipesViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView imageView;
        RelativeLayout mainLayout;


        public AllRecipesViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.recipeName);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            mainLayout = (RelativeLayout) view.findViewById(R.id.main_item);

        }
    }

    @Override
    public AllRecipesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        AllRecipesViewHolder recipesViewHolder = new AllRecipesViewHolder(v);
        return recipesViewHolder;
    }

    @Override
    public void onBindViewHolder(AllRecipesViewHolder holder, final int position) {

        String name = dataSet.get(position).getName();
        String imagePath = dataSet.get(position).getImage();

        holder.name.setText(name);

        if (imagePath != null && !"".equalsIgnoreCase(imagePath)) {
            Picasso.with(context).load(imagePath).fit().into(holder.imageView);
        } else {

//            Resources resources = context.getResources();
//            int resourceId = resources.getIdentifier("nutella", "drawable",
//                    context.getPackageName());
//            if (resourceId != 0) {

//            }
            if (position == 0) {
                Picasso.with(context).load(R.mipmap.nutella).fit().into(holder.imageView);
            }

        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onItemClick(position);
            }
        });
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
