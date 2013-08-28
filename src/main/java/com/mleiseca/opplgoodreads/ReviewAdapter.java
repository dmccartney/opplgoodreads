package com.mleiseca.opplgoodreads;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.MapMaker;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mleiseca.opplgoodreads.libraries.LibraryQuery;
import com.mleiseca.opplgoodreads.libraries.LibraryQueryResult;
import com.mleiseca.opplgoodreads.libraries.chicagoswan.ChicagoSwanClient;
import com.mleiseca.opplgoodreads.xml.objects.Review;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA. User: mleiseca Date: 8/18/13 Time: 7:29 AM To change this template use File | Settings | File Templates.
 */
public class ReviewAdapter extends ArrayAdapter<Review> {

    private static final String TAG = ReviewAdapter.class.getSimpleName();
    Context context;
    int layoutResourceId;
    Review[] data;
    ChicagoSwanClient client;
    ExecutorService executorService;

    LoadingCache<LibraryQuery, List<LibraryQueryResult>> libraryData = CacheBuilder.newBuilder()
        .maximumSize(10000)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build(
            new CacheLoader<LibraryQuery, List<LibraryQueryResult>>() {
                public List<LibraryQueryResult> load(LibraryQuery key) throws Exception {
                    return client.performQuery(key);
                }
            });

    public ReviewAdapter(Context context, int layoutResourceId, Review[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.client = new ChicagoSwanClient();
        executorService = Executors.newFixedThreadPool(5);
    }

    @Override public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RewiewHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RewiewHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.txtShelfItemTitle);
            holder.txtAuthor= (TextView)row.findViewById(R.id.txtShelfItemAuthor);
            holder.txtStatus= (TextView)row.findViewById(R.id.txtShelfItemStatus);
            row.setTag(holder);
        }
        else
        {
            holder = (RewiewHolder)row.getTag();
            if(holder.libraryStatusFuture != null){
                holder.libraryStatusFuture.cancel(true);
            }

            if(holder.cancelledFlag != null){
                holder.cancelledFlag.set(true);
            }

        }

        Review review = data[position];
        final RewiewHolder finalHolder = holder;
        final String title = review.getBook().getTitle();
        final String authorName = extractAuthorName(review);
        final AtomicBoolean cancelledFlag = new AtomicBoolean(false);
        holder.txtTitle.setText(title);
        holder.txtAuthor.setText(authorName);
        holder.txtStatus.setText("L");
        holder.cancelledFlag = cancelledFlag;
        holder.libraryStatusFuture = executorService.submit(new Runnable() {
            @Override public void run() {
                try {
                    final List<LibraryQueryResult> results = getLibraryDataForPosition(position);

                    if(cancelledFlag.get()){
                        Log.d(TAG, "Got response from client for "+title+" request was cancelled");
                    }else{
                        Log.d(TAG, "Got response from client for "+title+"...with number of parsed results:" + results.size());
                        finalHolder.txtStatus.post(new Runnable(){
                            @Override public void run() {
                                int size = results.size();
                                finalHolder.txtStatus.setText("" + (size == 0 ? "-" : size));
                            }
                        });
                    }
                } catch (Exception e) {
                    finalHolder.txtStatus.post(new Runnable(){
                        @Override public void run() {
                            finalHolder.txtStatus.setText("ERROR");
                        }
                    });
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });

        return row;


    }

    private String extractAuthorName(Review review) {
        if(review.getBook().getAuthors().isEmpty()){
            return "";
        }else{
//            review.getBook().getAuthors().get(0).g
            //todo: ugh
            return "";
        }
    }

    public List<LibraryQueryResult> getLibraryDataForPosition(int position) throws Exception {
        Review review = data[position];
        final String title = review.getBook().getTitle();
        final String authorName = extractAuthorName(review);

        return libraryData.get(new LibraryQuery(authorName, title, ""));
    }

    private static class RewiewHolder {
        TextView txtTitle;
        TextView txtAuthor;
        TextView txtStatus;
        Future libraryStatusFuture;
        AtomicBoolean cancelledFlag;
    }
}
