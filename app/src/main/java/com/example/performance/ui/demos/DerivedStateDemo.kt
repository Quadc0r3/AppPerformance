package com.example.performance.ui.demos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.performance.ui.common.DemoScaffold
import kotlinx.coroutines.launch

@Composable
fun DerivedStateDemo(onBack: () -> Unit) {
    DemoScaffold(
        title = "Derived State",
        onBack = onBack,
        naiveContent = { NaiveScrollList() },
        optimizedContent = { OptimizedScrollList() }
    )
}

@Composable
fun NaiveScrollList() {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // PITFALL:
    // Wir lesen `firstVisibleItemIndex` direkt im Composition-Scope.
    // Da sich dieser Wert bei JEDEM Pixel-Scrollen ändert (innerhalb einer Animation),
    // wird diese gesamte Composable function (NaiveScrollList) extrem oft neu ausgeführt
    // (Recomposition), obwohl sich visuell am Button vielleicht gar nichts ändert.
    val showButton = listState.firstVisibleItemIndex > 0
    
    // Performance-Logging
    SideEffect {
        // Dieser Log wird beim Scrollen spammen!
        println("Recomposing NaiveScrollList (showButton=$showButton)")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(100) { index ->
                Text(
                    text = "Item #$index",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }

        if (showButton) {
            Button(
                onClick = { scope.launch { listState.scrollToItem(0) } },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Nach oben")
            }
        }
    }
}

@Composable
fun OptimizedScrollList() {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // BEST PRACTICE:
    // Wir nutzen derivedStateOf.
    // Der Block wird immer dann neu berechnet, wenn sich `firstVisibleItemIndex` ändert.
    // ABER: Das Ergebnis (Result State) ändert sich NUR, wenn der boolean von false -> true wechselt (oder andersrum).
    // Nur wenn sich das Ergebnis ändert, wird eine Recomposition ausgelöst.
    val showButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }
    
    SideEffect {
        // Dieser Log kommt nur 1x, wenn der Button erscheint/verschwindet.
        println("Recomposing OptimizedScrollList (showButton=$showButton)")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(100) { index ->
                Text(
                    text = "Item #$index - Scroll mich!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }

        if (showButton) {
            Button(
                onClick = { scope.launch { listState.scrollToItem(0) } },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Nach oben")
            }
        }
    }
}
