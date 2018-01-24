package org.cameronmoreau.aryelp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragment;
import com.beyondar.android.view.BeyondarViewAdapter;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;

import org.cameronmoreau.aryelp.models.Place;
import org.cameronmoreau.aryelp.models.PlacesResult;
import org.cameronmoreau.aryelp.services.PlacesApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Cameron on 10/1/16.
 */

public class ARActivity extends AppCompatActivity implements OnClickBeyondarObjectListener {

    BeyondarFragment beyondar;
    World world;

    ArrayList<BeyondarObject> arItems;
    HashMap<String, String> tempPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.simple_camera);
        checkPermissions();

        arItems = new ArrayList<>();
        tempPlaces = new HashMap<>();

        // Add temp places
        // Pls dont judge
        tempPlaces.put("Starbucks - $$$", "ChIJXXBI75iDRoYRsfgNW3cJV7c");
        tempPlaces.put("Panda Express - $$", "ChIJuw7CzqGDRoYRKKCUT9edOuM");
        tempPlaces.put("Smoothie King - $$$", "ChIJuw7CzqGDRoYRKKCUT9edOuM");

        world = new World(this);
        world.setDefaultImage(R.mipmap.ic_food_light);
        world.setGeoPosition(30.612763, -96.340797);

        ARItemViewAdapter adapter = new ARItemViewAdapter(this);

        beyondar = (BeyondarFragment) getFragmentManager().findFragmentById(R.id.beyondarFragment);
        beyondar.setOnClickBeyondarObjectListener(this);
        beyondar.setBeyondarViewAdapter(adapter);
        beyondar.setWorld(world);
        beyondar.showFPS(true);

        addFakeItems();

        // Add items to world
        for(BeyondarObject item : arItems) {
            world.addBeyondarObject(item);
        }
    }

    void addFakeItems() {
        ArrayList<String> keys = new ArrayList<>();
        for(Map.Entry<String, String> map : tempPlaces.entrySet()) {
            keys.add(map.getKey());
        }

        //ChIJXXBI75iDRoYRsfgNW3cJV7c
        GeoObject go1 = new GeoObject(1);
        go1.setGeoPosition(30.612548, -96.340620);
        go1.setName(keys.get(0));


        //e542f13ff1e3823d30aa59719daf04dbca72a2c1
        GeoObject go2 = new GeoObject(2);
        go2.setGeoPosition(30.612524, -96.340826);
        go2.setName(keys.get(0));

        //82bc7d2965348632ec5dbec7298fa2f77b267199
        GeoObject go3 = new GeoObject(3);
        go3.setGeoPosition(30.612833, -96.341262);
        go3.setName(keys.get(0));

        arItems.add(go1);
        arItems.add(go2);
        arItems.add(go3);
    }

    void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("Permission accepted", permissions.toString());
    }

    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> arrayList) {
        String name = arrayList.get(0).getName();
        String id = tempPlaces.get(name);

        Toast.makeText(this, "Clicked on " + name, Toast.LENGTH_SHORT).show();

        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(DetailActivity.ID, id);
        startActivity(i);
    }

    private class ARItemViewAdapter extends BeyondarViewAdapter {
        LayoutInflater inflater;

        public ARItemViewAdapter(Context context) {
            super(context);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(BeyondarObject beyondarObject, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.ar_item, null);

            TextView textView = (TextView) view.findViewById(R.id.tv_name);
            textView.setText(beyondarObject.getName());

            // Once the view is ready we specify the position
            setPosition(beyondarObject.getScreenPositionTopRight());

            return view;
        }
    }
}
