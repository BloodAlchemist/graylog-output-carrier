package com.alchemist.graylog.plugin.sender;

import com.alchemist.graylog.plugin.GraylogOutputCarrierConfig;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.streams.Stream;

import java.util.List;

/**
 * Class SenderFactory.
 */
public final class SenderFactory {

    /**
     * Constructor.
     */
    private SenderFactory() {
    }

    /**
     * Get Sender by type.
     *
     * @param stream           Stream
     * @param type             String
     * @param webhookUrl       String
     * @param channel          String
     * @param graylogUrl       String
     * @param textLimit        int
     * @param additionalFields List
     * @return ISender
     * @throws Exception Exception
     */
    public static ISender getSender(final Stream stream, final String type, final String webhookUrl,
                                    final String channel, final String graylogUrl, final int textLimit,
                                    final List<String> additionalFields) throws Exception {
        if (type == null || type.isEmpty()) {
            throw new Exception("Sender type is wrong");
        }

        if (type.equalsIgnoreCase(SlackSender.TAG)) {
            return new SlackSender(stream, webhookUrl, channel, graylogUrl, textLimit, additionalFields);
        }

        if (type.equalsIgnoreCase(TelegramSender.TAG)) {
            return new TelegramSender(stream, webhookUrl, channel, graylogUrl, textLimit, additionalFields);
        }

        throw new Exception("Unsupported Sender type");
    }

    /**
     * Build Sender.
     *
     * @param stream        Stream
     * @param configuration Configuration
     * @return ISender
     * @throws Exception Exception
     */
    public static ISender getSender(final Stream stream, final Configuration configuration) throws Exception {
        return getSender(stream,
                GraylogOutputCarrierConfig.getWebhookType(configuration),
                GraylogOutputCarrierConfig.getWebhookURL(configuration),
                GraylogOutputCarrierConfig.getChannel(configuration),
                GraylogOutputCarrierConfig.getGraylogUrl(configuration),
                GraylogOutputCarrierConfig.getTextLimit(configuration),
                GraylogOutputCarrierConfig.getAdditionalFields(configuration));
    }
}
