package com.alchemist.graylog.plugin.sender;

import org.graylog2.plugin.Message;

/**
 * Interface ISender.
 */
public interface ISender {

    /**
     * Perform message.
     *
     * @param message Message
     * @throws Exception Exception
     */
    void perform(final Message message) throws Exception;
}
