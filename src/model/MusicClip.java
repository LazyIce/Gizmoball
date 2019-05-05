package model;

import javax.sound.sampled.*;

import java.io.*;
import java.net.*;

public class MusicClip {
	private AudioInputStream m_sound;
	File m_soundFile;
	private Clip m_clip;
	private String m_filename;
	private String dir;

	public MusicClip(String name, String dir) {
		m_filename = name;
		this.dir = "Music/" + dir;
		loadFile();
	}

	private void loadFile() {
		try {
			m_clip = AudioSystem.getClip();

			if (m_clip == null) {
				throw new UnsupportedAudioFileException();
			}
			m_soundFile = new File(dir + m_filename);
			m_sound = AudioSystem.getAudioInputStream(m_soundFile);
			;
			m_clip.open(m_sound);

		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void loadNewFile(String newFile) {
		try {
			m_clip = AudioSystem.getClip();
			if (m_clip == null) {
				throw new UnsupportedAudioFileException();
			} else {
				m_filename = newFile;

				if (m_clip.isOpen()) {
					m_clip.close();
				}
				m_soundFile = new File(dir + m_filename);
				m_sound = AudioSystem.getAudioInputStream(m_soundFile);
				m_clip.open(m_sound);
			}

		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public Clip getClip() {
		return m_clip;
	}

	public File getFile() {
		return m_soundFile;
	}

	// Checks that file has been loaded
	public boolean isLoaded() {
		return (m_soundFile != null);

	}

	public void playFromBeginning() {
		// Exit if the sample hasn't been loaded
		if (!isLoaded()) {
			return;
		}
		
		m_clip.loop(m_clip.LOOP_CONTINUOUSLY);

		new Thread(new Runnable() {
			public void run() {
				// Resets the sound clip
				m_clip.setFramePosition(0);
				m_clip.start();
			}
		}).start();
	}

	public void playMusic() {
		if (!isLoaded()) {
			return;
		}
		m_clip.loop(Clip.LOOP_CONTINUOUSLY);
		new Thread(new Runnable() {
			public void run() {

				m_clip.start();
			}
		}).start();
	}

	public void stop() {
		m_clip.stop();
	}
}
