package nz.james.crosswordhelper;

/**
 * Created by james on 1/02/2017.
 */

public class Card {

    private String name;
    private String desc;
    private String pos;
    private String due;
    private String idList;

    public Card(String name, String desc, String pos, String due, String idList){
        this.name = name;
        this.desc = desc;
        this.pos = pos;
        this.due = due;
        this.idList = idList;
    }

    public Card(){

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public String getIdList() {
        return idList;
    }

    public void setIdList(String idList) {
        this.idList = idList;
    }
}
