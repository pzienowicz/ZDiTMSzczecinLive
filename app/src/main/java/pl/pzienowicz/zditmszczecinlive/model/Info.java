package pl.pzienowicz.zditmszczecinlive.model;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Info {

    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @SerializedName("od")
    private String from;

    @SerializedName("do")
    private String to;

    @SerializedName("opis")
    private String description;

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getDescription() {
        return description;
    }

    public String getToDate() {
        if(getTo() == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(getTo()) * 1000);

        return dateFormat.format(calendar.getTime());
    }

    public String getFromDate() {
        if(getFrom() == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(getFrom()) * 1000);

        return dateFormat.format(calendar.getTime());
    }
}
