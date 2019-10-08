package com.alchemist.graylog.plugin.helpers;

import org.graylog2.plugin.Message;
import org.graylog2.plugin.streams.Stream;

import java.util.logging.Logger;

/**
 * Class MessageHelper.
 *
 * @author Alchemist
 */
public final class MessageHelper {
    private static final Logger logger = Logger.getLogger(MessageHelper.class.getName());

    private static final int DEFAULT_LEVEL = 7;

    /**
     * Constructor.
     */
    private MessageHelper() {
    }

    /**
     * Get direct URL to message.
     *
     * @param root    String
     * @param stream  Stream
     * @param message Message
     * @return String
     */
    public static String getURL(final String root, final Stream stream, final Message message) {
        return String.format("%sstreams/%s/search?relative=0&q=_id:%s", root, stream.getId(), message.getId());
    }

    /**
     * Get message level.
     *
     * @param message Message
     * @return int
     */
    public static int getLevel(final Message message) {
        try {
            return ParseHelper.toInt(message.getField("level").toString());
        } catch (final Exception e) {
            logger.warning(String.format("Message level is incorrect: %s", e.getMessage()));
        }
        return DEFAULT_LEVEL;
    }

    /**
     * Get message facility.
     *
     * @param message Message
     * @return String
     */
    public static String getFacility(final Message message) {
        try {
            return message.getField("facility").toString();
        } catch (final Exception e) {
            logger.warning(String.format("Message facility is incorrect: %s", e.getMessage()));
        }
        return null;
    }

    /**
     * Get value.
     *
     * @param message Message
     * @param field   String
     * @return String
     */
    public static String getStringValue(final Message message, final String field) {
        try {
            if (message.hasField(field)) {
                return message.getField(field).toString();
            }
        } catch (final Exception e) {
            logger.warning(String.format("Message field %s cannot be cast to String", e.getMessage()));
        }
        return null;
    }
}
