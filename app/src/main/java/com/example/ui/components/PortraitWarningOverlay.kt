package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.MafiaBgNearBlack
import com.example.ui.theme.MafiaBronzeBorder
import com.example.ui.theme.MafiaGoldAccent
import com.example.ui.theme.MafiaGoldBright
import com.example.ui.theme.TextCreamPrimary
import com.example.ui.theme.TextCreamSecondary

/**
 * Friendly overlay when device is in vertical/portrait orientation:
 * Asks user to rotate to landscape for the full 16:9 cinematic pixel art experience,
 * while offering an adaptive continue button.
 */
@Composable
fun PortraitRotationPrompt(
    onContinueInPortrait: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MafiaBgNearBlack)
            .padding(24.dp)
            .testTag("portrait_rotation_prompt"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(CutCornerShape(12.dp))
                .background(Color(0xFF141923))
                .pixelBorder(
                    borderColor = MafiaGoldAccent,
                    borderWidth = 2.dp,
                    outerBorderColor = Color(0xFF07090F),
                    shape = CutCornerShape(12.dp)
                )
                .padding(24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_mafia_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(68.dp)
                    .clip(CutCornerShape(6.dp))
                    .border(2.dp, MafiaBronzeBorder, CutCornerShape(6.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                imageVector = Icons.Default.ScreenRotation,
                contentDescription = "Rotate",
                tint = MafiaGoldBright,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "يُفضّل تدوير الشاشة لوضع أفقي",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MafiaGoldBright,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "صُممت لعبة مافيا بكسل بنسبة سينمائية 16:9 لمكتب الدون المشوق وأزرار التحكم العريضة. يُرجى تدوير جهازك للاستمتاع بالتجربة الكاملة.",
                fontFamily = CairoFontFamily,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = TextCreamSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(CutCornerShape(6.dp))
                    .background(Color(0xFF261D12))
                    .border(1.5.dp, MafiaGoldAccent, CutCornerShape(6.dp))
                    .clickable { onContinueInPortrait() }
                    .testTag("continue_in_portrait_btn"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "المتابعة بالوضع الرأسي التكيفي",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MafiaGoldAccent
                )
            }
        }
    }
}
