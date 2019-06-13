package ie.dublinmapper.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.bluelinelabs.conductor.Controller
import ie.dublinmapper.DublinMapperApplication
import ie.dublinmapper.di.ApplicationComponent
import java.io.Serializable

fun Controller.getApplicationComponent(): ApplicationComponent {
    return (requireActivity().application as DublinMapperApplication).applicationComponent
}

fun Controller.getColour(@ColorRes id: Int): Int {
    return ContextCompat.getColor(requireContext(), id)
}

fun Controller.showKeyboard(view: View) {
    val inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.showSoftInput(view, 0)
}

fun Controller.hideKeyboard(view: View) {
    val inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Controller.requireContext(): Context {
    return applicationContext ?: throw IllegalStateException("${javaClass.simpleName} not attached to a context")
}

fun Controller.requireActivity(): Activity {
    return activity ?: throw IllegalStateException("${javaClass.simpleName} not attached to an activity")
}

fun Controller.requireStringArg(key: String): String {
    return args.getString(key) ?: throw IllegalStateException("${javaClass.simpleName} - no argument provided for key: $key")
}

fun Controller.requireIntArg(key: String): Int {
    val intArg = args.getInt(key)
    if (intArg == 0) {
        throw IllegalStateException("${javaClass.simpleName} - no argument provided for key: $key")
    }
    return intArg
}

fun Controller.requireSerializableArg(key: String): Serializable {
    return args.getSerializable(key) ?: throw IllegalStateException("${javaClass.simpleName}] - no argument provided for key: $key")
}
