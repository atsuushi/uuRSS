package uurss;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.security.*;
import java.util.concurrent.*;

import org.apache.log4j.*;

/**
 * The task which download feed data(XML).
 */
final class DownloadTask implements Callable<File> {

    private static final Logger log = Logger.getLogger(DownloadTask.class);

    private static MessageDigest instance;
    static {
        try {
            instance = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    private FeedInfo info;
    private File root;

    DownloadTask(FeedInfo info, File root) {
        this.info = info;
        this.root = root;
    }

    /* @see java.util.concurrent.Callable#call() */
    public File call() throws Exception {
        File feedFile = new File(root, getMessageDigestString(info.name) + ".xml");
        info.setFile(feedFile);
        if (log.isDebugEnabled()) {
            log.debug(String.format("%s(%s) => [%s]", info.name, info.url, feedFile.getName()));
        }
        URL url = new URL(info.url);
        InputStream is = url.openStream();
        try {
            FileOutputStream fos = new FileOutputStream(feedFile);
            try {
                final FileChannel wch = fos.getChannel();
                final ReadableByteChannel rch = Channels.newChannel(is);
                for (long p = 0, read = 0; (read = wch.transferFrom(rch, p, 8192)) > 0; p += read) {
                    //
                }
            } finally {
                fos.close();
            }
        } finally {
            is.close();
        }
        return feedFile;
    }

    private static String getMessageDigestString(String input) {
        StringBuilder buffer = new StringBuilder();
        for (byte b : instance.digest(input.getBytes())) {
            buffer.append(String.format("%02X", b));
        }
        return buffer.toString();
    }

}
