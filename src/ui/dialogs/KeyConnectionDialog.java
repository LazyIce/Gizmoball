package ui.dialogs;

import controller.BoardAction;
import controller.KeyConnection;
import controller.KeyConnection.Direction;
import model.Board;
import model.GizmoObject;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class KeyConnectionDialog extends JDialog implements KeyListener, ActionListener, ItemListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Board m_board;
	private GizmoObject m_gizmo;
	private KeyConnection m_keyFound = null;
	private Integer m_keyCode = null;
	private JFrame m_window;
	
	private List<Direction> m_directions;
	
	private int m_selectedComboIndex;

	public KeyConnectionDialog(JFrame window, Board model, GizmoObject selectedGizmo) {
		super(window, true);
		
		m_window = window;
		m_board = model;
		m_gizmo = selectedGizmo;
		m_selected = false;
		m_directions = new ArrayList<Direction>();
		
		initComponents();
		setLocationRelativeTo(window);
		
		for (KeyConnection key : m_board.getKeyConnections()) {
			for (Entry<Direction, List<BoardAction>> entry : key.getActions().entrySet())
			{
			    List<BoardAction> actions = entry.getValue();
			    for (BoardAction action : actions) {
			    	if (action.getName().equals(m_gizmo.getName())) {
			    		m_keyFound = key;
			    		m_directions.add(entry.getKey());
			    	}
			    }
			}
		}
		
		if (m_directions.size() == 2) {
			jComboBox1.setSelectedIndex(0);
		}
		
		if (m_directions.size() == 1) {
			if (m_directions.get(0).equals(Direction.UP)) {
				jComboBox1.setSelectedIndex(2);
			} else {
				jComboBox1.setSelectedIndex(1);
			}
		}
		
		m_selectedComboIndex = jComboBox1.getSelectedIndex();
		
		if (m_keyFound != null) {
			if (KeyEvent.getKeyText(m_keyFound.getKey()).equals("Unknown keyCode: 0x0")) {
				jToggleButton1.setText(String.format("<html><h3>%d</h3></html>", m_keyFound.getKey()));
			} else {
				jToggleButton1.setText(String.format("<html><h3>%s</h3></html>", KeyEvent.getKeyText(m_keyFound.getKey())));
			}
			
			m_keyCode = m_keyFound.getKey();
		}
		
		jButton3.addActionListener(this);
		jButton2.addActionListener(this);
		
		jToggleButton1.addItemListener(this);
		jToggleButton1.addKeyListener(this);
		
		jToggleButton1.grabFocus();
	}

	private void initComponents()
	{
		jLabel1 = new JLabel();
		jLabel2 = new JLabel();
		jLabel3 = new JLabel();
		jComboBox1 = new JComboBox();
		jLabel4 = new JLabel();
		jLabel5 = new JLabel();
		jButton2 = new JButton();
		jButton3 = new JButton();
		jToggleButton1 = new JToggleButton();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Setup Key Connection");
		setResizable(false);

		jLabel1.setIcon(new ImageIcon("icons/key_connect_icon.png"));

		jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14));
		jLabel2.setText(String.format("Setup key connection for gizmo at position (%d, %d)", m_gizmo.getX(), m_gizmo.getY()));

		jLabel3.setText("Activate:");

		jComboBox1.setModel(new DefaultComboBoxModel(new String[] { "Pressed and Released", "Pressed", "Released" }));

		jLabel4.setText("Key:");

		jLabel5.setText("Enter the key to use and how it should be activated. ");

		jButton2.setText("OK");

		jButton3.setText("Cancel");

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
										.addComponent(jButton3)
										.addGap(18, 18, 18)
										.addComponent(jButton2))
								.addGroup(layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
												.addComponent(jLabel1)
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(jLabel4)
														.addComponent(jLabel3)))
										.addGap(18, 18, 18)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(jToggleButton1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
										.addComponent(jComboBox1, 0, 255, Short.MAX_VALUE))))
										.addContainerGap())
        );

		layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton2, jButton3});

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jToggleButton1, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addGap(11, 11, 11))
        );

		KeyStroke pressed = KeyStroke.getKeyStroke("pressed SPACE");
		KeyStroke released = KeyStroke.getKeyStroke("released SPACE");
		InputMap im = jToggleButton1.getInputMap();
		im.put(pressed, "none");
		im.put(released, "none");

        pack();
    }

    private JButton jButton2;
    private JButton jButton3;
    private JComboBox jComboBox1;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JToggleButton jToggleButton1;
	private boolean m_selected;

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (m_selected) {
			m_keyCode = arg0.getKeyCode();
			if (KeyEvent.getKeyText(arg0.getKeyCode()).equals("Unknown keyCode: 0x0")) {
				jToggleButton1.setText(String.format("<html><h3>%d</h3></html>", arg0.getKeyCode()));
			} else {
				jToggleButton1.setText(String.format("<html><h3>%s</h3></html>", KeyEvent.getKeyText(arg0.getKeyCode())));
			}
			jToggleButton1.setSelected(false);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if (arg0.getSource() == jButton3) {
			this.dispose();
		}
		
		if (arg0.getSource() == jButton2) {
			
			if (m_keyCode == null) {
				JOptionPane.showMessageDialog(m_window, "No key has been entered for this gizmo. Please enter a key.", "Key Connection Error", JOptionPane.ERROR_MESSAGE);
			} else {
				// no changes have been made, so do nothing.
				if (m_keyFound != null && m_keyCode == m_keyFound.getKey() && m_selectedComboIndex == jComboBox1.getSelectedIndex()) {
					;
				} else {
					List<Direction> directions = new ArrayList<Direction>();
					if (jComboBox1.getSelectedIndex() == 0) {
						directions.add(Direction.UP);
						directions.add(Direction.DOWN);
					} else if (jComboBox1.getSelectedIndex() == 1) {
						directions.add(Direction.DOWN);
					} else if (jComboBox1.getSelectedIndex() == 2) {
						directions.add(Direction.UP);
					}
				
					KeyConnection connection = null;
					List<KeyConnection> connections = m_board.getKeyConnections();
					for (KeyConnection key : connections) {
						if (key.getKey() == m_keyCode.intValue()) {
							connection = key;
							break;
						}
					}
				
					if (connection == null) {
						connection = new KeyConnection(m_keyCode);
						try {
							m_board.addKeyConnection(connection);
						} catch (Exception e) {
						;
						}
					} else {
						connection.clearActions(m_gizmo.getName());
					}
				
					if (m_keyFound != null) {
						m_keyFound.clearActions(m_gizmo.getName());
					}
				
					for (Direction d : directions) {
						try {
							connection.addAction(d, m_gizmo);
						} catch (Exception e) {
							JOptionPane.showMessageDialog(m_window, e.getMessage(), "Can't setup keyboard action", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				this.dispose();
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		int state = arg0.getStateChange();	
		m_selected = (state == ItemEvent.SELECTED);
	}
}
