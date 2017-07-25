package pl.pzienowicz.zditmszczecinlive.model;

import com.google.gson.annotations.SerializedName;

public class Line {

    @SerializedName("oznaczenie")
    private String name;

    @SerializedName("id")
    private int id;

    @SerializedName("rodzaj")
    private String type;

    @SerializedName("pomarancz")
    private int isChanged;

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

    public boolean isChanged() {
        return isChanged == 1;
    }
}
