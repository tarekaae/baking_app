package com.tarek.nanodegree.bakingapp.view.phase2;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.tarek.nanodegree.bakingapp.BackingAppWidget;
import com.tarek.nanodegree.bakingapp.R;

import com.tarek.nanodegree.bakingapp.model.pojo.Ingredient;
import com.tarek.nanodegree.bakingapp.model.pojo.Recipe;
import com.tarek.nanodegree.bakingapp.model.pojo.Step;
import com.tarek.nanodegree.bakingapp.view.adapters.IngredientAdapter;
import com.tarek.nanodegree.bakingapp.view.adapters.StepAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Steps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class StepListActivity extends AppCompatActivity implements StepAdapter.OnItemClicked {

    private Recipe recipeDetails;
    private String recipeDetailsAsString;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private int mScrollPosition;

    private RecyclerView.LayoutManager stepLayoutManager;

    private RecyclerView stepRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            recipeDetailsAsString = getIntent().getExtras().getString("all_recipes");
            recipeDetails = new Gson().fromJson(recipeDetailsAsString, Recipe.class);
        }


        if (recipeDetails != null) {

            IngredientAdapter mRecyclerViewAdapter = new IngredientAdapter(
                    this, (ArrayList<Ingredient>) recipeDetails.getIngredients());

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

            RecyclerView ingerdiantRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewIngediantr);

            ingerdiantRecyclerView.setLayoutManager(layoutManager);
            ingerdiantRecyclerView.setAdapter(mRecyclerViewAdapter);

            //

            StepAdapter mStepRecyclerViewAdapter = new StepAdapter(
                    this, (ArrayList<Step>) recipeDetails.getSteps());

            mStepRecyclerViewAdapter.setOnClick(this);

            stepLayoutManager = new LinearLayoutManager(this);

            stepRecyclerView = (RecyclerView) findViewById(R.id.stepsRecycler);

            stepRecyclerView.setLayoutManager(stepLayoutManager);
            stepRecyclerView.setAdapter(mStepRecyclerViewAdapter);


            if (savedInstanceState != null) {
                int pos = savedInstanceState.getInt("stepScroll");
                ((LinearLayoutManager) stepRecyclerView.getLayoutManager()).scrollToPosition(pos);
            }
        }

        if (findViewById(R.id.step_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

    }


    @Override
    public void onItemClick(int position) {

        String widgetTitle = recipeDetails.getName();

        List<Ingredient> ingredients = recipeDetails.getIngredients();
        String widgetBody = "";

        double quantity = -1;
        DecimalFormat df = new DecimalFormat("###.#");

        for (int i = 0; i < ingredients.size(); i++) {
            quantity = ingredients.get(i).getQuantity();
            widgetBody +=
                    "-  " +
                            df.format(quantity)
                            + " " +
                            ingredients.get(i).getMeasure()
                            + " " +
                            ingredients.get(i).getIngredient()
                            +
                            "\n";

        }

        SharedPreferences preferences = getSharedPreferences("pref", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("widget_title", widgetTitle);
        editor.putString("widget_body", widgetBody);
        editor.apply();

        int[] ids = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, BackingAppWidget.class));
        BackingAppWidget mWidget = new BackingAppWidget();
        mWidget.onUpdate(this, AppWidgetManager.getInstance(this), ids);


        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(StepDetailFragment.ARG_ITEM_ID, recipeDetailsAsString);
            arguments.putInt(StepDetailFragment.ARG_ITEM_ID_INT, position);

            StepDetailFragment fragment = new StepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_detail_container, fragment)
                    .commit();
        } else {

            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putExtra(StepDetailFragment.ARG_ITEM_ID, recipeDetailsAsString);
            intent.putExtra(StepDetailFragment.ARG_ITEM_ID_INT, position);

            startActivity(intent);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private RecyclerView.LayoutManager getLayoutManager() {

        return stepLayoutManager;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mScrollPosition = ((LinearLayoutManager) stepRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        outState.putInt("stepScroll", mScrollPosition);
        super.onSaveInstanceState(outState);
    }

}
