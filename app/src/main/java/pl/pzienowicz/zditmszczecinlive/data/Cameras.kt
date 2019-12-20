package pl.pzienowicz.zditmszczecinlive.data

import pl.pzienowicz.zditmszczecinlive.model.Camera

object Cameras {

    const val STREAM_URL = "https://www.lantech.com.pl/_lantech/kamery_nowe.php?cam="
    const val THUMB_URL = "https://stream5.lantech.com.pl/lantech_thumbnail/"


    val all: ArrayList<Camera>
        get() {
            val cameras = ArrayList<Camera>()

            cameras.add(Camera("Wały Chrobrego", THUMB_URL + "walychrobrego.jpg", STREAM_URL + "walychrobrego"))
            cameras.add(Camera("Przyjaciół Żołnierza", THUMB_URL + "przyjaciolzolnierza.jpg", STREAM_URL + "przyjaciolzolnierza"))
            cameras.add(Camera("Boguchwały", THUMB_URL + "boguchwaly.jpg", STREAM_URL + "boguchwaly"))
            cameras.add(Camera("Cyryla i Metodego", THUMB_URL + "cyryla.jpg", STREAM_URL + "cyryla"))
            cameras.add(Camera("Urząd Wojewódzki", THUMB_URL + "zuw.jpg", STREAM_URL + "zuw"))
            cameras.add(Camera("Plac Szarych Szeregów", THUMB_URL + "n24s.jpg", STREAM_URL + "n24s"))
            cameras.add(Camera("Plac Rodła", THUMB_URL + "pazim1.jpg", STREAM_URL + "pazim1"))
            cameras.add(Camera("Prawobrzeże - Słoneczne", THUMB_URL + "sch.jpg", STREAM_URL + "sch"))

            return cameras
        }

}