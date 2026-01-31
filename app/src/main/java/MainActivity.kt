package com.example.performance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                PerformanceDemoApp()
            }
        }
    }
}

@Composable
fun PerformanceDemoApp() {
    var optimized by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (optimized) "Optimized Version" else "Bad Version",
                style = MaterialTheme.typography.titleMedium
            )

            Button(onClick = { optimized = !optimized }) {
                Text("Toggle Version")
            }
        }

        Divider()

        if (optimized) {
            OptimizedScreen()
        } else {
            BadScreen()
        }
    }
}


//BAD
@Composable
fun BadScreen() {
    var counter by remember { mutableStateOf(0) }

    Column {
        Button(
            onClick = { counter++ },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Counter: $counter")
        }

        LazyColumn {
            items(1000) { index ->
                BadListItem(index)
            }
        }
    }
}

@Composable
fun BadListItem(index: Int) {
    SideEffect {
        println("Recomposed BAD item $index")
    }

    Text(
        text = "Bad Item #$index",
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

//GOOD
@Composable
fun OptimizedScreen() {
    Column {

        CounterButton()

        LazyColumn {
            items(
                items = (0 until 1000).toList(),
                key = { it } // ✅ stabile Keys
            ) { index ->
                OptimizedListItem(index)
            }
        }
    }
}

@Composable
fun CounterButton() {
    var counter by remember { mutableStateOf(0) }

    Button(
        onClick = { counter++ },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Counter: $counter")
    }
}

@Composable
fun OptimizedListItem(index: Int) {
    SideEffect {
        println("Recomposed OPT item $index")
    }

    Text(
        text = "Optimized Item #$index",
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}
