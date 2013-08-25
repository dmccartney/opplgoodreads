package com.mleiseca.opplgoodreads.libraries.chicagoswan;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA. User: mleiseca Date: 8/24/13 Time: 9:28 PM To change this template use File | Settings | File Templates.
 */
public class ChicagoSwanClientTest {

    ChicagoSwanClient client;

    @Before
    public void setup(){

        client = new ChicagoSwanClient();

    }

    @Test public void buildUrlShouldMatchForTheStand() throws Exception{
        assertThat(client.buildUrl("stephen king", "the stand"), is("http://swanencore.mls.lib.il.us/iii/encore/search/C__S%28the+stand+stephen+king%29+c%3A163__Ff%3Afacetmediatype%3Ab%3Ab%3ABOOK%3A%3A__Orightresult?lang=eng&suite=cobalt"));
    }

}
