package org.oruko.dictionary.importer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;

/**
 * Validates imported file
 *
 * @author Dadepo Aderemi.
 */
@Component
public class ImporterValidator {

    ColumnOrder columnOrder;

    @Autowired
    public void setColumnOrder(ColumnOrder columnOrder) {
        this.columnOrder = columnOrder;
    }

    /**
     * Used to check if the column order is in the required order.
     * @param sheet a {@link org.apache.poi.xssf.usermodel.XSSFSheet} representing the spreadsheet uploaded
     * @return true or false if column is in order
     */
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
            result = cell.toString().equalsIgnoreCase(columnOrder.getColumnOrder().get(counter));
            if (!result) {
                break;
            }
            counter++;
        }
        return result;
    }
}
