package com.example.android.movies;


import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class MoviesFragment extends Fragment {

    private static CustomAdapter adapter;
    private static FavoritesAdapter favoritesAdapter;
    private static ArrayList<Result> adapterArray = new ArrayList<Result>();
    private static ArrayList<Object> favoritesAdapterArray = new ArrayList<Object>();
    private static AlertDialog alertDialog;
    private static GridView gridView;
    private RecyclerView.LayoutManager layoutManager;
    Parcelable state;




    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("state",gridView.onSaveInstanceState());
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onResume() {
        super.onResume();
        String preference = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("SortBy", "Most Popular");
        switch (preference) {
            case "Most Popular":
                ;
            case "Top Rated":
                updateData();
                break;
            case "Favorites":
                getFavorites();
                break;

        }
    }







    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movies_fragment_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.sortBy_action){
            alertDialog.show();

            // Create Dialog Listener
            View.OnClickListener mClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RadioButton r = (RadioButton) view;
                    String text = r.getText().toString();
                    if(text.equals("Favorites")){
                        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("SortBy", text).commit();
                        alertDialog.hide();
                        getFavorites();
                    }else {
                        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("SortBy", text).commit();
                        alertDialog.hide();
                        updateData();
                    }

                }
            };

            // Retrieve radio buttons
            RadioButton radioButton_one = (RadioButton) alertDialog.findViewById(R.id.most_popular_button);
            RadioButton radioButton_two = (RadioButton) alertDialog.findViewById(R.id.top_rated_button);
            RadioButton radioButton_three = (RadioButton) alertDialog.findViewById(R.id.favorites_button);

            // Set onClick listeners
            radioButton_one.setOnClickListener(mClickListener);
            radioButton_two.setOnClickListener(mClickListener);
            radioButton_three.setOnClickListener(mClickListener);


            // Show The already Selected Option
            String preference = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("SortBy","Most Popular");
            if(preference.equals("Top Rated")){
                radioButton_two.setChecked(true);
            }else if(preference.equals("Most Popular")) {
                radioButton_one.setChecked(true);
            }
            else{
                radioButton_three.setChecked(true);
            }


        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        String preference = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("SortBy","Most Popular");
        gridView = (GridView) rootView.findViewById(R.id.mainGridView);

        if(savedInstanceState!=null)
        state = savedInstanceState.getParcelable("state");


        // initializing the adapter
        adapter   = new CustomAdapter(getActivity(),adapterArray);
        favoritesAdapter = new FavoritesAdapter(getActivity(),favoritesAdapterArray);

        //Getting the date from the internet
        if(preference.equals("Favorites")){
            getFavorites();


        }else {
            new FetchMoviesTask().execute(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("SortBy", "Top Rated"));
        }





        //Creating the sortBy Dialog
        AlertDialog.Builder sortByDialog = new AlertDialog.Builder(getContext());
        sortByDialog.setView(R.layout.sortby_dialogue_box);
        sortByDialog.setCancelable(true);
        alertDialog = sortByDialog.create();


        return rootView ;
    }

    public class FetchMoviesTask extends AsyncTask<String,Void,List<Result>>{
        @Override
        protected List<Result> doInBackground(String...params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            String jsonResponse = null;

            try {
                Uri uri;
                URL url;
                if(((String)params[0]).equals("Top Rated")){
                    uri = Uri.parse("http://api.themoviedb.org/3/movie/top_rated").buildUpon().appendQueryParameter("api_key", com.example.android.movies.BuildConfig.API_KEY).build();
                    url = new URL(uri.toString());

                }else {
                    uri = Uri.parse("http://api.themoviedb.org/3/movie/popular").buildUpon().appendQueryParameter("api_key", com.example.android.movies.BuildConfig.API_KEY).build();
                    url = new URL(uri.toString());
                }

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    jsonResponse = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    jsonResponse = null;
                }
                jsonResponse = buffer.toString();
                return parseResponse(jsonResponse);
            } catch (IOException e) {

                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                jsonResponse = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MoviesFragment", "Error closing stream", e);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Result> results) {
            if(results != null ) {
                // data retrieved correctly
                adapterArray.clear();
                adapter.notifyDataSetChanged();
                for (int i = 0; i < results.size(); i++) {
                    adapterArray.add(results.get(i));
                }
                adapter.notifyDataSetChanged();

            }else{
                // failed to get data
                Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_LONG).show();
            }

            gridView.setAdapter(adapter);
            if(state!=null)
            gridView.onRestoreInstanceState(state);
        }

        // PArsing the json response using Gson
        private List<Result> parseResponse(String jsonResponse){
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            Response response = gson.fromJson(jsonResponse,Response.class);
            List<Result> results = response.getResults();
            return results;

        }


    }

    //Getting the latest data
    private void updateData(){
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("SortBy",""));
    }

    private   void getFavorites(){
        //favoritesAdapterArray.clear();
        gridView.setAdapter(favoritesAdapter);


        ArrayList<Movie> movies = getFavouritesFromDB();


        favoritesAdapterArray.clear();

        for(int i=0;i<movies.size();i++){
            favoritesAdapterArray.add(movies.get(i));
        }

        favoritesAdapter.notifyDataSetChanged();







    }

    public void updateFavoritesArray(){
        gridView.setAdapter(favoritesAdapter);


        ArrayList<Movie> movies = getFavouritesFromDB();


        favoritesAdapterArray.clear();

        for(int i=0;i<movies.size();i++){
            favoritesAdapterArray.add(movies.get(i));
        }

        favoritesAdapter.notifyDataSetChanged();
    }

    private ArrayList<Movie> getFavouritesFromDB(){
        Cursor cursor = getContext().getContentResolver().query(FavouritesContract.FavouriteMovieEntry.CONTENT_URI,null,null,null,null);
        ArrayList<Movie> movies = new ArrayList<Movie>();

        if(cursor.getCount()>0){
            while(cursor.moveToNext()){
                Movie movie = new Movie();
                movie.setTitle(cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouriteMovieEntry.COLUMN_NAME_TITLE)));
                movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouriteMovieEntry.COLUMN_NAME_RELEASE_DATE)));
                movie.setVoteAverage(Double.parseDouble(cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouriteMovieEntry.COLUMN_NAME_VOTE_AVERAGE))));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouriteMovieEntry.COLUMN_NAME_OVERVIEW)));
                movie.setBackdropPath(cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouriteMovieEntry.COLUMN_NAME_BACK_DROP_PATH)));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouriteMovieEntry.COLUMN_NAME_POSTER_PATH)));
                movie.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouriteMovieEntry.COLUMN_NAME_ID))));
                movies.add(movie);
            }
        }
        return movies;
    }

}
