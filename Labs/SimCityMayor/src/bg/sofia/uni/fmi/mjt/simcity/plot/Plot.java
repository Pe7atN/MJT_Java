package bg.sofia.uni.fmi.mjt.simcity.plot;

import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableNotFoundException;
import bg.sofia.uni.fmi.mjt.simcity.exception.InsufficientPlotAreaException;
import bg.sofia.uni.fmi.mjt.simcity.property.buildable.Buildable;
e
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Plot<E extends Buildable> implements PlotAPI<E> {

    private int buildableArea;
    private Map<String, E> buildings;

    private boolean checkAddressExist(String address) {
        for (String addresses : buildings.keySet()) {
            if (addresses.equals(address)) {
                return true;
            }
        }

        return false;
    }

    public Plot(int buildableArea) {
        this.buildableArea = buildableArea;
        this.buildings = new HashMap<>();
    }

    @Override
    public void construct(String address, E buildable) {

        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("The address is null or blank!");
        }

        if (buildable == null) {
            throw new IllegalArgumentException("The buildable is null!");
        }

        if (buildable.getArea() > buildableArea) {
            throw new InsufficientPlotAreaException("The area exceeds the remaining plot area!");
        }

        if (checkAddressExist(address)) {
            throw new BuildableAlreadyExistsException("The address is already occupied!");
        }

        buildableArea -= buildable.getArea();
        buildings.put(address, buildable);
    }

    @Override
    public void constructAll(Map<String, E> buildables) {

        if (buildables == null || buildables.isEmpty()) {
            throw new IllegalArgumentException("The buildables is null or empty!");
        }

        for (String address : buildables.keySet()) {
            if (checkAddressExist(address)) {
                throw new BuildableAlreadyExistsException("The address is already occupied!");
            }
        }

        int combinedArea = 0;
        for (E building : buildables.values()) {
            combinedArea += building.getArea();
        }

        if (combinedArea > buildableArea) {
            throw new InsufficientPlotAreaException("The combined area exceeds the remaining plot area!");
        }

        buildableArea -= combinedArea;
        buildings.putAll(buildables);
    }

    @Override
    public void demolish(String address) {

        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("The address is null or blank!");
        }

        if (!checkAddressExist(address)) {
            throw new BuildableNotFoundException("Buildable with such address does not exist!");
        }

        buildableArea += buildings.get(address).getArea();
        buildings.remove(address);
    }

    @Override
    public void demolishAll() {

        Iterator<String> iterator = buildings.keySet().iterator();
        while (iterator.hasNext()) {
            String address = iterator.next();
            buildableArea += buildings.get(address).getArea();
            iterator.remove();
        }
    }

    @Override
    public Map<String, E> getAllBuildables() {
        return Map.copyOf(buildings);
    }

    @Override
    public int getRemainingBuildableArea() {
        return buildableArea;
    }

}
