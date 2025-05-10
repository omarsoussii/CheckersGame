package utils;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SoundManager {
    private final Map<String, List<Clip>> soundEffects = new ConcurrentHashMap<>();
    private Clip backgroundMusic;
    private final Random random = new Random();
    private float effectsVolume = 0.7f;
    private float musicVolume = 0.5f;
    private boolean soundsEnabled = true;

    private static final AudioFormat TARGET_FORMAT = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);

    public void playBackgroundMusic(String resourcePath) {
        if (!soundsEnabled) return;
        stopBackgroundMusic();

        try (var is = getClass().getResourceAsStream(resourcePath);
             var original = AudioSystem.getAudioInputStream(new BufferedInputStream(is))) {
            var converted = AudioSystem.getAudioInputStream(TARGET_FORMAT, original);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(converted);
            setMusicVolume(musicVolume);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            System.err.println("Error loading background music: " + e.getMessage());
        }
    }

    public void loadSoundEffects(String eventName, String... resourcePaths) {
        if (!soundsEnabled || resourcePaths == null) return;
        List<Clip> clips = new ArrayList<>();

        for (String path : resourcePaths) {
            try (var is = getClass().getResourceAsStream(path);
                 var original = AudioSystem.getAudioInputStream(new BufferedInputStream(is))) {
                var converted = AudioSystem.getAudioInputStream(TARGET_FORMAT, original);
                Clip clip = AudioSystem.getClip();
                clip.open(converted);
                setClipVolume(clip, effectsVolume);
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) clip.setFramePosition(0);
                });
                clips.add(clip);
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
                System.err.println("Error loading sound " + path + ": " + e.getMessage());
            }
        }
        if (!clips.isEmpty()) soundEffects.put(eventName, clips);
    }

    public void playRandomSoundEffect(String eventName) {
        if (!soundsEnabled || !soundEffects.containsKey(eventName)) return;
        var clips = soundEffects.get(eventName);
        new Thread(() -> {
            var clip = clips.get(random.nextInt(clips.size()));
            synchronized (clip) {
                clip.stop();
                clip.setFramePosition(0);
                clip.start();
            }
        }).start();
    }

    public void setMusicVolume(float volume) {
        musicVolume = Math.max(0f, Math.min(1f, volume));
        if (backgroundMusic != null) setClipVolume(backgroundMusic, musicVolume);
    }

    public void setEffectsVolume(float volume) {
        effectsVolume = Math.max(0f, Math.min(1f, volume));
        soundEffects.values().forEach(clips -> clips.forEach(clip -> setClipVolume(clip, effectsVolume)));
    }

    public void setSoundsEnabled(boolean enabled) {
        soundsEnabled = enabled;
        if (!enabled) stopBackgroundMusic();
    }

    public void cleanup() {
        stopBackgroundMusic();
        soundEffects.values().forEach(clips -> clips.forEach(clip -> {
            clip.stop();
            clip.close();
        }));
        soundEffects.clear();
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.close();
            backgroundMusic = null;
        }
    }

    private void setClipVolume(Clip clip, float volume) {
        try {
            var gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(20f * (float) Math.log10(volume));
        } catch (IllegalArgumentException e) {
            System.err.println("Volume control not supported for clip");
        }
    }
}