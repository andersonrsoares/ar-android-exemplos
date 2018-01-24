package org.cameronmoreau.aryelp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.cameronmoreau.aryelp.adapters.PlacesListAdapter;
import org.cameronmoreau.aryelp.models.Place;
import org.cameronmoreau.aryelp.models.PlacesResult;
import org.cameronmoreau.aryelp.services.PlacesApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView placesRecycler;
    PlacesListAdapter adapter;

    Spinner filterSpinner;

    boolean dataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        placesRecycler = (RecyclerView) findViewById(R.id.rv_places);
        filterSpinner = (Spinner) findViewById(R.id.spinner_filter);

        placesRecycler.setHasFixedSize(true);

        adapter = new PlacesListAdapter();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        placesRecycler.setLayoutManager(llm);
        placesRecycler.setAdapter(adapter);

        // Filter spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_label_white,
                getResources().getStringArray(R.array.filter_types)
        );
        filterSpinner.setAdapter(spinnerAdapter);

        if(!dataLoaded) loadPlaces();
    }

    void loadPlaces() {
        PlacesApi api = PlacesApi.retrofit.create(PlacesApi.class);
        Call<PlacesResult> call = api.getNearbyPlaces();
        call.enqueue(new Callback<PlacesResult>() {
            @Override
            public void onResponse(Call<PlacesResult> call, Response<PlacesResult> response) {

                List<Place> places = response.body().getResults();
                for(Place p : places) {
                    adapter.addPlace(p);
                    Log.d("TEST", p.toString());
                }

                dataLoaded = true;
            }

            @Override
            public void onFailure(Call<PlacesResult> call, Throwable t) {
                Log.e("TEST", "Failed!");
                Log.e("TEST", t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_ar:
                Intent i = new Intent(this, ARActivity.class);
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
