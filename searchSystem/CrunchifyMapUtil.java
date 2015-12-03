package searchSystem;

import java.util.*;

/**
 * @author Crunchify.com
 * code from http://crunchify.com/java-how-to-sort-a-map-on-the-values-the-map-interface-java-collections/
 *
 */

public class CrunchifyMapUtil {

	/*
	 * Sort a map according to values.
	 * @param <K> the key of the map.
	 * @param <V> the value to sort according to.
	 * @param crunchifySortMap the map to sort.
	 * @return a map sorted on the values.
	 */

    public static <K, V extends Comparable<? super V>> Map<K, V> crunchifySortMap(final Map<K, V> mapToSort) {
        List<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(mapToSort.size());

        entries.addAll(mapToSort.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(final Map.Entry<K, V> entry1, final Map.Entry<K, V> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });

        Map<K, V> sortedCrunchifyMap = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : entries) {
            sortedCrunchifyMap.put(entry.getKey(), entry.getValue());
        }
        return sortedCrunchifyMap;
    }
}
