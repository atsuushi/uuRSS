package uurss;

import java.io.*;
import java.util.*;

/**
 * FeedListDAO for CSV impl.
 */
final class FeedListCsvDAO extends FeedListDAO {

    private List<FeedInfo> list;

    FeedListCsvDAO() throws IOException {
        List<FeedInfo> a = new ArrayList<FeedInfo>();
        File csvfile = new File(System.getProperty("csv", ""));
        Scanner scanner = new Scanner(csvfile);
        try {
            scanner.nextLine(); // skip header
            while (scanner.hasNextLine()) {
                String record = scanner.nextLine();
                FeedInfo info = toFeedInfo(record);
                if (info.enabled) {
                    a.add(info);
                }
            }
        } finally {
            scanner.close();
        }
        Collections.sort(a, new Comparator<FeedInfo>() {

            public int compare(FeedInfo o1, FeedInfo o2) {
                return Integer.valueOf(o1.showorder).compareTo(o2.showorder);
            }

        });
        this.list = a;
    }

    private static FeedInfo toFeedInfo(String record) {
        String[] a = record.split(",");
        return new FeedInfo(a[0], a[1], a[2], Boolean.valueOf(a[3]), Integer.valueOf(a[4]), a[5]);
    }

    @Override
    List<FeedInfo> select(String category) {
        List<FeedInfo> a = new ArrayList<FeedInfo>();
        for (FeedInfo info : list) {
            if (info.category.equals(category)) {
                a.add(info);
            }
        }
        return a;
    }

}
