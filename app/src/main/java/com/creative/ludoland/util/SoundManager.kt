package com.creative.ludoland.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build

class SoundManager(context: Context) {

    private var toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 80)
    } catch (e: Exception) {
        null
    }

    var isMuted: Boolean = false

    fun playTapSound() {
        if (isMuted) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
        } catch (_: Exception) {}
    }

    fun playDiceRollSound() {
        if (isMuted) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_DTMF_0, 100)
        } catch (_: Exception) {}
    }

    fun playMoveSound() {
        if (isMuted) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 70)
        } catch (_: Exception) {}
    }

    fun playCaptureSound() {
        if (isMuted) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 180)
        } catch (_: Exception) {}
    }

    fun playWinSound() {
        if (isMuted) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_SUP_RINGTONE, 350)
        } catch (_: Exception) {}
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
