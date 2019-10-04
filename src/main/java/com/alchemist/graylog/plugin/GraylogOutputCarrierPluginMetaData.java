package com.alchemist.graylog.plugin;

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

/**
 * Class GraylogOutputCarrierPluginMetaData.
 *
 * @author Alchemist
 */
public final class GraylogOutputCarrierPluginMetaData implements PluginMetaData {

    /**
     * Get unique id.
     *
     * @return String
     */
    @Override
    public String getUniqueId() {
        return "com.alchemist.graylog.plugin.GraylogOutputCarrier";
    }

    /**
     * Get name.
     *
     * @return String
     */
    @Override
    public String getName() {
        return "GraylogOutputCarrier";
    }

    /**
     * Get author.
     *
     * @return String
     */
    @Override
    public String getAuthor() {
        return "Alchemist";
    }

    /**
     * Get url.
     *
     * @return URL
     */
    @Override
    public URI getURL() {
        return URI.create("https://www.graylog.org/");
    }

    /**
     * Get version.
     *
     * @return Version
     */
    @Override
    public Version getVersion() {
        return Version.from(1, 1, 0);
    }

    /**
     * Get description.
     *
     * @return String
     */
    @Override
    public String getDescription() {
        return "Graylog output plugin";
    }

    /**
     * Get required version.
     *
     * @return Version
     */
    @Override
    public Version getRequiredVersion() {
        return Version.from(1, 0, 0);
    }

    /**
     * Get required capabilities.
     *
     * @return Set
     */
    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.emptySet();
    }
}
