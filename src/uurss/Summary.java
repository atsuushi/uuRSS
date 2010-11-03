package uurss;

import java.util.*;

import com.sun.syndication.feed.synd.*;

/**
 * Summary of Feed.
 */
final class Summary extends TreeMap<Integer, ListMap<FeedInfo, SyndEntry>> {

    Summary() {
        // empty
    }

    Summary(Summary summary) {
        putAll(summary);
    }

}
