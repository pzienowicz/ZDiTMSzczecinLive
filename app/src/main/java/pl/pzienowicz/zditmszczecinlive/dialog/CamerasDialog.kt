package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import android.widget.*
import com.squareup.picasso.Picasso
import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.data.Cameras
import pl.pzienowicz.zditmszczecinlive.databinding.DialogCamerasBinding
import pl.pzienowicz.zditmszczecinlive.isLandscape
import pl.pzienowicz.zditmszczecinlive.setFullWidth

class CamerasDialog(context: Context) : Dialog(context) {

    private var binding: DialogCamerasBinding

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogCamerasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        draw(binding.camerasContainer)
    }

    private fun draw(camerasContainer: TableLayout)
    {
        val cameras = Cameras.all

        var linesPerRow = Config.CAMERAS_PER_ROW
        if (context.isLandscape) {
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
            row.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

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
                        dialog.setFullWidth()
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