package bg.sofia.uni.fmi.mjt.intelligenthome.center;

import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceNotFoundException;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.DeviceType;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;
import bg.sofia.uni.fmi.mjt.intelligenthome.storage.DeviceStorage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class IntelligentHomeCenterTest {

    private IoTDevice device = mock();
    private DeviceStorage deviceStorageMock = mock();
    private IntelligentHomeCenter home = new IntelligentHomeCenter(deviceStorageMock);

    @Test
    void testRegisterNewDevice() throws DeviceAlreadyRegisteredException {
        when(deviceStorageMock.exists(device.getId())).thenReturn(false);
        home.register(device);
        verify(deviceStorageMock, times(1)).store(any(), any());
    }

    @Test
    void testRegisterWhenDeviceIsNull() throws DeviceAlreadyRegisteredException {

        assertThrows(IllegalArgumentException.class, () -> home.register(null),
                "IllegalArgumentException expected to be thrown when device is null!");

        verify(deviceStorageMock, never()).store(any(), any());
    }

    @Test
    void testRegisterAlreadyExistingDevice() throws DeviceAlreadyRegisteredException {
        when(deviceStorageMock.exists(device.getId())).thenReturn(true);

        assertThrows(DeviceAlreadyRegisteredException.class, () -> home.register(device),
                "DeviceAlreadyRegisteredException expected to be thrown when device is already registered!");

        verify(deviceStorageMock, never()).store(any(), any());
    }


    @Test
    void testUnregisterExistingDevice() throws DeviceNotFoundException {
        when(deviceStorageMock.exists(device.getId())).thenReturn(true);
        home.unregister(device);

        verify(deviceStorageMock, times(1)).delete(device.getId());
    }

    @Test
    void testUnregisterWhenDeviceIsNull() throws DeviceNotFoundException {
        assertThrows(IllegalArgumentException.class, () -> home.unregister(null),
                "IllegalArgumentException expected to be thrown when device is null!");

        verify(deviceStorageMock, never()).delete(any());
    }

    @Test
    void testUnregisterWhenDeviceNotFound() throws DeviceNotFoundException {
        when(deviceStorageMock.exists(device.getId())).thenReturn(false);
        assertThrows(DeviceNotFoundException.class, () -> home.unregister(device),
                "DeviceNotFoundException expected to be thrown when device is not found!");

        verify(deviceStorageMock, never()).delete(any());
    }

    @Test
    void testGetDeviceByIdExistingDevice() throws DeviceNotFoundException {

        String deviceId = "DeviceId";
        when(deviceStorageMock.exists(deviceId)).thenReturn(true);
        when(deviceStorageMock.get(deviceId)).thenReturn(device);

        assertEquals(device, home.getDeviceById(deviceId),
                "It should return the device");

        verify(deviceStorageMock, times(1)).get(deviceId);
    }

    @Test
    void testGetDeviceByIdWhenDeviceIsNull() throws DeviceNotFoundException {
        assertThrows(IllegalArgumentException.class, () -> home.getDeviceById(null),
                "IllegalArgumentException expected to be thrown when device is not found!");;

        verify(deviceStorageMock, never()).get(any());
    }

    @Test
    void testGetDeviceByIdWhenDeviceIsEmpty() throws DeviceNotFoundException {
        assertThrows(IllegalArgumentException.class, () -> home.getDeviceById(""),
                "IllegalArgumentException expected to be thrown when device is empty!");

        verify(deviceStorageMock, never()).get(any());
    }

    @Test
    void testGetDeviceByIdWhenDeviceIsNotFound() throws DeviceNotFoundException {

        String deviceId = "DeviceId";
        when(deviceStorageMock.exists(deviceId)).thenReturn(false);

        assertThrows(DeviceNotFoundException.class, () -> home.getDeviceById(deviceId),
                "DeviceNotFoundException expected to be thrown when device is not found!");

        verify(deviceStorageMock, never()).get(deviceId);
    }

    @Test
    void testGetDeviceQuantityPerType() {
        IoTDevice device1 = mock();
        IoTDevice device2 = mock();
        IoTDevice device3 = mock();

        when(device1.getType()).thenReturn(DeviceType.BULB);
        when(device2.getType()).thenReturn(DeviceType.SMART_SPEAKER);
        when(device3.getType()).thenReturn(DeviceType.BULB);

        List<IoTDevice> listMock = List.of(device1,device2,device3);
        when(deviceStorageMock.listAll()).thenReturn(listMock);

        int result = home.getDeviceQuantityPerType(DeviceType.BULB);

        assertEquals(2,result,
                "It should return the right quantity!");

    }

    @Test
    void testGetDeviceQuantityPerTypeWhenTypeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> home.getDeviceQuantityPerType(null),
                "IllegalArgumentException expected to be thrown when device is not found!");;

        verify(deviceStorageMock, never()).listAll();
    }

    @Test
    void testGetTopNDevicesByPowerConsumption() {
        IoTDevice device1 = mock();
        IoTDevice device2 = mock();
        IoTDevice device3 = mock();

        when(device1.getPowerConsumptionKWh()).thenReturn(35l);
        when(device2.getPowerConsumptionKWh()).thenReturn(10l);
        when(device3.getPowerConsumptionKWh()).thenReturn(20l);

        when(device1.getId()).thenReturn("1");
        when(device2.getId()).thenReturn("2");
        when(device3.getId()).thenReturn("3");

        List<IoTDevice> listMock = List.of(device1,device2,device3);
        when(deviceStorageMock.listAll()).thenReturn(listMock);

        List<String> resListMock = List.of("1","3","2");

        assertEquals(resListMock,home.getTopNDevicesByPowerConsumption(3),
                "It should return them in the right order!");
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionWhenNIsLess() {
        IoTDevice device1 = mock();
        IoTDevice device2 = mock();
        IoTDevice device3 = mock();

        when(device1.getPowerConsumptionKWh()).thenReturn(35l);
        when(device2.getPowerConsumptionKWh()).thenReturn(10l);
        when(device3.getPowerConsumptionKWh()).thenReturn(20l);

        when(device1.getId()).thenReturn("1");
        when(device3.getId()).thenReturn("3");

        List<IoTDevice> listMock = List.of(device1,device2,device3);
        when(deviceStorageMock.listAll()).thenReturn(listMock);

        List<String> resListMock = List.of("1","3");

        assertEquals(resListMock,home.getTopNDevicesByPowerConsumption(2),
                "It should return them in the right order!");
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionNisBiggerThanSize() {
        IoTDevice device1 = mock();
        IoTDevice device2 = mock();
        IoTDevice device3 = mock();

        when(device1.getPowerConsumptionKWh()).thenReturn(35l);
        when(device2.getPowerConsumptionKWh()).thenReturn(10l);
        when(device3.getPowerConsumptionKWh()).thenReturn(20l);

        when(device1.getId()).thenReturn("1");
        when(device2.getId()).thenReturn("2");
        when(device3.getId()).thenReturn("3");

        List<IoTDevice> listMock = List.of(device1,device2,device3);
        when(deviceStorageMock.listAll()).thenReturn(listMock);

        List<String> resListMock = List.of("1","3","2");

        assertEquals(resListMock,home.getTopNDevicesByPowerConsumption(50),
                "It should return them in the right order!");
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionNIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> home.getTopNDevicesByPowerConsumption(-3),
                "IllegalArgumentException expected to be thrown when n is negative!");;

        verify(deviceStorageMock, never()).listAll();
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionNIsZero() {

        assertThrows(IllegalArgumentException.class, () -> home.getTopNDevicesByPowerConsumption(0),
                "IllegalArgumentException expected to be thrown when n is zero!");
    }


    @Test
    void testGetFirstNDevicesByRegistrationNIsZero() {
        assertThrows(IllegalArgumentException.class, () -> home.getFirstNDevicesByRegistration(0),
                "IllegalArgumentException expected to be thrown when n is zero!");;

        verify(deviceStorageMock, never()).listAll();
    }

    @Test
    void testGetFirstNDevicesByRegistrationNIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> home.getFirstNDevicesByRegistration(-5),
                "IllegalArgumentException expected to be thrown when n is negative!");;

        verify(deviceStorageMock, never()).listAll();
    }



    @Test
    void testGetFirstNDevicesByRegistration() {
        IoTDevice device1 = mock();
        IoTDevice device2 = mock();
        IoTDevice device3 = mock();

        when(device1.getRegistration()).thenReturn(35l);
        when(device2.getRegistration()).thenReturn(10l);
        when(device3.getRegistration()).thenReturn(20l);

        List<IoTDevice> listMock = List.of(device1,device2,device3);
        when(deviceStorageMock.listAll()).thenReturn(listMock);

        List<IoTDevice> resListMock = List.of(device2,device3,device1);

        assertEquals(resListMock,home.getFirstNDevicesByRegistration(3),
                "It should return them in the right order!");
    }

    @Test
    void testGetFirstNDevicesByRegistrationNIsBiggerThanSize() {
        IoTDevice device1 = mock();
        IoTDevice device2 = mock();
        IoTDevice device3 = mock();

        when(device1.getRegistration()).thenReturn(35l);
        when(device2.getRegistration()).thenReturn(10l);
        when(device3.getRegistration()).thenReturn(20l);

        List<IoTDevice> listMock = List.of(device1,device2,device3);
        when(deviceStorageMock.listAll()).thenReturn(listMock);

        List<IoTDevice> resListMock = List.of(device2,device3,device1);

        assertEquals(resListMock,home.getFirstNDevicesByRegistration(50),
                "It should return them in the right order!");
    }

    @Test
    void testGetFirstNDevicesByRegistrationNIsSmallerThanSize() {
        IoTDevice device1 = mock();
        IoTDevice device2 = mock();
        IoTDevice device3 = mock();

        when(device1.getRegistration()).thenReturn(35l);
        when(device2.getRegistration()).thenReturn(10l);
        when(device3.getRegistration()).thenReturn(20l);

        List<IoTDevice> listMock = List.of(device1,device2,device3);
        when(deviceStorageMock.listAll()).thenReturn(listMock);

        List<IoTDevice> resListMock = List.of(device2);

        assertEquals(resListMock,home.getFirstNDevicesByRegistration(1),
                "It should return them in the right order!");
    }

}
