package com.tarek.nanodegree.bakingapp.model.api;

import com.tarek.nanodegree.bakingapp.model.pojo.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by tarek.abdulkader on 1/31/2018.
 */

public interface RecipeAPI {

    @GET("2017/May/59121517_baking/baking.json")
    Call<ArrayList<Recipe>> getAllRecipes();


}
