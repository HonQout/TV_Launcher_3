package com.android.tvlauncher3.activity.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel
import com.android.tvlauncher3.page.ApplicationsRoute
import com.android.tvlauncher3.page.LauncherRoute

@Composable
fun PagesNavigation(
    modifier: Modifier,
    viewModel: MainViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "Launcher",
        modifier = modifier
            .background(Color.Transparent)
    ) {
        composable(route = "Launcher") {
            LauncherRoute(
                viewModel = viewModel,
                toDestination = {
                    navController.navigate("Applications")
                },
            )
        }

        composable(
            route = "Applications",
            enterTransition = {
                slideInVertically(
                    animationSpec = tween(
                        durationMillis = 600
                    ),
                    initialOffsetY = { fullHeight -> fullHeight }
                )
            },
            exitTransition = {
                slideOutVertically(
                    animationSpec = tween(
                        durationMillis = 600
                    ),
                    targetOffsetY = { fullHeight -> fullHeight }
                )
            }
        ) {
            ApplicationsRoute(
                viewModel = viewModel,
                toDestination = {
                    navController.popBackStack()
                }
            )
        }
    }
}