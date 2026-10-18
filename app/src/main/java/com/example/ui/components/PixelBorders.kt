package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MafiaBronzeBorder
import com.example.ui.theme.MafiaGoldAccent
import com.example.ui.theme.MafiaGoldBright

/**
 * Pixel-art cut corner shape for authentic 16-bit dialog boxes and button frames
 */
val PixelBeveledShape = CutCornerShape(8.dp)
val PixelButtonShape = CutCornerShape(6.dp)

/**
 * Draws a pixel-style double bronze border with beveled corners
 */
fun Modifier.pixelBorder(
    borderColor: Color = MafiaBronzeBorder,
    borderWidth: Dp = 2.dp,
    outerBorderColor: Color = Color(0xFF100B06),
    shape: CutCornerShape = PixelBeveledShape
): Modifier = this
    .border(width = borderWidth + 1.dp, color = outerBorderColor, shape = shape)
    .padding(1.dp)
    .border(width = borderWidth, color = borderColor, shape = shape)

/**
 * Draws a golden highlight pixel border for the active/selected character or element
 */
fun Modifier.pixelBorderActive(
    borderColor: Color = MafiaGoldBright,
    borderWidth: Dp = 2.5.dp,
    shape: CutCornerShape = PixelButtonShape
): Modifier = this
    .border(width = borderWidth + 1.5.dp, color = MafiaGoldAccent, shape = shape)
    .padding(1.dp)
    .border(width = borderWidth, color = borderColor, shape = shape)

/**
 * Draws a diamond node on a progress track
 */
@Composable
fun DiamondNode(
    isActive: Boolean,
    isCompleted: Boolean,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 10.dp
) {
    val fillColor = when {
        isActive -> MafiaGoldBright
        isCompleted -> MafiaGoldAccent
        else -> Color(0xFF4A3828)
    }
    val borderColor = if (isActive || isCompleted) MafiaGoldBright else MafiaBronzeBorder

    Box(
        modifier = modifier
            .drawBehind {
                val s = size.minDimension
                val half = s / 2f
                val path = Path().apply {
                    moveTo(half, 0f)
                    lineTo(s, half)
                    lineTo(half, s)
                    lineTo(0f, half)
                    close()
                }
                drawPath(path, color = fillColor)
                // Border
                drawPath(
                    path,
                    color = borderColor,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )
            }
    )
}
