package events;

import model.Board;
import parser.GizmoParser;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SaveMenuActionListener implements ActionListener
{
	private JFrame m_window;
	private Board m_board;
	private JFileChooser m_fc;
	
	public SaveMenuActionListener (JFrame window, Board board)
	{
		m_board = board;
		m_window = window;
	
		m_fc = new JFileChooser();
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Gizmoball File Format (*.gizmos)", "gizmos");
		FileNameExtensionFilter filter2 = new FileNameExtensionFilter("Extended Gizmoball File Format (*.egizmos)", "egizmos");
		
		m_fc.setAcceptAllFileFilterUsed(false);
		m_fc.addChoosableFileFilter(filter);
		m_fc.addChoosableFileFilter(filter2);
		
		m_fc.setFileFilter(filter);
		
		m_fc.addPropertyChangeListener(new PropertyChangeListener() {
	        public void propertyChange(PropertyChangeEvent evt) {
	          if (JFileChooser.FILE_FILTER_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
	        	  m_fc.setSelectedFile(new File(""));
	          }
	        }
	      });
	}
	
	public void actionPerformed (ActionEvent event)
	{	
		m_fc.setDialogTitle("Save game...");
		
		int ret = m_fc.showSaveDialog(m_window);
		
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = m_fc.getSelectedFile();
			
			if (file == null) {
				JOptionPane.showMessageDialog(m_window, "Please select a file before clicking the save button.", "Select Game File...", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (m_board.getGizmos().size() == 1) {
				JOptionPane.showMessageDialog(m_window, "Cannot save game as there are no gizmos on the board to save.", "Select Game File...", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (file.exists()) {
				int option = JOptionPane.showConfirmDialog(null,
						"Are you sure you want to overwrite this game?", "Overwrite Game File...", JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.NO_OPTION) {
					return;
				}
			}
			
			try {
				GizmoParser parser = new GizmoParser(m_board);
				
				FileFilter f = m_fc.getFileFilter();
				
				Board.Format bf = Board.Format.STANDARD;
				
				if (f.getDescription().equals("Extended Gizmoball File Format (*.egizmos)")) {
					bf = Board.Format.EXTENDED;
					
					if (file.getPath().endsWith(".gizmos")) {
						JOptionPane.showMessageDialog(m_window, 
								"The specified file " + file.getName() + " does not have a .egizmos extension.",
								"Load Game Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				} else {
					if (file.getPath().endsWith(".egizmos")) {
						JOptionPane.showMessageDialog(m_window, 
								"The specified file " + file.getName() + " does not have a .gizmos extension.",
								"Load Game Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				
				parser.save(file, bf);
			} catch (FileNotFoundException fe) {
					JOptionPane.showMessageDialog(m_window, 
							"Could not find the specified file: " + file.getName(),
							"Save Game Error",
							JOptionPane.ERROR_MESSAGE);
			} catch (IOException ie) {
				JOptionPane.showMessageDialog(m_window,
					    "An error occurred when saving the game. Could not save game " + file.getName(),
					    "Save Game Error",
					    JOptionPane.ERROR_MESSAGE);
			} catch (NullPointerException e) {
				JOptionPane.showMessageDialog(m_window,
					    e.getMessage(),
					    "Save Game Error",
					    JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(m_window,
					    e.getMessage(),
					    "Save Game Error",
					    JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
