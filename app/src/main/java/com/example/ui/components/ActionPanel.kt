package com.example.ui.components

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FrontHand
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GamePhase
import com.example.model.GameRole
import com.example.model.PlayerPublic
import com.example.ui.theme.ActionBlueBorder
import com.example.ui.theme.ActionBlueDark
import com.example.ui.theme.ActionBlueHighlight
import com.example.ui.theme.ActionGreenBorder
import com.example.ui.theme.ActionGreenDark
import com.example.ui.theme.ActionGreenHighlight
import com.example.ui.theme.ActionRedBorder
import com.example.ui.theme.ActionRedDark
import com.example.ui.theme.ActionRedHighlight
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.MafiaBronzeBorder
import com.example.ui.theme.MafiaGoldAccent
import com.example.ui.theme.MafiaGoldBright
import com.example.ui.theme.TextCreamPrimary
import com.example.ui.theme.TextMuted

data class GameActionButton(
    val id: String,
    val titleArabic: String,
    val icon: ImageVector,
    val tintType: ActionTint,
    val isEnabled: Boolean = true,
    val onClick: () -> Unit
)

enum class ActionTint {
    BLUE,
    RED,
    GREEN
}

/**
 * Action panel matching Section 1.4 & Section 6:
 * - Three large buttons in a row with colored tint (Blue, Red, Green) and pixel borders
 * - Dynamic based on role and phase
 * - For Citizen at night: shows «حان وقت الليل، انتظر حتى الصباح»
 * - When action submitted: shows «تم إرسال اختيارك» and blocks repeat submissions
 */
@Composable
fun ActionPanel(
    currentPhase: GamePhase,
    userRole: GameRole,
    selectedPlayer: PlayerPublic?,
    isActionSubmitted: Boolean,
    onActionSelected: (actionId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .testTag("action_panel"),
        contentAlignment = Alignment.Center
    ) {
        // Case 1: Action already submitted for current phase
        if (isActionSubmitted) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(52.dp)
                    .clip(CutCornerShape(6.dp))
                    .background(Color(0xE6141F16))
                    .border(2.dp, ActionGreenBorder, CutCornerShape(6.dp))
                    .testTag("action_submitted_banner"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = ActionGreenHighlight,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "تم إرسال اختيارك",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MafiaGoldBright
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "• في انتظار اكتمال قرارات الآخرين",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp,
                        color = TextCreamPrimary
                    )
                }
            }
            return@Box
        }

        // Case 2: Citizen at Night -> No night action permitted by rule
        if (currentPhase == GamePhase.NIGHT && userRole == GameRole.CITIZEN) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(52.dp)
                    .clip(CutCornerShape(6.dp))
                    .background(Color(0xE6121722))
                    .border(2.dp, MafiaBronzeBorder, CutCornerShape(6.dp))
                    .testTag("citizen_night_wait"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bedtime,
                        contentDescription = "Night Wait",
                        tint = MafiaGoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "حان وقت الليل، انتظر حتى الصباح",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextCreamPrimary
                    )
                }
            }
            return@Box
        }

        // Case 3: Dynamic 3-button panel based on Role & Phase
        val buttons = generateDynamicButtons(
            phase = currentPhase,
            role = userRole,
            selectedPlayer = selectedPlayer,
            onActionSelected = onActionSelected
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            buttons.forEach { button ->
                ActionButtonItem(
                    button = button,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ActionButtonItem(
    button: GameActionButton,
    modifier: Modifier = Modifier
) {
    val (bgColor, borderColor, highlightColor) = when (button.tintType) {
        ActionTint.BLUE -> Triple(ActionBlueDark, ActionBlueBorder, ActionBlueHighlight)
        ActionTint.RED -> Triple(ActionRedDark, ActionRedBorder, ActionRedHighlight)
        ActionTint.GREEN -> Triple(ActionGreenDark, ActionGreenBorder, ActionGreenHighlight)
    }

    val shape = CutCornerShape(6.dp)

    Box(
        modifier = modifier
            .height(52.dp)
            .testTag("btn_${button.id}")
            .clip(shape)
            .background(if (button.isEnabled) bgColor.copy(alpha = 0.85f) else Color(0x66181C24))
            .border(
                width = 2.dp,
                color = if (button.isEnabled) borderColor else Color(0x66555555),
                shape = shape
            )
            .clickable(enabled = button.isEnabled) { button.onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = button.icon,
                contentDescription = button.titleArabic,
                tint = if (button.isEnabled) highlightColor else TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = button.titleArabic,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (button.isEnabled) TextCreamPrimary else TextMuted
            )
        }
    }
}

private fun generateDynamicButtons(
    phase: GamePhase,
    role: GameRole,
    selectedPlayer: PlayerPublic?,
    onActionSelected: (String) -> Unit
): List<GameActionButton> {
    val targetName = selectedPlayer?.name ?: "الهدف"
    val isTargetValid = selectedPlayer != null && selectedPlayer.isAlive

    return when (phase) {
        GamePhase.NIGHT -> {
            when (role) {
                GameRole.MAFIA -> listOf(
                    GameActionButton(
                        id = "mafia_bribe",
                        titleArabic = "رشوة / تمويه",
                        icon = Icons.Default.AttachMoney,
                        tintType = ActionTint.GREEN,
                        isEnabled = true,
                        onClick = { onActionSelected("mafia_bribe") }
                    ),
                    GameActionButton(
                        id = "mafia_eliminate",
                        titleArabic = if (isTargetValid) "تصفية $targetName" else "تصفية الضحية",
                        icon = Icons.Default.SportsMma,
                        tintType = ActionTint.RED,
                        isEnabled = isTargetValid && !selectedPlayer.isUser,
                        onClick = { onActionSelected("mafia_eliminate") }
                    ),
                    GameActionButton(
                        id = "mafia_alliance",
                        titleArabic = "تفاوض العصابة",
                        icon = Icons.Default.Handshake,
                        tintType = ActionTint.BLUE,
                        isEnabled = true,
                        onClick = { onActionSelected("mafia_alliance") }
                    )
                )

                GameRole.DOCTOR -> listOf(
                    GameActionButton(
                        id = "doctor_protect",
                        titleArabic = if (isTargetValid) "حماية $targetName" else "حماية شخص",
                        icon = Icons.Default.HealthAndSafety,
                        tintType = ActionTint.GREEN,
                        isEnabled = isTargetValid,
                        onClick = { onActionSelected("doctor_protect") }
                    ),
                    GameActionButton(
                        id = "doctor_self",
                        titleArabic = "حماية ذاتية",
                        icon = Icons.Default.PanTool,
                        tintType = ActionTint.RED,
                        isEnabled = true,
                        onClick = { onActionSelected("doctor_self") }
                    ),
                    GameActionButton(
                        id = "doctor_observe",
                        titleArabic = "مراقبة ليلية",
                        icon = Icons.Default.Handshake,
                        tintType = ActionTint.BLUE,
                        isEnabled = true,
                        onClick = { onActionSelected("doctor_observe") }
                    )
                )

                GameRole.DETECTIVE -> listOf(
                    GameActionButton(
                        id = "detective_inspect",
                        titleArabic = if (isTargetValid) "تحقيق مع $targetName" else "تحقيق سري",
                        icon = Icons.Default.Search,
                        tintType = ActionTint.BLUE,
                        isEnabled = isTargetValid && !selectedPlayer.isUser,
                        onClick = { onActionSelected("detective_inspect") }
                    ),
                    GameActionButton(
                        id = "detective_patrol",
                        titleArabic = "كمين المباحث",
                        icon = Icons.Default.SportsMma,
                        tintType = ActionTint.RED,
                        isEnabled = true,
                        onClick = { onActionSelected("detective_patrol") }
                    ),
                    GameActionButton(
                        id = "detective_informant",
                        titleArabic = "دفع للمخبر",
                        icon = Icons.Default.AttachMoney,
                        tintType = ActionTint.GREEN,
                        isEnabled = true,
                        onClick = { onActionSelected("detective_informant") }
                    )
                )

                GameRole.CITIZEN -> emptyList() // Handled by night wait banner
            }
        }

        GamePhase.VOTING -> listOf(
            GameActionButton(
                id = "vote_defend",
                titleArabic = "دفاع وبراءة",
                icon = Icons.Default.Handshake,
                tintType = ActionTint.BLUE,
                isEnabled = true,
                onClick = { onActionSelected("vote_defend") }
            ),
            GameActionButton(
                id = "vote_convict",
                titleArabic = if (isTargetValid) "إدانة $targetName" else "إدانة المشتبه به",
                icon = Icons.Default.SportsMma,
                tintType = ActionTint.RED,
                isEnabled = isTargetValid && !selectedPlayer.isUser,
                onClick = { onActionSelected("vote_convict") }
            ),
            GameActionButton(
                id = "vote_abstain",
                titleArabic = "امتناع / تخطي",
                icon = Icons.Default.AttachMoney,
                tintType = ActionTint.GREEN,
                isEnabled = true,
                onClick = { onActionSelected("vote_abstain") }
            )
        )

        // Day discussion / Dawn / Intro default actions
        else -> listOf(
            GameActionButton(
                id = "action_negotiate",
                titleArabic = "تفاوض وتحالف",
                icon = Icons.Default.Handshake,
                tintType = ActionTint.BLUE,
                isEnabled = true,
                onClick = { onActionSelected("action_negotiate") }
            ),
            GameActionButton(
                id = "action_accuse",
                titleArabic = if (isTargetValid) "اتهام $targetName" else "توجيه اتهام",
                icon = Icons.Default.SportsMma,
                tintType = ActionTint.RED,
                isEnabled = isTargetValid && !selectedPlayer.isUser,
                onClick = { onActionSelected("action_accuse") }
            ),
            GameActionButton(
                id = "action_fund",
                titleArabic = "شراء ولاء",
                icon = Icons.Default.AttachMoney,
                tintType = ActionTint.GREEN,
                isEnabled = true,
                onClick = { onActionSelected("action_fund") }
            )
        )
    }
}
