package bg.sofia.uni.fmi.mjt.simcity.utility;

import bg.sofia.uni.fmi.mjt.simcity.property.billable.Billable;

import java.util.HashMap;
import java.util.Map;

public class UtilityService implements UtilityServiceAPI {

    private static final int UTILITY_TYPE_COUNT = 3;
    private Map<UtilityType, Double> taxRates;

    public UtilityService(Map<UtilityType, Double> taxRates) {
        this.taxRates = new HashMap<>(taxRates);
    }

    @Override
    public <T extends Billable> double getUtilityCosts(UtilityType utilityType, T billable) {
        if (utilityType == null) {
            throw new IllegalArgumentException("The utilityType is null!");
        }

        if (billable == null) {
            throw new IllegalArgumentException("The billable is null!");
        }

        double totalCost = switch (utilityType) {
            case WATER -> billable.getWaterConsumption();
            case ELECTRICITY -> billable.getElectricityConsumption();
            case NATURAL_GAS -> billable.getNaturalGasConsumption();
        };

        return totalCost * taxRates.get(utilityType);
    }

    @Override
    public <T extends Billable> double getTotalUtilityCosts(T billable) {

        if (billable == null) {
            throw new IllegalArgumentException("The billable is null!");
        }

        double totalCost = 0;
        for (UtilityType type : UtilityType.values()) {
            totalCost += switch (type) {
                case WATER -> billable.getWaterConsumption() * taxRates.get(type);
                case ELECTRICITY -> billable.getElectricityConsumption() * taxRates.get(type);
                case NATURAL_GAS -> billable.getNaturalGasConsumption() * taxRates.get(type);
            };
        }

        return totalCost;
    }

    @Override
    public <T extends Billable> Map<UtilityType, Double> computeCostsDifference(T firstBillable, T secondBillable) {

        if (firstBillable == null || secondBillable == null) {
            throw new IllegalArgumentException("One of the billable is null!");
        }
        Map<UtilityType, Double> costDifferences = new HashMap<>(UTILITY_TYPE_COUNT);

        for (UtilityType type : UtilityType.values()) {
            double difference = Math.abs(getUtilityCosts(type, firstBillable) - getUtilityCosts(type, secondBillable));
            costDifferences.put(type, difference);
        }

        return Map.copyOf(costDifferences);
    }

}
