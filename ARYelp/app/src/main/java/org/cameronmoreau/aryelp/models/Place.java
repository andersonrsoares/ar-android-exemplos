package org.cameronmoreau.aryelp.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * Created by Cameron on 10/1/16.
 */

public class Place {

    @SerializedName("place_id")
    String id;

    String name;

    @SerializedName("formatted_address")
    String address;

    List<PlacePhoto> photos;

    double rating;

    @SerializedName("price_level")
    int priceLevel;

//    String phone;
    String website;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getRating() {
        return rating;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public String getWebsite() {
        return website;
    }

    public List<PlacePhoto> getPhotos() {
        return photos;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhotos(List<PlacePhoto> photos) {
        this.photos = photos;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Map<String, Object> getDetails() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
    }

    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", photos=" + photos +
                ", rating=" + rating +
                ", priceLevel=" + priceLevel +
                ", website='" + website + '\'' +
                '}';
    }
}
