package com.mleiseca.opplgoodreads;

import android.app.Application;
import roboguice.RoboGuice;

public class OpplGoodreadsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new ApplicationModule());
    }
}
