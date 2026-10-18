package com.example.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.model.DialogueEntry
import com.example.model.FinalPlayerResult
import com.example.model.GamePhase
import com.example.model.GameRole
import com.example.model.PlayerPrivate
import com.example.model.PlayerPublic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    val phase: GamePhase = GamePhase.INTRO,
    val roundNumber: Int = 1,
    val publicPlayers: List<PlayerPublic> = emptyList(),
    val selectedPlayerId: String? = null,
    val currentDialogue: DialogueEntry? = null,
    val isActionSubmitted: Boolean = false,
    val userActionFeedback: String? = null,
    val isPrivateRoleModalOpen: Boolean = false,
    val isPublicDetailsOpen: Boolean = false,
    val gameOverWinningTeam: GameRole.Team? = null,
    val finalResults: List<FinalPlayerResult> = emptyList(),
    val isPortraitWarningDismissed: Boolean = false
)

class GameViewModel : ViewModel() {

    // Authoritative secret game state (Internal to ViewModel, NEVER exposed directly)
    private var internalPrivatePlayers: MutableMap<String, PlayerPrivate> = mutableMapOf()
    private val userId = "player_user"

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _userPrivateState = MutableStateFlow(
        PlayerPrivate(
            id = userId,
            role = GameRole.DETECTIVE,
            isUser = true
        )
    )
    val userPrivateState: StateFlow<PlayerPrivate> = _userPrivateState.asStateFlow()

    init {
        startNewGame(userPreferredRole = GameRole.DETECTIVE)
    }

    fun startNewGame(userPreferredRole: GameRole = GameRole.DETECTIVE) {
        // Build 5 characters with rich pixel portraits
        val publicList = listOf(
            PlayerPublic(
                id = userId,
                name = "أنت (طارق)",
                isAlive = true,
                avatarDrawable = R.drawable.ic_mafia_logo,
                isUser = true,
                publicStatus = "حي"
            ),
            PlayerPublic(
                id = "char_don",
                name = "الدون سالفاتوري",
                isAlive = true,
                avatarDrawable = R.drawable.img_portrait_don,
                isUser = false,
                publicStatus = "حي"
            ),
            PlayerPublic(
                id = "char_detective",
                name = "المحقق كريم",
                isAlive = true,
                avatarDrawable = R.drawable.img_portrait_detective,
                isUser = false,
                publicStatus = "حي"
            ),
            PlayerPublic(
                id = "char_doctor",
                name = "الدكتورة أمينة",
                isAlive = true,
                avatarDrawable = R.drawable.img_portrait_doctor,
                isUser = false,
                publicStatus = "حي"
            ),
            PlayerPublic(
                id = "char_salim",
                name = "التاجر سالم",
                isAlive = true,
                avatarDrawable = R.drawable.img_portrait_don,
                isUser = false,
                publicStatus = "حي"
            )
        )

        // Assign private roles authoritative map
        // Don is Mafia, Salim is Mafia ally or Citizen, Doctor is Doctor, Karim is Detective/Citizen
        val (donRole, karimRole, doctorRole, salimRole) = when (userPreferredRole) {
            GameRole.DETECTIVE -> Quadruple(GameRole.MAFIA, GameRole.CITIZEN, GameRole.DOCTOR, GameRole.MAFIA)
            GameRole.MAFIA -> Quadruple(GameRole.MAFIA, GameRole.DETECTIVE, GameRole.DOCTOR, GameRole.CITIZEN)
            GameRole.DOCTOR -> Quadruple(GameRole.MAFIA, GameRole.DETECTIVE, GameRole.CITIZEN, GameRole.CITIZEN)
            GameRole.CITIZEN -> Quadruple(GameRole.MAFIA, GameRole.DETECTIVE, GameRole.DOCTOR, GameRole.CITIZEN)
        }

        internalPrivatePlayers = mutableMapOf(
            userId to PlayerPrivate(
                id = userId,
                role = userPreferredRole,
                isUser = true,
                knownMafiaAllies = if (userPreferredRole == GameRole.MAFIA) listOf("الدون سالفاتوري") else emptyList()
            ),
            "char_don" to PlayerPrivate(id = "char_don", role = donRole),
            "char_detective" to PlayerPrivate(id = "char_detective", role = karimRole),
            "char_doctor" to PlayerPrivate(id = "char_doctor", role = doctorRole),
            "char_salim" to PlayerPrivate(id = "char_salim", role = salimRole)
        )

        _userPrivateState.value = internalPrivatePlayers[userId]!!

        val initialDialogue = DialogueEntry(
            speakerId = "char_don",
            speakerName = "الدون سالفاتوري",
            avatarDrawable = R.drawable.img_portrait_don,
            textArabic = "مرحباً بكم في مكتبي في هذه الليلة العاصفة. هناك خائن بيننا وستُحسم الأمور قبل شروق الشمس.",
            phase = GamePhase.INTRO
        )

        _uiState.value = GameUiState(
            phase = GamePhase.INTRO,
            roundNumber = 1,
            publicPlayers = publicList,
            selectedPlayerId = "char_don",
            currentDialogue = initialDialogue,
            isActionSubmitted = false
        )
    }

    fun selectPlayer(player: PlayerPublic) {
        _uiState.update { it.copy(selectedPlayerId = player.id) }
    }

    fun openPublicDetails() {
        _uiState.update { it.copy(isPublicDetailsOpen = true) }
    }

    fun closePublicDetails() {
        _uiState.update { it.copy(isPublicDetailsOpen = false) }
    }

    fun togglePrivateRoleModal(open: Boolean) {
        _uiState.update { it.copy(isPrivateRoleModalOpen = open) }
    }

    fun dismissPortraitWarning() {
        _uiState.update { it.copy(isPortraitWarningDismissed = true) }
    }

    fun getInvestigationNoteFor(playerId: String): String? {
        val userPrivate = _userPrivateState.value
        val isSuspectMafia = userPrivate.investigationHistory[playerId] ?: return null
        return if (isSuspectMafia) "تم تأكيد انتمائه إلى المافيا! ⚠️" else "سجلّه نظيف، مواطن بريء ✅"
    }

    /**
     * Advances story dialogue or transitions phase
     */
    fun advanceDialogueOrPhase() {
        val currentState = _uiState.value
        when (currentState.phase) {
            GamePhase.INTRO -> {
                // Transition to Night Phase
                startNightPhase()
            }
            GamePhase.DAWN -> {
                // Transition to Day Discussion
                startDayDiscussionPhase()
            }
            GamePhase.DAY_DISCUSSION -> {
                // Transition to Voting
                startVotingPhase()
            }
            GamePhase.VERDICT -> {
                // Check if Game is Won or proceed to Next Night
                if (currentState.gameOverWinningTeam != null) {
                    revealAllSecretRoles(currentState.gameOverWinningTeam!!)
                } else {
                    _uiState.update { it.copy(roundNumber = it.roundNumber + 1) }
                    startNightPhase()
                }
            }
            else -> {
                // Dialogue progression in same phase if multiple dialogues exist
            }
        }
    }

    private fun startNightPhase() {
        val userRole = _userPrivateState.value.role
        val dialogueText = when (userRole) {
            GameRole.CITIZEN -> "أغلقت شوارع المدينة أبوابها وساد الظلام. كمواطن شريف، نم بسلام وانتظر ضوء الصباح."
            GameRole.MAFIA -> "أسدل الليل ستاره. اختر مع العائلة هدفاً لتصفيته دون ترك أي أثر."
            GameRole.DOCTOR -> "الليل موحش ورصاص الغدر يتربص. اختر شخصاً لحمايته بحقيبتك الطبية الليلة."
            GameRole.DETECTIVE -> "ساعة التحريات قد حانت. تفحص سجلات مشتبه به واحد في الخفاء الليلة."
        }

        _uiState.update {
            it.copy(
                phase = GamePhase.NIGHT,
                isActionSubmitted = false,
                userActionFeedback = null,
                currentDialogue = DialogueEntry(
                    speakerId = "narrator",
                    speakerName = "راوي الليل",
                    avatarDrawable = R.drawable.ic_mafia_logo,
                    textArabic = dialogueText,
                    phase = GamePhase.NIGHT
                )
            )
        }
    }

    fun submitUserAction(actionId: String) {
        val state = _uiState.value
        if (state.isActionSubmitted) return

        val selectedPlayer = state.publicPlayers.find { it.id == state.selectedPlayerId }
        val userRole = _userPrivateState.value.role

        var feedback = "تم إرسال اختيارك"

        when (state.phase) {
            GamePhase.NIGHT -> {
                when (userRole) {
                    GameRole.DETECTIVE -> {
                        if (actionId == "detective_inspect" && selectedPlayer != null) {
                            val targetPrivate = internalPrivatePlayers[selectedPlayer.id]
                            val isMafia = targetPrivate?.role == GameRole.MAFIA
                            _userPrivateState.update { current ->
                                current.copy(
                                    investigationHistory = current.investigationHistory + (selectedPlayer.id to isMafia)
                                )
                            }
                            feedback = if (isMafia) "المحقق: تأكدت الشبهات! الهدف عضو مافيا!" else "المحقق: الهدف بريء من تهمة المافيا."
                        } else {
                            feedback = "تم إرسال دوريات المباحث للمراقبة."
                        }
                    }
                    GameRole.MAFIA -> {
                        feedback = "تم تحديد هدف التصفية وسينفذ رجالك الأمر."
                    }
                    GameRole.DOCTOR -> {
                        feedback = "تم توفير الحراسة والإسعاف للشخص المختار."
                    }
                    GameRole.CITIZEN -> {
                        feedback = "حان وقت الليل، انتظر حتى الصباح"
                    }
                }
            }
            GamePhase.VOTING -> {
                if (actionId == "vote_convict" && selectedPlayer != null) {
                    feedback = "صوّتت رسمياً لإدانة ${selectedPlayer.name}."
                    _uiState.update { current ->
                        current.copy(
                            publicPlayers = current.publicPlayers.map { p ->
                                if (p.id == selectedPlayer.id) p.copy(votesReceived = p.votesReceived + 1) else p
                            }
                        )
                    }
                } else if (actionId == "vote_defend") {
                    feedback = "قدمت دفاعك القانوني أمام الحاضرين."
                } else {
                    feedback = "امتنعت عن التصويت في هذه الجولة."
                }
            }
            else -> {
                feedback = "تم تسجيل موقفك في محضر الجلسة."
            }
        }

        _uiState.update {
            it.copy(
                isActionSubmitted = true,
                userActionFeedback = feedback
            )
        }

        // Simulate night conclusion and transition to Dawn after short delay or via next click
        viewModelScope.launch {
            if (state.phase == GamePhase.NIGHT) {
                // Advance to Dawn automatically after brief pause
                kotlinx.coroutines.delay(1200)
                resolveNightAndGoDawn(selectedPlayerId = selectedPlayer?.id)
            } else if (state.phase == GamePhase.VOTING) {
                kotlinx.coroutines.delay(1200)
                resolveVotingAndGoVerdict()
            }
        }
    }

    private fun resolveNightAndGoDawn(selectedPlayerId: String?) {
        val userRole = _userPrivateState.value.role
        val currentPlayers = _uiState.value.publicPlayers.toMutableList()

        // Determine victim: If player is Mafia, they chose the victim. Otherwise Don Salvatore targets someone
        val victimId = if (userRole == GameRole.MAFIA && selectedPlayerId != null) {
            selectedPlayerId
        } else {
            // Bot Don targets non-mafia living player
            val eligible = currentPlayers.filter { it.isAlive && internalPrivatePlayers[it.id]?.role != GameRole.MAFIA }
            eligible.randomOrNull()?.id
        }

        // Check if Doctor saved them
        val savedId = if (userRole == GameRole.DOCTOR && selectedPlayerId != null) {
            selectedPlayerId
        } else {
            "char_detective" // Bot doctor defends detective
        }

        val dawnDialogue: DialogueEntry

        if (victimId != null && victimId != savedId) {
            // Victim eliminated!
            val updated = currentPlayers.map {
                if (it.id == victimId) it.copy(isAlive = false, publicStatus = "مستبعد") else it
            }
            val victimName = currentPlayers.find { it.id == victimId }?.name ?: "أحد الأشخاص"
            dawnDialogue = DialogueEntry(
                speakerId = "narrator",
                speakerName = "أخبار الصباح",
                avatarDrawable = R.drawable.ic_mafia_logo,
                textArabic = "أشرقت الشمس معلنة مأساة: عُثر على $victimName مستبعداً خارج مكتب الدون بعد إطلاق نار ليلي!",
                phase = GamePhase.DAWN
            )
            _uiState.update { it.copy(publicPlayers = updated) }
        } else {
            // Doctor successfully saved them
            dawnDialogue = DialogueEntry(
                speakerId = "char_doctor",
                speakerName = "الدكتورة أمينة",
                avatarDrawable = R.drawable.img_portrait_doctor,
                textArabic = "بفضل الله، تم التدخل الطبي السريع وإسعاف المصابين في اللحظات الأخيرة. لم يسقط أي ضحية الليلة!",
                phase = GamePhase.DAWN
            )
        }

        _uiState.update {
            it.copy(
                phase = GamePhase.DAWN,
                currentDialogue = dawnDialogue,
                isActionSubmitted = false,
                userActionFeedback = null
            )
        }

        checkWinCondition()
    }

    private fun startDayDiscussionPhase() {
        _uiState.update {
            it.copy(
                phase = GamePhase.DAY_DISCUSSION,
                isActionSubmitted = false,
                currentDialogue = DialogueEntry(
                    speakerId = "char_detective",
                    speakerName = "المحقق كريم",
                    avatarDrawable = R.drawable.img_portrait_detective,
                    textArabic = "لدينا أدلة على تحركات مشبوهة الليلة الماضية. أطالب الجميع بتقديم أقوالهم قبل بدء التصويت!",
                    phase = GamePhase.DAY_DISCUSSION
                )
            )
        }
    }

    private fun startVotingPhase() {
        // Reset votes received
        _uiState.update { current ->
            current.copy(
                phase = GamePhase.VOTING,
                isActionSubmitted = false,
                userActionFeedback = null,
                publicPlayers = current.publicPlayers.map { it.copy(votesReceived = 0) },
                currentDialogue = DialogueEntry(
                    speakerId = "char_don",
                    speakerName = "الدون سالفاتوري",
                    avatarDrawable = R.drawable.img_portrait_don,
                    textArabic = "بدأ التصويت الرسمي. حددوا المشتبه به الأكبر لاقتياده إلى قاضي المدينة.",
                    phase = GamePhase.VOTING
                )
            )
        }
    }

    private fun resolveVotingAndGoVerdict() {
        // Bot votes added
        val currentPlayers = _uiState.value.publicPlayers
        val living = currentPlayers.filter { it.isAlive }

        // Find suspect with maximum votes
        val maxVoted = living.maxByOrNull { it.votesReceived }

        val verdictDialogue: DialogueEntry
        if (maxVoted != null && maxVoted.votesReceived > 0) {
            val updated = currentPlayers.map {
                if (it.id == maxVoted.id) it.copy(isAlive = false, publicStatus = "مستبعد") else it
            }
            verdictDialogue = DialogueEntry(
                speakerId = "narrator",
                speakerName = "حكم المحكمة",
                avatarDrawable = R.drawable.ic_mafia_logo,
                textArabic = "بأغلبية أصوات الحاضرين، تقرر استبعاد «${maxVoted.name}» من المدينة بتهمة الخيانة!",
                phase = GamePhase.VERDICT
            )
            _uiState.update { it.copy(publicPlayers = updated) }
        } else {
            verdictDialogue = DialogueEntry(
                speakerId = "narrator",
                speakerName = "حكم المحكمة",
                avatarDrawable = R.drawable.ic_mafia_logo,
                textArabic = "تساوت الأصوات أو امتنع الأغلبية. لم يتم استبعاد أي شخص في هذه الجولة.",
                phase = GamePhase.VERDICT
            )
        }

        _uiState.update {
            it.copy(
                phase = GamePhase.VERDICT,
                currentDialogue = verdictDialogue,
                isActionSubmitted = false
            )
        }

        checkWinCondition()
    }

    private fun checkWinCondition() {
        val livingPlayers = _uiState.value.publicPlayers.filter { it.isAlive }
        val livingMafiaCount = livingPlayers.count { internalPrivatePlayers[it.id]?.role == GameRole.MAFIA }
        val livingCitizensCount = livingPlayers.size - livingMafiaCount

        if (livingMafiaCount == 0) {
            // Citizens Win
            _uiState.update { it.copy(gameOverWinningTeam = GameRole.Team.CITIZENS) }
        } else if (livingMafiaCount >= livingCitizensCount) {
            // Mafia Win
            _uiState.update { it.copy(gameOverWinningTeam = GameRole.Team.MAFIA) }
        }
    }

    private fun revealAllSecretRoles(winningTeam: GameRole.Team) {
        val results = _uiState.value.publicPlayers.map { publicPlayer ->
            FinalPlayerResult(
                player = publicPlayer,
                secretRole = internalPrivatePlayers[publicPlayer.id]?.role ?: GameRole.CITIZEN
            )
        }
        _uiState.update {
            it.copy(
                phase = GamePhase.GAME_OVER,
                finalResults = results
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
