package org.cameronmoreau.aryelp.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.cameronmoreau.aryelp.DetailActivity;
import org.cameronmoreau.aryelp.R;
import org.cameronmoreau.aryelp.models.Place;

import java.util.ArrayList;

/**
 * Created by Cameron on 10/2/16.
 */

public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.ViewHolder> {

    ArrayList<Place> places;

    public PlacesListAdapter() {
        this.places = new ArrayList<>();
    }

    public void addPlace(Place place) {
        places.add(place);
        this.notifyItemInserted(places.size() - 1);
    }

    @Override
    public PlacesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.places_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlacesListAdapter.ViewHolder holder, int position) {
        holder.setPlace(places.get(position));
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        String id;

        TextView tvName, tvPrice;
        RatingBar ratingBar;
        ImageView ivMain;

        ViewHolder(View view) {
            super(view);

            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvPrice = (TextView) view.findViewById(R.id.tv_price);
            ratingBar = (RatingBar) view.findViewById(R.id.rating);
            ivMain = (ImageView) view.findViewById(R.id.iv_main);
            view.setOnClickListener(this);
        }

        void setPlace(Place place) {
            id = place.getId();

            tvName.setText(place.getName());
            ratingBar.setRating((float) place.getRating());
            setPrice(place.getPriceLevel());

            if(place.getPhotos() != null) {
               if(place.getPhotos().size() > 0) {
                   String url = place.getPhotos().get(0).getUrl();

                   Picasso.with(tvName.getContext())
                           .load(url)
                           .resize(200, 200)
                           .centerCrop()
                           .into(ivMain);
               }
            }
        }

        private void setPrice(int price) {
            tvPrice.setText("");
            for(int i = 0; i < price + 1; i++) {
                tvPrice.append("$");
            }
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), DetailActivity.class);
            i.putExtra(DetailActivity.ID, id);
            v.getContext().startActivity(i);
        }
    }
}
