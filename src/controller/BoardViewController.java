package controller;

import controller.KeyConnection.Direction;
import model.Block;
import model.Board;
import model.GizmoObject;
import model.Mode;
import view.BoardView;

import java.awt.event.*;
import java.util.List;
import java.util.Map;

public class BoardViewController implements ActionListener, KeyListener, MouseListener {

	private BoardView m_boardView;
	private GizmoObject moveSelection = null;
	private GizmoObject connectionSelection = null;
	private GizmoObject disConnectionSelection = null;

	public BoardViewController(BoardView boardView) {
		m_boardView = boardView;
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	@Override
	public void keyPressed(KeyEvent event) {
		if (m_boardView.getModel().getMode() != Mode.PLAY_MODE_RUNNING) {
			return;
		}
		
		int keyCode = event.getKeyCode();
		Board board = m_boardView.getModel();

		KeyConnection connection = null;
		List<KeyConnection> keys = board.getKeyConnections();

		for (KeyConnection key : keys) {            //寻找该Board的m_keys中有没有该按键
			if (key.equals(new KeyConnection(keyCode))) {
				connection = key;
				break;
			}
		}

		if (connection != null) {
			Map<Direction, List<BoardAction>> actions = connection.getActions();
			if (actions.containsKey(Direction.DOWN)
					&& (!connection.isPressed())) {
				connection.triggerAction(Direction.DOWN);
			}
			connection.setPressed(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if (m_boardView.getModel().getMode() != Mode.PLAY_MODE_RUNNING) {
			return;
		}
		
		int keyCode = event.getKeyCode();
		Board board = m_boardView.getModel();
		
		if (keyCode==KeyEvent.VK_F1) {
			//按F1，进入Debug模式
			board.debug = !board.debug;
			System.out.println("Debug: " + board.debug);
		}
		else if (keyCode==KeyEvent.VK_F2) {
			//按F2，当当前为Debug模式时，一步一步地走
			board.update();
		}
		else if (keyCode==KeyEvent.VK_F3) {
			//按F3，接下来切开闭框模式
			m_boardView.wireframe = !m_boardView.wireframe;
			System.out.println("Wireframe: " + m_boardView.wireframe );
		}
		if (keyCode==KeyEvent.VK_F4) {
			//按F4，接下来开闭显示速度方向
			m_boardView.direction = !m_boardView.direction;
			System.out.println("Commencing direction...");
		}
		if (keyCode==KeyEvent.VK_F5) {
			//按F5，接下来将会显示场上有多少小球
			m_boardView.ballcount = !m_boardView.ballcount;
		}
		
		KeyConnection connection = null;
		List<KeyConnection> keys = board.getKeyConnections();

		for (KeyConnection key : keys) {
			if (key.equals(new KeyConnection(keyCode))) {
				connection = key;
				break;
			}
		}

		if (connection != null) {
			Map<Direction, List<BoardAction>> actions = connection.getActions();
			if (actions.containsKey(Direction.UP) && connection.isPressed()) {
				connection.triggerAction(Direction.UP);
			}
			connection.setPressed(false);
		}
		
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}
}
