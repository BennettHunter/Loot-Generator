import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

public class LootGenerator {
	public static void main(String[] args) throws FileNotFoundException {
		File monstersFile = new File("data/large/monstats.txt");
		File armorFile = new File("data/large/TreasureClassEx.txt");
		Monster m = pickMonster(monstersFile);
		System.out.println("Fighting " + m.name);
		System.out.println("You have slain " + m.name + "!");
		System.out.println(m.name + " dropped: ");
		System.out.println();
		String loot = fetchTreasureClass(m, armorFile);
		System.out.println(loot);
		
		
	}
	public static Monster pickMonster(File f) throws FileNotFoundException {
		Scanner s = new Scanner(f);
		ArrayList<String[]> monsters = new ArrayList<String[]>();
		while(s.hasNext()) {
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

		while(s.hasNext()) {
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
		while(armorMap.containsKey(curKey)) {
			index = r.nextInt(3);
			curKey = armorMap.get(curKey)[index];
		}
		return curKey;
	}
}
