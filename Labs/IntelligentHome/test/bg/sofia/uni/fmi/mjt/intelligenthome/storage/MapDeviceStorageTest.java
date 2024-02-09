package bg.sofia.uni.fmi.mjt.intelligenthome.storage;

import bg.sofia.uni.fmi.mjt.intelligenthome.device.AmazonAlexa;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapDeviceStorageTest {
    MapDeviceStorage storage = new MapDeviceStorage();

    @Test
    void testDeleteDeviceIsNull() {
        assertFalse(storage.delete(null),
                "Expected to return false when id is null!");
    }

    @Test
    void testDeleteExistingDevice() {
        AmazonAlexa device = new AmazonAlexa("deviceName", 10, LocalDateTime.now());
        storage.store(device.getId(), device);
        assertTrue(storage.delete(device.getId()),
                "Expected to return true when device is removed!");

    }

    @Test
    void testDeleteNonExistingDevice() {
        String deviceID = "deviceID";

        assertFalse(storage.delete(deviceID),
                "Expected to return false when device is not found!");

    }

    @Test
    void testExistDevice(){
        AmazonAlexa device = new AmazonAlexa("deviceName", 10, LocalDateTime.now());
        storage.store(device.getId(), device);
        assertTrue(storage.exists(device.getId()),
                "Expected to return true when device is in the storage!");
    }

    @Test
    void testNonExistDevice(){
        AmazonAlexa device = new AmazonAlexa("deviceName", 10, LocalDateTime.now());
        assertFalse(storage.exists(device.getId()),
                "Expected to return false when device is not in the storage!");
    }

    @Test
    void testGetDeviceWhenInStorage(){
        AmazonAlexa device = new AmazonAlexa("deviceName", 10, LocalDateTime.now());
        storage.store(device.getId(), device);
        assertEquals(device,storage.get(device.getId()),
                "Expected to return the device!");
    }

    @Test
    void testGetDeviceWhenItIsNotInStorage(){
        AmazonAlexa device = new AmazonAlexa("deviceName", 10, LocalDateTime.now());
        assertEquals(null,storage.get(device.getId()),
                "Expected to return the device!");
    }

}
