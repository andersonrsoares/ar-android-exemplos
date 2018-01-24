package org.cameronmoreau.aryelp.models;

/**
 * Created by Cameron on 10/2/16.
 */

public class PlaceResult {

    Place result;

    public Place getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "PlaceResult{" +
                "result=" + result +
                '}';
    }
}
