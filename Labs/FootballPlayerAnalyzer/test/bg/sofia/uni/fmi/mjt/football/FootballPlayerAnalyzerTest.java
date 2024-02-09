package bg.sofia.uni.fmi.mjt.football;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FootballPlayerAnalyzerTest {

    FootballPlayerAnalyzer manager =
            new FootballPlayerAnalyzer(new FileReader("fifa_players_smaller.csv"));

    public FootballPlayerAnalyzerTest() throws FileNotFoundException {
    }


    @Test
    void testGetHighestPaidPlayerByNationality() {
        String line = "L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left";
        Player messi = Player.of(line);
        assertEquals(messi, manager.getHighestPaidPlayerByNationality("Argentina"),
                "It was not the expected player!");
    }

    @Test
    void testGetHighestPaidPlayerByNationalityWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> manager.getHighestPaidPlayerByNationality(null),
                "IllegalArgumentException was expected to be thrown");
    }

    @Test
    void testGetAllNationalities() {
        Set<String> nationalities = Set.of(
                "Argentina", "Denmark", "France", "Italy", "Senegal", "Netherlands",
                "Germany", "Uruguay", "Spain", "Belgium");

        assertEquals(nationalities, manager.getAllNationalities(),
                "The nationalities were not the expected ones!");
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudget() {
        String line = "K. Mbappé;Kylian Mbappé;12/20/1998;20;152.4;73;RW,ST,RM;France;88;95;81000000;100000;Right";
        Player mbappe = Player.of(line);
        Optional<Player> topProspect = Optional.of(mbappe);
        assertEquals(topProspect, manager.getTopProspectPlayerForPositionInBudget(Position.ST, 82000000),
                "Its not the expected player!");
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetWhenIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.getTopProspectPlayerForPositionInBudget(null, 20000),
                "IllegalArgumentException was expected to be thrown when position is null");
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetWithNegativeBudget() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.getTopProspectPlayerForPositionInBudget(Position.LB, -2),
                "IllegalArgumentException was expected to be thrown when budget is negative");
    }

    @Test
    void testGetSimilarPlayers() {
        String line = "L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left";
        Player messi = Player.of(line);
        Set<Player> similarPlayer = Set.of(messi);
        assertEquals(similarPlayer, manager.getSimilarPlayers(messi),
                "Its not the expected set of players!");
    }

    @Test
    void testGetSimilarPlayersWhenIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.getSimilarPlayers(null),
                "IllegalArgumentException was expected to be thrown when player is null");
    }

    @Test
    void testGetPlayersByFullNameKeyword() {
        String lineDijk = "V. van Dijk;Virgil van Dijk;7/8/1991;27;193.04;92.1;CB;Netherlands;88;90;59500000;215000;Right";
        String lineCavani = "E. Cavani;Edinson Roberto Cavani Gómez;2/14/1987;32;185.42;77.1;ST;Uruguay;89;89;60000000;200000;Right";
        Player djik = Player.of(lineDijk);
        Player cavani = Player.of(lineCavani);
        Set<Player> playersWithKeyword = Set.of(djik, cavani);
        assertEquals(playersWithKeyword, manager.getPlayersByFullNameKeyword("va"),
                "It was not the expected set of players!");
    }

    @Test
    void testGetPlayersByFullNameKeywordWhenIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.getPlayersByFullNameKeyword(null),
                "IllegalArgumentException was expected to be thrown when keyword is null");
    }

    @Test
    void testGroupByPosition() {
        Map<Position, Set<Player>> playersWithPositions = new HashMap<>();
        List<Player> players = manager.getAllPlayers();

        for (Position pos : Position.values()) {
            Set<Player> currentPlayers = new HashSet<>();
            for (Player player : players) {
                if (player.positions().contains(pos)) {
                    currentPlayers.add(player);
                }
            }

            if(currentPlayers.isEmpty()){
                continue;
            }

            playersWithPositions.put(pos, currentPlayers);
        }

        assertEquals(playersWithPositions, manager.groupByPosition(),
                "Players are not group as expected!");

    }

}
