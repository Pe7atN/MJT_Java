package bg.sofia.uni.fmi.mjt.csvprocessor.table.column;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class BaseColumn implements Column {

    Set<String> values;

    public BaseColumn() {
        this(new LinkedHashSet<>());
    }

    public BaseColumn(Set<String> values) {
        this.values = values;
    }

    public int getSize() {
        return values.size();
    }

    @Override
    public void addData(String data) {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }

        if (data.isBlank()) {
            throw new IllegalArgumentException("data cannot be blank");
        }

        values.add(data);
    }

    @Override
    public Collection<String> getData() {
        return Collections.unmodifiableSet(values);
    }

}
