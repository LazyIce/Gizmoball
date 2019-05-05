package events;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import java.io.File;
import java.io.FileInputStream;

public class MidiSequencer {
	private Sequence song;
	private File file;
	Sequencer sequencer;
	FileInputStream is;
	String filename;
	String dir;
	
	public MidiSequencer(String filename, String dir) {
		this.filename = filename;
		this.dir = dir;
		loadFile();
	}
	
	public void loadFile() {
		try {
			sequencer = MidiSystem.getSequencer();

			if (sequencer == null) {
				throw new MidiUnavailableException();
			}
			sequencer.open();
			is = new FileInputStream("Music/" + dir + filename);
			song = MidiSystem.getSequence(is);
			sequencer.setSequence(song);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadNewFile(String newFile) {
		filename = newFile;
		if (sequencer.isOpen()) {
			sequencer.close();
		}

		try {
			sequencer = MidiSystem.getSequencer();

			if (sequencer == null) {
				throw new MidiUnavailableException();
			}
			sequencer.open();
			is = new FileInputStream("Music/" + dir + newFile);
			song = MidiSystem.getSequence(is);
			sequencer.setSequence(song);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void play() {
		if (!sequencer.isOpen()) {
			return;
		}
		sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
		sequencer.start();
	}

	public void stop() {
		sequencer.stop();
	}
}
