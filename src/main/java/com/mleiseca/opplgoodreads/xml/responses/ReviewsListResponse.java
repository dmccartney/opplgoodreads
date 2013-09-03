package com.mleiseca.opplgoodreads.xml.responses;

import com.google.common.collect.ForwardingList;

import com.mleiseca.opplgoodreads.xml.objects.Review;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: mleiseca Date: 8/16/13 Time: 2:49 PM To change this template use File | Settings | File Templates.
 */
@Root(name = "GoodreadsResponse", strict = false)
public class ReviewsListResponse {

    //    <reviews start="1" end="20" total="379">
    @Element Reviews reviews;

    public Integer getStart() {
        return reviews.start;
    }

    public Integer getEnd() {
        return reviews.end;
    }

    public Integer getTotal() {
        return reviews.total;
    }

    public List<Review> getReviewList() {
        return reviews.review;
    }

    static class Reviews{

        @Attribute
        Integer start;
        @Attribute
        Integer end;
        @Attribute
        Integer total;

        @ElementList(inline = true)
        ArrayList<Review> review;

    }
}
