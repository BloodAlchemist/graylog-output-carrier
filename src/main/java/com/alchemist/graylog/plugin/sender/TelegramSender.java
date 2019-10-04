package com.alchemist.graylog.plugin.sender;

import com.alchemist.graylog.plugin.helpers.MessageHelper;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.streams.Stream;

import java.util.List;

/**
 * Class TelegramSender.
 */
public final class TelegramSender extends AbstractSender {
    public static final String TAG = TelegramSender.class.getSimpleName();

    private static final String ERROR = "&#x1F525;";
    private static final String WARNING = "&#x26A0;";
    private static final String OK = "&#x1F4E2;";

    private static final String PRETEXT_TPL = "<b>%s</b>: %s ";
    private static final String TEMPLATE = "{" +
            "\"chat_id\":\"%s\"," +
            "\"text\":\"%s[%s] %s <a href='%s'>View</a>\\n%s\"," +
            "\"disable_notification\":\"false\", \"parse_mode\":\"html\"}";

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
    public TelegramSender(final Stream stream, final String webhookUrl, final String channel, final String graylogUrl,
                          final int textLimit, final List<String> additionalFields) throws Exception {
        super(stream, webhookUrl, channel, graylogUrl, textLimit, additionalFields);
    }

    /**
     * Prepare message.
     *
     * @param message Message
     * @return String
     * @throws Exception Exception
     */
    @Override
    protected String prepare(final Message message) throws Exception {
        return String.format(TEMPLATE, channel, getEmoji(message), getTimestamp(message),
                getPretext(message, PRETEXT_TPL), getUrl(message), getText(message));
    }

    /**
     * Get message emoji.
     *
     * @param message Message
     * @return String
     */
    private String getEmoji(final Message message) {
        switch (MessageHelper.getLevel(message)) {
            case 0:
            case 1:
            case 2:
            case 3:
                return ERROR;
            case 4:
                return WARNING;
        }
        return OK;
    }
}
