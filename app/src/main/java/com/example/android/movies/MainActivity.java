package com.example.android.movies;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.widget.GridView;

public class MainActivity extends ActionBarActivity {

    public boolean isTwoPane;
    private Parcelable state;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.movie_detail_container)!=null){
            isTwoPane = true;
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction().add(R.id.movie_detail_container,new Fragment()).commit();
            }
        }else{
            if(savedInstanceState == null){
               getSupportFragmentManager().beginTransaction().add(R.id.movies_container,new MoviesFragment()).commit();
                gridView = (GridView) findViewById(R.id.mainGridView);
            }

            isTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    protected void onPause() {

        super.onPause();

    }

    @Override
    protected void onResume() {

        super.onResume();
    }
}
