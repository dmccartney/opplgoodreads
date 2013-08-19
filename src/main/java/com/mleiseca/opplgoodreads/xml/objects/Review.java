package com.mleiseca.opplgoodreads.xml.objects;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created with IntelliJ IDEA. User: mleiseca Date: 8/16/13 Time: 3:07 PM To change this template use File | Settings | File Templates.
 */
@Root(strict = false)
public class Review {

    @Element
    String id;

    @Element
    Book book;


    public String getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }
}
