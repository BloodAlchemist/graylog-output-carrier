package com.alchemist.graylog.plugin;

import com.google.common.collect.ImmutableSet;
import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

import java.util.Collection;

/**
 * Class GraylogOutputCarrierPlugin.
 *
 * @author Alchemist
 */
public final class GraylogOutputCarrierPlugin implements Plugin {

    /**
     * Get plugin metadata.
     *
     * @return PluginMetaData
     */
    @Override
    public PluginMetaData metadata() {
        return new GraylogOutputCarrierPluginMetaData();
    }

    /**
     * Get plugin modules.
     *
     * @return Collection
     */
    @Override
    public Collection<PluginModule> modules() {
        return ImmutableSet.of(new GraylogOutputCarrierPluginModule());
    }
}
