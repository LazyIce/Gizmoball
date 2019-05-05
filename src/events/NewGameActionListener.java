package events;

import ui.windows.MainWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewGameActionListener implements ActionListener
{
	private MainWindow m_window;
	public NewGameActionListener (MainWindow window)
	{
		m_window = window;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (m_window.getBoardView().getModel().getGizmos().size() > 1) {
			int option = JOptionPane.showConfirmDialog(m_window,
					"Are you sure you want to close the current game?", "Close Current Game...", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.NO_OPTION) {

			} else {
				m_window.getBoardView().getModel().clear();
				m_window.repaint();
			}
		} else {
			m_window.repaint();
		}
	}
}
