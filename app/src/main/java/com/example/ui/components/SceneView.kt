package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.PlayerPublic
import com.example.ui.theme.MafiaGoldAccent
import com.example.ui.theme.MafiaGoldBright

/**
 * Scene component matching Section 1.2:
 * - 16:9 cinematic 16-bit pixel art mafia office
 * - Interactive character zones with subtle highlight (glow/outline) on the selected character
 * - Warm interior amber lighting, cool window rain lighting, subtle vignette
 */
@Composable
fun MafiaSceneView(
    players: List<PlayerPublic>,
    selectedPlayerId: String?,
    onPlayerSelected: (PlayerPublic) -> Unit,
    modifier: Modifier = Modifier
) {
    // Subtle animated ambient light flicker
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_lights")
    val lampFlicker by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lamp_flicker"
    )
    val neonPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neon_pulse"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("mafia_scene_view")
    ) {
        val width = maxWidth
        val height = maxHeight

        // 1. Base 16:9 Pixel Art Mafia Office Scene
        Image(
            painter = painterResource(id = R.drawable.img_mafia_office_scene),
            contentDescription = "Mafia Boss Office at Night",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Warm Brass Lamp Pool (Left-Center)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = width * 0.18f, y = height * 0.15f)
                .size(width * 0.28f, height * 0.55f)
                .alpha(0.22f * lampFlicker)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFD54F),
                            Color(0xFFE65100),
                            Color.Transparent
                        )
                    )
                )
        )

        // 3. Cool Blue Neon Ambient from Window (Right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = -width * 0.05f, y = height * 0.1f)
                .size(width * 0.35f, height * 0.65f)
                .alpha(0.18f * neonPulse)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF29B6F6),
                            Color(0xFF673AB7),
                            Color.Transparent
                        )
                    )
                )
        )

        // 4. Subtle Vignette around scene perimeter
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x330B0D14),
                                Color(0x990B0D14)
                            ),
                            center = Offset(size.width / 2f, size.height / 2f),
                            radius = size.width * 0.75f
                        )
                    )
                }
        )

        // 5. Interactive Character Hotspots & Highlights:
        // Positioned over characters in the pixel scene:
        // - Character 1 (Don): Center-left behind desk (~28% W, ~36% H)
        // - Character 2 (Tariq / Suspender young gangster): Center (~50% W, ~42% H)
        // - Character 3 (Detective / Leaning gangster): Center-right (~72% W, ~40% H)
        // - Character 4 (Dr. Amina / Doctor): Far-left bookshelf (~14% W, ~38% H)
        // - Character 5 (Salim / Businessman): Far-right armchair (~86% W, ~45% H)

        val characterPositions = listOf(
            Pair(0.28f, 0.36f), // Don Salvatore
            Pair(0.50f, 0.40f), // Tariq
            Pair(0.72f, 0.38f), // Detective Karim
            Pair(0.14f, 0.38f), // Dr. Amina
            Pair(0.86f, 0.44f)  // Salim
        )

        players.take(characterPositions.size).forEachIndexed { index, player ->
            val (posXRatio, posYRatio) = characterPositions[index]
            val isSelected = player.id == selectedPlayerId
            val interactionSource = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier
                    .offset(
                        x = width * posXRatio - (width * 0.07f),
                        y = height * posYRatio - (height * 0.14f)
                    )
                    .size(width * 0.14f, height * 0.42f)
                    .testTag("scene_char_${player.id}")
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        onPlayerSelected(player)
                    },
                contentAlignment = Alignment.Center
            ) {
                // Active character gets a subtle highlight (pixel outline / golden aura), NOT a text label!
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(width * 0.12f, height * 0.38f)
                            .shadow(
                                elevation = 12.dp,
                                shape = CutCornerShape(8.dp),
                                ambientColor = MafiaGoldBright,
                                spotColor = MafiaGoldBright
                            )
                            .border(
                                width = 2.dp,
                                color = MafiaGoldBright,
                                shape = CutCornerShape(8.dp)
                            )
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MafiaGoldAccent.copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CutCornerShape(8.dp)
                            )
                    )
                }
            }
        }
    }
}
