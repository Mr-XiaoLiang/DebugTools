package com.lollipop.debug.desktop

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lollipop.debug.desktop.local.Strings
import com.lollipop.debug.desktop.local.impl.LocalStringsCn
import com.lollipop.debug.desktop.preferences.Settings


@Composable
fun DebugToolsDesktop() {

}

fun main() = application {
    // 先初始化语言
    Strings.current = Strings.findLocal(Settings.localLanguage) ?: LocalStringsCn
    Window(onCloseRequest = ::exitApplication, title = Strings.appName) {
        DebugToolsDesktop()
    }
}