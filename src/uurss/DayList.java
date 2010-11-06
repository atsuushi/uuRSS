package uurss;

import java.util.*;

/**
 * DayList is used for generating a few days around the day (bean).
 */
public final class DayList {

    private final List<Integer> list;
    private final int day;

    /**
     * Constructor.
     * @param days
     * @param day
     */
    public DayList(Collection<Integer> days, int day) {
        this.list = Collections.unmodifiableList(new ArrayList<Integer>(new TreeSet<Integer>(days)));
        this.day = day;
    }

    /**
     * Gets a few days around the day.
     * @param count
     * @return days
     */
    public List<?> getFewDays(int count) {
        int index = list.indexOf(day);
        if (index < 0) {
            return Collections.EMPTY_LIST;
        }
        int start = index - count;
        if (start < 0) {
            start = 0;
        }
        int end = index + count + 1;
        if (end > list.size()) {
            end = list.size();
        }
        return list.subList(start, end);
    }

}
