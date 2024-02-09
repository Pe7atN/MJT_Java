package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

public class AmazonAlexaTest {

    AmazonAlexa device = new AmazonAlexa("myDevice",30, LocalDateTime.of(2023,12,12,12,12));

    @Test
    void testGetName(){
        assertEquals("myDevice",device.getName(),
                "Name is not the same");
    }

    @Test
    void testGetType(){
        assertEquals(DeviceType.SMART_SPEAKER,device.getType(),
                "Type is not the same");
    }

}
