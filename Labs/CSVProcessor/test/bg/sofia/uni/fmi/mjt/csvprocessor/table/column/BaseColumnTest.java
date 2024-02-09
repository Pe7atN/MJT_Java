package bg.sofia.uni.fmi.mjt.csvprocessor.table.column;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BaseColumnTest {

    @Test
    void testAddData(){
        BaseColumn column = new BaseColumn();
        column.addData("Hello");
        assertEquals(Set.of("Hello"),column.getData(),
                "");
    }

    @Test
    void testAddDataToNull(){
        BaseColumn column = new BaseColumn();

        assertThrows(IllegalArgumentException.class, () -> column.addData(null),
                "IllegalArgumentException expected to be thrown when data is null!");
    }

    @Test
    void testAddDataWhenDataIsBlank(){
        BaseColumn column = new BaseColumn();

        assertThrows(IllegalArgumentException.class, () -> column.addData("    "),
                "IllegalArgumentException expected to be thrown when data is blank!");
    }

    @Test
    void testGetSizeEmptyColumn(){
        BaseColumn column = new BaseColumn();
        assertEquals(0,column.getSize(),
                "Size should be 0 of an empty column!");
    }

    @Test
    void testGetSizeOfColumn(){
        BaseColumn column = new BaseColumn();
        column.addData("Hello");
        column.addData("Hello1");
        column.addData("Hello2");
        column.addData("Hello3");
        assertEquals(4,column.getSize(),
                "It does not print the right size");
    }
}
