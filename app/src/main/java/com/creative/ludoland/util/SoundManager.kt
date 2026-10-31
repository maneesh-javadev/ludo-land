package com.creative.ludoland.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator

class SoundManager(private val context: Context) {

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
            getOrResetToneGenerator()?.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
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
            toneGenerator?.release()
        } catch (_: Exception) {}
        toneGenerator = null
    }
}
