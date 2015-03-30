package org.oruko.dictionary.importer;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;

/**
 * Class that holds the order in which the columns in an excel sheet being imported needs to be arranged
 *
 * @author Dadepo Aderemi.
 */
@Component
public class ColumnOrder {

    @Value("${nameentry.column.order}")
    private String[] order;

    private BiMap<Integer, String> columnOrder;

    public BiMap<Integer, String> getColumnOrder() {
        return columnOrder;
    }

    public String getColumnOrderAsString() {
        return columnOrder.inverse().keySet().toString();
    }

    @PostConstruct
    public void initColumnOrder() {
        final Map<Integer, String> tempColumnOrder = new HashMap<Integer, String>();
        for (int index = 0; index < order.length; index++) {
            tempColumnOrder.put(index, order[index]);
        }
        columnOrder = ImmutableBiMap.copyOf(Collections.unmodifiableMap(tempColumnOrder));
    }
}
