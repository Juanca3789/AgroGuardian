package com.dev.jcctech.agroguardian.ui.screen.forum

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dev.jcctech.agroguardian.data.inyection.AppProviders
import com.dev.jcctech.agroguardian.ui.viewmodel.ForumViewModel
import com.dev.jcctech.agroguardian.ui.viewmodel.factory.ForumFactory

private const val FORUM_LIST_ROUTE = "forum_list"
private const val FORUM_DETAIL_ROUTE = "forum_detail/{questionId}"
private const val FORUM_ASK_ROUTE = "forum_ask"

@Composable
fun ForumNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = FORUM_LIST_ROUTE) {
        composable(FORUM_LIST_ROUTE) {
            ForumScreen(
                onQuestionClick = { questionId ->
                    navController.navigate("forum_detail/$questionId")
                },
                onAskQuestion = {
                    navController.navigate(FORUM_ASK_ROUTE)
                }
            )
        }

        composable(
            route = FORUM_DETAIL_ROUTE,
            arguments = listOf(navArgument("questionId") { type = NavType.StringType })
        ) {
            QuestionDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = FORUM_ASK_ROUTE) {
            val repository = AppProviders.LocalForumRepository.current
            val factory = ForumFactory(repository)
            val forumViewModel: ForumViewModel = viewModel(factory = factory)

            AskQuestionScreen(
                onNavigateBack = { navController.popBackStack() },
                onAskQuestion = { title, content ->
                    forumViewModel.createQuestion(title, content)
                    navController.popBackStack()
                }
            )
        }
    }
}
