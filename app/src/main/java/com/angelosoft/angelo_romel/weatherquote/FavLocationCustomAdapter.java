package com.angelosoft.angelo_romel.weatherquote;

import android.graphics.drawable.AnimationDrawable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by angelo_romel on 30/05/2016.
 */
public class FavLocationCustomAdapter extends ArrayAdapter<String> {
    public String[] favPlace;
    Fragment frag;
    Context context;

    public FavLocationCustomAdapter(Context c, Fragment frag, String[] favPlace) {
        super(c, R.layout.listitem_fav_location, R.id.city_textview, favPlace);
        this.favPlace = favPlace;
        this.frag = frag;
        context = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.listitem_fav_location, parent, false);
        ImageView placeImageView = (ImageView)row.findViewById(R.id.image_view);
        TextView favPlace = (TextView)row.findViewById(R.id.city_textview);

        favPlace.setText(this.favPlace[position]);
        //placeImageView.setImageResource(R.drawable.current_place_animation);
        return row;
    }
}
