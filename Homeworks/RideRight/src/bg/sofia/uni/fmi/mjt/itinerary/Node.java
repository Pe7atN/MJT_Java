package bg.sofia.uni.fmi.mjt.itinerary;

import java.math.BigDecimal;

public class Node implements Comparable<Node> {
    private City city;
    private Journey cheapestJourney;
    private Node parent;
    private BigDecimal totalPrice = BigDecimal.valueOf(Double.MAX_VALUE);
    private BigDecimal factualPrice = BigDecimal.valueOf(Double.MAX_VALUE);
    private final BigDecimal approximatePrice;

    public Node(City city, BigDecimal approximatePrice) {
        this.city = city;
        this.approximatePrice = approximatePrice;
    }

    @Override
    public int compareTo(Node n) {
        int priceComparison = totalPrice.compareTo(n.totalPrice);

        if (priceComparison == 0) {
            return city.name().compareTo(n.city.name());
        }

        return priceComparison;
    }

    public BigDecimal getFactualPrice() {
        return factualPrice;
    }

    public City getCity() {
        return city;
    }

    public Journey getCheapestJourney() {
        return cheapestJourney;
    }

    public Node getParent() {
        return parent;
    }

    public void calcTotalPrice() {
        totalPrice = factualPrice.add(approximatePrice);
    }

    public void setFactualPrice(BigDecimal factualPrice) {
        this.factualPrice = factualPrice;
    }

    public void setCheapestJourney(Journey cheapestJourney) {
        this.cheapestJourney = cheapestJourney;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void updateNode(BigDecimal factualPrice, Journey cheapestJourney, Node parent) {
        this.setFactualPrice(factualPrice);
        this.calcTotalPrice();
        this.setCheapestJourney(cheapestJourney);
        this.setParent(parent);
    }
}
