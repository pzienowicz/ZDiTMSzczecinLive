package pl.pzienowicz.zditmszczecinlive.timer

import java.util.*

class MapTimer(val onTime: () -> Unit) {

    private val timer = Timer()

    fun start() {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                onTime()
            }
        }, 0, (30 * 1000).toLong())
    }

    fun stop() {
        timer.cancel()
    }

}