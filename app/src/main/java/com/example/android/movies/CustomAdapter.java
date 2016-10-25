package com.example.android.movies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class CustomAdapter extends BaseAdapter{

    List<Result> result;
    Context context;

    private static LayoutInflater inflater=null;
    public CustomAdapter(Activity mainActivity, ArrayList<Result> results) {
        // TODO Auto-generated constructor stub
        result=results;
        context=mainActivity;
          inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public static class Holder
    {
        TextView tv;
        ImageView img;
        public Holder(View rowView){
            tv=(TextView) rowView.findViewById(R.id.grid_item);
            img = (ImageView) rowView.findViewById(R.id.movie_icon);
        }

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View rowView;
        rowView = inflater.inflate(R.layout.fragment_movies_grid_item, null);
        Holder holder=new Holder(rowView);

        holder.tv.setText(result.get(position).getTitle());

        String imageUrl = "http://image.tmdb.org/t/p/w185"+result.get(position).getPosterPath();
        Picasso.with(context).load(imageUrl).into(holder.img);




        rowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (!((MainActivity) context).isTwoPane) {
                    Intent intent = new Intent(context, MovieDetails.class);
                    intent.putExtra("Movie", result.get(position));
                    context.startActivity(intent);
                }else{
                    MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
                    Bundle extras = new Bundle();
                    extras.putSerializable("Movie", result.get(position));
                    movieDetailsFragment.setArguments(extras);
                    ((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container,movieDetailsFragment).commit();

                }
            }
        });

        return rowView;
    }

}