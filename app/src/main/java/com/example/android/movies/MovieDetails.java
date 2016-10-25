package com.example.android.movies;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class MovieDetails extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Bundle extras = getIntent().getExtras();
        if(savedInstanceState==null){
            MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
            movieDetailsFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().add(R.id.movie_detail_container,movieDetailsFragment).commit();
        }
    }


}
