package com.example.performance.ui.demos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.performance.ui.common.DemoScaffold

// ----------------------------------------------------------------------------
// NAIVE IMPLEMENTATION (PITFALL)
// ----------------------------------------------------------------------------

// Pitfall: Eine Klasse mit 'var' gilt als "instabil" (Unstable).
// Compose kann nicht garantieren, dass sich die Daten nicht geändert haben,
// daher wird JEDES Item neu gezeichnet (Recomposed), auch wenn sich nur eines ändert.
data class UnstableArticle(
    var id: Int,
    var title: String,
    var isFavorite: Boolean
)

@Composable
fun UnstableTypesDemo(onBack: () -> Unit) {
    DemoScaffold(
        title = "Unstable Types",
        onBack = onBack,
        naiveContent = { NaiveList() },
        optimizedContent = { OptimizedList() }
    )
}

@Composable
private fun NaiveList() {
    // Wir erzeugen eine Liste von instabilen Objekten
    // Pitfall: Die Verwendung von mutableStateOf mit einer MutableList ist oft fehleranfällig,
    // aber hier geht es primär um die Item-Klasse.
    var articles by remember {
        mutableStateOf(List(50) { i ->
            UnstableArticle(i, "Artikel #$i", false)
        })
    }
    
    // Ein Button, der irgendeine Änderung auslöst (z.B. force recompose des Parents)
    // oder wir toggeln ein Item.
    
    Column {
        Text(
            "Hier nutzen wir 'data class' mit 'var'. Schau im Layout Inspector: Wenn du ein Herz klickst, recomposed oft die ganze Liste oder benachbarte Items unnötig, weil Compose die Stabilität nicht garantieren kann.",
            modifier = Modifier.padding(16.dp)
        )
        
        LazyColumn {
            items(articles) { article ->
                UnstableArticleItem(
                    article = article,
                    onToggleFavorite = {
                        // Wir müssen hier eine neue Liste erstellen, um State-Change zu triggern,
                        // da 'articles' ein State<List> ist.
                        articles = articles.map {
                            if (it.id == article.id) it.copy(isFavorite = !it.isFavorite) else it
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun UnstableArticleItem(article: UnstableArticle, onToggleFavorite: () -> Unit) {
    // SideEffect um Recomposition sichtbar zu machen (im Logcat oder Debugger)
    SideEffect { println("Recomposing Unstable Item ${article.id}") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleFavorite() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = article.title)
        Checkbox(checked = article.isFavorite, onCheckedChange = { onToggleFavorite() })
    }
}

// ----------------------------------------------------------------------------
// OPTIMIZED IMPLEMENTATION (Best Practice)
// ----------------------------------------------------------------------------

// Best Practice: @Immutable oder einfach alle Properties 'val'.
// Das garantiert Compose, dass sich dieses Objekt nicht "klammheimlich" ändert.
@Immutable 
data class StableArticle(
    val id: Int,
    val title: String,
    val isFavorite: Boolean
)

@Composable
private fun OptimizedList() {
    var articles by remember {
        mutableStateOf(List(50) { i ->
            StableArticle(i, "Artikel #$i", false)
        })
    }

    Column {
        Text(
            "Hier nutzen wir @Immutable und 'val'. Klicke ein Item an: Nur dieses spezifische Item wird neu gezeichnet (Recomposed). Der Rest wird übersprungen (Skipped).",
            modifier = Modifier.padding(16.dp)
        )
        
        LazyColumn {
            // Best Practice: Key angeben! Hilft Compose, Items wiederzufinden.
            items(items = articles, key = { it.id }) { article ->
                StableArticleItem(
                    article = article,
                    onToggleFavorite = {
                        articles = articles.map {
                            if (it.id == article.id) it.copy(isFavorite = !it.isFavorite) else it
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun StableArticleItem(article: StableArticle, onToggleFavorite: () -> Unit) {
    SideEffect { println("Recomposing Stable Item ${article.id}") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleFavorite() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = article.title)
        Checkbox(checked = article.isFavorite, onCheckedChange = { onToggleFavorite() })
    }
}
