package com.mleiseca.opplgoodreads;

import com.google.inject.Inject;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.mleiseca.opplgoodreads.xml.objects.Review;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: mleiseca Date: 8/16/13 Time: 3:53 PM To change this template use File | Settings | File Templates.
 */
public class DisplayShelfActivity extends ListActivity {

    @Inject
    GoodreadsAPI mGoodreadsAPI;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.

        //todo: is there a nice way to inject this?
        mGoodreadsAPI = new GoodreadsAPI(this, null);

//        setContentView(R.layout.shelf);

        View header = (View)getLayoutInflater().inflate(R.layout.shelf_header_row, null);
        getListView().addHeaderView(header);

        List<Review> reviews = mGoodreadsAPI.retrieveBooksOnShelf("to-read");
        final ListAdapter adapter = new ReviewAdapter(this,R.layout.shelf_item_row, reviews.toArray(new Review[reviews.size()]));
        getListView().setAdapter(adapter);


    }
}
