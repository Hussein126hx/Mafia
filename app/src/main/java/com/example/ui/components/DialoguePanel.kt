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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DialogueEntry
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.MafiaBronzeBorder
import com.example.ui.theme.MafiaGoldAccent
import com.example.ui.theme.MafiaGoldBright
import com.example.ui.theme.MafiaSurfaceDark
import com.example.ui.theme.TextCreamPrimary
import com.example.ui.theme.TextCreamSecondary

/**
 * Dialogue panel matching Section 1.3:
 * - Wide dark panel spanning ~94% width (3% margin each side), bronze pixel border, beveled corners
 * - Left: square speaker portrait in bronze frame overlapping left edge and sticking up above it
 * - Right of portrait: speaker name above dialogue text in Arabic, RTL, right-aligned
 * - Bottom-right corner: small downward triangle continue indicator
 */
@Composable
fun DialoguePanel(
    dialogue: DialogueEntry?,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (dialogue == null) return

    val infiniteTransition = rememberInfiniteTransition(label = "indicator_blink")
    val indicatorAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Force RTL layout direction across Arabic text
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("dialogue_panel")
                .clickable { onContinueClick() }
        ) {
            // Main Panel Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp) // allow portrait overlap on left
                    .background(
                        color = Color(0xF50D111A),
                        shape = CutCornerShape(8.dp)
                    )
                    .pixelBorder(
                        borderColor = MafiaBronzeBorder,
                        borderWidth = 2.dp,
                        outerBorderColor = Color(0xFF07090F),
                        shape = CutCornerShape(8.dp)
                    )
                    .padding(start = 58.dp, end = 24.dp, top = 8.dp, bottom = 10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Speaker Name
                    Text(
                        text = dialogue.speakerName,
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MafiaGoldBright,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Dialogue Text
                    Text(
                        text = dialogue.textArabic,
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = TextCreamPrimary,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Bottom-Right Corner: Small Downward Triangle "Continue" Indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 12.dp, y = 4.dp)
                        .alpha(indicatorAlpha)
                        .size(10.dp)
                        .drawBehind {
                            val path = Path().apply {
                                moveTo(0f, 0f)
                                lineTo(size.width, 0f)
                                lineTo(size.width / 2f, size.height)
                                close()
                            }
                            drawPath(path, color = MafiaGoldBright)
                        }
                )
            }

            // Left: Square Speaker Portrait in bronze frame sticking up above edge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 0.dp, y = (-10).dp)
                    .size(62.dp)
                    .clip(CutCornerShape(6.dp))
                    .background(Color(0xFF1B140B))
                    .border(2.5.dp, MafiaGoldAccent, CutCornerShape(6.dp))
                    .border(1.dp, Color(0xFF0B0D14), CutCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = dialogue.avatarDrawable),
                    contentDescription = dialogue.speakerName,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CutCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
