package com.alchemist.graylog.plugin;

import com.alchemist.graylog.plugin.output.GraylogOutputCarrierMessageOutput;
import org.graylog2.plugin.PluginModule;

/**
 * Class GraylogOutputCarrierPluginModule.
 *
 * @author Alchemist
 */
public final class GraylogOutputCarrierPluginModule extends PluginModule {

    /**
     * Configure.
     */
    @Override
    protected void configure() {
        addMessageOutput(GraylogOutputCarrierMessageOutput.class);
    }
}
