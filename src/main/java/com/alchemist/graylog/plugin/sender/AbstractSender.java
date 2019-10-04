package com.alchemist.graylog.plugin.sender;

import com.alchemist.graylog.plugin.helpers.MessageHelper;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.streams.Stream;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

/**
 * Class AbstractSender.
 *
 * @author Alchemist
 */
public abstract class AbstractSender implements ISender {

    private static final String TEXT_TPL = "%s...";

    protected final URL webhook;
    protected final String channel;
    protected final String graylogUrl;
    protected final int textLimit;
    protected final Stream stream;
    protected final List<String> fields;

    /**
     * Constructor.
     *
     * @param stream           Stream
     * @param webhookUrl       String
     * @param channel          String
     * @param graylogUrl       String
     * @param textLimit        int
     * @param additionalFields List
     */
    public AbstractSender(final Stream stream, final String webhookUrl, final String channel, final String graylogUrl,
                          final int textLimit, final List<String> additionalFields) throws Exception {
        this.stream = stream;

        // Set webhook URL
        try {
            this.webhook = new URL(webhookUrl);
        } catch (final MalformedURLException e) {
            throw new Exception("Error while constructing webhook URL", e);
        }

        this.channel = channel;
        this.graylogUrl = graylogUrl;
        this.textLimit = textLimit;
        this.fields = new ArrayList<String>() {{
            add("app");
            add("env");
            add("source");
        }};

        if (fields != null && !fields.isEmpty()) {
            this.fields.addAll(additionalFields);
        }
    }

    /**
     * Perform message.
     *
     * @param message Message
     * @throws Exception Exception
     */
    @Override
    public void perform(final Message message) throws Exception {
        send(prepare(message));
    }

    /**
     * Send JSON message.
     *
     * @param json String
     * @return String
     * @throws Exception Exception
     */
    protected String send(final String json) throws Exception {
        // Prepare request
        final HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) webhook.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
        } catch (final IOException e) {
            throw new Exception("Could not open connection to webhook API", e);
        }

        // Send request
        try (final Writer writer = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
            writer.write(json);
            writer.flush();
        } catch (final IOException e) {
            throw new Exception("Could not POST to webhook API", e);
        }

        // Check response
        if (conn.getResponseCode() == 200) {
            try (final InputStream responseStream = conn.getInputStream()) {
                final byte[] responseBytes = ByteStreams.toByteArray(responseStream);
                return new String(responseBytes, Charsets.UTF_8);

            } catch (final IOException e) {
                throw new Exception("Could not read response body from webhook API", e);
            }
        } else if (conn.getResponseCode() == 429) {
            throw new Exception("Too many requests");
        } else if (conn.getResponseCode() == 400) {
            throw new Exception(String.format("Bad request on message: %s", json));
        } else {
            throw new Exception(String.format("Unexpected HTTP response status %s", conn.getResponseCode()));
        }
    }

    /**
     * Prepare message.
     *
     * @param message Message
     * @return String
     * @throws Exception Exception
     */
    protected abstract String prepare(final Message message) throws Exception;

    /**
     * Get message URL.
     *
     * @param message Message
     * @return String
     */
    protected String getUrl(final Message message) {
        return MessageHelper.getURL(graylogUrl, stream, message);
    }

    /**
     * Get message timestamp.
     *
     * @param message Message
     * @return String
     */
    protected String getTimestamp(final Message message) {
        return message.getTimestamp()
                .toDateTime(DateTimeZone.getDefault())
                .toString(DateTimeFormat.mediumDateTime());
    }

    /**
     * Get message pre-text.
     *
     * @param message String
     * @param format  String
     * @return String
     */
    protected String getPretext(final Message message, final String format) {
        final StringBuilder buf = new StringBuilder();
        for (final String field : fields) {
            if (message.hasField(field)) {
                buf.append(String.format(format, field, message.getField(field)));
            }
        }
        return buf.toString().trim();
    }

    /**
     * Get message text.
     *
     * @param message String
     * @param format  String
     * @return String
     */
    protected String getText(final Message message, final String format) {
        final String text = escape(message.getMessage());
        if (text.length() <= textLimit) {
            return text;
        }
        return String.format(format, text.substring(0, textLimit));
    }

    /**
     * Get message text.
     *
     * @param message String
     * @return String
     */
    protected String getText(final Message message) {
        return getText(message, TEXT_TPL);
    }

    /**
     * Escape string.
     *
     * @param value String
     * @return String
     */
    protected String escape(final String value) {
        return escapeHtml(new String(value.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
    }
}
