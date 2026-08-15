package com.example.arrowmaze3d.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.example.arrowmaze3d.settings.GameSettings;
import com.example.arrowmaze3d.utilities.AppLogger;

public class AudioManager {
    private final GameSettings settings;

    // Procedurally generated audio tones via AudioTrack or LibGDX Sound
    private Sound stepSound;
    private Sound rotateSound;
    private Sound triggerSound;
    private Sound victorySound;
    private Sound teleportSound;

    public AudioManager(GameSettings settings) {
        this.settings = settings;
        createProceduralSounds();
    }

    private void createProceduralSounds() {
        try {
            // Generate audio buffers dynamically for sound effects
            byte[] stepPcm = generateTonePcm(440, 0.05f); // 440 Hz click
            byte[] rotatePcm = generateTonePcm(660, 0.1f); // 660 Hz clink
            byte[] triggerPcm = generateTonePcm(880, 0.12f); // 880 Hz trigger
            byte[] victoryPcm = generateArpeggioPcm(new float[]{523.25f, 659.25f, 783.99f, 1046.50f}, 0.12f); // C Major chord
            byte[] teleportPcm = generateTonePcm(330, 0.18f);

            stepSound = Gdx.audio.newSound(createSoundFile(stepPcm, 22050));
            rotateSound = Gdx.audio.newSound(createSoundFile(rotatePcm, 22050));
            triggerSound = Gdx.audio.newSound(createSoundFile(triggerPcm, 22050));
            victorySound = Gdx.audio.newSound(createSoundFile(victoryPcm, 22050));
            teleportSound = Gdx.audio.newSound(createSoundFile(teleportPcm, 22050));
        } catch (Exception e) {
            AppLogger.e("Failed to create procedural audio", e);
        }
    }

    public void playStep() {
        if (settings.isSoundEnabled() && stepSound != null) {
            stepSound.play(settings.getSfxVolume() * 0.6f);
        }
    }

    public void playRotate() {
        if (settings.isSoundEnabled() && rotateSound != null) {
            rotateSound.play(settings.getSfxVolume() * 0.8f);
        }
    }

    public void playTrigger() {
        if (settings.isSoundEnabled() && triggerSound != null) {
            triggerSound.play(settings.getSfxVolume() * 0.9f);
        }
    }

    public void playVictory() {
        if (settings.isSoundEnabled() && victorySound != null) {
            victorySound.play(settings.getSfxVolume());
        }
    }

    public void playTeleport() {
        if (settings.isSoundEnabled() && teleportSound != null) {
            teleportSound.play(settings.getSfxVolume() * 0.7f);
        }
    }

    public void triggerHaptic() {
        if (settings.isHapticsEnabled() && Gdx.input != null) {
            Gdx.input.vibrate(30);
        }
    }

    private byte[] generateTonePcm(float freqHz, float durationSec) {
        int sampleRate = 22050;
        int numSamples = (int) (sampleRate * durationSec);
        byte[] pcm = new byte[numSamples * 2]; // 16-bit PCM mono
        for (int i = 0; i < numSamples; i++) {
            double angle = 2.0 * Math.PI * i * freqHz / sampleRate;
            float envelope = 1.0f - ((float) i / numSamples); // decay
            short sample = (short) (Math.sin(angle) * 32767 * envelope * 0.5f);
            pcm[i * 2] = (byte) (sample & 0xFF);
            pcm[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
        }
        return pcm;
    }

    private byte[] generateArpeggioPcm(float[] freqs, float noteDurationSec) {
        int sampleRate = 22050;
        int totalSamples = (int) (sampleRate * noteDurationSec * freqs.length);
        byte[] pcm = new byte[totalSamples * 2];
        int noteSamples = (int) (sampleRate * noteDurationSec);

        for (int n = 0; n < freqs.length; n++) {
            float freq = freqs[n];
            for (int i = 0; i < noteSamples; i++) {
                int sampleIdx = n * noteSamples + i;
                double angle = 2.0 * Math.PI * i * freq / sampleRate;
                float envelope = 1.0f - ((float) i / noteSamples);
                short sample = (short) (Math.sin(angle) * 32767 * envelope * 0.6f);
                pcm[sampleIdx * 2] = (byte) (sample & 0xFF);
                pcm[sampleIdx * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
            }
        }
        return pcm;
    }

    private com.badlogic.gdx.files.FileHandle createSoundFile(byte[] pcmData, int sampleRate) {
        // Create in-memory WAV file
        byte[] wavHeader = createWavHeader(pcmData.length, sampleRate, 1, 16);
        byte[] fullWav = new byte[wavHeader.length + pcmData.length];
        System.arraycopy(wavHeader, 0, fullWav, 0, wavHeader.length);
        System.arraycopy(pcmData, 0, fullWav, wavHeader.length, pcmData.length);

        com.badlogic.gdx.files.FileHandle tmp = Gdx.files.local("tmp_snd_" + System.nanoTime() + ".wav");
        tmp.writeBytes(fullWav, false);
        return tmp;
    }

    private byte[] createWavHeader(int pcmLength, int sampleRate, int channels, int bitsPerSample) {
        int totalDataLen = pcmLength + 36;
        int byteRate = sampleRate * channels * bitsPerSample / 8;
        byte[] header = new byte[44];

        header[0] = 'R'; header[1] = 'I'; header[2] = 'F'; header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W'; header[9] = 'A'; header[10] = 'V'; header[11] = 'E';
        header[12] = 'f'; header[13] = 'm'; header[14] = 't'; header[15] = ' ';
        header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0; // Subchunk1Size (16 for PCM)
        header[20] = 1; header[21] = 0; // AudioFormat (1 for PCM)
        header[22] = (byte) channels; header[23] = 0;
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (channels * bitsPerSample / 8); header[33] = 0; // BlockAlign
        header[34] = (byte) bitsPerSample; header[35] = 0;
        header[36] = 'd'; header[37] = 'a'; header[38] = 't'; header[39] = 'a';
        header[40] = (byte) (pcmLength & 0xff);
        header[41] = (byte) ((pcmLength >> 8) & 0xff);
        header[42] = (byte) ((pcmLength >> 16) & 0xff);
        header[43] = (byte) ((pcmLength >> 24) & 0xff);

        return header;
    }

    public void dispose() {
        if (stepSound != null) stepSound.dispose();
        if (rotateSound != null) rotateSound.dispose();
        if (triggerSound != null) triggerSound.dispose();
        if (victorySound != null) victorySound.dispose();
        if (teleportSound != null) teleportSound.dispose();
    }
}
