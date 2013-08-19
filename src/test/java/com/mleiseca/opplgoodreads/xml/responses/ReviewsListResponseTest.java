package com.mleiseca.opplgoodreads.xml.responses;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA. User: mleiseca Date: 8/16/13 Time: 2:51 PM To change this template use File | Settings | File Templates.
 */
public class ReviewsListResponseTest {

    @Before
    public void setup() throws Exception{

    }

    @Test
    public void parsingNormalResponse() throws Exception{
        String input = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("reviews-list-example.xml"));

        Serializer serializer = new Persister();

        ReviewsListResponse response = serializer.read(ReviewsListResponse.class, input);

        assertThat(response.getReviews().size(), is(20));
        assertThat(response.getReviews().get(0).getBook().getTitle(), is("Eyeball to Eyeball"));
        assertThat(response.getReviews().get(0).getBook().getAuthors().size(), is(1));
    }
}
