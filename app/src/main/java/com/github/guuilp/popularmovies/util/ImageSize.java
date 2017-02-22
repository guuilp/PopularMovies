package com.github.guuilp.popularmovies.util;

/**
 * Created by Guilherme on 22/02/2017.
 */

public enum ImageSize {
    SMALL("w185"),
    DEFAULT("w500"),
    A_LITTLE_BIGGER("w780");

    private final String code;

    ImageSize(String code) {
        this.code = code;
    }

    public static ImageSize fromCode(String code) {
        if (code != null) {
            for (ImageSize g : ImageSize.values()) {
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