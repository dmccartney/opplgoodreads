package com.mleiseca.opplgoodreads.libraries;

/**
 * Created with IntelliJ IDEA. User: mleiseca Date: 8/23/13 Time: 11:43 PM To change this template use File | Settings | File Templates.
 */
public class LibraryQueryResult {
    private final String title;
    private final String author;
    private final String library;
    private final String callNumber;
    private final String status;

    public LibraryQueryResult(String title, String author, String library, String callNumber, String status) {
        this.title = title;
        this.author = author;
        this.library = library;
        this.callNumber = callNumber;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getLibrary() {
        return library;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public String getStatus() {
        return status;
    }
}
