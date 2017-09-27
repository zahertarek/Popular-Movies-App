package com.example.android.movies;

import android.provider.BaseColumns;

/**
 * Created by New on 9/26/2017.
 */

public final class FavouritesContract {

    private FavouritesContract() {}

    public static class FavouriteMovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "favouriteMovie" ;
        public static final String COLUMN_NAME_TITLE = "title" ;
        public static final String COLUMN_NAME_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_NAME_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_BACK_DROP_PATH = "backDropPath";
        public static final String COLUMN_NAME_POSTER_PATH = "posterPath";
        public static final String COLUMN_NAME_ID = "id";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_TITLE + " TEXT," +
                        COLUMN_NAME_RELEASE_DATE + " TEXT,"+
                        COLUMN_NAME_VOTE_AVERAGE + " TEXT,"+
                        COLUMN_NAME_OVERVIEW + " TEXT,"+
                        COLUMN_NAME_BACK_DROP_PATH + " TEXT,"+
                        COLUMN_NAME_ID+ " TEXT,"+
                        COLUMN_NAME_POSTER_PATH+ " TEXT)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }
}
