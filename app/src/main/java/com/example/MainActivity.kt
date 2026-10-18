package com.example

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.game.GameViewModel
import com.example.model.GamePhase
import com.example.ui.components.ActionPanel
import com.example.ui.components.DialoguePanel
import com.example.ui.components.GameOverDialog
import com.example.ui.components.MafiaSceneView
import com.example.ui.components.MafiaTopBar
import com.example.ui.components.PortraitRotationPrompt
import com.example.ui.components.PrivateRoleSheet
import com.example.ui.components.PublicPlayerDialog
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.MafiaBgNearBlack
import com.example.ui.theme.MafiaBronzeBorder
import com.example.ui.theme.MafiaGoldAccent
import com.example.ui.theme.MafiaGoldBright
import com.example.ui.theme.MafiaPixelTheme
import com.example.ui.theme.TextCreamPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MafiaPixelTheme {
                MafiaGameApp()
            }
        }
    }
}

@Composable
fun MafiaGameApp(
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userPrivate by viewModel.userPrivateState.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Enforce RTL layout direction across the entire Mafia game
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MafiaBgNearBlack)
                .windowInsetsPadding(WindowInsets.safeDrawing),
            containerColor = MafiaBgNearBlack
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Check if in Portrait mode and user hasn't explicitly dismissed prompt
                if (!isLandscape && !uiState.isPortraitWarningDismissed) {
                    PortraitRotationPrompt(
                        onContinueInPortrait = { viewModel.dismissPortraitWarning() }
                    )
                } else {
                    // Landscape / Cinematic Layout (Primary target matching Section 1 & 3)
                    CinematicGameLayout(
                        uiState = uiState,
                        userRole = userPrivate.role,
                        onPlayerSelected = { player -> viewModel.selectPlayer(player) },
                        onOpenPlayerDetails = { viewModel.openPublicDetails() },
                        onOpenPrivateRole = { viewModel.togglePrivateRoleModal(true) },
                        onAdvanceDialogue = { viewModel.advanceDialogueOrPhase() },
                        onActionSelected = { actionId -> viewModel.submitUserAction(actionId) },
                        onRestartGame = { viewModel.startNewGame() }
                    )
                }

                // Strictly Private Role Sheet
                if (uiState.isPrivateRoleModalOpen) {
                    PrivateRoleSheet(
                        userPrivate = userPrivate,
                        onDismiss = { viewModel.togglePrivateRoleModal(false) }
                    )
                }

                // Public Player Details Dialog (Never reveals secret roles!)
                if (uiState.isPublicDetailsOpen && uiState.selectedPlayerId != null) {
                    val selectedPlayer = uiState.publicPlayers.find { it.id == uiState.selectedPlayerId }
                    if (selectedPlayer != null) {
                        PublicPlayerDialog(
                            player = selectedPlayer,
                            investigationNote = viewModel.getInvestigationNoteFor(selectedPlayer.id),
                            onDismiss = { viewModel.closePublicDetails() }
                        )
                    }
                }

                // Game Over Screen (Official reveal of all players' real roles)
                if (uiState.phase == GamePhase.GAME_OVER && uiState.gameOverWinningTeam != null) {
                    GameOverDialog(
                        winningTeam = uiState.gameOverWinningTeam!!,
                        finalResults = uiState.finalResults,
                        onRestartGame = { viewModel.startNewGame() }
                    )
                }
            }
        }
    }
}

/**
 * Cinematic 16:9 Landscape Layout divided strictly into percentage zones:
 * - Top ~0 to 12%: Top bar with player avatars and diamond progress track
 * - Scene ~0 to 65%: Mafia office behind UI with interactive characters
 * - Dialogue ~66 to 82%: Speaker portrait + RTL Arabic text + blinking triangle
 * - Actions ~83 to 97%: Three large colored buttons (Blue/Red/Green)
 */
@Composable
fun CinematicGameLayout(
    uiState: com.example.game.GameUiState,
    userRole: com.example.model.GameRole,
    onPlayerSelected: (com.example.model.PlayerPublic) -> Unit,
    onOpenPlayerDetails: () -> Unit,
    onOpenPrivateRole: () -> Unit,
    onAdvanceDialogue: () -> Unit,
    onActionSelected: (String) -> Unit,
    onRestartGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedPlayer = uiState.publicPlayers.find { it.id == uiState.selectedPlayerId }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("cinematic_game_layout")
    ) {
        val totalHeight = maxHeight

        // 1. Scene Area (~0 to 65% of height) in background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalHeight * 0.65f)
                .align(Alignment.TopCenter)
        ) {
            MafiaSceneView(
                players = uiState.publicPlayers,
                selectedPlayerId = uiState.selectedPlayerId,
                onPlayerSelected = { player ->
                    onPlayerSelected(player)
                }
            )

            // Top Bar overlaid on top (~0 to 12% height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
            ) {
                MafiaTopBar(
                    players = uiState.publicPlayers,
                    selectedPlayerId = uiState.selectedPlayerId,
                    currentPhase = uiState.phase,
                    roundNumber = uiState.roundNumber,
                    onPlayerSelected = { player ->
                        onPlayerSelected(player)
                    }
                )
            }

            // Private Role Button & Selected Player Info Quick-Access (Top-Left & Top-Right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Secret Role Peek Button (Strictly Private)
                Box(
                    modifier = Modifier
                        .clip(CutCornerShape(4.dp))
                        .background(Color(0xE6141923))
                        .border(1.5.dp, MafiaGoldAccent, CutCornerShape(4.dp))
                        .clickable { onOpenPrivateRole() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("private_role_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Role",
                            tint = MafiaGoldBright,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "دورك السري 🔒",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MafiaGoldBright
                        )
                    }
                }

                // Public Info button for currently selected character
                if (selectedPlayer != null) {
                    Box(
                        modifier = Modifier
                            .clip(CutCornerShape(4.dp))
                            .background(Color(0xCC1A1610))
                            .border(1.dp, MafiaBronzeBorder, CutCornerShape(4.dp))
                            .clickable { onOpenPlayerDetails() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("public_details_btn")
                    ) {
                        Text(
                            text = "ملف «${selectedPlayer.name}» ℹ️",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = TextCreamPrimary
                        )
                    }
                }
            }
        }

        // 2. Bottom UI Zone (~35% of total height): Dialogue & Action Panels
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(totalHeight * 0.35f)
                .padding(bottom = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Dialogue Panel (~66 to 82% of height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                DialoguePanel(
                    dialogue = uiState.currentDialogue,
                    onContinueClick = onAdvanceDialogue
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Action Panel (~83 to 97% of height)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ActionPanel(
                    currentPhase = uiState.phase,
                    userRole = userRole,
                    selectedPlayer = selectedPlayer,
                    isActionSubmitted = uiState.isActionSubmitted,
                    onActionSelected = onActionSelected
                )
            }
        }
    }
}
