package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WiFiThermostatTest {

    WiFiThermostat device = new WiFiThermostat("myDevice",30, LocalDateTime.of(2023,12,12,12,12));

    @Test
    void testGetName(){
        assertEquals("myDevice",device.getName(),
                "Name is not the same");
    }

    @Test
    void testGetType(){
        assertEquals(DeviceType.THERMOSTAT,device.getType(),
                "Type is not the same");
    }
}
