package com.hivestudio.ui.screens.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import com.hivestudio.ui.theme.HiveStudioTheme
import org.junit.Rule
import org.junit.Test

class AuthScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreenDisplaysRequiredFields() {
        composeTestRule.setContent {
            HiveStudioTheme(darkTheme = true) {
                LoginScreen(
                    onSuccess = {},
                    onOpenRegister = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Hive Studio").assertIsDisplayed()
        composeTestRule.onNodeWithText("Вход").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Пароль").assertIsDisplayed()
        composeTestRule.onNode(hasText("Войти") and hasClickAction()).assertIsDisplayed()
    }

    @Test
    fun registerScreenDisablesSubmitWhenPasswordsDoNotMatch() {
        composeTestRule.setContent {
            HiveStudioTheme(darkTheme = true) {
                RegisterScreen(
                    onSuccess = {},
                    onOpenLogin = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Email").performTextInput("producer@hive.dev")
        composeTestRule.onNodeWithText("Пароль").performTextInput("secret123")
        composeTestRule.onNodeWithText("Повтор пароля").performTextInput("mismatch")
        composeTestRule.onNodeWithText("Никнейм").performTextInput("Hive Demo")

        composeTestRule.onNodeWithText("Пароли должны совпадать").assertIsDisplayed()
        composeTestRule.onNode(hasText("Создать профиль") and hasClickAction()).assertIsNotEnabled()
    }
}
