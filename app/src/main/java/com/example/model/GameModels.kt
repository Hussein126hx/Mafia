package com.example.model

enum class GameRole(
    val titleArabic: String,
    val descriptionArabic: String,
    val team: Team
) {
    CITIZEN(
        titleArabic = "مواطن شريف",
        descriptionArabic = "ليس لديك قدرات ليلية خاصة. قوتك تكمن في قوة الملاحظة، النقاش الصادق، والتصويت بحكمة أثناء النهار.",
        team = Team.CITIZENS
    ),
    MAFIA(
        titleArabic = "عضو مافيا",
        descriptionArabic = "تعمل في الخفاء مع عائلة الدون. تختارون في كل ليلة ضحية لتصفيتها، وتتظاهرون بالبراءة في النهار.",
        team = Team.MAFIA
    ),
    DOCTOR(
        titleArabic = "طبيب الحي",
        descriptionArabic = "تسهر ليلاً لإنقاذ الأرواح. يمكنك حماية شخص واحد كل ليلة من رصاص المافيا الغادر.",
        team = Team.CITIZENS
    ),
    DETECTIVE(
        titleArabic = "محقق المباحث",
        descriptionArabic = "تبحث عن الحقيقة خلف الكواليس. تحقق كل ليلة في هوية مشتبه به واحد لتعرف إن كان ينتمي للمافيا.",
        team = Team.CITIZENS
    );

    enum class Team {
        CITIZENS,
        MAFIA
    }
}

enum class GamePhase(val titleArabic: String) {
    INTRO("توزيع الأدوار السرية"),
    NIGHT("مرحلة الليل"),
    DAWN("بزوغ الفجر والنتائج"),
    DAY_DISCUSSION("نقاش النهار"),
    VOTING("التصويت العلني"),
    VERDICT("قرار المحاكمة"),
    GAME_OVER("نهاية اللعبة")
}

/**
 * STRICT SECURITY SEPARATION:
 * Public information shared across all UI components and visible to every player.
 * NEVER contains the secret role of the player.
 */
data class PlayerPublic(
    val id: String,
    val name: String,
    val isAlive: Boolean = true,
    val avatarDrawable: Int,
    val isUser: Boolean = false,
    val votesReceived: Int = 0,
    val publicStatus: String = "حي"
)

/**
 * Private information that belongs EXCLUSIVELY to the authoritative game engine / local user.
 * Never exposed on other players' public cards or top bar avatars.
 */
data class PlayerPrivate(
    val id: String,
    val role: GameRole,
    val isUser: Boolean = false,
    val knownMafiaAllies: List<String> = emptyList(),
    val investigationHistory: Map<String, Boolean> = emptyMap() // targetId -> isMafia
)

data class DialogueEntry(
    val speakerId: String,
    val speakerName: String,
    val avatarDrawable: Int,
    val textArabic: String,
    val phase: GamePhase
)

data class FinalPlayerResult(
    val player: PlayerPublic,
    val secretRole: GameRole
)

