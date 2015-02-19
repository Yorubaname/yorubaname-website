package org.oruko.dictionary;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.NameEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Importer for importing names from an excel sheet into
 * {@link org.oruko.dictionary.model.NameEntry}
 * @author Dadepo Aderemi.
 */
@Component
public class ExcelImporter implements ImporterInterface {

    @Autowired
    private NameEntryRepository nameEntryRepository;

    private Map<String, Integer> columnIndex; {
        columnIndex = new LinkedHashMap<>();
        columnIndex.put("name", 0);
        columnIndex.put("tone", 1);
        columnIndex.put("meaning", 2);
        columnIndex.put("location", 3);
    };

    @Override
    public ImportStatus doImport(File fileSource) throws IOException, InvalidFormatException {
        XSSFWorkbook wb = new XSSFWorkbook(fileSource);
        XSSFSheet sheet = wb.getSheetAt(0);


        if (isColumnNameInOrder(sheet)) {
            Iterator<Row> rowIterator = sheet.rowIterator();


            while (rowIterator.hasNext()) {
                String name = "";
                String tone = "";
                String meaning = "";
                String location = "";

                Row row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    continue;
                }

                Cell nameCell = row.getCell(columnIndex.get("name"));
                if (nameCell != null) {
                    name = nameCell.toString();
                }
                Cell toneCell = row.getCell(columnIndex.get("tone"));
                if (toneCell != null) {
                    tone  = toneCell.toString();
                }
                Cell meaningCell = row.getCell(columnIndex.get("meaning"));
                if (meaningCell != null) {
                    meaning = meaningCell.toString();
                }
                Cell locationCell = row.getCell(columnIndex.get("location"));
                if (locationCell != null) {
                    location = locationCell.toString();
                }


                if (name.isEmpty() && tone.isEmpty() && meaning.isEmpty() && location.isEmpty()) {
                    continue;
                }

                NameEntry nameEntry = new NameEntry();
                nameEntry.setName(name);
                nameEntry.setTonalMark(tone.toCharArray());
                nameEntry.setMeaning(meaning);
                nameEntry.setGeoLocation(location);

                nameEntryRepository.save(nameEntry);
            }
        }

        return null;
    }


    // ==================================================== Helpers ====================================================


    private boolean isColumnNameInOrder(XSSFSheet sheet) {
        boolean result = false;
        int counter = 0;
        ArrayList<String> order = getColumnOrder();
        XSSFRow row = sheet.getRow(0);

        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (cell.toString().isEmpty()) {
                break;
            }
            result = cell.toString().equalsIgnoreCase(order.get(counter));
            if (!result) break;
            counter++;
        }
        return result;
    }

    // TODO externalize where the order of column is specified put in properties file
    private ArrayList<String> getColumnOrder() {
        ArrayList<String> columnNames = new ArrayList<String>();
        columnNames.add("name");
        columnNames.add("tone");
        columnNames.add("meaning");
        columnNames.add("location");
        return columnNames;

    }
}
