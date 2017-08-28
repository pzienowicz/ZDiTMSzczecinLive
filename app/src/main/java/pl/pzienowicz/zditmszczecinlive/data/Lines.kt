package pl.pzienowicz.zditmszczecinlive.data

import java.util.ArrayList

import pl.pzienowicz.zditmszczecinlive.model.Line
import pl.pzienowicz.zditmszczecinlive.model.LineType

object Lines {

    val tramNormal: ArrayList<Line>
        get() {
            val lines = ArrayList<Line>()

            lines.add(Line("1", 1, LineType.TRAM_NORMAL))
            lines.add(Line("2", 2, LineType.TRAM_NORMAL))
            lines.add(Line("3", 3, LineType.TRAM_NORMAL))
            lines.add(Line("4", 4, LineType.TRAM_NORMAL))
            lines.add(Line("5", 5, LineType.TRAM_NORMAL))
            lines.add(Line("6", 6, LineType.TRAM_NORMAL))
            lines.add(Line("7", 7, LineType.TRAM_NORMAL))
            lines.add(Line("8", 8, LineType.TRAM_NORMAL))
            lines.add(Line("9", 9, LineType.TRAM_NORMAL))
            lines.add(Line("10", 84, LineType.TRAM_NORMAL))
            lines.add(Line("11", 10, LineType.TRAM_NORMAL))
            lines.add(Line("12", 11, LineType.TRAM_NORMAL))

            return lines
        }

    val busNormal: ArrayList<Line>
        get() {
            val lines = ArrayList<Line>()

            lines.add(Line("51", 12, LineType.BUS_NORMAL))
            lines.add(Line("52", 13, LineType.BUS_NORMAL))
            lines.add(Line("53", 14, LineType.BUS_NORMAL))
            lines.add(Line("54", 15, LineType.BUS_NORMAL))
            lines.add(Line("55", 16, LineType.BUS_NORMAL))
            lines.add(Line("57", 19, LineType.BUS_NORMAL))
            lines.add(Line("58", 20, LineType.BUS_NORMAL))
            lines.add(Line("59", 21, LineType.BUS_NORMAL))
            lines.add(Line("60", 22, LineType.BUS_NORMAL))
            lines.add(Line("61", 23, LineType.BUS_NORMAL))
            lines.add(Line("62", 24, LineType.BUS_NORMAL))
            lines.add(Line("63", 25, LineType.BUS_NORMAL))
            lines.add(Line("64", 26, LineType.BUS_NORMAL))
            lines.add(Line("65", 27, LineType.BUS_NORMAL))

            lines.add(Line("66", 28, LineType.BUS_NORMAL))
            lines.add(Line("67", 29, LineType.BUS_NORMAL))
            lines.add(Line("68", 30, LineType.BUS_NORMAL))
            lines.add(Line("69", 31, LineType.BUS_NORMAL))
            lines.add(Line("70", 32, LineType.BUS_NORMAL))
            lines.add(Line("71", 33, LineType.BUS_NORMAL))
            lines.add(Line("72", 94, LineType.BUS_NORMAL))
            lines.add(Line("73", 35, LineType.BUS_NORMAL))
            lines.add(Line("74", 36, LineType.BUS_NORMAL))
            lines.add(Line("75", 37, LineType.BUS_NORMAL))
            lines.add(Line("76", 38, LineType.BUS_NORMAL))
            lines.add(Line("77", 39, LineType.BUS_NORMAL))
            lines.add(Line("78", 40, LineType.BUS_NORMAL))

            lines.add(Line("79", 41, LineType.BUS_NORMAL))
            lines.add(Line("80", 42, LineType.BUS_NORMAL))
            lines.add(Line("81", 43, LineType.BUS_NORMAL))
            lines.add(Line("82", 44, LineType.BUS_NORMAL))
            lines.add(Line("83", 79, LineType.BUS_NORMAL))
            lines.add(Line("84", 45, LineType.BUS_NORMAL))
            lines.add(Line("85", 17, LineType.BUS_NORMAL))
            lines.add(Line("86", 85, LineType.BUS_NORMAL))
            lines.add(Line("87", 46, LineType.BUS_NORMAL))
            lines.add(Line("88", 86, LineType.BUS_NORMAL))
            lines.add(Line("93", 34, LineType.BUS_NORMAL))
            lines.add(Line("101", 47, LineType.BUS_NORMAL))
            lines.add(Line("102", 48, LineType.BUS_NORMAL))

            lines.add(Line("103", 49, LineType.BUS_NORMAL))
            lines.add(Line("105", 91, LineType.BUS_NORMAL))
            lines.add(Line("106", 50, LineType.BUS_NORMAL))
            lines.add(Line("107", 51, LineType.BUS_NORMAL))
            lines.add(Line("108", 92, LineType.BUS_NORMAL))
            lines.add(Line("109", 52, LineType.BUS_NORMAL))
            lines.add(Line("110", 53, LineType.BUS_NORMAL))
            lines.add(Line("111", 54, LineType.BUS_NORMAL))
            lines.add(Line("121", 90, LineType.BUS_NORMAL))
            lines.add(Line("122", 89, LineType.BUS_NORMAL))
            lines.add(Line("123", 87, LineType.BUS_NORMAL))
            lines.add(Line("124", 100, LineType.BUS_NORMAL))
            return lines
        }

    val busExpress: ArrayList<Line>
        get() {
            val lines = ArrayList<Line>()

            lines.add(Line("A", 55, LineType.BUS_EXPRESS))
            lines.add(Line("B", 56, LineType.BUS_EXPRESS))
            lines.add(Line("C", 57, LineType.BUS_EXPRESS))
            lines.add(Line("D", 58, LineType.BUS_EXPRESS))
            lines.add(Line("E", 59, LineType.BUS_EXPRESS))
            lines.add(Line("F", 60, LineType.BUS_EXPRESS))
            lines.add(Line("G", 61, LineType.BUS_EXPRESS))

            return lines
        }

    val busNight: ArrayList<Line>
        get() {
            val lines = ArrayList<Line>()

            lines.add(Line("521", 62, LineType.BUS_NIGHT))
            lines.add(Line("522", 63, LineType.BUS_NIGHT))
            lines.add(Line("523", 64, LineType.BUS_NIGHT))
            lines.add(Line("524", 65, LineType.BUS_NIGHT))
            lines.add(Line("525", 66, LineType.BUS_NIGHT))
            lines.add(Line("526", 67, LineType.BUS_NIGHT))
            lines.add(Line("527", 68, LineType.BUS_NIGHT))
            lines.add(Line("528", 69, LineType.BUS_NIGHT))
            lines.add(Line("529", 70, LineType.BUS_NIGHT))
            lines.add(Line("530", 71, LineType.BUS_NIGHT))
            lines.add(Line("531", 72, LineType.BUS_NIGHT))
            lines.add(Line("532", 73, LineType.BUS_NIGHT))
            lines.add(Line("533", 74, LineType.BUS_NIGHT))
            lines.add(Line("534", 75, LineType.BUS_NIGHT))
            lines.add(Line("535", 93, LineType.BUS_NIGHT))
            lines.add(Line("536", 88, LineType.BUS_NIGHT))

            return lines
        }

    val busSubstitute: ArrayList<Line>
        get() {
            val lines = ArrayList<Line>()

            lines.add(Line("856", 98, LineType.BUS_SUBSTITUTE))
            lines.add(Line("872", 111, LineType.BUS_SUBSTITUTE))
            lines.add(Line("896", 99, LineType.BUS_SUBSTITUTE))


            return lines
        }
}
