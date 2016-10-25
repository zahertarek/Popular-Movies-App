package com.example.android.movies;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment {

    List<Trailer> trailers;
    List<Review> reviews;
    Result result;
    Realm realm;


    public MovieDetailsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_details_fragment, container, false);



        //Getting the selected Movie Object
        Intent intent = getActivity().getIntent();
        final String title;
        String releaseDate;
        final String voteAverage;
        final String overview;
        final String backdropPath;
        final String posterPath;
        final int id;

        if(getArguments().getBoolean("Favorites")){
           id = (int) getArguments().getLong("Movie");
            RealmConfiguration realmConfig = new RealmConfiguration.Builder(getContext()).build();
            Realm.setDefaultConfiguration(realmConfig);

            // Get a Realm instance for this thread
            realm = Realm.getDefaultInstance();

            Movie movie = realm.where(Movie.class).equalTo("id",id).findFirst();

            title = movie.getTitle();
            releaseDate = movie.getReleaseDate();
            voteAverage = ""+movie.getVoteAverage();
            overview =  movie.getOverview();
            backdropPath = movie.getBackdropPath();
            posterPath = movie.getPosterPath();

        }else{
            result = (Result) getArguments().getSerializable("Movie");
             title = result.getTitle();
             releaseDate = result.getReleaseDate();
             voteAverage = ""+result.getVoteAverage();
             overview = result.getOverview();
             backdropPath = result.getBackdropPath();
             posterPath = result.getPosterPath();
             id =(int) result.getId();


        }


        //Loading Trailers
        new FetchTrailersTask().execute(""+id);

        //Loading Reviews
        new FetchReviewsTask().execute(""+id);

        //Setting the imageViews
        String imageUrl =  "http://image.tmdb.org/t/p/w342"+backdropPath;
        Picasso.with(getActivity()).load(imageUrl).error(R.drawable.loader_anim).placeholder(R.drawable.loader_anim).into((ImageView) rootView.findViewById(R.id.movie_image));
        String frameUrl = "http://image.tmdb.org/t/p/w342"+posterPath;
        Picasso.with(getActivity()).load(frameUrl).into((ImageView) rootView.findViewById(R.id.movie_image_frame));

        //Setting the textViews text
        ( (TextView) rootView.findViewById(R.id.movie_title) ).setText(title);
        ( (TextView) rootView.findViewById(R.id.release_date) ).setText(releaseDate);
        ( (TextView) rootView.findViewById(R.id.vote_average) ).setText(voteAverage);
        ( (TextView) rootView.findViewById(R.id.description) ).setText(overview);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);

        //Set Favourite Button ActionListener
        final ImageButton star = (ImageButton) rootView.findViewById(R.id.favorite);

        // check to set the correct icon on/off

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getContext()).build();
        Realm.setDefaultConfiguration(realmConfig);

        // Get a Realm instance for this thread
         realm = Realm.getDefaultInstance();

        Movie movie = realm.where(Movie.class).equalTo("id",id).findFirst();

        if(movie==null)
            star.setImageResource(android.R.drawable.btn_star_big_off);

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check if this movie is in favourites and then change the icon and do the action
                final Movie movie = realm.where(Movie.class).equalTo("id",id).findFirst();

                if(movie == null ){
                final Movie favoriteMovie = new Movie();
                favoriteMovie.setTitle(title);
                favoriteMovie.setBackdropPath(backdropPath);
                favoriteMovie.setPosterPath(posterPath);
                favoriteMovie.setId(id);
                favoriteMovie.setOverview(overview);
                favoriteMovie.setVoteAverage(Double.parseDouble(voteAverage));

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Movie RealMovie = realm.copyToRealm(favoriteMovie);
                    }

                });
                    star.setImageResource(android.R.drawable.btn_star_big_on);
                }else{
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            movie.removeFromRealm();
                        }
                    });
                    star.setImageResource(android.R.drawable.btn_star_big_off);
                }
                MoviesFragment moviesFragment = (MoviesFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.movies_fragment);
                if(moviesFragment != null){
                    moviesFragment.updateFavoritesArray();
                }

            }
        });




        return rootView;

    }

    public class FetchTrailersTask extends AsyncTask<String,Void,List<Trailer>>{

        @Override
        protected List<Trailer> doInBackground(String... strings) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            String jsonResponse = null;

            try {
                Uri uri;
                URL url;

                    uri = Uri.parse("http://api.themoviedb.org/3/movie/"+strings[0]+"/videos").buildUpon().appendQueryParameter("api_key", BuildConfig.API_KEY).build();
                    url = new URL(uri.toString());


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
                        Log.e("MovieDetailsFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Trailer> trailers) {
            if(getActivity()==null)
                return;
            LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.trailers_layout);
            if (trailers != null) {
                for (int i = 0; i < trailers.size(); i++) {
                    final String trailerKey = trailers.get(i).getKey();
                    ImageView imageView = new ImageView(getContext());
                    imageView.setPadding(30, 30, 30, 30);
                    String imageUrl = "https://img.youtube.com/vi/" + trailerKey + "/sddefault.jpg";
                    Picasso.with(getContext()).load(imageUrl).into(imageView);
                    layout.addView(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailerKey)));
                        }
                    });
                }

            }
            else {
                // failed to get data
                Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_LONG).show();
            }

        }

        private List<Trailer> parseResponse(String jsonResponse){
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            TrailerResult response = gson.fromJson(jsonResponse,TrailerResult.class);
            List<Trailer> results = response.getResults();
            trailers = results ;
            return results;

        }



    }


    public class FetchReviewsTask extends AsyncTask<String,Void,List<Review>>{

        @Override
        protected List<Review> doInBackground(String... strings) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            String jsonResponse = null;

            try {
                Uri uri;
                URL url;

                uri = Uri.parse("http://api.themoviedb.org/3/movie/"+strings[0]+"/reviews").buildUpon().appendQueryParameter("api_key", BuildConfig.API_KEY).build();
                url = new URL(uri.toString());


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
                        Log.e("MovieDetailsFragment", "Error closing stream", e);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            LinearLayout layout;
            if(getActivity()==null)
                return;
                layout = (LinearLayout) getActivity().findViewById(R.id.reviews);
            if (reviews != null) {
                for (int i = 0; i < reviews.size(); i++) {
                    String author = reviews.get(i).getAuthor();
                    String content = reviews.get(i).getContent();

                    TextView authorView = new TextView(getContext());
                    TextView contentView = new TextView(getContext());

                    authorView.setText("Author: " + author);
                    authorView.setTextColor(Color.BLACK);
                    authorView.setPadding(20,20,20,60);
                    contentView.setText(content);
                    contentView.setPadding(20,20,20,60);

                    layout.addView(authorView);
                    layout.addView(contentView);
                }

            }
            else
            {
                // failed to get data
                Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_LONG).show();
            }
        }

        private List<Review> parseResponse(String jsonResponse){
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            ReviewResult response = gson.fromJson(jsonResponse,ReviewResult.class);
            List<Review> results = response.getResults();
            reviews = results;
            return results;

        }



    }





}
