package pl.pzienowicz.zditmszczecinlive.model

import com.google.gson.annotations.SerializedName

data class Data<K>(
    @SerializedName("data")
    val items: List<K>
)