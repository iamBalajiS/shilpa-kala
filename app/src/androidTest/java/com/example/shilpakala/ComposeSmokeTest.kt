package com.example.shilpakala

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.shilpakala.ui.theme.ShilpaKalaTheme
import org.junit.Rule
import org.junit.Test

class ComposeSmokeTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun composeThemeRendersText() {
        composeRule.setContent {
            ShilpaKalaTheme {
                Text("ShilpaKala")
            }
        }
        composeRule.onNodeWithText("ShilpaKala").assertIsDisplayed()
    }
}
