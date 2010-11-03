package uurss;

import java.io.*;
import java.util.*;

/**
 * Feed info (Bean).
 */
public final class FeedInfo {

    final String name;
    final String url;
    final String category;
    final boolean enabled;
    final int showorder;
    final String fullname;

    private File file;

    FeedInfo(String name,
             String url,
             String category,
             boolean enabled,
             int showorder,
             String fullname) {
        super();
        this.name = name;
        this.url = url;
        this.category = category;
        this.enabled = enabled;
        this.showorder = showorder;
        this.fullname = fullname;
    }

    /**
     * Gets name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets url.
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets category.
     * @return category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Gets enabled.
     * @return enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Gets showorder.
     * @return showorder
     */
    public int getShoworder() {
        return showorder;
    }

    /**
     * Gets fullname.
     * @return fullname
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * Gets file.
     * @return file
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets file.
     * @param file file
     */
    public void setFile(File file) {
        this.file = file;
    }

    Map<String, ?> toMap() {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("name", name);
        m.put("url", url);
        m.put("category", category);
        m.put("enabled", enabled);
        m.put("showorder", showorder);
        m.put("fullname", fullname);
        return m;
    }

    @Override
    public String toString() {
        return "FeedInfo: " + toMap();
    }

}
