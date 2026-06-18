package com.cheminee.metronome.data

sealed class TimeSignature(
    open val numerator: Int,
    open val beatUnit: Int,
    open val displayName: String,
    open val accentPattern: List<Boolean>
) {
    data class Preset(
        val presetType: PresetType,
        override val numerator: Int,
        override val beatUnit: Int,
        override val displayName: String,
        override val accentPattern: List<Boolean>
    ) : TimeSignature(numerator, beatUnit, displayName, accentPattern)

    data class Custom(
        override var numerator: Int,
        override var beatUnit: Int,
        override var displayName: String,
        override var accentPattern: List<Boolean>
    ) : TimeSignature(numerator, beatUnit, displayName, accentPattern) {
        companion object {
            fun create(num: Int, unit: Int): Custom {
                return Custom(
                    numerator = num,
                    beatUnit = unit,
                    displayName = "$num/$unit",
                    accentPattern = generatePattern(num, unit)
                )
            }
        }
    }

    enum class PresetType {
        FOUR_FOUR, THREE_FOUR, TWO_FOUR
    }

    companion object {
        val FOUR_FOUR = Preset(
            PresetType.FOUR_FOUR, 4, 4, "4/4", listOf(true, false, false, false)
        )
        val THREE_FOUR = Preset(
            PresetType.THREE_FOUR, 3, 4, "3/4", listOf(true, false, false)
        )
        val TWO_FOUR = Preset(
            PresetType.TWO_FOUR, 2, 4, "2/4", listOf(true, false)
        )

        val DEFAULT: Preset = FOUR_FOUR
        val PRESETS: List<Preset> = listOf(FOUR_FOUR, THREE_FOUR, TWO_FOUR)
        val ALL: List<TimeSignature> = PRESETS

        fun fromString(value: String, customNum: Int, customUnit: Int): TimeSignature {
            val preset = PRESETS.find { it.presetType.name == value }
            return when {
                preset != null -> preset
                value == "CUSTOM" -> Custom.create(customNum, customUnit)
                else -> DEFAULT
            }
        }

        fun generatePattern(numerator: Int, @Suppress("UNUSED_PARAMETER") beatUnit: Int): List<Boolean> {
            val n = numerator.coerceIn(1, 12)
            return List(n) { it == 0 }
        }
    }
}