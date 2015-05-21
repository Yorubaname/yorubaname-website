package org.oruko.dictionary.importer;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.oruko.dictionary.model.DuplicateNameEntry;
import org.oruko.dictionary.model.GeoLocation;
import org.oruko.dictionary.model.NameEntry;
import org.oruko.dictionary.model.repository.DuplicateNameEntryRepository;
import org.oruko.dictionary.model.repository.GeoLocationRepository;
import org.oruko.dictionary.model.repository.NameEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Importer for importing names from an excel sheet into
 * {@link org.oruko.dictionary.model.NameEntry}
 *
 * @author Dadepo Aderemi.
 */
@Component
public class ExcelImporter implements ImporterInterface {

    private Logger logger = LoggerFactory.getLogger(ExcelImporter.class);

    @Autowired
    private NameEntryRepository nameEntryRepository;

    @Autowired
    private GeoLocationRepository geoLocationRepository;

    @Autowired
    private DuplicateNameEntryRepository duplicateEntryRepository;

    @Autowired
    private ImporterValidator validator;

    @Autowired
    ColumnOrder columnOrder;

    @Override
    public ImportStatus doImport(File fileSource) {
        ImportStatus status = new ImportStatus();

        XSSFSheet sheet;
        try {
            sheet = getSheet(fileSource, 0);
        } catch (IOException e) {
            logger.error("Failed to import file {} with error {}", fileSource.getAbsoluteFile(), e.getMessage());
            status.setErrorMessages(e.getMessage());
            return status;
        } catch (InvalidFormatException e) {
            logger.error("Failed to import file {} with error {}", fileSource.getAbsoluteFile(), e.getMessage());
            status.setErrorMessages(e.getMessage());
            return status;
        }

        if (validator.isColumnNameInOrder(sheet)) {
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

                Cell nameCell = row.getCell(columnOrder.getColumnOrder().inverse().get("name"));
                if (nameCell != null) {
                    name = nameCell.toString();
                }
                Cell toneCell = row.getCell(columnOrder.getColumnOrder().inverse().get("tone"));
                if (toneCell != null) {
                    tone = toneCell.toString();
                }
                Cell meaningCell = row.getCell(columnOrder.getColumnOrder().inverse().get("meaning"));
                if (meaningCell != null) {
                    meaning = meaningCell.toString();
                }
                Cell locationCell = row.getCell(columnOrder.getColumnOrder().inverse().get("location"));
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
                nameEntry.setGeoLocation(getGeoLocation(location));

                if (alreadyExists(name)) {
                    duplicateEntryRepository.save(new DuplicateNameEntry(nameEntry));
                } else {
                    nameEntryRepository.save(nameEntry);
                }

                status.incrementNumberOfNames();
            }
        } else {
            status.setErrorMessages("Columns not in order. Should be in the following order {ORDER}"
                                            .replace("{ORDER}", columnOrder.getColumnOrderAsString()));
        }

        return status;
    }

    private GeoLocation getGeoLocation(String location) {
        return geoLocationRepository.findByPlace(location);
    }

    // ==================================================== Helpers ====================================================

    private XSSFSheet getSheet(File file, int sheetIndex) throws IOException, InvalidFormatException {
        XSSFWorkbook wb = new XSSFWorkbook(file);
        return wb.getSheetAt(sheetIndex);
    }

    private boolean alreadyExists(String name) {
        NameEntry entry = nameEntryRepository.findByName(name);
        if (entry == null) {
            return false;
        }
        return true;
    }
}
