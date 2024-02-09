package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.MarkdownTablePrinter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CsvProcessor implements CsvProcessorAPI {

    private Table table;

    public CsvProcessor() {
        this(new BaseTable());
    }

    public CsvProcessor(Table table) {
        this.table = table;
    }

    @Override
    public void readCsv(Reader reader, String delimiter) throws CsvDataNotCorrectException {
        if (delimiter.equals(".")) {
            delimiter = "\\.";
        }

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {

            String line = bufferedReader.readLine();
            if (line == null) {
                return;
            }

            String[] words = line.split(delimiter);
            table.addData(words);
            int previousLength = words.length;

            while ((line = bufferedReader.readLine()) != null) {
                words = line.split(delimiter);
                int currentLength = words.length;

                if (currentLength != previousLength) {
                    throw new CsvDataNotCorrectException("data is in wrong format");
                }
                table.addData(words);
                previousLength = currentLength;
            }
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from a file", e);
        }
    }

    @Override
    public void writeTable(Writer writer, ColumnAlignment... alignments) {
        MarkdownTablePrinter printer = new MarkdownTablePrinter();
        List<String> markDownTable = new ArrayList<>(printer.printTable(table, alignments));
        try (BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

            for (int i = 0; i < markDownTable.size() - 1; i++) {
                bufferedWriter.write(markDownTable.get(i));
                bufferedWriter.newLine();
            }
            bufferedWriter.write(markDownTable.get(markDownTable.size() - 1));

            bufferedWriter.flush();
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from a file", e);
        }
    }
}
