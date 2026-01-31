package com.example.performance.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

/**
 * A simple visualizer that flashes a random color when the component is recomposed.
 * Useful for debugging recomposs without external tools.
 */
@Composable
fun RecompositionVisualizer(modifier: Modifier = Modifier) {
    val color = remember { 
        Color(
            red = Random.nextFloat(), 
            green = Random.nextFloat(), 
            blue = Random.nextFloat(),
            alpha = 0.5f
        ) 
    }
    
    // SideEffect runs on every successful recomposition
    SideEffect {
        println("Recomposing Visualizer")
    }

    Box(
        modifier = modifier
            .background(color)
            .padding(4.dp)
    ) {
        Text("Recomposed!", style = MaterialTheme.typography.labelSmall)
    }
}
