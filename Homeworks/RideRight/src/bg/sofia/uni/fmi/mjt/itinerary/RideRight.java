package bg.sofia.uni.fmi.mjt.itinerary;

import bg.sofia.uni.fmi.mjt.itinerary.exception.CityNotKnownException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SequencedCollection;
import java.util.Set;

public class RideRight implements ItineraryPlanner {
    private List<Journey> schedule; //edges
    private Set<City> allCities; //vertices
    private static final int DOLLARS_PER_KILOMETER = 20;

    public RideRight(List<Journey> schedule) {
        this.schedule = schedule;
        getAllCities();
    }

    private void getAllCities() {
        allCities = new HashSet<>();
        for (Journey journey : schedule) {
            allCities.add(journey.from());
            allCities.add(journey.to());
        }
    }

    private boolean isCityKnown(City city) {
        return allCities.contains(city);
    }

    private double getDistanceInKilometers(City from, City to) {
        final int metersInKilometer = 1000;
        double dx = Math.abs(from.location().x() - to.location().x());
        double dy = Math.abs(from.location().y() - to.location().y());

        return (dx + dy) / metersInKilometer;
    }

    private BigDecimal getApproximatePrice(double distance) {
        return BigDecimal.valueOf(distance * DOLLARS_PER_KILOMETER);
    }

    private Map<City, Node> getNodes(City target) {
        Map<City, Node> nodesForAlgorithm = new HashMap<>();
        for (City city : allCities) {
            Node current = new Node(city,
                    getApproximatePrice(getDistanceInKilometers(city, target)));
            nodesForAlgorithm.put(city, current);
        }

        return Map.copyOf(nodesForAlgorithm);
    }

    private Set<City> getNeighbourCities(City start) {
        Set<City> neighbourCities = new HashSet<>();
        for (Journey journeys : schedule) {
            if (journeys.from().equals(start)) {
                neighbourCities.add(journeys.to());
            }
        }

        return Set.copyOf(neighbourCities);
    }

    //We know for sure that there is a path between
    //these two cities and the cheapestJourney is not going to be null
    private Journey findCheapestPathBetweenTwoCities(City start, City destination) {

        Journey cheapestJourney = null;
        BigDecimal minCurrentPrice = BigDecimal.valueOf(Double.MAX_VALUE);
        for (Journey journey : schedule) {
            if (journey.from().equals(start) && journey.to().equals(destination)) {
                BigDecimal getCurrentPrice =
                        journey.price().add(journey.price().multiply(journey.vehicleType().getGreenTax()));

                if (minCurrentPrice.compareTo(getCurrentPrice) > 0) {
                    cheapestJourney = journey;
                    minCurrentPrice = getCurrentPrice;
                }
            }
        }

        return cheapestJourney;
    }

    private SequencedCollection<Journey> buildThePath(Node current) {
        List<Journey> cheapestPath = new ArrayList<>();

        //In findCheapestPath for the startNode we set the cheapestJourney to be null,
        //and this is the end of our while loop
        while (current.getCheapestJourney() != null) {
            cheapestPath.add(current.getCheapestJourney());
            current = current.getParent();
        }

        Collections.reverse(cheapestPath);
        return List.copyOf(cheapestPath);
    }

    @Override
    public SequencedCollection<Journey> findCheapestPath(City start, City destination, boolean allowTransfer)
            throws CityNotKnownException, NoPathToDestinationException {

        if (!isCityKnown(start) || !isCityKnown(destination)) {
            throw new CityNotKnownException("The start or destination are not known!");
        }

        if (start.equals(destination)) {
            //If the start and the destination is the same
            //city, we just return an empty path
            return List.of();
        }

        if (!allowTransfer) {
            //If there is not a valid journey between them, cheapestJourney will be null
            Journey cheapestJourney = findCheapestPathBetweenTwoCities(start, destination);

            if (cheapestJourney == null) {
                throw new NoPathToDestinationException("No path was found from start to destination!");
            } else {
                return List.of(cheapestJourney);
            }
        }

        Map<City, Node> listOfNodes = getNodes(destination);
        PriorityQueue<Node> openQueue = new PriorityQueue<>();
        PriorityQueue<Node> closedQueue = new PriorityQueue<>();

        Node startNode = listOfNodes.get(start);
        startNode.updateNode(BigDecimal.ZERO, null, null);
        openQueue.add(startNode);

        while (!openQueue.isEmpty()) {
            Node current = openQueue.poll();

            if (current.getCity().equals(destination)) {
                return buildThePath(current);
            }

            closedQueue.add(current);

            for (City city : getNeighbourCities(current.getCity())) {
                Node next = listOfNodes.get(city);

                if (closedQueue.contains(next)) {
                    //We have already found the cheapest path to this city
                    continue;
                }

                Journey cheapestJourney = findCheapestPathBetweenTwoCities(current.getCity(), next.getCity());
                BigDecimal currentPrice = cheapestJourney.price()
                        .add(cheapestJourney.price().multiply(cheapestJourney.vehicleType().getGreenTax()));
                currentPrice = currentPrice.add(current.getFactualPrice());

                if (!openQueue.contains(next)) {
                    next.updateNode(currentPrice, cheapestJourney, current);
                    openQueue.add(next);
                } else {

                    if (currentPrice.compareTo(next.getFactualPrice()) < 0) {
                        next.updateNode(currentPrice, cheapestJourney, current);
                    }
                }
            }
        }

        throw new NoPathToDestinationException("No path was found from start to destination!");
    }
}