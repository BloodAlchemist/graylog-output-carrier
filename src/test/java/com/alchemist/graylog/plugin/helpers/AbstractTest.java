package com.alchemist.graylog.plugin.helpers;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

import java.util.logging.Logger;

public abstract class AbstractTest {

    @org.mockito.Mock
    private Logger logger;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        new MockUp<Logger>() {
            @Mock
            public Logger getLogger(String name) {
                return logger;
            }
        };
    }
}
