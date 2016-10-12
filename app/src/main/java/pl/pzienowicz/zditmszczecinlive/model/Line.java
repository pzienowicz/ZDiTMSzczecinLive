package pl.pzienowicz.zditmszczecinlive.model;

public class Line {

    private String name;
    private int id;
    private String type;

    public Line(String name, int id, String type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
