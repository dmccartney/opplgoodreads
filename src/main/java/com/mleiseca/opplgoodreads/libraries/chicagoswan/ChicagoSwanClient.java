package com.mleiseca.opplgoodreads.libraries.chicagoswan;

import com.google.common.base.Joiner;

import android.util.Log;

import com.mleiseca.opplgoodreads.libraries.LibraryQuery;
import com.mleiseca.opplgoodreads.libraries.LibraryQueryResult;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: mleiseca Date: 8/24/13 Time: 9:19 AM To change this template use File | Settings | File Templates.
 */
public class ChicagoSwanClient {

    public static final String TAG = ChicagoSwanClient.class.getSimpleName();

    private static final String URL_START_FORMAT =
    "http://swanencore.mls.lib.il.us/iii/encore/search/C__S%28";
    private static final String URL_END_FORMAT =
        "%29+c%3A163__Ff%3Afacetmediatype%3Ab%3Ab%3ABOOK%3A%3A__Orightresult?lang=eng&suite=cobalt";
    private final SwanBookQueryParser parser;

    public ChicagoSwanClient() {
        this.parser = new SwanBookQueryParser();
    }

    public List<LibraryQueryResult> performQuery(LibraryQuery query) throws Exception{
        String url = buildUrl(query.getAuthor(), query.getTitle());

        HttpGet get = new HttpGet(url);
        HttpResponse response = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        response = httpClient.execute(get);
        InputStream is = response.getEntity().getContent();
        String output = IOUtils.toString(is, "UTF-8");

        Log.d(TAG, output);

        return parser.parse(output);
    }

    String buildUrl(String author, String title) {
        return URL_START_FORMAT +
               Joiner.on('+').join(Arrays.asList(title.split("\\s"))) +
               "+" +
               Joiner.on('+').join(Arrays.asList(author.split("\\s")))  +
               URL_END_FORMAT;
    }
}
