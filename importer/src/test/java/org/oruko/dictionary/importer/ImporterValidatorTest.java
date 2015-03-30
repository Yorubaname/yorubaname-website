package org.oruko.dictionary.importer;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.*;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImporterValidatorTest {

    ImporterValidator validator;

    ColumnOrder columnOrder;

    @Before
    public void setUp() {
        validator = new ImporterValidator();
        columnOrder = new ColumnOrder();
        columnOrder.setOrder(new String[]{"name","tone","meaning","morphology","location"});
        columnOrder.initColumnOrder();
    }

    @Test
    public void testIsColumnNameInOrder_in_wrong_order() throws Exception {
        File file = new ClassPathResource("testdata/wrong_column_order.xlsx").getFile();
        XSSFWorkbook wb = new XSSFWorkbook(file);
        XSSFSheet sheet = wb.getSheetAt(0);
        validator.setColumnOrder(columnOrder);
        boolean columnNameInOrder = validator.isColumnNameInOrder(sheet);
        assertFalse(columnNameInOrder);
    }

    @Test
    public void testIsColumnNameInOrder_in_order() throws Exception {
        File file = new ClassPathResource("testdata/right_column_order.xlsx").getFile();
        XSSFWorkbook wb = new XSSFWorkbook(file);
        XSSFSheet sheet = wb.getSheetAt(0);
        validator.setColumnOrder(columnOrder);
        boolean columnNameInOrder = validator.isColumnNameInOrder(sheet);
        assertTrue(columnNameInOrder);
    }
}