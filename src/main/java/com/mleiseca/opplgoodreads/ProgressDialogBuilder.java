package com.mleiseca.opplgoodreads;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA. User: mleiseca Date: 8/15/13 Time: 10:28 PM To change this template use File | Settings | File Templates.
 */
public class ProgressDialogBuilder {

    // TODO inject this
    static public Dialog makeDialog(final Context ctx) {
        final LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.progress_dialog, null);
        Dialog dialog = new Dialog(ctx);
        dialog.setContentView(layout);
        return dialog;
    }
}
