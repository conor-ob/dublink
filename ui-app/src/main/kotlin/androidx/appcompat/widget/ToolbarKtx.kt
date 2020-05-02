package androidx.appcompat.widget

fun Toolbar.updateTitle(newText: String) {
    val currentText = this.title
    if (currentText != newText) {
        this.title = newText
    }
}

fun Toolbar.updateSubtitle(newText: String) {
    val currentText = this.subtitle
    if (currentText != newText) {
        this.subtitle = newText
    }
}
