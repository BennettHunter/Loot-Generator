import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class LootGenerator {
    public static void main(String[] args) throws FileNotFoundException {

        // setting file paths 
        File monstersFile = new File("data/large/monstats.txt");
        File armorFile = new File("data/large/TreasureClassEx.txt");
        File armorFileStats = new File("data/large/armor.txt");
        File suffixFile = new File("data/large/MagicSuffix.txt");
        File prefixFile = new File("data/large/MagicPrefix.txt");

        //main driver loop of the game
        Scanner s = new Scanner(System.in);
        do {
            playGame(monstersFile, armorFile, armorFileStats, suffixFile,
                    prefixFile);
        } while (prompt(s));
        s.close();
    }

    /**
     * Prompts the user if they still want to play the game
     * If the user inputs an invalid response, the prompt is called again
     * @param s The scanner needed to read the user input
     * @return true if the user wants to fight again, false if not
     */
    private static boolean prompt(Scanner s) {
        String input = "";
        System.out.print("Fight again [y/n]? ");
        input = s.nextLine();
        System.out.println();
        if (input.equalsIgnoreCase("y")) {
            return true;
        } else if (input.equalsIgnoreCase("n")) {
            return false;
        } else {
            return prompt(s);
        }
    }

    /**
     * Main driver for the game, generates loot based on monster
     * @param monstersFile file for the monster
     * @param armorFile file for the armor
     * @param armorFileStats file for the armor stats
     * @param suffixFile file with listed suffixes 
     * @param prefixFile file with listed prefixes
     * @throws FileNotFoundException
     */
    private static void playGame(File monstersFile, File armorFile,
            File armorFileStats, File suffixFile, File prefixFile)
                    throws FileNotFoundException {
        boolean hasPrefix = ThreadLocalRandom.current().nextBoolean();
        boolean hasSuffix = ThreadLocalRandom.current().nextBoolean();
        String[] prefixData = { "", "", "" };
        String[] suffixData = { "", "", "" };
        if (hasPrefix) {
            prefixData = getAffix(prefixFile);
        }
        if (hasSuffix) {
            suffixData = getAffix(suffixFile);
        }
        Monster m = pickMonster(monstersFile);
        String item = fetchTreasure(m, armorFile);
        int baseStats = generateBaseStats(item, armorFileStats);

        System.out.println("Fighting " + m.name);
        System.out.println("You have slain " + m.name + "!");
        System.out.println(m.name + " dropped: ");
        System.out.println();

        printLoot(prefixData, suffixData, baseStats, item);
    }


    /**
     * Prints out the loot and its stats for the user to see
     * @param prefixData prefix associated with the loot and also stats associated with the prefix
     * @param suffixData suffix associated with the loot and also stats associated with the suffix
     * @param baseStats The stats associated with the base item
     * @param item the armor dropped by the monster
     */
    private static void printLoot(String[] prefixData, String[] suffixData,
            int baseStats, String item) {
        prefixData[0] = prefixData[0].length() > 0 ? prefixData[0] + " "
                : prefixData[0]; // only add leading space if there is a prefix
        String loot = prefixData[0] + item + " " + suffixData[0];

        System.out.println(loot);

        System.out.println("Defense: " + baseStats);
        System.out.println(prefixData[2] + " " + prefixData[1]);
        System.out.println(suffixData[2] + " " + suffixData[1]);
    }

    /**
     * Picks a random monster from file f
     * @param f the file used to pick a random monster. It is assumed that the file is
     * formatted correctly 
     * @return A monster 
     * @throws FileNotFoundException
     */
    private static Monster pickMonster(File f) throws FileNotFoundException {
        Scanner s = new Scanner(f);
        ArrayList<String[]> monsters = new ArrayList<String[]>();
        while (s.hasNext()) {
            monsters.add(s.nextLine().split("\n"));
        }
        Random r = new Random();
        int index = r.nextInt(monsters.size());
        s.close();
        return new Monster(monsters.get(index)[0]);
    }

    /**
     * Fetches the treasure associated with the monster m 
     * @param m the monster slain
     * @param f File with associated treasure classes and armor. It is assumed that the file is
     * formatted correctly  
     * @return A string of the dropped item 
     * @throws FileNotFoundException
     */
    private static String fetchTreasure(Monster m, File f)
            throws FileNotFoundException {
        Map<String, String[]> armorMap = new TreeMap<>();
        Scanner s = new Scanner(f);

        while (s.hasNext()) {
            String curLine = s.nextLine();
            String[] armorArr = curLine.split("\t");
            String key = armorArr[0];
            String[] valueList = Arrays.copyOfRange(armorArr, 1,
                    armorArr.length);
            armorMap.put(key, valueList);
        }

        Random r = new Random();
        int index = r.nextInt(3);
        String curKey = armorMap.get(m.treasureClass)[index];
        s.close();
        while (armorMap.containsKey(curKey)) {
            index = r.nextInt(3);
            curKey = armorMap.get(curKey)[index];
        }
        return curKey;
    }

    /**
     * Generates the base stats based on the loot given
     * @param loot the item dropped by the monster
     * @param f File with stats associated with items. It is assumed that the file is
     * formatted correctly
     * @return an int of the base defense of the armor
     * @throws FileNotFoundException
     */
    private static int generateBaseStats(String loot, File f)
            throws FileNotFoundException {
        Scanner s = new Scanner(f);

        while (s.hasNext()) {
            String curLine = s.nextLine();
            String[] stats = curLine.split("\t");
            if (stats[0].equals(loot)) {
                int randomNum = ThreadLocalRandom.current().nextInt(
                        Integer.parseInt(stats[1]),
                        Integer.parseInt(stats[2]) + 1);
                s.close();
                return randomNum;
            }
        }
        s.close();
        throw new IllegalArgumentException();
    }

    /**
     * Generates either a suffix or prefix based on the file inputed
     * @param f The file with prefixes or the file with suffixes and their associated stats
     * @return a String array with the 0th index being the actual affix
     * the 1st index is the affected trait 
     * the 2ed index is the modifier of the trait 
     * @throws FileNotFoundException
     */
    private static String[] getAffix(File f) throws FileNotFoundException {
        Scanner s = new Scanner(f);
        ArrayList<String[]> strArr = new ArrayList<String[]>();
        while (s.hasNext()) {
            String curLine = s.nextLine();
            strArr.add(curLine.split("\t"));
        }
        int index = ThreadLocalRandom.current().nextInt(0, strArr.size());
        String[] temp = strArr.get(index);
        String[] ret = { temp[0], temp[1],
                ((Integer) ThreadLocalRandom.current().nextInt(
                        Integer.parseInt(temp[2]),
                        Integer.parseInt(temp[3]) + 1)).toString() };
        s.close();
        return ret;
    }
}
