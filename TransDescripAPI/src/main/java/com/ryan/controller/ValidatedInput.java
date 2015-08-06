package com.ryan.controller;

import org.springframework.http.HttpStatus;

/**
 * An object that holds all data that needs to be passed from the
 *     validation method back to the CRUD methods to utilize
 * @author Ryan Burns
 * @version 1.0 - 6/30/2015
 */
public class ValidatedInput {
    /**
     * The full language name (i.e. "english")
     */
    private final String language;
    /**
     * The language code, which is the first letter of the name (i.e. "e")
     */
    private final String languageCode;
    /**
     * The transaction code as an integer
     */
    private final int tCodeInt;
    /**
     * The HTTP status code, which will either be 200 if the inputs are valid
     *     or 400 if they are invalid
     */
    private final HttpStatus httpStatus;

    /**
     * The default constructor, takes in all parameters
     */
    public ValidatedInput(String language, String languageCode,
            int tCodeInt, HttpStatus httpStatus) {
        this.language = language;
        this.languageCode = languageCode;
        this.tCodeInt = tCodeInt;
        this.httpStatus = httpStatus;
    }

    public String getLanguage() {
        return language;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public int getTCodeInt() {
        return tCodeInt;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}