package com.example.performance.ui.demos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.performance.ui.common.DemoScaffold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

// Simuliert eine sehr teure Berechnung (z.B. komplexe Filterung oder Sortierung)
private fun heavyFilterOperation(query: String, data: List<String>): List<String> {
    // Künstliche Verzögerung um Last zu simulieren (Thread.sleep blockiert den Thread!)
    if (query.isEmpty()) return data
    
    // BAD PRACTICE: Thread.sleep im Main Thread blockiert die UI (Jank/Freeze)
    // Wir nutzen hier aber nur Rechenzeit, um CPU zu belasten. 
    // In echten Apps wäre das z.B. komplexe String-Distanz-Berechnung.
    Thread.sleep(200) 
    return data.filter { it.contains(query, ignoreCase = true) }
}

@Composable
fun HeavyComputationDemo(onBack: () -> Unit) {
    val rawData = remember { List(1000) { "Eintrag #$it mit einigem Text" } }
    
    DemoScaffold(
        title = "Heavy Computation",
        onBack = onBack,
        naiveContent = { NaiveComputationView(rawData) },
        optimizedContent = { OptimizedComputationView(rawData) }
    )
}

@Composable
fun NaiveComputationView(rawData: List<String>) {
    var query by remember { mutableStateOf("") }
    
    // PITFALL:
    // Die Berechnung passiert direkt im Body der Composable.
    // Bei JEDEM Tastenanschlag (query change) wird recomposed.
    // Während 'heavyFilterOperation' läuft, friert die UI ein.
    // Der Ripple-Effekt beim Tippen wird "laggy".
    val filteredList = heavyFilterOperation(query, rawData)

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Tippe etwas ein. Die UI wird freezen, weil die Berechnung (200ms sleep) auf dem UI Thread läuft.")
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Suche (Laggy)") }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn {
            items(filteredList) {
                Text(it, modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@Composable
fun OptimizedComputationView(rawData: List<String>) {
    var query by remember { mutableStateOf("") }
    var filteredList by remember { mutableStateOf(rawData) }
    var isLoading by remember { mutableStateOf(false) }

    // BEST PRACTICE:
    // Berechnung in einen Side-Effect (LaunchedEffect) auslagern.
    // Dieser läuft in einem CoroutineScope.
    // Mit Dispatchers.Default verschieben wir die Last vom Main Thread (UI) in den Background.
    LaunchedEffect(query) {
        isLoading = true
        // Optional: Debounce (kurz warten, falls der User schnell tippt)
        delay(300) 
        
        // Switch to Background Thread for calculation
        val result = withContext(Dispatchers.Default) {
            // Re-Implementation ohne Thread.sleep, da wir hier im Suspend-Context sind, 
            // aber um den Vergleich fair zu halten, simulieren wir Last sicher.
            // (Thread.sleep ist in coroutines eigentlich tabu, hier aber zur Demo der CPU Last ok, 
            // da Dispatchers.Default einen Threadpool hat).
             if (query.isEmpty()) rawData else {
                 // Simulation von Arbeit
                 Thread.sleep(200) 
                 rawData.filter { it.contains(query, ignoreCase = true) }
             }
        }
        filteredList = result
        isLoading = false
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Tippe etwas ein. Die UI bleibt flüssig (Ripple, Cursor), da die Arbeit im Hintergrund passiert.")
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Suche (Smooth)") }
        )
        
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn {
            items(filteredList) {
                Text(it, modifier = Modifier.padding(4.dp))
            }
        }
    }
}
