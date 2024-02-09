package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BaseTableTest {

    @Test
    void testAddDataWhenTableIsEmpty(){
        Table table = new BaseTable();
        try{
            table.addData(new String[]{"header1", "header2", "header3"});
        }   catch (CsvDataNotCorrectException e){
            //Do something
        }
        assertEquals(List.of("header1", "header2", "header3"),table.getColumnNames(),
            "The headers are not the same as they should be!");
    }

    @Test
    void testAddDataWithDuplicateHeaders(){
        Table table = new BaseTable();
        assertThrows(CsvDataNotCorrectException.class, () -> table.addData(new String[]{"header1", "header1", "header3"}),
                "CsvDataNotCorrectException was expected to be thrown!");
    }

    @Test
    void testAddDataWhenNull(){
        Table table = new BaseTable();
        assertThrows(IllegalArgumentException.class, () -> table.addData(null),
                "IllegalArgumentException expected to be thrown when data is null!");
    }

    @Test
    void testAddDataWhenDataIsNotCorrect(){
        Table table = new BaseTable();

        try{
            table.addData(new String[]{"header1", "header2", "header3"});
        }   catch (CsvDataNotCorrectException e){
            //Do something
        }

        assertThrows(CsvDataNotCorrectException.class, () -> table.addData(new String[]{"row1","row2"}),
                "CsvDataNotCorrectException expected to be thrown when data is not correct!");
    }

    @Test
    void testAddDataInEveryColumn(){
        Table table = new BaseTable();

        try{
            table.addData(new String[]{"header1", "header2", "header3"});
            table.addData(new String[]{"row1", "row2", "row3"});
            table.addData(new String[]{"row11", "row22", "row33"});
            table.addData(new String[]{"row111", "row222", "row333"});
        }   catch (CsvDataNotCorrectException e){
            //Do something
        }

        assertEquals(Set.of("row1","row11","row111"),table.getColumnData("header1"),
                "It was not the expected data of the column!");
    }

    @Test
    void testGetColumnDataWhenNull() {
        Table table = new BaseTable();
        assertThrows(IllegalArgumentException.class, () -> table.getColumnData(null),
                "IllegalArgumentException expected to be thrown when column is null!");
    }

    @Test
    void testGetColumnDataWhenIsBlank() {
        Table table = new BaseTable();
        assertThrows(IllegalArgumentException.class, () -> table.getColumnData("   "),
                "IllegalArgumentException expected to be thrown when column is blank!");
    }

    @Test
    void testGetColumnDataWhenIsNotContained() {
        Table table = new BaseTable();
        assertThrows(IllegalArgumentException.class, () -> table.getColumnData("header4"),
                "IllegalArgumentException expected to be thrown when column is not found!");
    }

    @Test
    void testGetColumnDataWhenTableHasOnlyHeaders() {
        Table table = new BaseTable();

        try{
            table.addData(new String[]{"header1", "header2", "header3"});
        }   catch (CsvDataNotCorrectException e){
            //Do something
        }

        assertEquals(Set.of(), table.getColumnData("header1"),
                "The content was not the expected one!");
    }

    @Test
    void testGetRowCountWhenEmpty() {
        Table table = new BaseTable();
        assertEquals(0, table.getRowsCount(),
                "Size should be 0 of an empty table!");
    }

    @Test
    void testGetRowCountOnlyHeader() {
        Table table = new BaseTable();

        try{
            table.addData(new String[]{"header1", "header2", "header3"});
        }   catch (CsvDataNotCorrectException e){
            //Do something
        }
        assertEquals(1, table.getRowsCount(),
                "Size should be 1 of an table with only headers!");
    }

    @Test
    void testGetRowCount() {
        Table table = new BaseTable();
        try{
            table.addData(new String[]{"header1", "header2", "header3"});
            table.addData(new String[]{"row1", "row2", "row3"});
            table.addData(new String[]{"row11", "row22", "row33"});
            table.addData(new String[]{"row111", "row222", "row333"});
        }   catch (CsvDataNotCorrectException e){
            //Do something
        }
        assertEquals(4, table.getRowsCount(),
                "It was not the expected size!");
    }

}

