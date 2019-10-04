package com.alchemist.graylog.plugin.output;

import com.alchemist.graylog.plugin.GraylogOutputCarrierConfig;
import com.alchemist.graylog.plugin.grace.GraceFactory;
import com.alchemist.graylog.plugin.grace.IGrace;
import com.alchemist.graylog.plugin.helpers.MessageHelper;
import com.alchemist.graylog.plugin.sender.ISender;
import com.alchemist.graylog.plugin.sender.SenderFactory;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationException;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.inputs.annotations.ConfigClass;
import org.graylog2.plugin.inputs.annotations.FactoryClass;
import org.graylog2.plugin.outputs.MessageOutput;
import org.graylog2.plugin.outputs.MessageOutputConfigurationException;
import org.graylog2.plugin.streams.Stream;

import java.util.List;
import java.util.logging.Logger;

/**
 * Class GraylogOutputCarrierMessageOutput.
 *
 * @author Alchemist
 */
public final class GraylogOutputCarrierMessageOutput implements MessageOutput {
    private static final Logger logger = Logger.getLogger(GraylogOutputCarrierMessageOutput.class.getSimpleName());

    private final Stream stream;

    private final int edgeLevel;
    private List<String> ignoredFacilities;

    private final IGrace grace;
    private final ISender sender;

    private boolean running;

    /**
     * Constructor.
     *
     * @param stream        Stream
     * @param configuration Configuration
     * @throws MessageOutputConfigurationException Exception
     */
    @Inject
    public GraylogOutputCarrierMessageOutput(final @Assisted Stream stream, final @Assisted Configuration configuration) throws MessageOutputConfigurationException {
        this.stream = stream;

        try {
            GraylogOutputCarrierConfig.checkConfiguration(configuration);
        } catch (final ConfigurationException e) {
            throw new MessageOutputConfigurationException(String.format("Missing configuration: %s", e.getMessage()));
        }

        this.edgeLevel = GraylogOutputCarrierConfig.getLevel(configuration);
        this.ignoredFacilities = GraylogOutputCarrierConfig.getIgnoredFacilities(configuration);

        try {
            this.sender = SenderFactory.getSender(stream, configuration);
            this.grace = GraceFactory.getGrace(stream, configuration);
        } catch (final Exception e) {
            throw new MessageOutputConfigurationException(String.format("Error configuration: %s", e.getMessage()));
        }

        this.running = true;
        logger.info("Started");
    }

    /**
     * Perform message by output.
     *
     * @param message Message
     * @throws Exception Exception
     */
    @Override
    public void write(final Message message) throws Exception {
        // Check level
        final int messageLevel = MessageHelper.getLevel(message);
        if (edgeLevel < messageLevel) {
            logger.warning(String.format("Skip message from stream: %s due to level: %s < %s", stream, edgeLevel, messageLevel));
            return;
        }

        // Check facility
        if (ignoredFacilities != null && !ignoredFacilities.isEmpty()) {
            final String facility = MessageHelper.getFacility(message);
            if (facility != null && !facility.isEmpty()) {
                for (final String rule : ignoredFacilities) {
                    if (rule.trim().contains(facility.trim().toLowerCase())) {
                        logger.warning(String.format("Skipped message from stream: %s due to facility: %s ~ %s", stream, rule, facility));
                        return;
                    }
                }
            }
        }

        // Check grace period
        if (!grace.isPass()) {
            logger.info(String.format("Skipped message from stream: %s due to grace period", stream));
            return;
        }

        // Perform message
        try {
            sender.perform(message);
        } catch (final Exception e) {
            throw new RuntimeException("Could not send message to webhook:", e);
        }
    }

    /**
     * Perform messages list by output.
     *
     * @param messages List
     * @throws Exception Exception
     */
    @Override
    public void write(final List<Message> messages) throws Exception {
        logger.info(String.format("Received messages list size %s", messages.size()));
        for (final Message message : messages) {
            write(message);
        }
    }

    /**
     * Is output running.
     *
     * @return boolean
     */
    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * Stop output.
     */
    @Override
    public void stop() {
        logger.info("Stopped");
        this.running = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //

    /**
     * Interface Factory.
     *
     * @author Alchemist
     */
    @FactoryClass
    public interface Factory extends MessageOutput.Factory<GraylogOutputCarrierMessageOutput> {

        /**
         * Create
         *
         * @param stream        Stream
         * @param configuration Configuration
         * @return WebhookOutput
         */
        @Override
        GraylogOutputCarrierMessageOutput create(final Stream stream, final Configuration configuration);

        /**
         * Get config
         *
         * @return Config
         */
        @Override
        Config getConfig();

        /**
         * Get descriptor
         *
         * @return Descriptor
         */
        @Override
        Descriptor getDescriptor();
    }

    /**
     * Class Config.
     *
     * @author Alchemist
     */
    @ConfigClass
    public static class Config extends MessageOutput.Config {

        /**
         * Get requested configuration.
         *
         * @return ConfigurationRequest
         */
        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            return GraylogOutputCarrierConfig.getConfiguration();
        }
    }

    /**
     * Class Descriptor.
     *
     * @author Alchemist
     */
    public static class Descriptor extends MessageOutput.Descriptor {

        /**
         * Descriptor
         */
        public Descriptor() {
            super("Graylog output carrier plugin", false, "", "Writes messages to a external channel.");
        }
    }
}
