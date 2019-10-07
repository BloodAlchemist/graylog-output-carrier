package com.alchemist.graylog.plugin.sender;

import com.alchemist.graylog.plugin.helpers.MessageHelper;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.streams.Stream;

import java.util.List;

/**
 * Class MattermostSender
 */
public final class MattermostSender extends AbstractSender {
    public static final String TAG = MattermostSender.class.getSimpleName();

    private static final String ERROR = "#D40E0D";
    private static final String WARNING = "#EBB424";
    private static final String OK = "#49C39E";

    private static final String PRETEXT_TPL = "[%s]: %s ";
    private static final String TEMPLATE = "{" +
            "\"username\":\"Graylog\"," +
            "\"channel\":\"%s\"," +
            "\"attachments\":[{" +
            "\"author_name\":\"%s\"," +
            "\"title\":\"%s\"," +
            "\"color\":\"%s\"," +
            "\"text\":\"%s [View](%s)\"" +
            "}]}";

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
    public MattermostSender(final Stream stream, final String webhookUrl, final String channel, final String graylogUrl,
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
        return String.format(TEMPLATE, channel, getPretext(message, PRETEXT_TPL), getColor(message), getText(message),
                getUrl(message), getTimestamp(message));
    }

    /**
     * Get message emoji.
     *
     * @param message Message
     * @return String
     */
    private String getColor(final Message message) {
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
