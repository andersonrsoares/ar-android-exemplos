package org.cameronmoreau.aryelp.models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Cameron on 10/2/16.
 */

public class PlacePhoto {

    //https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU&key=YOUR_API_KEY

    @SerializedName("photo_reference")
    String reference;

    public String getUrl() {
        return "https://maps.googleapis.com/maps/api/place/photo?" +
                "&maxwidth=600" +
                "&photoreference=" + reference +
                "&key=AIzaSyBTuELqSKtgOgqjTU0EJ096aRm3cMdnSzQ";
    }
}
