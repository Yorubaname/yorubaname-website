package org.oruko.dictionary.importer;

import com.google.common.collect.BiMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Component;

import java.util.Iterator;

/**
 * Validates imported file
 *
 * @author Dadepo Aderemi.
 */
@Component
public class ImporterValidator {

    BiMap columnOrder = ColumnOrder.getColumnOrder();

    public boolean isColumnNameInOrder(XSSFSheet sheet) {
        boolean result = false;
        int counter = 0;
        XSSFRow row = sheet.getRow(0);
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (cell.toString().isEmpty()) {
                break;
            }
            result = cell.toString().equalsIgnoreCase((String) columnOrder.get(counter));
            if (!result) {
                break;
            }
            counter++;
        }
        return result;
    }
}
