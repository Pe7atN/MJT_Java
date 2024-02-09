package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvProcessorTest {

    @Test
    void testWriter() {
        String csvContent = """
                header1,header2,b,
                row1,row2,a,
                row11,row22,c,
                row111,row222,d""";
        StringReader stringReader = new StringReader(csvContent);
        CsvProcessor processor = new CsvProcessor();

        try{
            processor.readCsv(stringReader,",");
        }   catch (CsvDataNotCorrectException e) {
            //Do something
        }

        StringWriter stringWriter = new StringWriter();
        ColumnAlignment[] alignments =
                new ColumnAlignment[]{ColumnAlignment.NOALIGNMENT, ColumnAlignment.RIGHT};
        String expected = """
                   | header1 | header2 | b   |
                   | ------- | ------: | --- |
                   | row1    | row2    | a   |
                   | row11   | row22   | c   |
                   | row111  | row222  | d   |""";


        expected = expected.replace("\n", System.lineSeparator());
        processor.writeTable(stringWriter,alignments);

        assertEquals(expected,stringWriter.toString(),
                "The table was not the expected one!");
    }

    @Test
    void testWriterOnlyHeaders() {

        String csvContent = "header1.header2.b";
        StringReader stringReader = new StringReader(csvContent);
        CsvProcessor processor = new CsvProcessor();
        try{
            processor.readCsv(stringReader,".");
        }   catch (CsvDataNotCorrectException e) {
            //Do something
        }

        StringWriter stringWriter = new StringWriter();
        ColumnAlignment[] alignments =
                new ColumnAlignment[]{ColumnAlignment.NOALIGNMENT, ColumnAlignment.RIGHT};
        String expected = """
                   | header1 | header2 | b   |
                   | ------- | ------: | --- |""";


        expected = expected.replace("\n", System.lineSeparator());
        processor.writeTable(stringWriter,alignments);

        assertEquals(expected,stringWriter.toString(),
                "The table was not the expected one!");
    }

    @Test
    void testReadCsvWhenDataIsWrong(){

        String csvContent = "header1,header2,header3,\n" +
                "row1,row2";
        StringReader stringReader = new StringReader(csvContent);
        CsvProcessor processor = new CsvProcessor();

        assertThrows(CsvDataNotCorrectException.class, () -> processor.readCsv(stringReader,","),
                "CsvDataNotCorrectException was expected to be thrown");
    }

}
