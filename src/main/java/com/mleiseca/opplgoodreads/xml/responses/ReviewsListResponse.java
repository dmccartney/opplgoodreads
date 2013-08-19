package com.mleiseca.opplgoodreads.xml.responses;

import com.mleiseca.opplgoodreads.xml.objects.Review;

import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: mleiseca Date: 8/16/13 Time: 2:49 PM To change this template use File | Settings | File Templates.
 */
@Root(name = "GoodreadsResponse", strict = false)
public class ReviewsListResponse {

    @ElementList
    private List<Review> reviews;

    public List<Review> getReviews() {
        return reviews;
    }
}
