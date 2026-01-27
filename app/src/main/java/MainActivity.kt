package com.example.performance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                PerformanceDemoScreen()
            }
        }
    }
}

@Composable
fun PerformanceDemoScreen() {
    var optimized by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (optimized) "Optimized Version" else "Bad Version",
                fontWeight = FontWeight.Bold
            )

            Button(onClick = { optimized = !optimized }) {
                Text("Toggle")
            }
        }

        Divider()

        if (optimized) {
            OptimizedList()
        } else {
            BadList()
        }
    }
}

@Composable
fun BadList() {
    // Absichtlich schlechte Performance
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
    ) {
        repeat(1000) { index ->
            BadListItem(index)
        }
    }
}

@Composable
fun BadListItem(index: Int) {
    // Teure Berechnung im UI-Thread
    val randomValue = remember {
        heavyCalculation()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFFFCDD2))
            .padding(16.dp)
    ) {
        Text(text = "Bad Item #$index → $randomValue")
    }
}

@Composable
fun OptimizedList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(1000) { index ->
            OptimizedListItem(index)
        }
    }
}

@Composable
fun OptimizedListItem(index: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFC8E6C9))
            .padding(16.dp)
    ) {
        Text(text = "Optimized Item #$index")
    }
}

// Simuliert unnötige Arbeit
fun heavyCalculation(): Int {
    var result = 0
    repeat(10000) {
        result += Random.nextInt(0, 10)
    }
    return result
}
