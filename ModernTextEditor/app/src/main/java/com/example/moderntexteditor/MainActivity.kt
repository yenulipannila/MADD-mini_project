package com.example.moderntexteditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.moderntexteditor.ui.theme.ModernTextEditor2Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ModernTextEditor2Theme {
                EditorHomeScreen()
            }
        }
    }
}

@Composable
fun EditorHomeScreen() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Modern Text Editor",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "File Storage System Ready",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditorHomeScreenPreview() {

    ModernTextEditor2Theme {
        EditorHomeScreen()
    }
}