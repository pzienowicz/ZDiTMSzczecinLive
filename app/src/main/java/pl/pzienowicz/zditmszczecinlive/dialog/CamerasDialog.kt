package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.Window
import android.widget.*
import com.squareup.picasso.Picasso
import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.data.Cameras

class CamerasDialog(context: Context) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_cameras)

        findViewById<TableLayout>(R.id.camerasContainer)
            .also { draw(it) }
    }

    private fun draw(camerasContainer: TableLayout)
    {
        val cameras = Cameras.all

        var linesPerRow = Config.CAMERAS_PER_ROW
        if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            linesPerRow = Config.CAMERAS_PER_ROW_LANDSCAPE
        }
        val rows: Int = if (cameras.size % linesPerRow == 0) {
            cameras.size / linesPerRow
        } else {
            cameras.size / linesPerRow + 1
        }
        var iterator = 0

        for (i in 1..rows) {
            val row = TableRow(context)
            row.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)

            for (j in 1..linesPerRow) {
                val cellLayout = LayoutInflater.from(context).inflate(R.layout.view_camera, null)
                val tv = cellLayout.findViewById<TextView>(R.id.name)
                val thumbnail = cellLayout.findViewById<ImageView>(R.id.thumbnail)

                if (iterator < cameras.size) {
                    val camera = cameras[iterator]

                    tv.text = camera.name
                    Picasso.get().load(camera.thumbnail).into(thumbnail)
                    thumbnail.setOnClickListener {
                        dismiss()

                        val dialog = CameraDialog(context, camera)
                        dialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        dialog.show()
                    }
                }

                row.addView(cellLayout)
                iterator++
            }
            camerasContainer.addView(row)
        }
    }
}