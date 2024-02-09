package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarkDownTablePrinterTest {

    @Test
    void testPrintTable() {
        MarkdownTablePrinter printer = new MarkdownTablePrinter();
        Table table = new BaseTable();
        ColumnAlignment[] alignments =
                new ColumnAlignment[]{ColumnAlignment.NOALIGNMENT, ColumnAlignment.RIGHT};
        List<String> expected = List.of(
                "| header1 | header2 | b   |",
                "| ------- | ------: | --- |",
                "| row1    | row2    | a   |",
                "| row11   | row22   | c   |",
                "| row111  | row222  | d   |"
        );


        try {
            table.addData(new String[]{"header1", "header2", "b"});
            table.addData(new String[]{"row1", "row2", "a"});
            table.addData(new String[]{"row11", "row22", "c"});
            table.addData(new String[]{"row111", "row222", "d"});
        } catch (CsvDataNotCorrectException e) {
            //Do something
        }

        assertEquals(expected, printer.printTable(table,alignments),
                "The table was not the expected one!");
    }

    @Test
    void testPrintTableWithOnlyHeaders() {
        MarkdownTablePrinter printer = new MarkdownTablePrinter();
        Table table = new BaseTable();
        ColumnAlignment[] alignments =
                new ColumnAlignment[]{ColumnAlignment.LEFT, ColumnAlignment.RIGHT, ColumnAlignment.CENTER};
        List<String> expected = List.of(
                "| header1 | header2 | header3 |",
                "| :------ | ------: | :-----: |"
        );

        try {
            table.addData(new String[]{"header1", "header2", "header3"});
        } catch (CsvDataNotCorrectException e) {
            //Do something
        }

        assertEquals(expected, printer.printTable(table,alignments),
                "The table was not the expected one!");
    }
}
