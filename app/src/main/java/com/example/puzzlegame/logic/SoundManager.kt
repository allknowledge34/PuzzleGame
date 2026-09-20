package com.example.puzzlegame.logic

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.puzzlegame.R

class SoundManager(context: Context) {
    private var soundPool: SoundPool? = null
    
    private var placeSoundId = 0
    private var clearSoundId = 0
    private var invalidSoundId = 0
    private var rotateSoundId = 0
    private var holdSoundId = 0
    private var refreshSoundId = 0
    private var gameoverSoundId = 0

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
            
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
            
        soundPool?.let { pool ->
            placeSoundId = pool.load(context, R.raw.place, 1)
            clearSoundId = pool.load(context, R.raw.clear, 1)
            invalidSoundId = pool.load(context, R.raw.invalid, 1)
            rotateSoundId = pool.load(context, R.raw.rotate, 1)
            holdSoundId = pool.load(context, R.raw.hold, 1)
            refreshSoundId = pool.load(context, R.raw.refresh, 1)
            gameoverSoundId = pool.load(context, R.raw.gameover, 1)
        }
    }

    fun playPlace() { soundPool?.play(placeSoundId, 1f, 1f, 0, 0, 1f) }
    fun playClear() { soundPool?.play(clearSoundId, 1f, 1f, 0, 0, 1f) }
    fun playInvalid() { soundPool?.play(invalidSoundId, 1f, 1f, 0, 0, 1f) }
    fun playRotate() { soundPool?.play(rotateSoundId, 1f, 1f, 0, 0, 1f) }
    fun playHold() { soundPool?.play(holdSoundId, 1f, 1f, 0, 0, 1f) }
    fun playRefresh() { soundPool?.play(refreshSoundId, 1f, 1f, 0, 0, 1f) }
    fun playGameOver() { soundPool?.play(gameoverSoundId, 1f, 1f, 0, 0, 1f) }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}
