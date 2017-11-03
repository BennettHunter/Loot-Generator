
public class Monster {
	public String name;
	public String treasureClass;
	public String level;
	public String type;
	public String[] item = {null, null, null};

	public Monster(String line) {
		String[] parsed = line.split("\t");
		this.name = parsed[0];
		this.treasureClass = parsed[3];
		this.level = parsed[2];
		this.type = parsed[1];
	}
	
	public void getTC(String line) {
		String[] parsed = line.split("\t");
		this.item[0] = parsed[0];
		this.item[1] = parsed[1];
		this.item[2] = parsed[2];
	}

}
