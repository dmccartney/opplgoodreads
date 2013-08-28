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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LibraryQuery that = (LibraryQuery) o;

        if (author != null ? !author.equals(that.author) : that.author != null) {
            return false;
        }
        if (isbn != null ? !isbn.equals(that.isbn) : that.isbn != null) {
            return false;
        }
        if (title != null ? !title.equals(that.title) : that.title != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = author != null ? author.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (isbn != null ? isbn.hashCode() : 0);
        return result;
    }
}
