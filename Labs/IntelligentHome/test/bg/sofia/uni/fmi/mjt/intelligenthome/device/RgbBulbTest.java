package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RgbBulbTest {

    RgbBulb device = new RgbBulb("myDevice",30, LocalDateTime.of(2023,12,12,12,12));

    @Test
    void testGetName(){
        assertEquals("myDevice",device.getName(),
                "Name is not the same");
    }

    @Test
    void testGetType(){
        assertEquals(DeviceType.BULB,device.getType(),
                "Type is not the same");
    }
}
