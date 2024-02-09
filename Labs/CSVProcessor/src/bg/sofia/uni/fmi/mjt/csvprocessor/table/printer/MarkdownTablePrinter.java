package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MarkdownTablePrinter implements TablePrinter {

    private static final int MINIMAL_SIZE_OF_COLUMN = 3;

    @Override
    public Collection<String> printTable(Table table, ColumnAlignment... alignments) {
        List<String> rows = new ArrayList<>();
        List<String> headers = new ArrayList<>(table.getColumnNames());
        List<Integer> maxLengths = new ArrayList<>(findMaxLengths(table));

        rows.add(printRow(headers, maxLengths));
        rows.add(printAlignment(alignments, maxLengths));
        for (int i = 0; i < table.getRowsCount() - 1; i++) {

            List<String> currentRow = new ArrayList<>();
            for (String header : headers) {
                List<String> columnData = new ArrayList<>(table.getColumnData(header));
                currentRow.add(columnData.get(i));
            }

            rows.add(printRow(currentRow, maxLengths));
        }

        return List.copyOf(rows);
    }

    private String printRow(List<String> row, List<Integer> maxLengths) {
        StringBuilder finalRow = new StringBuilder();
        for (int i = 0; i < row.size(); i++) {
            String value = row.get(i);
            finalRow.append("| ").append(value)
                    .append(" ".repeat(maxLengths.get(i) - value.length() + 1));
        }
        finalRow.append("|");

        return finalRow.toString();
    }

    private String printAlignment(ColumnAlignment[] alignments, List<Integer> maxLengths) {
        StringBuilder finalRow = new StringBuilder();

        for (int i = 0; i < maxLengths.size(); i++) {

            int currentMaxLength = maxLengths.get(i);
            finalRow.append("| ");

            if (alignments.length <= i) {
                finalRow.append("-".repeat(currentMaxLength));
                finalRow.append(" ");
                continue;
            }

            switch (alignments[i]) {
                case NOALIGNMENT -> finalRow.append("-".repeat(currentMaxLength));
                case CENTER -> finalRow.append(":").append("-".repeat(currentMaxLength - 2)).append(":");
                case LEFT -> finalRow.append(":").append("-".repeat(currentMaxLength - 1));
                case RIGHT -> finalRow.append("-".repeat(currentMaxLength - 1)).append(":");
            }
            finalRow.append(" ");
        }
        finalRow.append("|");

        return finalRow.toString();
    }

    private Collection<Integer> findMaxLengths(Table table) {
        List<String> headers = new ArrayList<>(table.getColumnNames());
        List<Integer> maxLengths = new ArrayList<>();

        for (String header : headers) {
            int headerLength = header.length();
            Set<String> column = new LinkedHashSet<>(table.getColumnData(header));
            int maxLengthColumn = maxLengthOfColumn(column);
            int maxLength = Integer.max(headerLength, maxLengthColumn);

            if (maxLength < MINIMAL_SIZE_OF_COLUMN) {
                maxLength = MINIMAL_SIZE_OF_COLUMN;
            }
            maxLengths.add(maxLength);
        }

        return List.copyOf(maxLengths);
    }

    private Integer maxLengthOfColumn(Collection<String> column) {
        int maxLength = -1;
        for (String data : column) {
            if (maxLength < data.length()) {
                maxLength = data.length();
            }
        }

        return maxLength;
    }
}
