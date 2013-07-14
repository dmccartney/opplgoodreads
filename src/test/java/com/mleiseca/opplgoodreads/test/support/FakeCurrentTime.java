package com.mleiseca.opplgoodreads.test.support;

import com.mleiseca.opplgoodreads.util.CurrentTime;

public class FakeCurrentTime extends CurrentTime {
    private long fakeTime;

    @Override
    public long currentTimeMillis() {
        return fakeTime;
    }

    public void setCurrentTime(long time) {
        fakeTime = time;
    }
}
