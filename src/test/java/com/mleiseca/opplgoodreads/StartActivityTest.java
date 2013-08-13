package com.mleiseca.opplgoodreads;

import android.widget.TextView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class StartActivityTest {
    @Test
    public void shouldHaveATitle() {
        final StartActivity activity = new StartActivity();
        activity.onCreate(null);
//        TextView title = (TextView) activity.findViewById(R.id.title);
//        assertEquals("Hello World", title.getText());
    }
}
