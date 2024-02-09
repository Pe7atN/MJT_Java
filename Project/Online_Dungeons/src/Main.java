import bg.sofia.uni.fmi.mjt.dungeon.entities.heroes.Hero;
import bg.sofia.uni.fmi.mjt.dungeon.map.Dungeon;
import bg.sofia.uni.fmi.mjt.dungeon.map.Location;
import bg.sofia.uni.fmi.mjt.dungeon.server.DungeonServer;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeon.treasure.Weapon;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        char[][] dungeon = {
            {'#', '.', '#', '.', '.', '#', '.', '.', '#', '.'},
            {'.', '.', '#', '#', '.', '#', '.', '#', '#', '.'},
            {'.', '.', '#', '.', '.', '#', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', '#', '#'}
        };

//        //myDungeon.printDungeon();
//
//        Hero hero = new Hero(myDungeon, 1, new Location(0, 0));
//        myDungeon.addHero(hero);
//        Scanner scanner = new Scanner(System.in);
//        myDungeon.printDungeon();
//        Treasure weapon = new Weapon(new Location(0, 0), 2);
//
//        while (true) {
//            System.out.print("Enter a command: ");
//            String command = scanner.next().trim().toLowerCase();
//            int index;
//            switch (command) {
//                case "up":
//                    hero.moveUp();
//                    break;
//                case "down":
//                    hero.moveDown();
//                    break;
//                case "right":
//                    hero.moveRight();
//                    break;
//                case "left":
//                    hero.moveLeft();
//                    break;
//                case "pick":
//                    hero.pickTreasure();
//                    break;
//                case "inventory":
//                    System.out.println("Things in the backpack");
//                    for (Treasure treasure : hero.getBackPack()) {
//                        System.out.println(treasure.toString());
//                    }
//                    break;
//                case "level":
//                    System.out.println(hero.getLevel());
//                    break;
//                case "health":
//                    System.out.println(hero.getCurrentHealth());
//                    break;
//                case "attack":
//                    hero.attack();
//                    break;
//                case "xp":
//                    System.out.println(hero.getCurrentXP());
//                    break;
//                case "use":
//                    index = scanner.nextInt();
//                    hero.useItem(index);
//                    break;
//                case "remove":
//                    index = scanner.nextInt();
//                    hero.removeItem(index);
//                    break;
//                case "give":
//                    index = scanner.nextInt();
//                    hero.giveItem(index);
//                    break;
//                default:
//                    System.out.println("Invalid command. Please try again.");
//            }
//            System.out.println("------------DUNGEON-------------------------");
//            myDungeon.printDungeon();
//            System.out.println("--------------------------------------------");
//        }
    }
}