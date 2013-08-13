package com.mleiseca.opplgoodreads;

import android.widget.TextView;
import com.mleiseca.opplgoodreads.test.support.FakeCurrentTime;
import com.mleiseca.opplgoodreads.test.support.RobolectricTestRunnerWithInjection;
import com.mleiseca.opplgoodreads.util.CurrentTime;
import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunnerWithInjection.class)
public class StarterActivityWithRoboguiceTest {
    @Inject
    CurrentTime currentTime;

    @Test
    public void testCurrentTimeIsInjected() throws Exception {
        ((FakeCurrentTime) currentTime).setCurrentTime(12345L);
//        final StartActivity activity = new StartActivity();
//        activity.onCreate(null);
//        TextView title = (TextView) activity.findViewById(R.id.current_time);
//        assertEquals("12345", title.getText());
    }
}
