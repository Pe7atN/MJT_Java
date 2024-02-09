package bg.sofia.uni.fmi.mjt.football;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FootballPlayerAnalyzer {

    private List<Player> players;
    private static final int MAX_OVERALL_RATING_DIFFER = 3;

    public FootballPlayerAnalyzer(Reader reader) {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            bufferedReader.readLine();
            players = bufferedReader.lines().map(Player::of).toList();
        } catch (IOException e) {
            throw new UncheckedIOException("A problem occurred while reading file", e);
        }
    }

    public List<Player> getAllPlayers() {
        if (players.isEmpty()) {
            return List.of();
        }

        return List.copyOf(players);
    }

    public Set<String> getAllNationalities() {
        if (players.isEmpty()) {
            return Set.of();
        }

        return players.stream()
                .map(Player::nationality)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Player getHighestPaidPlayerByNationality(String nationality) {
        if (nationality == null) {
            throw new IllegalArgumentException("nationality cannot be null");
        }

        return players.stream()
                .filter(p -> p.nationality().equals(nationality))
                .max(Comparator.comparing(Player::wageEuro))
                .orElseThrow(() -> new NoSuchElementException("there is no player with provided nationality"));

    }

    public Map<Position, Set<Player>> groupByPosition() {
        return players.stream()
                .flatMap(player -> player.positions().stream()
                .map(position -> new AbstractMap.SimpleEntry<>(position, player)))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toUnmodifiableSet())));
    }

    public Optional<Player> getTopProspectPlayerForPositionInBudget(Position position, long budget) {
        if (position == null) {
            throw new IllegalArgumentException("position cannot be null");
        }

        if (budget < 0) {
            throw new IllegalArgumentException("budget cannot be negative");
        }

        Map<Player, Double> result =
                players.stream()
                        .filter(player -> player.positions().contains(position))
                        .filter(player -> player.valueEuro() <= budget)
                        .collect(Collectors.toMap(player -> player,
                                player -> (player.overallRating() + player.potential()) / (double) player.age()));

        return result.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public Set<Player> getSimilarPlayers(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }

        return players.stream()
                .filter(p -> p.preferredFoot().equals(player.preferredFoot()))
                .filter(p -> Math.abs(p.overallRating() - player.overallRating()) <= MAX_OVERALL_RATING_DIFFER)
                .filter(p -> p.positions().stream().anyMatch(player.positions()::contains))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Player> getPlayersByFullNameKeyword(String keyword) {
        if (keyword == null) {
            throw new IllegalArgumentException("keyword cannot be null");
        }

        return players.stream()
                .filter(p -> p.fullName().contains(keyword))
                .collect(Collectors.toUnmodifiableSet());
    }
}

