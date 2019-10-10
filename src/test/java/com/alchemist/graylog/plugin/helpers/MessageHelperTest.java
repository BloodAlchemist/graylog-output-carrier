package com.alchemist.graylog.plugin.helpers;

import org.graylog2.plugin.Message;
import org.graylog2.plugin.streams.Stream;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

public final class MessageHelperTest extends AbstractTest {

    private final String TEST = "test";

    @Mock
    private Stream stream;
    @Mock
    private Message message;

    @Test
    public void testGetURL() {
        assertEquals("%sstreams/%s/search?relative=0&q=_id:%s", MessageHelper.URL_TPL);

        when(stream.getId())
                .thenReturn("stream");
        when(message.getId())
                .thenReturn("message");

        assertEquals("http://test.com/streams/stream/search?relative=0&q=_id:message", MessageHelper.getURL("http://test.com/", stream, message));
    }

    @Test
    public void testGetLevel() {
        final String LEVEL = "level";

        assertEquals(7, MessageHelper.DEFAULT_LEVEL);
        assertEquals(MessageHelper.DEFAULT_LEVEL, MessageHelper.getLevel(null));

        when(message.getField(LEVEL))
                .thenReturn(null);
        assertEquals(MessageHelper.DEFAULT_LEVEL, MessageHelper.getLevel(message));

        when(message.getField(LEVEL))
                .thenReturn(3);
        assertEquals(3, MessageHelper.getLevel(message));
    }

    @Test
    public void testGetFacility() {
        final String FACILITY = "facility";

        assertNull(MessageHelper.getFacility(null));

        when(message.getField(FACILITY))
                .thenReturn(null);
        assertNull(MessageHelper.getFacility(message));

        when(message.getField(FACILITY))
                .thenReturn(TEST);
        assertEquals(TEST, MessageHelper.getFacility(message));
    }

    @Test
    public void testGetStringValue() {
        assertNull(MessageHelper.getStringValue(null, null));
        assertNull(MessageHelper.getStringValue(message, null));
        assertNull(MessageHelper.getStringValue(message, ""));

        when(message.hasField(TEST))
                .thenReturn(false);
        assertNull(MessageHelper.getStringValue(message, TEST));

        when(message.hasField(TEST))
                .thenReturn(true);
        when(message.getField(TEST))
                .thenReturn(null);
        assertNull(MessageHelper.getStringValue(message, TEST));

        when(message.hasField(TEST))
                .thenReturn(true);
        when(message.getField(TEST))
                .thenReturn(TEST);
        assertEquals(TEST, MessageHelper.getStringValue(message, TEST));
    }
}
