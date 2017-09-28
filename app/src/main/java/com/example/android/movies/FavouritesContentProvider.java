package com.example.android.movies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by New on 9/28/2017.
 */

public class FavouritesContentProvider extends ContentProvider {

    FavouriteMovieDbHelper favouriteMovieDbHelper;

    public static final int FAVOURITES = 100;
    public static final int FAVOURITE_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();


    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavouritesContract.AUTHORITY,FavouritesContract.PATH_FAVOURITES,FAVOURITES);
        uriMatcher.addURI(FavouritesContract.AUTHORITY,FavouritesContract.PATH_FAVOURITES+"/#",FAVOURITE_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        favouriteMovieDbHelper = new FavouriteMovieDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = favouriteMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        switch (match){
            case FAVOURITES:
                long id = db.insert(FavouritesContract.FavouriteMovieEntry.TABLE_NAME,null,contentValues);
                return ContentUris.withAppendedId(FavouritesContract.FavouriteMovieEntry.CONTENT_URI,id);

        }

        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        final SQLiteDatabase db = favouriteMovieDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        switch(match) {
            case FAVOURITES:
                return db.query(FavouritesContract.FavouriteMovieEntry.TABLE_NAME, null, null, null, null, null,
                        FavouritesContract.FavouriteMovieEntry.COLUMN_NAME_ID);

            case FAVOURITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                return db.query(FavouritesContract.FavouriteMovieEntry.TABLE_NAME, null, FavouritesContract.FavouriteMovieEntry.COLUMN_NAME_ID + "=?", new String[]{id}, null, null, null, null);
        }

        return null;
    }



    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        final SQLiteDatabase db = favouriteMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match){
            case FAVOURITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                return db.delete(FavouritesContract.FavouriteMovieEntry.TABLE_NAME, FavouritesContract.FavouriteMovieEntry.COLUMN_NAME_ID+"="+id,null);

        }



        return 0;
    }
}
