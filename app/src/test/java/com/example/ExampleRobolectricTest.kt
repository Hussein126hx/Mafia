package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.game.GameViewModel
import com.example.model.GamePhase
import com.example.model.GameRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verify app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Mafia Pixel", appName)
    }

    @Test
    fun `secret role privacy verification`() {
        val viewModel = GameViewModel()
        val uiState = viewModel.uiState.value
        val userPrivate = viewModel.userPrivateState.value

        // 1. Verify User has access to their private role
        assertNotNull(userPrivate.role)
        assertEquals(GameRole.DETECTIVE, userPrivate.role)

        // 2. Verify all public player models DO NOT contain or leak secret role
        uiState.publicPlayers.forEach { publicPlayer ->
            // Public player only exposes name, alive status, and public status
            assertTrue(publicPlayer.name.isNotEmpty())
            assertTrue(publicPlayer.publicStatus == "حي" || publicPlayer.publicStatus == "مستبعد")
            // PlayerPublic does not have any role property!
        }
    }

    @Test
    fun `action submission and game phase loop`() {
        val viewModel = GameViewModel()
        assertEquals(GamePhase.INTRO, viewModel.uiState.value.phase)

        // Advance to night phase
        viewModel.advanceDialogueOrPhase()
        assertEquals(GamePhase.NIGHT, viewModel.uiState.value.phase)
        assertFalse(viewModel.uiState.value.isActionSubmitted)

        // Select suspect and submit detective inspection
        val donPlayer = viewModel.uiState.value.publicPlayers.find { it.id == "char_don" }
        assertNotNull(donPlayer)
        viewModel.selectPlayer(donPlayer!!)
        viewModel.submitUserAction("detective_inspect")

        // Action submitted state is updated to prevent duplicate submissions
        assertTrue(viewModel.uiState.value.isActionSubmitted)
        assertNotNull(viewModel.uiState.value.userActionFeedback)
    }
}
