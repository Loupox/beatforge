import struct
import math
import wave
import os

def generate_click(freq, duration_ms, amplitude, sample_rate=44100, decay_factor=0.0):
    n_samples = int(sample_rate * duration_ms / 1000)
    signal = []
    for i in range(n_samples):
        t = i / sample_rate
        decay = math.exp(-decay_factor * i / n_samples)
        value = amplitude * math.sin(2 * math.pi * freq * t) * decay
        signal.append(int(value * 32767))
    return signal

def write_wav(filename, freqs, duration_ms, amplitude, decay_factors, sample_rate=44100):
    n_samples = int(sample_rate * duration_ms / 1000)
    buffer = [0] * n_samples
    for freq, df in zip(freqs, decay_factors):
        click = generate_click(freq, duration_ms, amplitude, sample_rate, df)
        for i in range(n_samples):
            buffer[i] += click[i]
    max_val = max(abs(v) for v in buffer)
    if max_val > 0:
        buffer = [int(v * 32767 / max_val) for v in buffer]
    else:
        buffer = [0] * n_samples
    with wave.open(filename, 'w') as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(sample_rate)
        for v in buffer:
            w.writeframes(struct.pack('<h', v))

out_dir = os.path.dirname(os.path.abspath(__file__))
normal_path = os.path.join(out_dir, 'click_normal.wav')
accent_path  = os.path.join(out_dir, 'click_accent.wav')

# Normal: 1000Hz, 50ms, decay 8
write_wav(normal_path, [1000], 50, 0.8, [8])

# Accent: blend of 1200 + 1800Hz, 80ms, decay 6 (warmer, fuller)
write_wav(accent_path, [1200, 1800], 80, 0.9, [6, 8])

print(f'click_normal.wav  ({os.path.getsize(normal_path)} bytes)')
print(f'click_accent.wav  ({os.path.getsize(accent_path)} bytes)')