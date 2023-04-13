package com.example.sling.ui

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sling.ui.screen.AddBookScreen
import com.example.sling.ui.screen.BookDetailScreen
import com.example.sling.ui.screen.BookListScreen

@Composable
fun SlingNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Route.List.route
    ) {
        composable(Route.List.route) {
            BookListScreen(
                onAddClicked = {
                    navController.navigate(Route.Add.route)
                }, onItemClicked = { bookId ->
                    navController.navigate(
                        "${Route.Detail.route}/$bookId"
                    )
                }
            )
        }
        composable(
            "${Route.Detail.route}/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) { backStackEntry ->
            BookDetailScreen(
                bookId = backStackEntry.arguments?.getString("bookId") ?: "",
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
        composable(Route.Add.route) {
            AddBookScreen {
                navController.navigateUp()
            }
        }
    }
}