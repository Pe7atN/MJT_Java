package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.BaseColumn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BaseTable implements Table {

    private List<String> headers;
    private List<BaseColumn> data;

    private void addDataInEveryColoumn(String[] data) {

        for (int i = 0; i < data.length; i++) {
            if (i >= this.data.size()) {
                this.data.add(new BaseColumn());
            }

            BaseColumn column = this.data.get(i);
            column.addData(data[i]);
        }
    }

    private void checkForDuplicates(String[] data) throws CsvDataNotCorrectException {
        Set<String> headers = new HashSet<>();
        for (String header : data) {
            if (!headers.add(header)) {
                throw new CsvDataNotCorrectException("there are duplicate columns!");
            }
        }
    }

    public BaseTable() {
        headers = new ArrayList<>();
        data = new ArrayList<>();
    }

    @Override
    public void addData(String[] data) throws CsvDataNotCorrectException {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }

        //The table doesn't have any columns
        if (headers.isEmpty()) {
            checkForDuplicates(data);
            headers.addAll(List.of(data));
            return;
        }

        if (headers.size() != data.length) {
            throw new CsvDataNotCorrectException("data is in incorrect format");
        }

        addDataInEveryColoumn(data);
    }

    @Override
    public Collection<String> getColumnNames() {
        return List.copyOf(headers);
    }

    @Override
    public Collection<String> getColumnData(String column) {
        if (column == null) {
            throw new IllegalArgumentException("column cannot be null");
        }

        if (column.isBlank()) {
            throw new IllegalArgumentException("column cannot be blank");
        }

        int index = headers.indexOf(column);

        if (index == -1) {
            throw new IllegalArgumentException("column cannot be found");
        }

        if (data.isEmpty()) {
            return Set.of();
        } else {
            return data.get(index).getData();
        }
    }

    @Override
    public int getRowsCount() {

        if (headers.isEmpty()) {
            return 0;
        } else if (data.isEmpty()) {
            return 1;
        } else {
            return data.get(0).getSize() + 1;
        }
    }
}
