package daniel.lop.io.marvelappstarter.util

import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import daniel.lop.io.marvelappstarter.R


fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(
        requireContext(),
        message,
        duration
    ).show()
}

fun Fragment.showErrorSnackbar(message: String, layout: View) {
    Snackbar
        .make(layout, message, Snackbar.LENGTH_INDEFINITE)
        .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.errorBackground))
        .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        .setAction(requireContext().getText(R.string.close_dialog)) {}.show()
}

fun Fragment.showSuccessSnackbar(message: String, layout: View) {
    Snackbar
        .make(layout, message, Snackbar.LENGTH_INDEFINITE)
        .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.successBackground))
        .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        .setAction(requireContext().getText(R.string.close_dialog)) {}.show()
}

fun Fragment.showAlertSnackbar(message: String, layout: View) {
    Snackbar
        .make(layout, message, Snackbar.LENGTH_INDEFINITE)
        .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.alertBackground))
        .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        .setAction(requireContext().getText(R.string.close_dialog)) {}.show()
}


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.isVisible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE
    else View.GONE
}

fun loadImage(
    imageView: ImageView,
    path: String,
    extension: String
) {
    val circularProgressDrawable = CircularProgressDrawable(imageView.context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()

    Glide.with(imageView.context)
        .load("$path.$extension")
        .placeholder(circularProgressDrawable)
        .into(imageView)
}

fun String.limitDescription(characters: Int): String {
    if (this.length > characters) {
        val firstCharacter = 0
        return "${this.substring(firstCharacter, characters)}..."
    }
    return this
}
