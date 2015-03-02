package org.oruko.dictionary.importer;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO look into moving this into property files so the order can be customized outside of the app
 *
 * Class that holds the order in which the columns in an excel sheet being imported needs to be arranged
 *
 * @author Dadepo Aderemi.
 */
@Component
public class ColumnOrder {

    private final static BiMap<Integer, String> columnOrder;
    static {
        final Map<Integer, String> tempColumnOrder = new HashMap<Integer, String>();
        tempColumnOrder.put(0, "name");
        tempColumnOrder.put(1, "tone");
        tempColumnOrder.put(2, "meaning");
        tempColumnOrder.put(3, "morphology");
        tempColumnOrder.put(4, "location");
        columnOrder = ImmutableBiMap.copyOf(Collections.unmodifiableMap(tempColumnOrder));
    };

    public static BiMap<Integer, String> getColumnOrder() {
        return columnOrder;
    }

}
