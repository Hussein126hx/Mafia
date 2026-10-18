package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GameRole
import com.example.model.PlayerPrivate
import com.example.model.PlayerPublic
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.MafiaBgNearBlack
import com.example.ui.theme.MafiaBronzeBorder
import com.example.ui.theme.MafiaGoldAccent
import com.example.ui.theme.MafiaGoldBright
import com.example.ui.theme.MafiaSurfaceDark
import com.example.ui.theme.MafiaWoodBrown
import com.example.ui.theme.TextCreamPrimary
import com.example.ui.theme.TextCreamSecondary
import com.example.ui.theme.TextMuted

/**
 * Public Player Card Dialog:
 * STRICT RULE: Only shows Name, public status («حي» or «مستبعد»), and investigation notes if authorized.
 * NEVER leaks secret role!
 */
@Composable
fun PublicPlayerDialog(
    player: PlayerPublic,
    investigationNote: String?,
    onDismiss: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(CutCornerShape(8.dp))
                    .background(Color(0xF810141E))
                    .pixelBorder(
                        borderColor = MafiaBronzeBorder,
                        borderWidth = 2.dp,
                        outerBorderColor = Color(0xFF07090F),
                        shape = CutCornerShape(8.dp)
                    )
                    .padding(16.dp)
                    .testTag("public_player_dialog")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ملف الشخصية العام",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MafiaGoldAccent
                        )
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp).testTag("close_player_dialog")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextCreamSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = player.name,
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MafiaGoldBright
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Public Status
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الحالة العامة: ",
                            fontFamily = CairoFontFamily,
                            fontSize = 13.sp,
                            color = TextCreamSecondary
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    if (player.isAlive) Color(0x334CAF50) else Color(0x33F44336),
                                    CutCornerShape(4.dp)
                                )
                                .border(
                                    1.dp,
                                    if (player.isAlive) Color(0xFF4CAF50) else Color(0xFFF44336),
                                    CutCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (player.isAlive) "«حي»" else "«مستبعد»",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (player.isAlive) Color(0xFF81C784) else Color(0xFFE57373)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "الأصوات المتراكمة: ${player.votesReceived}",
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp,
                        color = TextCreamPrimary
                    )

                    // Rule-permitted exception: Detective's private investigation result on this player
                    if (investigationNote != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1D2638), CutCornerShape(4.dp))
                                .border(1.dp, Color(0xFF3498DB), CutCornerShape(4.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "ملاحظة تحرياتك السرية: $investigationNote",
                                fontFamily = CairoFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF90CAF9),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .clip(CutCornerShape(4.dp))
                            .background(Color(0xFF251A10))
                            .border(1.dp, MafiaBronzeBorder, CutCornerShape(4.dp))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "إغلاق الملف",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MafiaGoldAccent
                        )
                    }
                }
            }
        }
    }
}

/**
 * Strictly Private Role Sheet:
 * Shows the user's OWN role with a privacy veil so no other person glancing at screen can see it.
 */
@Composable
fun PrivateRoleSheet(
    userPrivate: PlayerPrivate,
    onDismiss: () -> Unit
) {
    var isRevealed by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .clip(CutCornerShape(8.dp))
                    .background(Color(0xF910131B))
                    .pixelBorder(
                        borderColor = MafiaGoldAccent,
                        borderWidth = 2.dp,
                        outerBorderColor = Color(0xFF000000),
                        shape = CutCornerShape(8.dp)
                    )
                    .padding(18.dp)
                    .testTag("private_role_sheet")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock",
                                tint = MafiaGoldBright,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "بطاقتك السرية الخاصة",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MafiaGoldBright
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp).testTag("close_role_sheet")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextCreamSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "محمية كلياً: لا يستطيع أي لاعب آخر أو واجهة عامة الاطلاع على هذه البطاقة.",
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Privacy Shield Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CutCornerShape(6.dp))
                            .background(if (isRevealed) Color(0xFF20160F) else Color(0xFF141923))
                            .border(1.5.dp, if (isRevealed) MafiaGoldAccent else MafiaBronzeBorder, CutCornerShape(6.dp))
                            .clickable { isRevealed = !isRevealed }
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!isRevealed) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "Reveal",
                                    tint = MafiaGoldAccent,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "«اضغط هنا لكشف دورك السري بأمان»",
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MafiaGoldAccent
                                )
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "دورك الحقيقي:",
                                    fontFamily = CairoFontFamily,
                                    fontSize = 12.sp,
                                    color = TextCreamSecondary
                                )
                                Text(
                                    text = userPrivate.role.titleArabic,
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = MafiaGoldBright
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = userPrivate.role.descriptionArabic,
                                    fontFamily = CairoFontFamily,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    color = TextCreamPrimary,
                                    textAlign = TextAlign.Center
                                )

                                // Authorized team information
                                if (userPrivate.role == GameRole.MAFIA && userPrivate.knownMafiaAllies.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "حلفاؤك في المافيا: ${userPrivate.knownMafiaAllies.joinToString("، ")}",
                                        fontFamily = CairoFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF8A80)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VisibilityOff,
                                        contentDescription = "Hide",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "المس مجدداً للإخفاء",
                                        fontFamily = CairoFontFamily,
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .clip(CutCornerShape(4.dp))
                            .background(Color(0xFF2B1F13))
                            .border(1.dp, MafiaBronzeBorder, CutCornerShape(4.dp))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "تم وفهمت دوري",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MafiaGoldAccent
                        )
                    }
                }
            }
        }
    }
}
