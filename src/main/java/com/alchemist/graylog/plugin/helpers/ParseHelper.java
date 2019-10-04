package com.alchemist.graylog.plugin.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class Utils.
 *
 * @author Alchemist
 */
public final class ParseHelper {
    private static final Logger logger = Logger.getLogger(ParseHelper.class.getName());

    /**
     * Constructor.
     */
    private ParseHelper() {
    }

    /**
     * Parse integer.
     *
     * @param value String
     * @return int
     */
    public static int toInt(final String value) {
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            logger.warning(String.format("Error parsing int: %s", e.getMessage()));
        }
        return 0;
    }

    /**
     * Parse string with regex to list.
     *
     * @param value String
     * @return List
     */
    public static List<String> toList(final String value, final String regex) {
        try {
            if (value != null && !value.isEmpty() && regex != null && !regex.isEmpty()) {
                return Arrays.stream(value.split(regex)).map(String::trim).collect(Collectors.toList());
            }
        } catch (final Exception e) {
            logger.warning(String.format("Error parsing list, %s", e.getMessage()));
        }
        return new ArrayList<>();
    }

    /**
     * Parse CSV list.
     *
     * @param value String
     * @return List
     */
    public static List<String> toList(final String value) {
        return toList(value, ",");
    }


}
