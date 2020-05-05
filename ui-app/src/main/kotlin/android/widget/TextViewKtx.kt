package android.widget

fun TextView.updateText(newText: String) {
    val currentText = this.text
    if (currentText != newText) {
        this.text = newText
    }
}
