package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GamePhase
import com.example.model.PlayerPublic
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.MafiaBgNearBlack
import com.example.ui.theme.MafiaBronzeBorder
import com.example.ui.theme.MafiaGoldAccent
import com.example.ui.theme.MafiaGoldBright
import com.example.ui.theme.SilkscreenFontFamily
import com.example.ui.theme.TextCreamPrimary
import com.example.ui.theme.TextMuted

/**
 * Top bar matching Section 1.1:
 * - Square portrait frames in a row, centered, with bronze/brown pixel borders
 * - Active (selected) character has gold/yellow border and brighter frame
 * - Under each portrait is a small diamond node on a thin horizontal bronze line progress track
 * - Never shows secret roles!
 */
@Composable
fun MafiaTopBar(
    players: List<PlayerPublic>,
    selectedPlayerId: String?,
    currentPhase: GamePhase,
    roundNumber: Int,
    onPlayerSelected: (PlayerPublic) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayedPlayers = players.take(5) // clean cinematic display
    val selectedIndex = displayedPlayers.indexOfFirst { it.id == selectedPlayerId }.coerceAtLeast(0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("mafia_top_bar"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status Row (Phase & Round)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Round indicator
            Box(
                modifier = Modifier
                    .background(Color(0xCC141923), CutCornerShape(4.dp))
                    .border(1.dp, MafiaBronzeBorder, CutCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "الجولة $roundNumber",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MafiaGoldAccent
                )
            }

            // Phase indicator
            Box(
                modifier = Modifier
                    .background(Color(0xEE1A1208), CutCornerShape(4.dp))
                    .border(1.5.dp, MafiaGoldAccent, CutCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text(
                    text = currentPhase.titleArabic,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MafiaGoldBright
                )
            }

            // Public count indicator
            val aliveCount = players.count { it.isAlive }
            Box(
                modifier = Modifier
                    .background(Color(0xCC141923), CutCornerShape(4.dp))
                    .border(1.dp, MafiaBronzeBorder, CutCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$aliveCount أحياء",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = TextCreamPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Center Portraits Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            displayedPlayers.forEachIndexed { index, player ->
                val isSelected = player.id == selectedPlayerId
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) MafiaGoldBright else MafiaBronzeBorder,
                    label = "border_color"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Portrait Frame
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 46.dp else 40.dp)
                            .testTag("avatar_${player.id}")
                            .clip(CutCornerShape(4.dp))
                            .background(if (isSelected) Color(0xFF2A2012) else Color(0xFF141923))
                            .border(
                                width = if (isSelected) 2.5.dp else 1.5.dp,
                                color = borderColor,
                                shape = CutCornerShape(4.dp)
                            )
                            .clickable { onPlayerSelected(player) },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = player.avatarDrawable),
                            contentDescription = player.name,
                            modifier = Modifier
                                .size(if (isSelected) 42.dp else 36.dp)
                                .clip(CutCornerShape(2.dp)),
                            contentScale = ContentScale.Crop
                        )

                        // If eliminated: draw pixel skull / red X overlay
                        if (!player.isAlive) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color(0x99500000)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "✕",
                                    color = Color(0xFFFF5252),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Player Name (Arabic, no secret role!)
                    Text(
                        text = player.name,
                        fontFamily = CairoFontFamily,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MafiaGoldBright else if (player.isAlive) TextCreamPrimary else TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Progress Track: horizontal bronze line linking all with diamond nodes
        Box(
            modifier = Modifier
                .width(240.dp)
                .height(14.dp)
                .drawBehind {
                    val y = size.height / 2f
                    // Background bronze line
                    drawLine(
                        color = MafiaBronzeBorder,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 2f
                    )
                    // Gold progress line up to active selection
                    val progressRatio = if (displayedPlayers.size > 1) {
                        selectedIndex.toFloat() / (displayedPlayers.size - 1)
                    } else 0f
                    drawLine(
                        color = MafiaGoldBright,
                        start = Offset(0f, y),
                        end = Offset(size.width * progressRatio, y),
                        strokeWidth = 2.5f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                displayedPlayers.forEachIndexed { index, _ ->
                    val isNodeActive = index == selectedIndex
                    val isNodeCompleted = index <= selectedIndex
                    DiamondNode(
                        isActive = isNodeActive,
                        isCompleted = isNodeCompleted,
                        sizeDp = 8.dp,
                        modifier = Modifier.size(8.dp)
                    )
                }
            }
        }
    }
}
