package com.example.performance.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.performance.ui.demos.DerivedStateDemo
import com.example.performance.ui.demos.HeavyComputationDemo
import com.example.performance.ui.demos.UnstableTypesDemo
import com.example.performance.ui.home.HomeScreen

@Composable
fun NavigationRoot() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onDemoSelected = { route ->
                navController.navigate(route)
            })
        }
        
        composable("demo_unstable_types") {
            UnstableTypesDemo(onBack = { navController.popBackStack() })
        }

        composable("demo_derived_state") {
             DerivedStateDemo(onBack = { navController.popBackStack() })
        }

        composable("demo_heavy_computation") {
             HeavyComputationDemo(onBack = { navController.popBackStack() })
        }
    }
}
