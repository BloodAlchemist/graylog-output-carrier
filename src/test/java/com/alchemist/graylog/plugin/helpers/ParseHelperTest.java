package com.alchemist.graylog.plugin.helpers;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public final class ParseHelperTest extends AbstractTest {

    private static final List<String> LIST = new ArrayList<>(Arrays.asList("one", "two"));
    private static final Map<String, List<String>> MAP = new HashMap<String, List<String>>() {{
        put("key", LIST);
    }};

    @Test
    public void testToInt() {
        assertEquals(0, ParseHelper.toInt(null));
        assertEquals(0, ParseHelper.toInt(""));
        assertEquals(0, ParseHelper.toInt("test"));
        assertEquals(0, ParseHelper.toInt("9999test"));

        assertEquals(9999, ParseHelper.toInt("9999"));
        assertEquals(-9999, ParseHelper.toInt("-9999"));
    }

    @Test
    public void testToList() {
        assertEquals(new ArrayList<>(), ParseHelper.toList(null));
        assertEquals(new ArrayList<>(), ParseHelper.toList(""));
        assertEquals(new ArrayList<>(), ParseHelper.toList(",", "\\"));

        assertEquals(LIST, ParseHelper.toList("one, two"));
    }

    @Test
    public void testToMapList() {
        assertNull(ParseHelper.toMapList(null));
        assertNull(ParseHelper.toMapList(""));
        assertNull(ParseHelper.toMapList("test"));

        assertEquals(MAP, ParseHelper.toMapList("{\"key\":[\"one\",\"two\"]}"));
    }
}
