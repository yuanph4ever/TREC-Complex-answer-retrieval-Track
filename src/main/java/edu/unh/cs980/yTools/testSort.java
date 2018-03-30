package edu.unh.cs980.yTools;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/*
 * This is a testing to sort map by value
 */

public class testSort {
	
	private static Map<String, Float> sortByComparator(Map<String, Float> unsortMap, final boolean order){

        List<Entry<String, Float>> list = new LinkedList<Entry<String, Float>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Float>>()
        {
            public int compare(Entry<String, Float> o1,
                    Entry<String, Float> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
        for (Entry<String, Float> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
	
	public static void main(String[] args) {
		
		Map<String, Float> map = new HashMap<String, Float>();
		map.put("a", (float) 1.2);
		map.put("b", (float) 2.3);
		map.put("c", (float) 0.2);
		
		System.out.println(map);
		
		map = sortByComparator(map, false);
		
		System.out.println(map);
		
		for (Map.Entry<String, Float> entry : map.entrySet()) {
			  String key = entry.getKey();
			  float value = entry.getValue();
			  System.out.println("key is " + key + "; value is " + value);
			  
		}
		
	}

}
