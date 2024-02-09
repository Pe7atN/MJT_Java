package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Detail;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MJTSpaceScanner implements SpaceScannerAPI {

    SecretKey secretKey;
    List<Mission> missions;
    List<Rocket> rockets;
    private static final String COMA_DELIMITER = ",";
    private static final String COMPLEX_DELIMITER = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    //for Mission
    private static final int ID_MISSION_INDEX = 0;
    private static final int COMPANY_INDEX = 1;
    private static final int LOCATION_INDEX = 2;
    private static final int DATUM_INDEX = 3;
    private static final int DETAIL_INDEX = 4;
    private static final int STATUS_ROCKET_INDEX = 5;
    private static final int COST_INDEX = 6;
    private static final int STATUS_MISSION_INDEX = 7;

    //for Rocket
    private static final int ID_ROCKET_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int WIKI_INDEX = 2;
    private static final int HEIGHT_INDEX = 3;

    //for Detail
    private static final int ROCKET_NAME_INDEX = 0;
    private static final int PAYLOAD_INDEX = 1;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {
        missions = new ArrayList<>();
        rockets = new ArrayList<>();

        formatMissions(missionsReader);
        formatRockets(rocketsReader);
        this.secretKey = secretKey;
    }

    public Collection<Mission> getAllMissions() {
        return missions;
    }

    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("cannot be null");
        }

        return missions.stream()
                .filter(mission -> mission.missionStatus().equals(missionStatus))
                .toList();
    }

    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("cannot be null");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("to is before from");
        }

        Map<String, Long> result = missions.stream()
                .filter(mission -> mission.date().isAfter(from) && mission.date().isBefore(to))
                .filter(mission -> mission.missionStatus().equals(MissionStatus.SUCCESS))
                .collect(Collectors.groupingBy(Mission::company, Collectors.counting()));

        if (result.isEmpty()) {
            return "";
        }

        return result.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }

    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        return missions.stream()
                .collect(Collectors.groupingBy(this::getCountry, Collectors.toCollection(ArrayList::new)));
    }

    public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("cannot be null");
        }

        if (rocketStatus == null) {
            throw new IllegalArgumentException("cannot be null");
        }

        if (n <= 0) {
            throw new IllegalArgumentException("cannot be less or equal to zero");
        }

        return missions.stream()
                .filter(mission -> mission.missionStatus().equals(missionStatus)
                        && mission.rocketStatus().equals(rocketStatus))
                .filter(mission -> mission.cost().isPresent())
                .sorted(Comparator.comparing(mission -> mission.cost().orElse(0.0)))
                .limit(n)
                .toList();
    }

    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        return missions.stream()
                .collect(Collectors.groupingBy(Mission::company,
                        Collectors.collectingAndThen(Collectors.groupingBy(Mission::location, Collectors.counting()),
                                location -> location.entrySet().stream()
                                        .max(Map.Entry.comparingByValue())
                                        .map(Map.Entry::getKey)
                                        .orElse(""))));
    }

    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("cannot be null");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("to is before from");
        }

        return missions.stream()
                .filter(mission -> mission.date().isAfter(from) && mission.date().isBefore(to))
                .filter(mission -> mission.missionStatus().equals(MissionStatus.SUCCESS))
                .collect(Collectors.groupingBy(Mission::company,
                        Collectors.collectingAndThen(Collectors.groupingBy(Mission::location, Collectors.counting()),
                                location -> location.entrySet().stream()
                                        .max(Map.Entry.comparingByValue())
                                        .map(Map.Entry::getKey)
                                        .orElse(""))));
    }

    public Collection<Rocket> getAllRockets() {
        return rockets;
    }

    public List<Rocket> getTopNTallestRockets(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("cannot be less or equal to zero");
        }

        return rockets.stream()
                .filter(rocket -> rocket.height().isPresent())
                .sorted(Comparator.comparing(rocket -> rocket.height().orElse(0.0), Comparator.reverseOrder()))
                .limit(n)
                .toList();
    }

    public Map<String, Optional<String>> getWikiPageForRocket() {
        return rockets.stream()
                .filter(rocket -> rocket.wiki().isPresent())
                .collect(Collectors.toMap(Rocket::name, Rocket::wiki));
    }

    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n, MissionStatus missionStatus,
                                                                          RocketStatus rocketStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("cannot be null");
        }

        if (rocketStatus == null) {
            throw new IllegalArgumentException("cannot be null");
        }

        if (n <= 0) {
            throw new IllegalArgumentException("cannot be less or equal to zero");
        }

        return missions.stream()
                .filter(mission -> mission.missionStatus().equals(missionStatus))
                .filter(mission -> mission.rocketStatus().equals(rocketStatus))
                .filter(mission -> mission.cost().isPresent())
                .sorted(Comparator.comparing(rocket -> rocket.cost().orElse(0.0),  Comparator.reverseOrder()))
                .limit(n)
                .map(this::getRocketFromMission)
                .map(Rocket::wiki)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to) throws CipherException {
        if (outputStream == null || from == null || to == null) {
            throw new IllegalArgumentException("cannot be null");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("to is before from");
        }
        Optional<Rocket> mostReliable =
                missions.stream()
                        .filter(mission -> mission.date().isAfter(from) && mission.date().isBefore(to))
                        .collect(Collectors.groupingBy(this::getRocketFromMission,
                                LinkedHashMap::new, Collectors.toList()))
                        .entrySet()
                        .stream()
                        .filter(entry -> !entry.getValue().isEmpty())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> calculateReliability(entry.getValue()),
                                (existing, replacement) -> existing,
                                LinkedHashMap::new))
                        .entrySet()
                        .stream()
                        .max(Comparator.comparingDouble(Map.Entry::getValue))
                        .map(Map.Entry::getKey);

        if (mostReliable.isPresent()) {
            encryptRocket(outputStream, mostReliable.get());
        }
    }

    private void formatMissions(Reader missionsReader) {
        try (BufferedReader bufferedReader = new BufferedReader(missionsReader)) {
            String line;
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                missions.add(formatMission(line));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("A problem occurred while reading file", e);
        }
    }

    private void formatRockets(Reader rocketReader) {
        try (BufferedReader bufferedReader = new BufferedReader(rocketReader)) {
            String line;
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                rockets.add(formatRocket(line));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("A problem occurred while reading file", e);
        }
    }

    private Mission formatMission(String line) {
        final String[] tokens = line.split(COMPLEX_DELIMITER, -1);

        tokens[LOCATION_INDEX] = tokens[LOCATION_INDEX].replace("\"", "");
        tokens[DATUM_INDEX] = tokens[DATUM_INDEX].replace("\"", "");
        tokens[DETAIL_INDEX] = tokens[DETAIL_INDEX].replace("\"", "");
        tokens[COST_INDEX] = tokens[COST_INDEX].replace("\"", "");
        return new Mission(tokens[ID_MISSION_INDEX], tokens[COMPANY_INDEX], tokens[LOCATION_INDEX],
                formatDate(tokens[DATUM_INDEX]), formatDetail(tokens[DETAIL_INDEX]),
                formatRocketStatus(tokens[STATUS_ROCKET_INDEX]), Optional.ofNullable(formatCost(tokens[COST_INDEX])),
                formatMissionStatus(tokens[STATUS_MISSION_INDEX]));
    }

    private Rocket formatRocket(String line) {
        final String[] tokens = line.split(COMPLEX_DELIMITER, -1);

        return new Rocket(tokens[ID_ROCKET_INDEX], tokens[NAME_INDEX], Optional.ofNullable(tokens[WIKI_INDEX]),
                Optional.ofNullable(formatHeight(tokens[HEIGHT_INDEX])));
    }

    private Detail formatDetail(String line) {
        final String[] tokens = line.split("\\|");

        String name = tokens[ROCKET_NAME_INDEX];
        String payLoad = tokens[PAYLOAD_INDEX];
        return new Detail(name.substring(0, name.length() - 1), payLoad.substring(1));
    }

    private RocketStatus formatRocketStatus(String line) {
        for (RocketStatus status : RocketStatus.values()) {
            if (status.toString().equals(line)) {
                return status;
            }
        }

        return null;
    }

    private Double formatCost(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }

        line = line.replace(",", "");
        return Double.parseDouble(line);
    }

    private MissionStatus formatMissionStatus(String line) {
        for (MissionStatus status : MissionStatus.values()) {
            if (status.toString().equals(line)) {
                return status;
            }
        }

        return null;
    }

    private LocalDate formatDate(String line) {
        final String[] tokens = line.split(COMA_DELIMITER);
        final int dateIndex = 0;
        final int yearIndex = 1;
        final int monthFromIndex = 4;
        final int monthToIndex = 7;
        final int dayFromIndex = 8;
        final int dayToIndex = tokens[dateIndex].length();

        int year = Integer.parseInt(tokens[yearIndex].trim());
        String month = tokens[dateIndex].substring(monthFromIndex, monthToIndex);

        int monthInt = switch (month) {
            case "Jan" -> Month.JANUARY.getValue();
            case "Feb" -> Month.FEBRUARY.getValue();
            case "Mar" -> Month.MARCH.getValue();
            case "Apr" -> Month.APRIL.getValue();
            case "May" -> Month.MAY.getValue();
            case "Jun" -> Month.JUNE.getValue();
            case "Jul" -> Month.JULY.getValue();
            case "Aug" -> Month.AUGUST.getValue();
            case "Sep" -> Month.SEPTEMBER.getValue();
            case "Oct" -> Month.OCTOBER.getValue();
            case "Nov" -> Month.NOVEMBER.getValue();
            case "Dec" -> Month.DECEMBER.getValue();
            default -> 0;
        };
        int day = Integer.parseInt(tokens[dateIndex].substring(dayFromIndex, dayToIndex));
        return LocalDate.of(year, monthInt, day);
    }

    private Double formatHeight(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }

        final int from = 0;
        final int to = line.length() - 2;
        return Double.parseDouble(line.substring(from, to));
    }

    private String getCountry(Mission mission) {
        String location = mission.location();
        String[] tokens = location.split(COMA_DELIMITER);
        int countryIndex = tokens.length - 1;

        return tokens[countryIndex].substring(1);
    }

    private Rocket getRocketFromMission(Mission mission) {
        String rocketName = mission.detail().rocketName();
        for (Rocket rocket : rockets) {
            if (rocket.name().equals(rocketName)) {
                return rocket;
            }
        }

        return null;
    }

    private double calculateReliability(List<Mission> rocketMissions) {
        long successfulCount = rocketMissions.stream().filter(mission ->
                mission.missionStatus() == MissionStatus.SUCCESS).count();
        long unsuccessfulCount = rocketMissions.stream()
                .filter(mission -> mission.missionStatus().equals(MissionStatus.FAILURE)
                        || mission.missionStatus().equals(MissionStatus.PRELAUNCH_FAILURE)
                        || mission.missionStatus().equals(MissionStatus.PARTIAL_FAILURE))
                .count();
        long totalMissions = rocketMissions.size();

        if (totalMissions == 0) {
            return 0;
        }

        return (2.0 * successfulCount + unsuccessfulCount) / (2.0 * totalMissions);
    }

    private void encryptRocket(OutputStream outputStream, Rocket mostReliable) throws CipherException {
        Rijndael rijndael = new Rijndael(secretKey);
        byte[] rocketNameBytes = mostReliable.name().getBytes(StandardCharsets.UTF_8);

        rijndael.encrypt(new ByteArrayInputStream(rocketNameBytes), outputStream);
    }

}