package com.alchemist.graylog.plugin.grace;

/**
 * Interface IGrace.
 *
 * @author Alchemist
 */
public interface IGrace {

    /**
     * Check lock.
     *
     * @return boolean
     * @throws RuntimeException Exception
     */
    boolean isPass() throws RuntimeException;
}