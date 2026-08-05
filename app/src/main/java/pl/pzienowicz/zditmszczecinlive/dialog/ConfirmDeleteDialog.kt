package pl.pzienowicz.zditmszczecinlive.dialog

import android.content.Context
import pl.pzienowicz.zditmszczecinlive.databinding.DialogConfirmDeleteBinding

class ConfirmDeleteDialog(
    context: Context,
    message: String,
    private val onConfirmed: () -> Unit
) : AdaptiveSheetDialog(context) {

    private val binding = DialogConfirmDeleteBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
        binding.confirmDeleteMessage.text = message

        binding.confirmDeleteButton.setOnClickListener {
            onConfirmed()
            dismiss()
        }
        binding.cancelDeleteButton.setOnClickListener {
            dismiss()
        }
    }
}
