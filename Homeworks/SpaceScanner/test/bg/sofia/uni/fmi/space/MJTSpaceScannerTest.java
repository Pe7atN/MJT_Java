package bg.sofia.uni.fmi.space;

import bg.sofia.uni.fmi.mjt.space.MJTSpaceScanner;
import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Detail;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MJTSpaceScannerTest {

    private static final int KEY_SIZE_IN_BITS = 128;

    Mission mission1 = new Mission("0", "SpaceX", "LC-39A, Kennedy Space Center, Florida, USA", LocalDate.of(2020, 1, 7),
            new Detail("Falcon 9 Block 5", "Starlink V1 L9 & BlackSky"), RocketStatus.STATUS_ACTIVE, Optional.of(50.0), MissionStatus.SUCCESS);
    Mission mission2 = new Mission("1", "CASC", "Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China", LocalDate.of(2020, 2, 6),
            new Detail("Long March 2D", "Gaofen-9 04 & Q-SAT"), RocketStatus.STATUS_ACTIVE, Optional.of(29.75), MissionStatus.SUCCESS);
    Mission mission3 = new Mission("2", "SpaceX", "Pad A, Boca Chica, Texas, USA", LocalDate.of(2020, 3, 4),
            new Detail("Starship Prototype", "150 Meter Hop"), RocketStatus.STATUS_ACTIVE, Optional.empty(), MissionStatus.SUCCESS);
    Mission mission4 = new Mission("3", "Roscosmos", "Site 200/39, Baikonur Cosmodrome, Kazakhstan", LocalDate.of(2020, 4, 30),
            new Detail("Proton-M/Briz-M", "Ekspress-80 & Ekspress-103"), RocketStatus.STATUS_ACTIVE, Optional.of(65.0), MissionStatus.SUCCESS);
    Mission mission5 = new Mission("4", "ULA", "SLC-41, Cape Canaveral AFS, Florida, USA", LocalDate.of(2020, 5, 30),
            new Detail("Atlas V 541", "Perseverance"), RocketStatus.STATUS_ACTIVE, Optional.of(145.0), MissionStatus.SUCCESS);
    Mission mission6 = new Mission("5", "CASC", "LC-9, Taiyuan Satellite Launch Center, China", LocalDate.of(2020, 6, 25),
            new Detail("Long March 4B", "Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1"), RocketStatus.STATUS_ACTIVE, Optional.of(64.68), MissionStatus.SUCCESS);
    Mission mission7 = new Mission("14", "CASC", "Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China", LocalDate.of(2020, 7, 4),
            new Detail("Long March 2D", "Shiyan-6 02"), RocketStatus.STATUS_ACTIVE, Optional.of(29.75), MissionStatus.SUCCESS);
    List<Mission> allMissions = List.of(mission1, mission2, mission3, mission4, mission5, mission6, mission7);

    String missions = """
            Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket," Rocket",Status Mission
            0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Jan 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
            1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Feb 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Success
            2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Mar 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Success
            3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Apr 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Success
            4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu May 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
            5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Sat Jun 25, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Success
            14,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Sat Jul 04, 2020",Long March 2D | Shiyan-6 02,StatusActive,"29.75 ",Success
            """;

    Rocket rocket1 = new Rocket("103", "Atlas V 541", Optional.of("https://en.wikipedia.org/wiki/Atlas_V"), Optional.of(62.2));
    Rocket rocket2 = new Rocket("169", "Falcon 9 Block 5", Optional.of("https://en.wikipedia.org/wiki/Falcon_9"), Optional.of(70.0));
    Rocket rocket3 = new Rocket("213", "Long March 2D", Optional.of("https://en.wikipedia.org/wiki/Long_March_2D"), Optional.of(41.06));
    Rocket rocket4 = new Rocket("228", "Long March 4B", Optional.of("https://en.wikipedia.org/wiki/Long_March_4B"), Optional.of(44.1));
    Rocket rocket5 = new Rocket("294", "Proton-M/Briz-M", Optional.of("https://en.wikipedia.org/wiki/Proton-M"), Optional.of(58.2));
    Rocket rocket6 = new Rocket("371", "Starship Prototype", Optional.of("https://en.wikipedia.org/wiki/SpaceX_Starship"), Optional.of(50.0));
    Rocket rocket7 = new Rocket("372", "Starship-Super Heavy", Optional.of("https://en.wikipedia.org/wiki/BFR_(rocket)"), Optional.of(118.0));
    List<Rocket> allRockets = List.of(rocket1, rocket2, rocket3, rocket4, rocket5, rocket6, rocket7);

    String rockets = """
            "",Name,Wiki,Rocket Height
            103,Atlas V 541,https://en.wikipedia.org/wiki/Atlas_V,62.2 m
            169,Falcon 9 Block 5,https://en.wikipedia.org/wiki/Falcon_9,70.0 m
            213,Long March 2D,https://en.wikipedia.org/wiki/Long_March_2D,41.06 m
            228,Long March 4B,https://en.wikipedia.org/wiki/Long_March_4B,44.1 m
            294,Proton-M/Briz-M,https://en.wikipedia.org/wiki/Proton-M,58.2 m
            371,Starship Prototype,https://en.wikipedia.org/wiki/SpaceX_Starship,50.0 m
            372,Starship-Super Heavy,https://en.wikipedia.org/wiki/BFR_(rocket),118.0 m
            """;

    Reader readerMissions = new StringReader(missions);
    Reader readerRockets = new StringReader(rockets);
    SecretKey secretKey;

    {
        try {
            secretKey = generateSecretKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    MJTSpaceScanner scanner = new MJTSpaceScanner(readerMissions, readerRockets, secretKey);
    List<Mission> missionList = new ArrayList<>(scanner.getAllMissions());
    List<Rocket> rocketList = new ArrayList<>(scanner.getAllRockets());

    @Test
    void testGetAllMissions() {
        assertEquals(allMissions, scanner.getAllMissions(),
                "The list was not as the expected one!");
    }

    @Test
    void testGetAllMissionsWithCertainStatus() {
        assertEquals(allMissions, scanner.getAllMissions(MissionStatus.SUCCESS),
                "The list was not as the expected one!");
    }

    @Test
    void testGetAllMissionsWithCertainStatusWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> scanner.getAllMissions(null),
                "IllegalArgumentException was expected to be thrown");
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissions() {
        assertEquals("CASC", scanner.getCompanyWithMostSuccessfulMissions(LocalDate.of(2020, 1, 1),
                        LocalDate.of(2020, 12, 31)),
                "It was not the expected company!");
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsWhenOneIsNull() {
        assertThrows(IllegalArgumentException.class, () -> scanner.getCompanyWithMostSuccessfulMissions(
                        LocalDate.of(2021, 1, 1), null),
                "IllegalArgumentException was expected to be thrown");
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsWithWrongTime() {
        assertThrows(TimeFrameMismatchException.class, () -> scanner.getCompanyWithMostSuccessfulMissions(LocalDate.of(2021, 1, 1),
                        LocalDate.of(2020, 12, 31)),
                "TimeFrameMismatchException was expected to be thrown");
    }

    @Test
    void testGetMissionPerCountry() {
        Collection<Mission> usa = List.of(mission1, mission3, mission5);
        Collection<Mission> china = List.of(mission2, mission6, mission7);
        Collection<Mission> kazakhstan = List.of(mission4);

        Map<String, Collection<Mission>> test = Map.of(
                "USA", usa,
                "China", china,
                "Kazakhstan", kazakhstan
        );

        assertEquals(test, scanner.getMissionsPerCountry(),
                "It was not the expected map");
    }

    @Test
    void testGetTopNLeastExpensiveMissions() {
        List<Mission> test = List.of(mission2, mission7);
        assertEquals(test, scanner.getTopNLeastExpensiveMissions(2, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
                "It was not the expected list");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsWhenNIsZeroOrLess() {
        assertThrows(IllegalArgumentException.class, () -> scanner.getTopNLeastExpensiveMissions(0, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
                "IllegalArgumentException was expected to be thrown");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsWhenMissionStatusIsNull() {
        assertThrows(IllegalArgumentException.class, () -> scanner.getTopNLeastExpensiveMissions(2, null, RocketStatus.STATUS_ACTIVE),
                "IllegalArgumentException was expected to be thrown");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsWhenRocketStatusIsNull() {
        assertThrows(IllegalArgumentException.class, () -> scanner.getTopNLeastExpensiveMissions(2, MissionStatus.SUCCESS, null),
                "IllegalArgumentException was expected to be thrown");
    }

    @Test
    void testGetMostDesiredLocationForMissionsPerCompany() {
        Map<String, String> test = Map.of(
                "SpaceX", "LC-39A, Kennedy Space Center, Florida, USA",
                "CASC", "Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China",
                "Roscosmos", "Site 200/39, Baikonur Cosmodrome, Kazakhstan",
                "ULA", "SLC-41, Cape Canaveral AFS, Florida, USA"
        );

        assertEquals(test, scanner.getMostDesiredLocationForMissionsPerCompany(),
                "It was not the expected map");
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompany() {
        Map<String, String> test = Map.of(
                "SpaceX", "LC-39A, Kennedy Space Center, Florida, USA",
                "CASC", "Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China",
                "Roscosmos", "Site 200/39, Baikonur Cosmodrome, Kazakhstan",
                "ULA", "SLC-41, Cape Canaveral AFS, Florida, USA"
        );

        assertEquals(test, scanner.getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.of(2020, 1, 1),
                        LocalDate.of(2020, 12, 31)),
                "It was not the expected map");
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyWhenOneIsNull() {
        assertThrows(IllegalArgumentException.class, () -> scanner.getLocationWithMostSuccessfulMissionsPerCompany(
                        LocalDate.of(2021, 1, 1), null),
                "IllegalArgumentException was expected to be thrown");
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyWithWrongTime() {
        assertThrows(TimeFrameMismatchException.class, () -> scanner.getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.of(2021, 1, 1),
                        LocalDate.of(2020, 12, 31)),
                "TimeFrameMismatchException was expected to be thrown");
    }

    @Test
    void testGetAllRockets() {
        assertEquals(allRockets, scanner.getAllRockets(),
                "The list was not as the expected one!");
    }

    @Test
    void testGetNTallestRockets() {
        List<Rocket> test = List.of(rocket7, rocket2);
        assertEquals(test, scanner.getTopNTallestRockets(2),
                "it was not the expected list");
    }

    @Test
    void testGetNTallestRocketsWhenNIsZeroOrLess() {
        assertThrows(IllegalArgumentException.class, () -> scanner.getTopNTallestRockets(0),
                "IllegalArgumentException was expected to be thrown");
    }

    @Test
    void testGetWikiPageForRocket() {
        Map<String, Optional<String>> test = Map.of(
                "Atlas V 541", Optional.of("https://en.wikipedia.org/wiki/Atlas_V"),
                "Falcon 9 Block 5", Optional.of("https://en.wikipedia.org/wiki/Falcon_9"),
                "Long March 2D", Optional.of("https://en.wikipedia.org/wiki/Long_March_2D"),
                "Long March 4B", Optional.of("https://en.wikipedia.org/wiki/Long_March_4B"),
                "Proton-M/Briz-M", Optional.of("https://en.wikipedia.org/wiki/Proton-M"),
                "Starship Prototype", Optional.of("https://en.wikipedia.org/wiki/SpaceX_Starship"),
                "Starship-Super Heavy", Optional.of("https://en.wikipedia.org/wiki/BFR_(rocket)")
        );

        assertEquals(test, scanner.getWikiPageForRocket(),
                "It was not the expected map");
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissions() {
        List<String> test = List.of("https://en.wikipedia.org/wiki/Atlas_V",
                "https://en.wikipedia.org/wiki/Proton-M");

        assertEquals(test, scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(2, MissionStatus.SUCCESS,
                RocketStatus.STATUS_ACTIVE), "It was not the expected list");
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsWhenNIsZeroOrLess() {
        assertThrows(IllegalArgumentException.class, () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(0, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
                "IllegalArgumentException was expected to be thrown");
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsWhenMissionStatusIsNull() {
        assertThrows(IllegalArgumentException.class, () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(2, null, RocketStatus.STATUS_ACTIVE),
                "IllegalArgumentException was expected to be thrown");
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsIsNull() {
        assertThrows(IllegalArgumentException.class, () -> scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(2, MissionStatus.SUCCESS, null),
                "IllegalArgumentException was expected to be thrown");
    }

    @Test
    void testSaveMostReliableRocket() throws IOException, CipherException {
        Rocket mostReliable = rocket2;
        Rijndael rijndael = new Rijndael(secretKey);
        ByteArrayOutputStream encryptionDestination = new ByteArrayOutputStream();
        ByteArrayOutputStream decryptionFile = new ByteArrayOutputStream();
        scanner.saveMostReliableRocket(encryptionDestination, LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 12, 1));

        rijndael.decrypt(new ByteArrayInputStream(encryptionDestination.toByteArray()), decryptionFile);
        encryptionDestination.close();

        assertEquals(mostReliable.name(), decryptionFile.toString(),
                "It is not the expected rocket name");
        decryptionFile.close();
    }

    @Test
    void testSaveMostReliableRocketWhenOutputIsNull() {
        assertThrows(IllegalArgumentException.class, () -> scanner.saveMostReliableRocket(null,
                        LocalDate.of(2020, 1, 31), LocalDate.of(2020, 12, 31)),
                "IllegalArgumentException was expected to be thrown");
    }

    @Test
    void testSaveMostReliableRocketWhenTimeIsNull() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        assertThrows(IllegalArgumentException.class, () -> scanner.saveMostReliableRocket(byteArrayOutputStream,
                        null, LocalDate.of(2020, 12, 31)),
                "IllegalArgumentException was expected to be thrown");
    }

    @Test
    void testSaveMostReliableRocketWithWrongTime() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        assertThrows(TimeFrameMismatchException.class, () -> scanner.saveMostReliableRocket(byteArrayOutputStream,
                        LocalDate.of(2021, 12, 31), LocalDate.of(2020, 12, 31)),
                "TimeFrameMismatchException was expected to be thrown");
    }

    private static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(KEY_SIZE_IN_BITS);
        return keyGenerator.generateKey();
    }
}
