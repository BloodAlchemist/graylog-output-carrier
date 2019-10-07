package com.alchemist.graylog.plugin;

import com.alchemist.graylog.plugin.helpers.ParseHelper;
import com.alchemist.graylog.plugin.sender.MattermostSender;
import com.alchemist.graylog.plugin.sender.SlackSender;
import com.alchemist.graylog.plugin.sender.TelegramSender;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationException;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.configuration.fields.DropdownField;
import org.graylog2.plugin.configuration.fields.NumberField;
import org.graylog2.plugin.configuration.fields.TextField;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class GraylogOutputCarrierConfig.
 *
 * @author Alchemist
 */
public final class GraylogOutputCarrierConfig {

    private static final String CONF_WEBHOOK_TYPE = "webhook_type";
    private static final String CONF_WEBHOOK_URL = "webhook_url";
    private static final String CONF_CHANNEL = "channel";
    private static final String CONF_LEVEL = "level";
    private static final String CONF_GRACE = "grace";
    private static final String CONF_TEXT_LIMIT = "text_limit";
    private static final String CONF_IGNORED_FACILITIES = "ignored_facilities";
    private static final String CONF_ADDITIONAL_FIELDS = "additional_fields";
    private static final String CONF_GRAYLOG_URL = "graylog_url";

    private static final Map<String, String> WEBHOOK_TYPE = new HashMap<String, String>() {{
        put(SlackSender.TAG, "Slack messenger");
        put(TelegramSender.TAG, "Telegram messenger");
        put(MattermostSender.TAG, "Mattermost messenger");
    }};

    private static final int LEVEL_DEFAULT = 3;
    private static final int LEVEL_MIN = 0;
    private static final int LEVEL_MAX = 7;

    private static final int GRACE_DEFAULT = 10;
    private static final int GRACE_MIN = 1;
    private static final int GRACE_MAX = 60;

    private static final int TEXT_LIMIT_DEFAULT = 500;
    private static final int TEXT_LIMIT_MIN = 100;
    private static final int TEXT_LIMIT_MAX = 3000;

    private static final int IGNORED_FACILITIES_MAX = 500;
    private static final int ADDITIONAL_FIELDS_MAX = 500;

    /**
     * Constructor.
     */
    private GraylogOutputCarrierConfig() {
    }

    /**
     * Get configuration request.
     *
     * @return ConfigurationRequest
     */
    public static ConfigurationRequest getConfiguration() {
        final ConfigurationRequest configuration = new ConfigurationRequest();

        // Webhook type field
        configuration.addField(
                new DropdownField(CONF_WEBHOOK_TYPE, "Webhook type", SlackSender.TAG, WEBHOOK_TYPE,
                        "Select messenger.",
                        ConfigurationField.Optional.NOT_OPTIONAL));

        // Webhook URL field
        configuration.addField(
                new TextField(CONF_WEBHOOK_URL, "Webhook URL", null,
                        "Webhook URL.",
                        ConfigurationField.Optional.NOT_OPTIONAL));

        // Messenger channel field
        configuration.addField(
                new TextField(CONF_CHANNEL, "Messenger channel", null,
                        "Messenger channel.",
                        ConfigurationField.Optional.OPTIONAL));

        // Message level field
        configuration.addField(
                new NumberField(CONF_LEVEL, "Level", LEVEL_DEFAULT,
                        String.format("Set limit messages level (min %s, max: %s), values work like Syslog.", LEVEL_MIN, LEVEL_MAX),
                        ConfigurationField.Optional.NOT_OPTIONAL, NumberField.Attribute.ONLY_POSITIVE));

        // Grace field
        configuration.addField(
                new NumberField(CONF_GRACE, "Grace", GRACE_DEFAULT,
                        String.format("Wait (sec) between send, rest will be ignored (min: %s, max: %s).", GRACE_MIN, GRACE_MAX),
                        ConfigurationField.Optional.NOT_OPTIONAL, NumberField.Attribute.ONLY_POSITIVE));

        // Text limit field
        configuration.addField(
                new NumberField(CONF_TEXT_LIMIT, "Text limit", TEXT_LIMIT_DEFAULT,
                        String.format("Text message limit (min %s, max %s)", TEXT_LIMIT_MIN, TEXT_LIMIT_MAX),
                        ConfigurationField.Optional.NOT_OPTIONAL, NumberField.Attribute.ONLY_POSITIVE));

        // Ignored facilities field
        configuration.addField(
                new TextField(CONF_IGNORED_FACILITIES, "Ignored facilities", null,
                        "Ignored facilities separated by comma.",
                        ConfigurationField.Optional.OPTIONAL));

        // Additional fields field
        configuration.addField(
                new TextField(CONF_ADDITIONAL_FIELDS, "Additional fields", null,
                        "Additional fields separated by comma.",
                        ConfigurationField.Optional.OPTIONAL));

        // Graylog url field
        configuration.addField(
                new TextField(CONF_GRAYLOG_URL, "Graylog URL", null,
                        "URL to your Graylog web interface. Used to build links in notification.",
                        ConfigurationField.Optional.NOT_OPTIONAL));

        return configuration;
    }

    /**
     * Check configuration.
     *
     * @param configuration Configuration
     * @throws ConfigurationException Exception
     */
    public static void checkConfiguration(final Configuration configuration) throws ConfigurationException {

        if (!configuration.stringIsSet(CONF_WEBHOOK_TYPE)) {
            throw new ConfigurationException("Webhook type field is mandatory and must not be empty.");
        }

        if (configuration.stringIsSet(CONF_WEBHOOK_URL)) {
            checkUrl(configuration.getString(CONF_WEBHOOK_URL));
        } else {
            throw new ConfigurationException("Webhook URL field is mandatory and must not be empty.");
        }

        if (!configuration.intIsSet(CONF_LEVEL)) {
            throw new ConfigurationException("Level field is mandatory and must not be empty.");
        }

        int level = configuration.getInt(CONF_LEVEL);
        if ((level < LEVEL_MIN) || (level > LEVEL_MAX)) {
            throw new ConfigurationException("Level value is wrong.");
        }

        if (!configuration.intIsSet(CONF_GRACE)) {
            throw new ConfigurationException("Grace field is mandatory and must not be empty.");
        }

        final int grace = configuration.getInt(CONF_GRACE);
        if ((grace < GRACE_MIN) || (grace > GRACE_MAX)) {
            throw new ConfigurationException("Grace value is wrong.");
        }

        if (!configuration.intIsSet(CONF_TEXT_LIMIT)) {
            throw new ConfigurationException("Text limit field is mandatory and must not be empty.");
        }

        final int text = configuration.getInt(CONF_TEXT_LIMIT);
        if ((text < TEXT_LIMIT_MIN) || (text > TEXT_LIMIT_MAX)) {
            throw new ConfigurationException("Text limit value is wrong.");
        }

        if (configuration.stringIsSet(CONF_IGNORED_FACILITIES)) {
            final String ignored_facilities = configuration.getString(CONF_IGNORED_FACILITIES);
            if (ignored_facilities != null && ignored_facilities.length() > IGNORED_FACILITIES_MAX) {
                throw new ConfigurationException(String.format("Ignored facilities value is too long. Limit is %s symbols.", IGNORED_FACILITIES_MAX));
            }
        }

        if (configuration.stringIsSet(CONF_ADDITIONAL_FIELDS)) {
            final String additional_fields = configuration.getString(CONF_ADDITIONAL_FIELDS);
            if (additional_fields != null && additional_fields.length() > ADDITIONAL_FIELDS_MAX) {
                throw new ConfigurationException(String.format("Additional fields value is too long. Limit is %s symbols.", ADDITIONAL_FIELDS_MAX));
            }
        }

        if (configuration.stringIsSet(CONF_GRAYLOG_URL)) {
            checkUrl(configuration.getString(CONF_GRAYLOG_URL));
        } else {
            throw new ConfigurationException("Graylog url field is mandatory and must not be empty.");
        }
    }

    /**
     * Get webhook type.
     *
     * @param configuration Configuration
     * @return String
     */
    public static String getWebhookType(final Configuration configuration) {
        return configuration.getString(CONF_WEBHOOK_TYPE);
    }

    /**
     * Get webhook URL.
     *
     * @param configuration Configuration
     * @return String
     */
    public static String getWebhookURL(final Configuration configuration) {
        return configuration.getString(CONF_WEBHOOK_URL);
    }

    /**
     * Get channel.
     *
     * @param configuration Configuration
     * @return String
     */
    public static String getChannel(final Configuration configuration) {
        return configuration.getString(CONF_CHANNEL);
    }

    /**
     * Get level.
     *
     * @param configuration Configuration
     * @return int
     */
    public static int getLevel(final Configuration configuration) {
        return configuration.getInt(CONF_LEVEL);
    }

    /**
     * Get grace period.
     *
     * @param configuration Configuration
     * @return int
     */
    public static int getGrace(final Configuration configuration) {
        return configuration.getInt(CONF_GRACE);
    }

    /**
     * Get text limit.
     *
     * @param configuration Configuration
     * @return int
     */
    public static int getTextLimit(final Configuration configuration) {
        return configuration.getInt(CONF_TEXT_LIMIT);
    }

    /**
     * Get Graylog URL.
     *
     * @param configuration Configuration
     * @return String
     */
    public static String getGraylogUrl(final Configuration configuration) {
        return configuration.getString(CONF_GRAYLOG_URL);
    }

    /**
     * Get ignored facilities.
     *
     * @param configuration Configuration
     * @return List
     */
    public static List<String> getIgnoredFacilities(final Configuration configuration) {
        return ParseHelper.toList(configuration.getString(CONF_IGNORED_FACILITIES));
    }

    /**
     * Get additional fields.
     *
     * @param configuration Configuration
     * @return List
     */
    public static List<String> getAdditionalFields(final Configuration configuration) {
        return ParseHelper.toList(configuration.getString(CONF_ADDITIONAL_FIELDS));
    }

    /**
     * Check URL.
     *
     * @param url String
     * @throws ConfigurationException Exception
     */
    private static void checkUrl(final String url) throws ConfigurationException {
        try {
            final URI uri = new URI(url);
            if (!checkUriSchema(uri, "http", "https")) {
                throw new ConfigurationException("Webhook URL must be a valid HTTP/HTTPS URL.");
            }
        } catch (final URISyntaxException e) {
            throw new ConfigurationException(String.format("Couldn't parse URL: %s", url), e);
        }
    }

    /**
     * Check URI schema.
     *
     * @param uri          URI
     * @param validSchemes String...
     * @return boolean
     */
    private static boolean checkUriSchema(final URI uri, final String... validSchemes) {
        return uri.getScheme() != null && Arrays.binarySearch(validSchemes, uri.getScheme(), null) >= 0;
    }

}
