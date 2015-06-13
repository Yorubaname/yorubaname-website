package org.oruko.dictionary.importer;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that holds the order in which the columns in an excel sheet being imported needs to be arranged
 *
 * @author Dadepo Aderemi.
 */
@Component
public class ColumnOrder {

    private String[] order;


    public ColumnOrder() {
        String columnOrder = "name,pronunciation,ipa_notation,variant,syllable,meaning,"
                + "extended_meaning,morphology,etymology,geo_location,media";

        order = columnOrder.split(",");

        final Map<Integer, String> tempColumnOrder = new HashMap<Integer, String>();
        for (int index = 0; index < order.length; index++) {
            tempColumnOrder.put(index, order[index]);
        }
        this.columnOrder = ImmutableBiMap.copyOf(Collections.unmodifiableMap(tempColumnOrder));
    }

    public void setOrder(String[] order) {
        this.order = order;
    }

    private BiMap<Integer, String> columnOrder;

    public BiMap<Integer, String> getColumnOrder() {
        return columnOrder;
    }

    public String getColumnOrderAsString() {
        return columnOrder.inverse().keySet().toString();
    }
}
