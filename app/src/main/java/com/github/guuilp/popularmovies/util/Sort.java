package com.github.guuilp.popularmovies.util;

/**
 * Created by Guilherme on 22/02/2017.
 */

public enum Sort {
    POPULAR ("Popular"),
    TOP_RATED ("Top Rated");

    private final String code;

    Sort(String code) {
        this.code = code;
    }

    public static Sort fromCode(String code) {
        if (code != null) {
            for (Sort g : Sort.values()) {
                if (code.equalsIgnoreCase(g.code)) {
                    return g;
                }
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return code;
    }
}
