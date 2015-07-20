package org.oruko.dictionary.importer;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.oruko.dictionary.events.EventPubService;
import org.oruko.dictionary.events.NameUploadedEvent;
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

    private NameEntryRepository nameEntryRepository;
    private GeoLocationRepository geoLocationRepository;
    private DuplicateNameEntryRepository duplicateEntryRepository;
    private ImporterValidator validator;
    private EventPubService eventPubService;
    private ColumnOrder columnOrder;


    @Autowired
    public void setEventPubService(EventPubService eventPubService) {
        this.eventPubService = eventPubService;
    }

    @Autowired
    public void setNameEntryRepository(NameEntryRepository nameEntryRepository) {
        this.nameEntryRepository = nameEntryRepository;
    }

    @Autowired
    public void setGeoLocationRepository(
            GeoLocationRepository geoLocationRepository) {
        this.geoLocationRepository = geoLocationRepository;
    }

    @Autowired
    public void setDuplicateEntryRepository(
            DuplicateNameEntryRepository duplicateEntryRepository) {
        this.duplicateEntryRepository = duplicateEntryRepository;
    }

    @Autowired
    public void setValidator(ImporterValidator validator) {
        this.validator = validator;
    }

    @Autowired
    public void setColumnOrder(ColumnOrder columnOrder) {
        this.columnOrder = columnOrder;
    }
    

    @Override
    public ImportStatus importFile(File fileSource) {
        ImportStatus status = new ImportStatus();
        NameUploadedEvent nameUploadedEvent = new NameUploadedEvent();

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

        int totalNumberOfNames = sheet.getPhysicalNumberOfRows() - 1; // removes the header row
        nameUploadedEvent.setTotalNumberOfNames(totalNumberOfNames);
        nameUploadedEvent.isUploading(true);

        if (validator.isColumnNameInOrder(sheet)) {
            Iterator<Row> rowIterator = sheet.rowIterator();

            while (rowIterator.hasNext()) {
                boolean fieldIsEmpty = true;
                String name = "";
                String pronunciation = "";
                String ipaNotation = "";
                String variant = "";
                String syllable = "";
                String meaning = "";
                String extendedMeaning = "";
                String morphology = "";
                String etymology = "";
                String geoLocation = "";
                String media = "";

                Row row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    continue;
                }

                NameEntry nameEntry = new NameEntry();

                Cell nameCell = row.getCell(columnOrder.getColumnOrder().inverse().get("name"));
                if (nameCell != null) {
                    name = nameCell.toString();
                    if (!name.isEmpty()) {
                        fieldIsEmpty = false;
                        nameEntry.setName(name.trim());
                    } else {
                        // if name is empty then the row is nullified, so skip
                        continue;
                    }

                }
                Cell pronunciationCell = row.getCell(columnOrder.getColumnOrder().inverse().get("pronunciation"));
                if (pronunciationCell != null) {
                    pronunciation = pronunciationCell.toString();
                    if (!pronunciation.isEmpty()) {
                        fieldIsEmpty = false;
                        nameEntry.setPronunciation(pronunciation.trim());
                    }
                }

                Cell ipaCell = row.getCell(columnOrder.getColumnOrder().inverse().get("ipa_notation"));
                if (ipaCell != null) {
                    ipaNotation = ipaCell.toString();
                    if (!ipaNotation.isEmpty()) {
                        fieldIsEmpty = false;
                        nameEntry.setIpaNotation(ipaNotation.trim());
                    }
                }

                Cell variantCell = row.getCell(columnOrder.getColumnOrder().inverse().get("variant"));
                if (variantCell != null) {
                    variant = variantCell.toString();
                    if (!variant.isEmpty()) {
                        fieldIsEmpty = false;
                        nameEntry.setVariants(variant.trim());
                    }
                }

                Cell syllableCell = row.getCell(columnOrder.getColumnOrder().inverse().get("syllable"));
                if (syllableCell != null) {
                    syllable = syllableCell.toString();
                    if (!syllable.isEmpty()) {
                        fieldIsEmpty = false;
                        nameEntry.setSyllables(syllable.trim());
                    }
                }

                Cell meaningCell = row.getCell(columnOrder.getColumnOrder().inverse().get("meaning"));
                if (meaningCell != null) {
                    meaning = meaningCell.toString();
                    if (!meaning.isEmpty()) {
                        fieldIsEmpty = false;
                        nameEntry.setMeaning(meaning.trim());
                    }

                }

                Cell extendedMeaningCell = row.getCell(columnOrder.getColumnOrder().inverse().get("extended_meaning"));
                if (extendedMeaningCell != null) {
                    extendedMeaning = extendedMeaningCell.toString();
                    if (!extendedMeaning.isEmpty()) {
                        fieldIsEmpty = false;
                        nameEntry.setExtendedMeaning(extendedMeaning.trim());
                    }
                }

                Cell morphologyCell = row.getCell(columnOrder.getColumnOrder().inverse().get("morphology"));
                if (morphologyCell != null) {
                    morphology = morphologyCell.toString();
                    if (!morphology.isEmpty()) {
                        fieldIsEmpty = false;
                        nameEntry.setMorphology(morphology.trim());
                    }
                }

                Cell etymologyCell = row.getCell(columnOrder.getColumnOrder().inverse().get("etymology"));
                if (etymologyCell != null) {
                    etymology = etymologyCell.toString();
                    if (!etymology.isEmpty()) {
                        // TODO define format for etymology in spreadsheet
                        fieldIsEmpty = false;
                        nameEntry.setEtymology(null);
                    }
                }

                Cell geoLocationCell = row.getCell(columnOrder.getColumnOrder().inverse().get("geo_location"));
                if (geoLocationCell != null) {
                    geoLocation = geoLocationCell.toString();
                    if (!geoLocation.isEmpty()) {
                        fieldIsEmpty = false;
                        nameEntry.setGeoLocation(getGeoLocation(geoLocation));
                    }
                }


                Cell mediaCell = row.getCell(columnOrder.getColumnOrder().inverse().get("media"));
                if (mediaCell != null) {
                    media = mediaCell.toString();
                    if (!media.isEmpty()) {
                        fieldIsEmpty = false;
                        nameEntry.setMedia(media.trim());
                    }
                }

                if (fieldIsEmpty) {
                    continue;
                }

                try {
                    if (alreadyExists(name)) {
                        duplicateEntryRepository.save(new DuplicateNameEntry(nameEntry));
                        status.incrementNumberOfNames();
                    } else {
                        nameEntryRepository.save(nameEntry);
                        status.incrementNumberOfNames();
                    }
                } catch (Exception e) {
                    logger.debug("Exception while uploading name entry with name {}", name, e);
                }

                nameUploadedEvent.setTotalUploaded(status.getNumberOfNamesUpload());
                eventPubService.publish(nameUploadedEvent);
            }
        } else {
            status.setErrorMessages("Columns not in order. Should be in the following order {ORDER}"
                                            .replace("{ORDER}", columnOrder.getColumnOrderAsString()));
        }

        // publishes event that signifies end of uploading
        nameUploadedEvent.isUploading(false);
        eventPubService.publish(nameUploadedEvent);
        return status;
    }



    // ==================================================== Helpers ====================================================

    private GeoLocation getGeoLocation(String location) {
        return geoLocationRepository.findByPlace(location);
    }

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
