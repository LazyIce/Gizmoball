package model;

import javax.sound.sampled.*;

import java.io.*;
import java.net.*;

public class SoundClip {
	private AudioInputStream m_sound;   //音频流
	File m_soundFile;   //音频文件源
	private Clip m_clip;   //音频剪辑
	private String m_filename;   //音频文件的文件名
	FloatControl gainControl;

	public SoundClip(File file) {
		try {
			setFile(file);
			m_clip = AudioSystem.getClip();
			load(m_filename);
			m_clip.open(m_sound);
			gainControl = (FloatControl) m_clip.getControl(FloatControl.Type.MASTER_GAIN);
		} catch (Exception e) {
			System.out.println("File does not exist");
		}
	}

	public Clip getClip() {
		return m_clip;
	}

	public void setFile(File f) {
		m_soundFile = f;
		try {
			m_sound = AudioSystem.getAudioInputStream(m_soundFile);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getFile() {
		return m_soundFile;
	}

	public boolean isLoaded() {
		return (m_soundFile != null);

	}

	public boolean load(String file) {
		try {
			m_sound = AudioSystem.getAudioInputStream(getFile());
			return true;
		} catch (IOException e) {
			System.out.println("File does not exist");
			return false;
		} catch (UnsupportedAudioFileException e) {
			System.out.println("File is not a supported format.");
			return false;
		}
	}

	public void play() {
		if (!isLoaded()) {
			return;
		}
		//新建个线程播放音频
		new Thread(new Runnable() {
			public void run() {
				m_clip.setFramePosition(0);
				m_clip.start();
			}
		}).start();
	}

	public void setVolume(float f) {
		gainControl.setValue(f);
	}

}