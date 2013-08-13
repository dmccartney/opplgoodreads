package com.mleiseca.opplgoodreads.test.support;

import com.mleiseca.opplgoodreads.OpplGoodreadsApplication;
import com.mleiseca.opplgoodreads.StartActivity;
import com.mleiseca.opplgoodreads.util.CurrentTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import roboguice.RoboGuice;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class InjectMockAnnotationsTest {
    @InjectMock
    CurrentTime mockCurrentTime;

    private void injectMocks() {
        OpplGoodreadsApplication application = (OpplGoodreadsApplication) Robolectric.application;
        InjectMockModule module = new InjectMockModule();
        InjectMockAnnotations.initInjectMocks(this.getClass(), module, this);

        RoboGuice.setBaseApplicationInjector(application, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(application),
                module);

        RoboGuice.getInjector(application).injectMembers(this);
    }

    @Test
    public void fieldAnnotatedWith_InjectMock_shouldBeSetToMocksOfTheCorrectType() throws Exception {
        injectMocks();
        StartActivity myActivity = new StartActivity();

        myActivity.onCreate(null);

        verify(mockCurrentTime).currentTimeMillis();

        assertThat(myActivity.getCurrentTime()).isEqualTo(mockCurrentTime);
    }
}
