package com.tarek.nanodegree.bakingapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;


import com.google.gson.Gson;
import com.tarek.nanodegree.bakingapp.R;
import com.tarek.nanodegree.bakingapp.model.pojo.Recipe;
import com.tarek.nanodegree.bakingapp.model.api.ApiConstants;
import com.tarek.nanodegree.bakingapp.model.api.RecipeAPI;
import com.tarek.nanodegree.bakingapp.view.adapters.AllRecipesAdapter;
import com.tarek.nanodegree.bakingapp.view.phase2.StepListActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.support.test.espresso.idling.CountingIdlingResource;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeListActivity extends AppCompatActivity implements AllRecipesAdapter.OnItemClicked {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private Retrofit retrofit;
    ArrayList<Recipe> allRecipes;

    CountingIdlingResource idlingResource = new CountingIdlingResource("Data_Loader");

    public CountingIdlingResource getIdlingResource() {
        return idlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        idlingResource.increment();
        // back-end call //
        retrofit = new Retrofit.Builder().baseUrl(ApiConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        RecipeAPI recipeAPI = retrofit.create(RecipeAPI.class);

        Call<ArrayList<Recipe>> call = recipeAPI.getAllRecipes();

        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {

                allRecipes = response.body();
                View recyclerView = findViewById(R.id.recipe_list);
                assert recyclerView != null;
                setupRecyclerView((RecyclerView) recyclerView);
                idlingResource.decrement();

            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {

                Log.e("RecipeListActivity", "" + t.getMessage());
            }
        });

        if (findViewById(R.id.recipe_detail_container) != null) {

            mTwoPane = true;
        }


    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        AllRecipesAdapter mRecyclerViewAdapter = new AllRecipesAdapter(this, allRecipes);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerViewAdapter.setOnClick(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.onSaveInstanceState();

        if(isTablet()){
            recyclerView.setLayoutManager(gridLayoutManager);
        }




    }

    @Override
    public void onItemClick(int position) {
        String recipeDtails = new Gson().toJson(allRecipes.get(position));

        Intent intent = new Intent(this, StepListActivity.class);
        intent.putExtra("all_recipes", recipeDtails);
        startActivity(intent);
    }

    public boolean isTablet() {
        try {
            // Compute screen size
            DisplayMetrics dm = getResources().getDisplayMetrics();
            float screenWidth = dm.widthPixels / dm.xdpi;
            float screenHeight = dm.heightPixels / dm.ydpi;
            double size = Math.sqrt(Math.pow(screenWidth, 2) +
                    Math.pow(screenHeight, 2));
            // Tablet devices should have a screen size greater than 6 inches
            return size >= 6;
        } catch (Throwable t) {
            Log.e("RecipeListActivity", "Failed to compute screen size", t);
            return false;
        }

    }
}
