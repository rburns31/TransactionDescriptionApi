package com.ryan.controller;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles directing the URL pattern to the correct database query as well
 *     as validating any inputs that are passed in through the URL
 * @author Ryan Burns
 * @version 1.0 - 6/30/2015
 */
@Controller
public class BaseController {
    /**
     * Flip this between LocalDBApp and CloudDBApp to change the location of
     *     the database that will be used
     */
    private final DBApp dbApp = new CloudDBApp();
    /**
     * A mapping from the language code to its full name (i.e. "e"->"english")
     */
    private final static HashMap<String, String> codeToLang = new HashMap<>();
    static {
        codeToLang.put("e", "english");
        codeToLang.put("f", "french");
    }

    /**
     * Creates an entry in the database
     * @param language The desired language, passed in through the URL
     * @param tCode The transaction code, passed in through the URL
     * @param tDescription The trans. description, passed in through the URL
     * @return A response with an empty json body and a HTTP status code
     */
    @RequestMapping(value = "/create/{language}/{tCode}/{tDescription}",
            method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> createEntry(@PathVariable String language,
            @PathVariable String tCode, @PathVariable String tDescription) {
        ValidatedInput inputs = validateInputs(language, tCode);
        if (inputs.getHttpStatus() == HttpStatus.OK) {
            return dbApp.createEntry(inputs.getTCodeInt(),
                    inputs.getLanguageCode(), tDescription);
        }
        return new ResponseEntity<>("{ }", inputs.getHttpStatus());
    }

    /**
     * Reads an entry from the database
     * @param language The desired language, passed in through the URL
     * @param tCode The transaction code, passed in through the URL
     * @return A response with a json body and a HTTP status code
     */
    @RequestMapping(value = "/{language}/{tCode}", method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<String> readEntry(@PathVariable String language,
            @PathVariable String tCode) {
        ValidatedInput inputs = validateInputs(language, tCode);
        if (inputs.getHttpStatus() == HttpStatus.OK) {
            return dbApp.readEntry(inputs.getTCodeInt(),
                    inputs.getLanguageCode());
        }
        return new ResponseEntity<>("{ }", inputs.getHttpStatus());
    }

    /**
     * Updates an entry in the database
     * @param language The desired language, passed in through the URL
     * @param tCode The transaction code, passed in through the URL
     * @param tDescription The trans. description, passed in through the URL
     * @return A response with an empty json body and a HTTP status code
     */
    @RequestMapping(value = "/update/{language}/{tCode}/{tDescription}",
            method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<String> updateEntry(@PathVariable String language,
            @PathVariable String tCode, @PathVariable String tDescription) {
        ValidatedInput inputs = validateInputs(language, tCode);
        if (inputs.getHttpStatus() == HttpStatus.OK) {
            return dbApp.updateEntry(inputs.getTCodeInt(),
                    inputs.getLanguageCode(), tDescription);
        }
        return new ResponseEntity<>("{ }", inputs.getHttpStatus());
    }

    /**
     * Deletes an entry from the database
     * @param language The desired language, passed in through the URL
     * @param tCode The transaction code, passed in through the URL
     * @return A response with an empty json body and a HTTP status code
     */
    @RequestMapping(value = "/delete/{language}/{tCode}",
            method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<String> deleteEntry(@PathVariable String language,
            @PathVariable String tCode) {
        ValidatedInput inputs = validateInputs(language, tCode);
        if (inputs.getHttpStatus() == HttpStatus.OK) {
            return dbApp.deleteEntry(inputs.getTCodeInt(),
                    inputs.getLanguageCode());
        }
        return new ResponseEntity<>("{ }", inputs.getHttpStatus());
    }

    /**
     * Validates the transaction code and language passed in through the URL
     * @param language The language to be validated
     * @param tCode The transaction code to be validated
     * @return A ValidatedInput object holding all of the necessary valid input
     */
    private ValidatedInput validateInputs(String language, String tCode) {
        int tCodeInt = 0;
        language = language.toLowerCase();
        String languageCode = language;
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            // If they passed in the full language name
            if (language.length() > 1) {
                languageCode = langToCode(language);
            } else {
                if (codeToLang.containsKey(languageCode)) {
                    language = codeToLang.get(languageCode);
                } else {
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            }
            tCodeInt = Integer.parseInt(tCode);
        } catch (NumberFormatException nfe) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        if (languageCode == null || !codeToLang.containsValue(language)) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ValidatedInput(language, languageCode, tCodeInt, httpStatus);
    }

    /**
     * A mapping from the language's full name to its code (i.e. "english"->"e")
     * @param lang The full name of the language
     * @return The code of that language, or null if there isn't one
     */
    private String langToCode(String lang) {
        for (String code : codeToLang.keySet()) {
            if (codeToLang.get(code).equals(lang)) {
                return code;
            }
        }
        return null;
    }
}