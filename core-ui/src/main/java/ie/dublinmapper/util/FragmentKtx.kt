package ie.dublinmapper.util

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun Fragment.showKeyboard(view: View) {
    val inputManager = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.showSoftInput(view, 0)
}

fun Fragment.hideKeyboard(view: View) {
    val inputManager = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(view.windowToken, 0)
}
