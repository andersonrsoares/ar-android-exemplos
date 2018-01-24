package org.cameronmoreau.aryelp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.cameronmoreau.aryelp.models.Place;
import org.cameronmoreau.aryelp.models.PlaceResult;
import org.cameronmoreau.aryelp.services.PlacesApi;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Cameron on 10/2/16.
 */

public class DetailActivity extends AppCompatActivity {

    public static final String ID = "id";

    Place place;

    ImageView ivMain;
    TextView tvDetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String id = getIntent().getExtras().getString(ID, "");
        Log.d("TEST", "Got " + id);

        ivMain = (ImageView) findViewById(R.id.iv_main);
        tvDetails = (TextView) findViewById(R.id.tv_details);

        loadPlace(id);
    }

    void loadPlace(final String id) {
        PlacesApi api = PlacesApi.retrofit.create(PlacesApi.class);
        Call<PlaceResult> call = api.getPlace(id);
        call.enqueue(new Callback<PlaceResult>() {
            @Override
            public void onResponse(Call<PlaceResult> call, Response<PlaceResult> response) {

                place = response.body().getResult();
                Log.d("TEST", place.toString());
                loadUI();
            }

            @Override
            public void onFailure(Call<PlaceResult> call, Throwable t) {
                Log.e("TEST", "Failed!");
                Log.e("TEST", t.getMessage());
            }
        });
    }

    void loadUI() {
        getSupportActionBar().setTitle(place.getName());

        if(place.getPhotos() != null) {
            if(place.getPhotos().size() > 0) {
                Picasso.with(this)
                        .load(place.getPhotos().get(0).getUrl())
                        .resize(1200, 590)
                        .centerCrop()
                        .into(ivMain);
            }
        }

        for(Map.Entry<String, Object> item: place.getDetails().entrySet()) {
            if(item.getKey().equals("photos")) continue;
            tvDetails.append(item.getKey() + ": " + item.getValue() + "\n\n");
        }
    }
}
