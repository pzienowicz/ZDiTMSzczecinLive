package pl.pzienowicz.zditmszczecinlive.data

import pl.pzienowicz.zditmszczecinlive.model.Camera

object Cameras {

    private const val STREAM_URL = "https://www.lantech.com.pl/_lantech/kamery_nowe.php?cam="
    private const val THUMB_URL = "https://stream5.lantech.com.pl/lantech_thumbnail/"

    val all: List<Camera>
        get() = listOf(
                Camera(
                    "Wały Chrobrego",
                    THUMB_URL + "walychrobrego.jpg",
                    STREAM_URL + "walychrobrego"
                ),
                Camera(
                    "Przyjaciół Żołnierza",
                    THUMB_URL + "przyjaciolzolnierza.jpg",
                    STREAM_URL + "przyjaciolzolnierza"),
                Camera(
                    "Boguchwały",
                    THUMB_URL + "boguchwaly.jpg",
                    STREAM_URL + "boguchwaly"),
                Camera(
                    "Cyryla i Metodego",
                    THUMB_URL + "cyryla.jpg",
                    STREAM_URL + "cyryla"),
                Camera(
                    "Urząd Wojewódzki",
                    THUMB_URL + "zuw.jpg",
                    STREAM_URL + "zuw"),
                Camera(
                    "Plac Szarych Szeregów",
                    THUMB_URL + "n24s.jpg",
                    STREAM_URL + "n24s"),
                Camera(
                    "Plac Rodła",
                    THUMB_URL + "pazim1.jpg",
                    STREAM_URL + "pazim1"),
                Camera(
                    "Prawobrzeże - Słoneczne",
                    THUMB_URL + "sch.jpg",
                    STREAM_URL + "sch")
            )
}