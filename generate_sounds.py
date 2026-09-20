import wave
import struct
import math

SAMPLE_RATE = 44100

def make_wav(filename, samples):
    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1)
        wav_file.setsampwidth(2)
        wav_file.setframerate(SAMPLE_RATE)
        for s in samples:
            # clip
            s = max(-32768, min(32767, int(s * 32767)))
            wav_file.writeframesraw(struct.pack('<h', s))

def generate_tone(freq, duration, wave_type='sine', vol=0.5, fade_out=True):
    num_samples = int(SAMPLE_RATE * duration)
    samples = []
    for i in range(num_samples):
        t = i / SAMPLE_RATE
        if wave_type == 'sine':
            val = math.sin(2 * math.pi * freq * t)
        elif wave_type == 'square':
            val = 1.0 if math.sin(2 * math.pi * freq * t) > 0 else -1.0
        elif wave_type == 'saw':
            val = 2.0 * (t * freq - math.floor(t * freq + 0.5))
        
        env = 1.0
        if fade_out:
            env = max(0, 1.0 - (i / num_samples))
        samples.append(val * vol * env)
    return samples

# 1. place (short soft tick)
make_wav('app/src/main/res/raw/place.wav', generate_tone(400, 0.05, 'sine', 0.5))

# 2. clear (arpeggio)
clear_samples = generate_tone(440, 0.1) + generate_tone(554, 0.1) + generate_tone(659, 0.2)
make_wav('app/src/main/res/raw/clear.wav', clear_samples)

# 3. invalid (low buzz)
make_wav('app/src/main/res/raw/invalid.wav', generate_tone(150, 0.2, 'saw', 0.3))

# 4. rotate (short high click)
make_wav('app/src/main/res/raw/rotate.wav', generate_tone(800, 0.03, 'square', 0.2))

# 5. hold (whoosh)
make_wav('app/src/main/res/raw/hold.wav', generate_tone(300, 0.1, 'sine', 0.4))

# 6. refresh (chime)
refresh_samples = generate_tone(659, 0.1) + generate_tone(880, 0.3)
make_wav('app/src/main/res/raw/refresh.wav', refresh_samples)

# 7. gameover (descending)
go_samples = generate_tone(440, 0.3) + generate_tone(415, 0.3) + generate_tone(392, 0.5)
make_wav('app/src/main/res/raw/gameover.wav', go_samples)

print("Sounds generated.")
