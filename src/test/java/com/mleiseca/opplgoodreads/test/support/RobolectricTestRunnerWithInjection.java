package com.mleiseca.opplgoodreads.test.support;

import com.mleiseca.opplgoodreads.ApplicationModule;
import com.mleiseca.opplgoodreads.MySampleApplication;
import com.mleiseca.opplgoodreads.util.CurrentTime;
import org.junit.runners.model.InitializationError;
import org.mockito.MockitoAnnotations;
import org.robolectric.DefaultTestLifecycle;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.TestLifecycle;
import roboguice.RoboGuice;

public class RobolectricTestRunnerWithInjection extends RobolectricTestRunner {

    public RobolectricTestRunnerWithInjection(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected Class<? extends TestLifecycle> getTestLifecycleClass() {
        return TestLifeCycleWithInjection.class;
    }


    public static class TestApplicationModule extends ApplicationModule {
        @Override
        public void configure() {
            bind(CurrentTime.class).toInstance(new FakeCurrentTime());

            super.configure();
        }
    }

    public static class TestLifeCycleWithInjection extends DefaultTestLifecycle {
        @Override
        public void prepareTest(Object test) {
            MySampleApplication application = (MySampleApplication) Robolectric.application;


            RoboGuice.setBaseApplicationInjector(application, RoboGuice.DEFAULT_STAGE,
                    RoboGuice.newDefaultRoboModule(application), new TestApplicationModule());

            RoboGuice.getInjector(application).injectMembers(test);
            MockitoAnnotations.initMocks(test);
        }
    }
}
