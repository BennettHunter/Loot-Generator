
public class Monster {
    public String name;
    public String treasureClass;


    public Monster(String line) {
        String[] parsed = line.split("\t");
        this.name = parsed[0];
        this.treasureClass = parsed[3];
    }

}