package com.alchemist.graylog.plugin.grace;

import com.alchemist.graylog.plugin.GraylogOutputCarrierConfig;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.streams.Stream;

/**
 * CLass GraceFactory.
 */
public final class GraceFactory {

    /**
     * Constructor.
     */
    private GraceFactory() {
    }


    /**
     * Build Grace.
     *
     * @param type          String
     * @param stream        Stream
     * @param configuration Configuration
     * @return IGrace
     * @throws Exception Exception
     */
    public static IGrace getGrace(final String type, final Stream stream, final Configuration configuration) throws Exception {
        if (type == null || type.isEmpty()) {
            throw new Exception("Grace type is wrong");
        }

        if (type.equalsIgnoreCase(FileGrace.TAG)) {
            return new FileGrace(stream, GraylogOutputCarrierConfig.getGrace(configuration));
        }

        throw new Exception("Unsupported Grace type");
    }

    /**
     * Build Grace.
     *
     * @param stream        Stream
     * @param configuration Configuration
     * @return IGrace
     * @throws Exception Exception
     */
    public static IGrace getGrace(final Stream stream, final Configuration configuration) throws Exception {
        return getGrace(FileGrace.TAG, stream, configuration);
    }

}
