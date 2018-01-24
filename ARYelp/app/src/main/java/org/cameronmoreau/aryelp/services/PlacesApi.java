package org.cameronmoreau.aryelp.services;

import org.cameronmoreau.aryelp.models.PlaceResult;
import org.cameronmoreau.aryelp.models.PlacesResult;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Cameron on 10/1/16.
 */

public interface PlacesApi {

    //private static final String API_KEY = "AIzaSyBTuELqSKtgOgqjTU0EJ096aRm3cMdnSzQ";

    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&rankby=distance&types=food&key=YOUR_API_KEY


    @GET("nearbysearch/json?location=30.611948,-96.341453&rankby=distance&types=food|cafe|bar&key=AIzaSyBTuELqSKtgOgqjTU0EJ096aRm3cMdnSzQ")
    Call<PlacesResult> getNearbyPlaces();

    @GET("details/json?&key=AIzaSyBTuELqSKtgOgqjTU0EJ096aRm3cMdnSzQ")
    Call<PlaceResult> getPlace(
        @Query("placeid") String id
    );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
