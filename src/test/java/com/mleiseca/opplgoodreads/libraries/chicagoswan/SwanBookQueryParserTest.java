package com.mleiseca.opplgoodreads.libraries.chicagoswan;

import com.mleiseca.opplgoodreads.libraries.LibraryQueryResult;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA. User: mleiseca Date: 8/23/13 Time: 11:28 PM To change this template use File | Settings | File Templates.
 */
public class SwanBookQueryParserTest {

    @Before
    public void setup(){

    }

    @Test public void happyPathShouldExtractTheStand() throws Exception{
        String input = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("chicago-swan-response.html"));

        SwanBookQueryParser parser = new SwanBookQueryParser();

        List<LibraryQueryResult> results =  parser.parse(input);

        assertThat(results.size(), is(10));
//        System.out.println(results);

    }
}
