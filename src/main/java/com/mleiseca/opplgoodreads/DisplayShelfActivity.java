package com.mleiseca.opplgoodreads;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.mleiseca.opplgoodreads.libraries.LibraryQueryResult;
import com.mleiseca.opplgoodreads.xml.objects.Review;

import java.util.List;

import javax.annotation.Nullable;

import roboguice.activity.RoboListActivity;

/**
 * Created with IntelliJ IDEA. User: mleiseca Date: 8/16/13 Time: 3:53 PM To change this template use File | Settings | File Templates.
 */
public class DisplayShelfActivity extends RoboListActivity {

    @Inject
    GoodreadsAPI mGoodreadsAPI;

    ProgressDialog pd;
    private Context context;

    transient ReviewAdapter reviewAdapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.

        View header = (View)getLayoutInflater().inflate(R.layout.shelf_header_row, null);
        getListView().addHeaderView(header);

        context = this;

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(context);
                pd.setTitle("Loading...");
                pd.setMessage("Please wait.");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                final List<Review> reviews = mGoodreadsAPI.retrieveBooksOnShelf("to-read");
                //        List<Review> reviews = mGoodreadsAPI.retrieveBooksOnShelf("currently-reading");
                pd.dismiss();
                pd = null;

                getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                        try {
                            //todo: ack! +1 because of the header!
                            if(position == 0){
                                return;
                            }
                            List<LibraryQueryResult> results = reviewAdapter.getLibraryDataForPosition(position - 1);
                            if(results.size() > 0){
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Locations");
//                                builder.setTitle(R.string.pick_color);
                                ImmutableList<String>
                                    objects =
                                    ImmutableList.copyOf(Iterables.transform(results, new Function<LibraryQueryResult, String>() {
                                        @Nullable @Override public String apply(@Nullable LibraryQueryResult libraryQueryResult) {
                                            return libraryQueryResult == null ? "" : libraryQueryResult.getCallNumber() + " (" + libraryQueryResult.getStatus()+")";
                                        }
                                    }));
                                CharSequence[] data = objects.toArray(new CharSequence[objects.size()]);

                                builder.setItems(data, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // The 'which' argument contains the index position
                                        // of the selected item
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                });

                getListView().post(new Runnable() {
                    @Override public void run() {
                        reviewAdapter = new ReviewAdapter(context,R.layout.shelf_item_row, reviews.toArray(new Review[reviews.size()]));
                        getListView().setAdapter(reviewAdapter);
                    }
                });

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (pd!=null) {
                    pd.dismiss();
                }
            }

        };
        task.execute((Void[])null);
    }
}
