package com.alchemist.graylog.plugin.grace;

import org.apache.commons.codec.digest.DigestUtils;
import org.graylog2.plugin.streams.Stream;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Class FileGrace.
 *
 * @author Alchemist
 */
public final class FileGrace implements IGrace {
    public static final String TAG = FileGrace.class.getSimpleName();

    private static final Logger logger = Logger.getLogger(FileGrace.class.getName());
    private static final String PATH = "/tmp/graylog-grace/%s";

    private final String filepath;
    private final int wait;

    /**
     * Constructor.
     *
     * @param stream Stream
     * @param wait   int
     */
    public FileGrace(final Stream stream, final int wait) {
        this.filepath = String.format(PATH, DigestUtils.md5Hex(stream.getTitle()).toLowerCase());
        this.wait = wait * 1000; // sec to millis
    }

    /**
     * Check lock.
     *
     * @return boolean
     * @throws RuntimeException Exception
     */
    public boolean isPass() throws RuntimeException {
        final File file = new File(filepath);

        if (file.isDirectory()) {
            throw new RuntimeException("Grace file is wrong.");
        }

        if (!file.exists()) {
            if (file.getParentFile().mkdirs()) {
                logger.info("Create tmp directory.");
            }

            try {
                if (file.createNewFile()) {
                    logger.info("Create grace file.");
                    return true;
                }

                throw new RuntimeException("Can't create grace file.");
            } catch (final IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        if ((file.lastModified() + wait) < System.currentTimeMillis()) {
            if (file.setLastModified(System.currentTimeMillis())) {
                logger.info("The grace file has been successfully updated.");
                return true;
            }
            throw new RuntimeException("Can't update grace file.");
        }

        return false;
    }
}