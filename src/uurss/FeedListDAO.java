package uurss;

import java.util.*;

/**
 * DAO for getting feed info from feed list.
 */
abstract class FeedListDAO {

    abstract List<FeedInfo> select(String category) throws Exception;

    void close() throws Exception {
        // empty
    }

}
