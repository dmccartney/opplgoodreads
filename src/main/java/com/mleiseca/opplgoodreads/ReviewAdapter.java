package com.mleiseca.opplgoodreads;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mleiseca.opplgoodreads.xml.objects.Review;

/**
 * Created with IntelliJ IDEA. User: mleiseca Date: 8/18/13 Time: 7:29 AM To change this template use File | Settings | File Templates.
 */
public class ReviewAdapter extends ArrayAdapter<Review> {

    Context context;
    int layoutResourceId;
    Review[] data;

    public ReviewAdapter(Context context, int layoutResourceId, Review[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RewiewHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RewiewHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.txtShelfItemTitle);
            holder.txtAuthor= (TextView)row.findViewById(R.id.txtShelfItemAuthor);

            row.setTag(holder);
        }
        else
        {
            holder = (RewiewHolder)row.getTag();
        }

        Review review = data[position];
        holder.txtTitle.setText(review.getBook().getTitle());
        holder.txtAuthor.setText("author");

        return row;


    }

    private static class RewiewHolder {
        TextView txtTitle;
        TextView txtAuthor;
    }
}
