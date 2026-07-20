package pl.pzienowicz.zditmszczecinlive.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BoardTest {

    @Test
    fun parsesV2DepartureBoardForWidget() {
        val json = """
            {
              "data": {
                "stop": {
                  "name": "Brama Portowa",
                  "number": "10822"
                },
                "departures": [
                  {
                    "line": {
                      "number": "8"
                    },
                    "trip": {
                      "headsign": {
                        "short": "Zajezdnia Pogodno"
                      }
                    },
                    "departure_time": {
                      "scheduled": "2026-07-19T21:23:00.000000Z",
                      "estimated": "2026-07-19T21:22:59.000000Z",
                      "departing_now": true,
                      "real_time": true
                    }
                  }
                ],
                "messages": [
                  {
                    "pl": "Pierwsza wiadomość"
                  },
                  {
                    "message": "Druga wiadomość"
                  },
                  "Trzecia wiadomość"
                ]
              }
            }
        """.trimIndent()

        val board = Gson().fromJson(json, Board::class.java)
        val departure = board.departures?.first()

        assertNotNull(departure)
        assertEquals("8", departure?.line_number)
        assertEquals("Zajezdnia Pogodno", departure?.direction)
        assertEquals(0, departure?.time_real)
        assertNotNull(departure?.time_scheduled)
        assertEquals("Pierwsza wiadomość, Druga wiadomość, Trzecia wiadomość", board.message)
    }
}
