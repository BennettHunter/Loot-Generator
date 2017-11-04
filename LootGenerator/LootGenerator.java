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
	// public static void main(String[] args) throws FileNotFoundException {
	// File monstersFile = new File("data/large/monstats.txt");
	// File armorFile = new File("data/large/TreasureClassEx.txt");
	// File armorFileStats = new File("data/large/armor.txt");
	// File suffixFile = new File("data/large/MagicSuffix.txt");
	// File prefixFile = new File("data/large/MagicPrefix.txt");
	// boolean hasPrefix = ThreadLocalRandom.current().nextBoolean();
	// boolean hasSuffix = ThreadLocalRandom.current().nextBoolean();
	// String loot = "";
	// String[] prefixData = { "", "", "" };
	// String[] suffixData = { "", "", "" };
	// Monster m = pickMonster(monstersFile);
	//
	// String item = fetchTreasureClass(m, armorFile);
	// loot += item;
	// if (hasPrefix) {
	// prefixData = getAffix(prefixFile);
	// loot = prefixData[0] + " " + loot;
	// }
	// if (hasSuffix) {
	// suffixData = getAffix(suffixFile);
	// loot = loot + " " + suffixData[0];
	// }
	// int baseS = generateBaseStats(item, armorFileStats);
	//
	// System.out.println("Fighting " + m.name);
	// System.out.println("You have slain " + m.name + "!");
	// System.out.println(m.name + " dropped: ");
	// System.out.println();
	//
	// System.out.println(loot);
	//
	// System.out.println("Defense: " + baseS);
	// System.out.println(prefixData[2] + " " + prefixData[1]);
	// System.out.println(suffixData[2] + " " + suffixData[1]);
	// }
	public static void main(String[] args) throws FileNotFoundException {
		File monstersFile = new File("data/large/monstats.txt");
		File armorFile = new File("data/large/TreasureClassEx.txt");
		File armorFileStats = new File("data/large/armor.txt");
		File suffixFile = new File("data/large/MagicSuffix.txt");
		File prefixFile = new File("data/large/MagicPrefix.txt");

		Scanner s = new Scanner(System.in);
		do {
			playGame(monstersFile, armorFile, armorFileStats, suffixFile, prefixFile);
		} while (prompt(s));
		s.close();
	}

	public static boolean prompt(Scanner s) {
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

	public static void playGame(File monstersFile, File armorFile, File armorFileStats, File suffixFile,
			File prefixFile) throws FileNotFoundException {
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
		String item = fetchTreasureClass(m, armorFile);
		int baseStats = generateBaseStats(item, armorFileStats);

		System.out.println("Fighting " + m.name);
		System.out.println("You have slain " + m.name + "!");
		System.out.println(m.name + " dropped: ");
		System.out.println();

		printLoot(prefixData, suffixData, baseStats, item);
	}

	public static void printLoot(String[] prefixData, String[] suffixData, int baseStats, String item) {
		prefixData[0] = prefixData[0].length() > 0 ? prefixData[0] + " " : prefixData[0]; // don't want those lame ass
																							// spaces
		String loot = prefixData[0] + item + " " + suffixData[0];

		System.out.println(loot);

		System.out.println("Defense: " + baseStats);
		System.out.println(prefixData[2] + " " + prefixData[1]);
		System.out.println(suffixData[2] + " " + suffixData[1]);
	}

	public static Monster pickMonster(File f) throws FileNotFoundException {
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

	public static String fetchTreasureClass(Monster m, File f) throws FileNotFoundException {
		Map<String, String[]> armorMap = new TreeMap<>();
		Scanner s = new Scanner(f);

		while (s.hasNext()) {
			String curLine = s.nextLine();
			String[] armorArr = curLine.split("\t");
			String key = armorArr[0];
			String[] valueList = Arrays.copyOfRange(armorArr, 1, armorArr.length);
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

	public static int generateBaseStats(String loot, File f) throws FileNotFoundException {
		Scanner s = new Scanner(f);

		while (s.hasNext()) {
			String curLine = s.nextLine();
			String[] stats = curLine.split("\t");
			if (stats[0].equals(loot)) {
				int randomNum = ThreadLocalRandom.current().nextInt(Integer.parseInt(stats[1]),
						Integer.parseInt(stats[2]) + 1);
				s.close();
				return randomNum;
			}
		}
		s.close();
		throw new IllegalArgumentException();
	}

	public static String[] getAffix(File f) throws FileNotFoundException {
		Scanner s = new Scanner(f);
		ArrayList<String[]> strArr = new ArrayList<String[]>();
		while (s.hasNext()) {
			String curLine = s.nextLine();
			strArr.add(curLine.split("\t"));
		}
		int index = ThreadLocalRandom.current().nextInt(0, strArr.size());
		String[] temp = strArr.get(index);
		String[] ret = { temp[0], temp[1], ((Integer) ThreadLocalRandom.current().nextInt(Integer.parseInt(temp[2]),
				Integer.parseInt(temp[3]) + 1)).toString() };
		s.close();
		return ret;
	}
}
