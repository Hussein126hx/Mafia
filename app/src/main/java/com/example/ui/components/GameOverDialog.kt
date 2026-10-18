package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.FinalPlayerResult
import com.example.model.GameRole
import com.example.model.PlayerPublic
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.MafiaBronzeBorder
import com.example.ui.theme.MafiaGoldAccent
import com.example.ui.theme.MafiaGoldBright
import com.example.ui.theme.TextCreamPrimary
import com.example.ui.theme.TextCreamSecondary

/**
 * Game Over Dialog:

 * The ONLY place where all players' real secret roles are officially revealed to everyone.
 */
@Composable
fun GameOverDialog(
    winningTeam: GameRole.Team,
    finalResults: List<FinalPlayerResult>,
    onRestartGame: () -> Unit
) {
    val isCitizensWin = winningTeam == GameRole.Team.CITIZENS

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(CutCornerShape(8.dp))
                    .background(Color(0xF90E1118))
                    .pixelBorder(
                        borderColor = if (isCitizensWin) MafiaGoldAccent else Color(0xFFC0392B),
                        borderWidth = 2.5.dp,
                        outerBorderColor = Color(0xFF000000),
                        shape = CutCornerShape(8.dp)
                    )
                    .padding(16.dp)
                    .testTag("game_over_dialog")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isCitizensWin) "انتصار المواطنين الشرفاء!" else "سيطرة عائلة المافيا!",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = if (isCitizensWin) MafiaGoldBright else Color(0xFFFF5252),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isCitizensWin)
                            "تم القضاء على عناصر المافيا وعاد الأمان إلى شوارع المدينة."
                        else
                            "أحكمت المافيا قبضتها على المدينة بعد استبعاد المعارضين.",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp,
                        color = TextCreamSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "كشف جميع الهويات والأدوار السرية:",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MafiaGoldAccent
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // List revealing all players' true identities
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        finalResults.forEach { result ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF161B24), CutCornerShape(4.dp))
                                    .border(1.dp, MafiaBronzeBorder, CutCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = result.player.avatarDrawable),
                                        contentDescription = result.player.name,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CutCornerShape(2.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = result.player.name,
                                        fontFamily = CairoFontFamily,
                                        fontSize = 12.sp,
                                        color = TextCreamPrimary
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = result.secretRole.titleArabic,
                                        fontFamily = CairoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (result.secretRole == GameRole.MAFIA) Color(0xFFFF8A80) else MafiaGoldBright
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (result.player.isAlive) "(حي)" else "(مستبعد)",
                                        fontFamily = CairoFontFamily,
                                        fontSize = 10.sp,
                                        color = if (result.player.isAlive) Color(0xFF81C784) else Color(0xFFE57373)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(CutCornerShape(6.dp))
                            .background(Color(0xFF2C2013))
                            .border(2.dp, MafiaGoldAccent, CutCornerShape(6.dp))
                            .clickable { onRestartGame() }
                            .testTag("restart_game_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "بدء جولة جديدة",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MafiaGoldBright
                        )
                    }
                }
            }
        }
    }
}
