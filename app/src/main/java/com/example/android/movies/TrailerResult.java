package com.example.android.movies;

import java.util.List;

/**
 * Created by zaher on 8/31/16.
 */
public class TrailerResult {

    private int id;

    private List<Trailer> results;

    public List<Trailer> getResults() {
        return results;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
