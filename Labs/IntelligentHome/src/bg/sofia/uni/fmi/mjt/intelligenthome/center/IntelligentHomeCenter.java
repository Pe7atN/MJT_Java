package bg.sofia.uni.fmi.mjt.intelligenthome.center;

import bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator.KWhComparator;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator.RegistrationComparator;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceNotFoundException;

import bg.sofia.uni.fmi.mjt.intelligenthome.device.DeviceType;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;

import bg.sofia.uni.fmi.mjt.intelligenthome.storage.DeviceStorage;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class IntelligentHomeCenter {
    DeviceStorage storage;

    public IntelligentHomeCenter(DeviceStorage storage) {
        this.storage = storage;
    }

    public void register(IoTDevice device) throws DeviceAlreadyRegisteredException {
        if (device == null) {
            throw new IllegalArgumentException("device cannot be null");
        }

        if (storage.exists(device.getId())) {
            throw new DeviceAlreadyRegisteredException("device already registered");
        }

        storage.store(device.getId(), device);
        device.setRegistration(LocalDateTime.now());
    }

    public void unregister(IoTDevice device) throws DeviceNotFoundException {
        if (device == null) {
            throw new IllegalArgumentException("device cannot be null");
        }

        if (!storage.exists(device.getId())) {
            throw new DeviceNotFoundException("device cannot be found");
        }

        storage.delete(device.getId());
    }

    public IoTDevice getDeviceById(String id) throws DeviceNotFoundException {

        if (id == null) {
            throw new IllegalArgumentException("device cannot be null");
        }

        if (id.isEmpty() || id.isBlank()) {
            throw new IllegalArgumentException("device cannot be empty");
        }

        if (storage == null) {
            throw new IllegalArgumentException("storage cannot be null");
        }

        if (storage.exists(id)) {
            return storage.get(id);
        } else {
            throw new DeviceNotFoundException("device not found");
        }
    }

    public int getDeviceQuantityPerType(DeviceType type) {
        int quantity = 0;

        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }

        for (IoTDevice value : storage.listAll()) {
            if (value.getType().getShortName().equals(type.getShortName())) {
                quantity++;
            }
        }

        return quantity;
    }

    public Collection<String> getTopNDevicesByPowerConsumption(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }

        List<IoTDevice> list = new LinkedList<>();
        for (IoTDevice value : storage.listAll()) {
            list.add(value);
        }

        KWhComparator compareKWh = new KWhComparator();

        Collections.sort(list, compareKWh);

        if (n >= list.size()) {
            n = list.size();
        }

        List<String> arrList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            arrList.add(list.get(i).getId());
        }

        return arrList;
    }

    public Collection<IoTDevice> getFirstNDevicesByRegistration(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }

        List<IoTDevice> list = new LinkedList<>();

        for (IoTDevice value : storage.listAll()) {
            list.add(value);
        }

        RegistrationComparator compareReg = new RegistrationComparator();
        Collections.sort(list, compareReg);

        List<IoTDevice> arrList = new ArrayList<>();

        if (n >= list.size()) {
            n = list.size();
        }

        for (int i = 0; i < n; i++) {
            arrList.add(list.get(i));
        }

        return arrList;
    }

}