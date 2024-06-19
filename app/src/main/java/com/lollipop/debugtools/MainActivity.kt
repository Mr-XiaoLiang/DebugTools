package com.lollipop.debugtools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lollipop.debug.DToast
import com.lollipop.debug.DebugLocal
import com.lollipop.debugtools.ui.theme.DebugToolsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DebugToolsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        DebugLocal.init(application)
    }

    override fun onResume() {
        super.onResume()
        if (!DebugLocal.hasOverlayPermission(this)) {
            DebugLocal.requestOverlayPermission(this)
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    var index by remember {
        mutableStateOf(0)
    }
    Text(
        text = "Hello $name!",
        modifier = modifier.clickable {
            index++
            if (index % 3 == 0) {
                DToast.show("Hello Debug Debug Debug Debug Debug Debug Debug")
            } else {
                DToast.show("Hello Debug")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DebugToolsTheme {
        Greeting("Android")
    }
}