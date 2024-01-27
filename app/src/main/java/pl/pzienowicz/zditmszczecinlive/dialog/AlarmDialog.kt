package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.Window
import pl.pzienowicz.zditmszczecinlive.databinding.DialogAlarmBinding

class AlarmDialog(activity: Activity) : Dialog(activity) {

    private var binding: DialogAlarmBinding

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.confirmBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                activity.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
            dismiss()
        }
    }
}
