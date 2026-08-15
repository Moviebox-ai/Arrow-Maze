package com.example.arrowmaze3d.settings;

public class GameSettings {
    private float musicVolume = 0.8f;
    private float sfxVolume = 1.0f;
    private boolean soundEnabled = true;
    private boolean musicEnabled = true;
    private boolean hapticsEnabled = true;
    private String graphicsQuality = "HIGH"; // LOW, MEDIUM, HIGH

    public float getMusicVolume() {
        return musicEnabled ? musicVolume : 0f;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = Math.max(0f, Math.min(1f, musicVolume));
    }

    public float getSfxVolume() {
        return soundEnabled ? sfxVolume : 0f;
    }

    public void setSfxVolume(float sfxVolume) {
        this.sfxVolume = Math.max(0f, Math.min(1f, sfxVolume));
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }

    public boolean isHapticsEnabled() {
        return hapticsEnabled;
    }

    public void setHapticsEnabled(boolean hapticsEnabled) {
        this.hapticsEnabled = hapticsEnabled;
    }

    public String getGraphicsQuality() {
        return graphicsQuality;
    }

    public void setGraphicsQuality(String graphicsQuality) {
        this.graphicsQuality = graphicsQuality;
    }
}
