package uurss;

import java.util.*;

final class ListMap<K, V> extends LinkedHashMap<K, List<V>> {

    ListMap() {
        // empty
    }

    void add(K key, V value) {
        if (get(key) == null) {
            put(key, new ArrayList<V>());
        }
        get(key).add(value);
    }

}
