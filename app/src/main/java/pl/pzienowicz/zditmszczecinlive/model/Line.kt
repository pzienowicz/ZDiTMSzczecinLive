package pl.pzienowicz.zditmszczecinlive.model

import com.google.gson.annotations.SerializedName

class Line(
        @SerializedName("oznaczenie") val name: String,
        @SerializedName("id") val id: Int,
        @SerializedName("rodzaj") val type: String) {

    @SerializedName("pomarancz")
    private val isChanged: Int = 0

    fun isChanged(): Boolean {
        return isChanged == 1
    }
}
