package pl.pzienowicz.zditmszczecinlive.data;

import java.util.ArrayList;

import pl.pzienowicz.zditmszczecinlive.model.Line;
import pl.pzienowicz.zditmszczecinlive.model.LineType;

public class Lines {

    public static ArrayList<Line> getTramNormal()
    {
        ArrayList<Line> lines = new ArrayList<>();

        lines.add(new Line("1", 1, LineType.TRAM_NORMAL));
        lines.add(new Line("2", 2, LineType.TRAM_NORMAL));
        lines.add(new Line("3", 3, LineType.TRAM_NORMAL));
        lines.add(new Line("4", 4, LineType.TRAM_NORMAL));
        lines.add(new Line("5", 5, LineType.TRAM_NORMAL));
        lines.add(new Line("6", 6, LineType.TRAM_NORMAL));
        lines.add(new Line("7", 7, LineType.TRAM_NORMAL));
        lines.add(new Line("8", 8, LineType.TRAM_NORMAL));
        lines.add(new Line("9", 9, LineType.TRAM_NORMAL));
        lines.add(new Line("10", 84, LineType.TRAM_NORMAL));
        lines.add(new Line("11", 10, LineType.TRAM_NORMAL));
        lines.add(new Line("12", 11, LineType.TRAM_NORMAL));

        return lines;
    }

    public static ArrayList<Line> getBusNormal()
    {
        ArrayList<Line> lines = new ArrayList<>();

        lines.add(new Line("51", 12, LineType.BUS_NORMAL));
        lines.add(new Line("52", 13, LineType.BUS_NORMAL));
        lines.add(new Line("53", 14, LineType.BUS_NORMAL));
        lines.add(new Line("54", 15, LineType.BUS_NORMAL));
        lines.add(new Line("55", 16, LineType.BUS_NORMAL));
        lines.add(new Line("57", 19, LineType.BUS_NORMAL));
        lines.add(new Line("58", 20, LineType.BUS_NORMAL));
        lines.add(new Line("59", 21, LineType.BUS_NORMAL));
        lines.add(new Line("60", 22, LineType.BUS_NORMAL));
        lines.add(new Line("61", 23, LineType.BUS_NORMAL));
        lines.add(new Line("62", 24, LineType.BUS_NORMAL));
        lines.add(new Line("63", 25, LineType.BUS_NORMAL));
        lines.add(new Line("64", 26, LineType.BUS_NORMAL));
        lines.add(new Line("65", 27, LineType.BUS_NORMAL));

        lines.add(new Line("66", 28, LineType.BUS_NORMAL));
        lines.add(new Line("67", 29, LineType.BUS_NORMAL));
        lines.add(new Line("68", 30, LineType.BUS_NORMAL));
        lines.add(new Line("69", 31, LineType.BUS_NORMAL));
        lines.add(new Line("70", 32, LineType.BUS_NORMAL));
        lines.add(new Line("71", 33, LineType.BUS_NORMAL));
        lines.add(new Line("72", 94, LineType.BUS_NORMAL));
        lines.add(new Line("73", 35, LineType.BUS_NORMAL));
        lines.add(new Line("74", 36, LineType.BUS_NORMAL));
        lines.add(new Line("75", 37, LineType.BUS_NORMAL));
        lines.add(new Line("76", 38, LineType.BUS_NORMAL));
        lines.add(new Line("77", 39, LineType.BUS_NORMAL));
        lines.add(new Line("78", 40, LineType.BUS_NORMAL));

        lines.add(new Line("79", 41, LineType.BUS_NORMAL));
        lines.add(new Line("80", 42, LineType.BUS_NORMAL));
        lines.add(new Line("81", 43, LineType.BUS_NORMAL));
        lines.add(new Line("82", 44, LineType.BUS_NORMAL));
        lines.add(new Line("83", 79, LineType.BUS_NORMAL));
        lines.add(new Line("84", 45, LineType.BUS_NORMAL));
        lines.add(new Line("85", 17, LineType.BUS_NORMAL));
        lines.add(new Line("86", 85, LineType.BUS_NORMAL));
        lines.add(new Line("87", 46, LineType.BUS_NORMAL));
        lines.add(new Line("88", 86, LineType.BUS_NORMAL));
        lines.add(new Line("93", 34, LineType.BUS_NORMAL));
        lines.add(new Line("101", 47, LineType.BUS_NORMAL));
        lines.add(new Line("102", 48, LineType.BUS_NORMAL));

        lines.add(new Line("103", 49, LineType.BUS_NORMAL));
        lines.add(new Line("105", 91, LineType.BUS_NORMAL));
        lines.add(new Line("106", 50, LineType.BUS_NORMAL));
        lines.add(new Line("107", 51, LineType.BUS_NORMAL));
        lines.add(new Line("108", 92, LineType.BUS_NORMAL));
        lines.add(new Line("109", 52, LineType.BUS_NORMAL));
        lines.add(new Line("110", 53, LineType.BUS_NORMAL));
        lines.add(new Line("111", 54, LineType.BUS_NORMAL));
        lines.add(new Line("121", 90, LineType.BUS_NORMAL));
        lines.add(new Line("122", 89, LineType.BUS_NORMAL));
        lines.add(new Line("123", 87, LineType.BUS_NORMAL));
        lines.add(new Line("124", 100, LineType.BUS_NORMAL));
        return lines;
    }

    public static ArrayList<Line> getBusExpress() {
        ArrayList<Line> lines = new ArrayList<>();

        lines.add(new Line("A", 55, LineType.BUS_EXPRESS));
        lines.add(new Line("B", 56, LineType.BUS_EXPRESS));
        lines.add(new Line("C", 57, LineType.BUS_EXPRESS));
        lines.add(new Line("D", 58, LineType.BUS_EXPRESS));
        lines.add(new Line("E", 59, LineType.BUS_EXPRESS));
        lines.add(new Line("F", 60, LineType.BUS_EXPRESS));
        lines.add(new Line("G", 61, LineType.BUS_EXPRESS));

        return lines;
    }

    public static ArrayList<Line> getBusNight() {
        ArrayList<Line> lines = new ArrayList<>();

        lines.add(new Line("521", 62, LineType.BUS_NIGHT));
        lines.add(new Line("522", 63, LineType.BUS_NIGHT));
        lines.add(new Line("523", 64, LineType.BUS_NIGHT));
        lines.add(new Line("524", 65, LineType.BUS_NIGHT));
        lines.add(new Line("525", 66, LineType.BUS_NIGHT));
        lines.add(new Line("526", 67, LineType.BUS_NIGHT));
        lines.add(new Line("527", 68, LineType.BUS_NIGHT));
        lines.add(new Line("528", 69, LineType.BUS_NIGHT));
        lines.add(new Line("529", 70, LineType.BUS_NIGHT));
        lines.add(new Line("530", 71, LineType.BUS_NIGHT));
        lines.add(new Line("531", 72, LineType.BUS_NIGHT));
        lines.add(new Line("532", 73, LineType.BUS_NIGHT));
        lines.add(new Line("533", 74, LineType.BUS_NIGHT));
        lines.add(new Line("534", 75, LineType.BUS_NIGHT));
        lines.add(new Line("535", 93, LineType.BUS_NIGHT));
        lines.add(new Line("536", 88, LineType.BUS_NIGHT));

        return lines;
    }

    public static ArrayList<Line> getBusSubstitute() {
        ArrayList<Line> lines = new ArrayList<>();

        lines.add(new Line("856", 98, LineType.BUS_SUBSTITUTE));
        lines.add(new Line("896", 99, LineType.BUS_SUBSTITUTE));

        return lines;
    }
}
