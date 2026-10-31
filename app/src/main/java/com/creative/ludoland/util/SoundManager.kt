package com.creative.ludoland.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.ToneGenerator
import com.creative.ludoland.R

class SoundManager(private val context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var diceRollSoundId: Int = 0

    init {
        try {
            diceRollSoundId = soundPool.load(context, R.raw.dice_roll, 1)
        } catch (_: Exception) {}
    }

    private var toneGenerator: ToneGenerator? = createToneGenerator()

    var isMuted: Boolean = false

    private fun createToneGenerator(): ToneGenerator? {
        return try {
            ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        } catch (_: Exception) {
            try {
                ToneGenerator(AudioManager.STREAM_SYSTEM, 100)
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun getOrResetToneGenerator(): ToneGenerator? {
        if (toneGenerator == null) {
            toneGenerator = createToneGenerator()
        }
        return toneGenerator
    }

    fun playTapSound() {
        if (isMuted) return
        try {
            getOrResetToneGenerator()?.startTone(ToneGenerator.TONE_PROP_BEEP, 60)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    fun playDiceRollSound() {
        if (isMuted) return
        try {
            if (diceRollSoundId != 0) {
                soundPool.play(diceRollSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
            } else {
                getOrResetToneGenerator()?.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
            }
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    fun playMoveSound() {
        if (isMuted) return
        try {
            getOrResetToneGenerator()?.startTone(ToneGenerator.TONE_PROP_ACK, 80)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    fun playCaptureSound() {
        if (isMuted) return
        try {
            getOrResetToneGenerator()?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 180)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    fun playWinSound() {
        if (isMuted) return
        try {
            getOrResetToneGenerator()?.startTone(ToneGenerator.TONE_SUP_PIP, 350)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    fun release() {
        try {
            soundPool.release()
        } catch (_: Exception) {}

        try {
            toneGenerator?.release()
        } catch (_: Exception) {}
        toneGenerator = null
    }
}
