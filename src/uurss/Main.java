package uurss;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.Date;
import java.util.concurrent.*;

import org.apache.log4j.*;
import org.apache.velocity.*;
import org.apache.velocity.app.*;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.*;

/**
 * uuRSS main.
 */
public final class Main {

    private static final Logger log = Logger.getLogger(Main.class);

    private static final String NAME = "uuRSS";
    private static final String VERSION = "($Rev$)";
    private static final String NAMEWITHVERSION = NAME + VERSION;

    private Main() {
        // empty
    }

    private static List<FeedInfo> getFeedInfos(String[] categories, File cachedir) throws Exception {
        List<FeedInfo> a = new ArrayList<FeedInfo>();
        FeedListDAO dao = (System.getProperties().containsKey("csv"))
                ? new FeedListCsvDAO()
                : new FeedListJdbcDAO();
        try {
            for (final String category : categories) {
                a.addAll(dao.select(category));
            }
        } finally {
            dao.close();
        }
        ExecutorService exe = Executors.newFixedThreadPool(8);
        for (final FeedInfo info : a) {
            exe.submit(new DownloadTask(info, cachedir));
        }
        exe.shutdown();
        // TODO go next asap
        while (!exe.awaitTermination(1, TimeUnit.SECONDS)) {
            log.debug("waiting...");
        }
        return a;
    }

    private static Summary getSummary(List<FeedInfo> a) throws Exception {
        Summary summary = new Summary();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        SyndFeedInput input = new SyndFeedInput();
        for (final FeedInfo info : a) {
            Reader reader = new XmlReader(info.getFile());
            try {
                if (log.isDebugEnabled()) {
                    log.debug("parsing " + info.name);
                }
                SyndFeed feed = input.build(reader);
                @SuppressWarnings("unchecked")
                final List<SyndEntry> entries = feed.getEntries();
                for (SyndEntry entry : entries) {
                    Date updated = (entry.getUpdatedDate() == null)
                            ? entry.getPublishedDate()
                            : entry.getUpdatedDate();
                    final int key = Integer.parseInt(df.format(updated));
                    if (log.isTraceEnabled()) {
                        log.trace("entry=" + entry);
                    }
                    ListMap<FeedInfo, SyndEntry> m = summary.get(key);
                    if (m == null) {
                        m = new ListMap<FeedInfo, SyndEntry>();
                        summary.put(key, m);
                    }
                    m.add(info, entry);
                }
            } finally {
                reader.close();
            }
        }
        return summary;
    }

    private static Summary extractCategory(Summary summary, String category) {
        Summary summary1 = new Summary();
        for (Entry<Integer, ListMap<FeedInfo, SyndEntry>> entry : summary.entrySet()) {
            ListMap<FeedInfo, SyndEntry> v = entry.getValue();
            List<FeedInfo> includes = new ArrayList<FeedInfo>();
            for (FeedInfo info : v.keySet()) {
                if (info.category.equals(category)) {
                    includes.add(info);
                }
            }
            if (!includes.isEmpty()) {
                ListMap<FeedInfo, SyndEntry> a = new ListMap<FeedInfo, SyndEntry>();
                for (FeedInfo info : includes) {
                    a.put(info, v.get(info));
                }
                summary1.put(entry.getKey(), a);
            }
        }
        return summary1;
    }

    static void mkdirs(File dir) {
        if (dir.exists() && !dir.isDirectory()) {
            throw new IllegalStateException("not a directory: " + dir);
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalStateException("failed to make directory: " + dir);
        }
    }

    /**
     * @param args categories
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("usage: run category1 [category2 ...]");
            return;
        }
        if (log.isInfoEnabled()) {
            log.info(NAMEWITHVERSION + " START");
        }
        int status;
        try {
            final File root = new File(System.getProperty("result.dir", "./"));
            final File cachedir = new File(root, ".cache");
            mkdirs(cachedir);
            if (log.isInfoEnabled()) {
                log.info("get feed infos");
            }
            List<FeedInfo> a = getFeedInfos(args, cachedir);
            if (log.isInfoEnabled()) {
                log.info("edit feed into summary");
            }
            Summary summary = getSummary(a);
            if (log.isInfoEnabled()) {
                log.info("create result end");
            }
            // set up Velocity
            // TODO to file
            Properties p = new Properties();
            p.setProperty("resource.loader", "class");
            p.setProperty("class.resource.loader.class",
                          "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            p.setProperty("input.encoding", "Windows-31J");
            VelocityEngine engine = new VelocityEngine(p);
            for (final String category : args) {
                File dir = new File(root, category);
                mkdirs(dir);
                // generate index page
                if (log.isInfoEnabled()) {
                    log.info("index start: " + category);
                }
                Summary summary1 = extractCategory(summary, category);
                File indexFile = new File(dir, "index.html");
                {
                    PrintWriter out = new PrintWriter(indexFile);
                    try {
                        final String template = "uurss/index.vm";
                        VelocityContext context = new VelocityContext();
                        context.put("title", String.format("%s [%s] index", NAME, category));
                        context.put("version", NAMEWITHVERSION);
                        List<Integer> days = new ArrayList<Integer>(summary1.keySet());
                        Collections.reverse(days);
                        context.put("summary", summary1);
                        context.put("days", days);
                        engine.mergeTemplate(template, context, out);
                    } finally {
                        out.close();
                    }
                }
                // generate dairy page 
                if (log.isInfoEnabled()) {
                    log.info("day start");
                }
                for (Integer day : summary1.keySet()) {
                    ListMap<FeedInfo, SyndEntry> m = summary.get(day);
                    File file = new File(dir, String.format("%08d.html", day));
                    PrintWriter out = new PrintWriter(file);
                    try {
                        final String template = "uurss/day.vm";
                        VelocityContext context = new VelocityContext();
                        context.put("title", String.format("%s [%s]", NAME, category));
                        context.put("version", NAMEWITHVERSION);
                        context.put("results", m);
                        // TODO day adjuster
                        context.put("day", day);
                        engine.mergeTemplate(template, context, out);
                    } finally {
                        out.close();
                    }
                }
            }
            status = 0;
        } catch (SQLException ex) {
            log.error("", ex);
            status = 1;
        } catch (RuntimeException ex) {
            log.error("", ex);
            status = 1;
        } catch (Throwable th) {
            log.fatal("", th);
            status = 255;
        }
        if (log.isInfoEnabled()) {
            log.info(NAMEWITHVERSION + " END");
        }
        if (status != 0) {
            System.exit(status);
        }
    }

}
