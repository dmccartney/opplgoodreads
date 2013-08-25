package com.mleiseca.opplgoodreads.libraries;

/**
 * Created with IntelliJ IDEA. User: mleiseca Date: 8/24/13 Time: 9:19 AM To change this template use File | Settings | File Templates.
 */
public class LibraryQuery {

    private final String author;
    private final String title;
    private final String isbn;

    public LibraryQuery(String author, String title, String isbn) {
        this.author = author;
        this.title = title;
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }
}
