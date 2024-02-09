package bg.sofia.uni.fmi.mjt.football;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public record Player(String name, String fullName, LocalDate birthDate, int age, double heightCm,
                     double weightKg, List<Position> positions, String nationality, int overallRating, int potential,
                     long valueEuro, long wageEuro, Foot preferredFoot) {

    private static final String PLAYER_DELIMITER = ";";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
    private static final int NAME_INDEX = 0;
    private static final int FULL_NAME_INDEX = 1;
    private static final int BIRTH_DATE_INDEX = 2;
    private static final int AGE_INDEX = 3;
    private static final int HEIGHT_INDEX = 4;
    private static final int WEIGHT_INDEX = 5;
    private static final int POSITION_INDEX = 6;
    private static final int NATIONALITY_INDEX = 7;
    private static final int RATING_INDEX = 8;
    private static final int POTENTIAL_INDEX = 9;
    private static final int VALUE_INDEX = 10;
    private static final int WAGE_INDEX = 11;
    private static final int FOOT_INDEX = 12;

    public static Player of(String line) {
            final String[] tokens = line.split(PLAYER_DELIMITER);

        return new Player(tokens[NAME_INDEX], tokens[FULL_NAME_INDEX],
                LocalDate.parse(tokens[BIRTH_DATE_INDEX], FORMATTER),
                Integer.parseInt(tokens[AGE_INDEX]), Double.parseDouble(tokens[HEIGHT_INDEX]),
                Double.parseDouble(tokens[WEIGHT_INDEX]), formatPositions(tokens[POSITION_INDEX]),
                tokens[NATIONALITY_INDEX], Integer.parseInt(tokens[RATING_INDEX]),
                Integer.parseInt(tokens[POTENTIAL_INDEX]), Long.parseLong(tokens[VALUE_INDEX]),
                Long.parseLong(tokens[WAGE_INDEX]), formatFoot(tokens[FOOT_INDEX]));
    }

    private static List<Position> formatPositions(String positions) {
        final String[] positionsSplit = positions.split(",");
        List<Position> positinsList = new ArrayList<>();
        for (String position : positionsSplit) {
            positinsList.add(getPosition(position));
        }

        return List.copyOf(positinsList);
    }

    private static Position getPosition(String position) {
        for (Position pos : Position.values()) {
            if (pos.name().equals(position)) {
                return pos;
            }
        }
        return null;
    }

    private static Foot formatFoot(String foot) {
        Foot preferedFoot;
        if (foot.equals("Left")) {
            preferedFoot = Foot.LEFT;
        } else {
            preferedFoot = Foot.RIGHT;
        }

        return preferedFoot;
    }

}
