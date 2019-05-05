package controller;

import controller.KeyConnection.Direction;
import model.*;
import ui.dialogs.KeyConnectionDialog;
import ui.dialogs.Sliders;
import ui.windows.MainWindow;
import utils.StringUtils;
import view.BoardView;
import view.ToolButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

public class MainWindowController implements ActionListener, MouseListener, Observer {

	private MainWindow window = null;

	private ImageIcon play = new ImageIcon("icons/play_icon.png");
	private ImageIcon pause = new ImageIcon("icons/pause_icon.png");
	private GizmoObject moveSelection = null;       //要被移动对象
	private GizmoObject connectionSelection = null;     //要被关联对象
	private GizmoObject disConnectionSelection = null;      //要被取消关联对象
	private MusicClip buildSeq;
	private MusicClip playSeq;
	private String[] buildModeSongs = { "sims_build.wav", "shuffle_or_boogie.wav" };
	private String[] playModeSongs = { "shepard.wav", "bubbleman.wav" };

	private JButton pauseButton;

	public MainWindowController(MainWindow g) {
		window = g;
		buildSeq = window.getBuildSequencer();
		playSeq = window.getPlaySequencer();
		pauseButton = window.getPauseButton();
		window.getBoardView().getModel().addObserver(this);  //把自己设为Board的观察者
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		window.getBoardView().grabFocus();
		String s = "";
		if (e.getSource() instanceof ToolButton) {
			s = ((ToolButton) e.getSource()).originalName();

            //”Play Mode“按钮、”Build Mode“按钮、pause_Play按钮”pause“和”play“两种name

			// 点击Gizmo_Control按钮
			if (s.equals("Move")) {
				window.setCommand("Move");
				System.out.println("Left-click on gizmo you wish to move");
			}

			if (s.equals("Rotate")) {
				window.setCommand("Rotate");
				System.out.println("Left-click on gizmo you wish to rotate.");
			}

			if (s.equals("Scale")) {
                window.setCommand("Scale");
                System.out.println("Left-click on gizmo you wish to scale.");
            }

			if (s.equals("Connect")) {
				window.setCommand("Connect");
				System.out.println("Left-click on gizmo you wish to be connected to another gizmo.");
			}

			if (s.equals("Disconnect")) {
				window.setCommand("Disconnect");
                System.out.println("Left-click on gizmo you wish to be disconnected from another gizmo.");
			}

			if (s.equals("KeyConnect")) {
				window.setCommand("KeyConnect");
                System.out.println("Select a gizmo to setup a key connection on.");
			}

			if (s.equals("KeyDisconnect")) {
				window.setCommand("KeyDisconnect");
                System.out.println("Select a gizmo to remove a key connection from.");
			}

			// Adding Gizmo buttons
			if (s.equals("Circle")) {
				window.setCommand("Circle");
                System.out.println("Left-click on grid to add circle bumper.");
			}

			if (s.equals("Triangle")) {
				window.setCommand("Triangle");
                System.out.println("Left-click on grid to add triangle bumper.");
			}

			if (s.equals("Square")) {
				window.setCommand("Square");
                System.out.println("Left-click on grid to add square bumper.");
			}

			if (s.equals("Trapezoid")) {               //选择的是Trapezoid
                window.setCommand("Trapezoid");
                System.out.println("Left-click on grid to add trapezoid bumper.");
            }

            if (s.equals("Block")) {
                window.setCommand("Block");
            }

			if (s.equals("Absorber")) {
				window.setCommand("Absorber");
                System.out.println("Left-click on grid to add absorber.");
			}

			if (s.equals("Left Flipper")) {
				window.setCommand("Left Flipper");
                System.out.println("Left-click on grid to add a left flipper.");
			}

			if (s.equals("Right Flipper")) {
				window.setCommand("Right Flipper");
                System.out.println("Left-click on grid to add a right flipper.");
			}

			if (s.equals("Ball")) {
				window.setCommand("Ball");
                System.out.println("Left-click on grid to add ball.");
			}
			if (s.equals("Portal")) {
				window.setCommand("Portal");
                System.out.println("Left-click on grid to add portal.");
			}



//”Play Mode“按钮、”Build Mode“按钮、pause_Play按钮”Pause“和”Play“两种name
            if (s.equals("Pause")            //Play Mode下按下了暂停或继续按钮
                    || s.equals("Play")) {
                Mode m = window.getBoardView().getModel().getMode();
                if (m.equals(Mode.PLAY_MODE_RUNNING)) {        //如果此时状态为正在运行，则暂停
                    window.getBoardView().getModel()
                            .setMode(Mode.PLAY_MODE_NOT_RUNNING);
                    ((ToolButton) e.getSource()).setIcon(play);
                    ((ToolButton) e.getSource()).setOriginalName("Play");
                    System.out.println("Play Mode - Paused!");
                } else {                                       //如果此时状态为暂停，则继续
                    window.getBoardView().getModel()
                            .setMode(Mode.PLAY_MODE_RUNNING);
                    ((ToolButton) e.getSource()).setIcon(pause);
                    ((ToolButton) e.getSource()).setOriginalName("Pause");
                    System.out.println("Play Mode - Resumed!");
                }
            }

            if (s.equals("Build Mode")) {        //按下Build Mode切换为build模式

                window.showBuildMode();        //切换为Build Mode卡片

                window.getBoardView().getModel().setMode(Mode.BUILD_MODE);  //将Board的模式设为Build_Mode

                window.getBoardView().getModel().resetGame();    // 重置棋盘

                window.getBoardView().getModel().refresh();    //告知观察者发生了变化

                System.out.println("System Status:Build Mode!");

                playSeq.stop();         //play模式的音乐停止

                if (window.isMusicPlaying()) {      //如果音乐开着就播放build模式下的音乐
                    buildSeq.playMusic();
                }
            }

            if (s.equals("Play Mode")) {       //按下Play Mode按钮，切换为Play模式

                System.out.println("play playing? - " + playSeq.getClip().isRunning());
                System.out.println("build playing? - " + buildSeq.getClip().isRunning());
                window.showPlayMode();        //切换为Play Mode下的卡片

                window.getBoardView().getModel()          //将Board设置为Play_Mode_Running
                        .setMode(Mode.PLAY_MODE_RUNNING);

                window.getBoardView().draw();

                System.out.println("Play Mode!");

                buildSeq.stop();

                System.out.println("play playing? - " + playSeq.getClip().isRunning());
                System.out.println("build playing? - " + buildSeq.getClip().isRunning());

                if (window.isMusicPlaying()) {
                    playSeq.playMusic();
                }
            }
		}else{     //一些菜单项监听事件
            if (e.getActionCommand().equals("Sound On")) {       //声音开就关
                window.setMusicPlaying(false);
                window.setSoundItem("Sound Off");
                playSeq.stop();
                buildSeq.stop();
            }else if(e.getActionCommand().equals("Sound Off")){   //声音关就开
                window.setMusicPlaying(true);
                window.setSoundItem("Sound On");
                if (window.getBoardView().getModel().getMode()
                        .equals(Mode.BUILD_MODE)) {
                    buildSeq.playMusic();
                } else {
                    playSeq.playMusic();
                }
            }

		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {       //松开鼠标后添加响应组件
		if (arg0.getSource() instanceof JTable) {

		} else {

		}
		BoardView m_boardView = window.getBoardView();
		if (m_boardView.getModel().getMode().equals(Mode.BUILD_MODE)) {  //如果Board处于Build模式

			Point p = m_boardView.getMousePosition();      //获取点击位置
			if (p != null) {
			    int x = p.x;
				int y = p.y;

				System.out.println("Mouse at (" + arg0.getX() + "," + arg0.getY() + ").");

				// 被点击位置逻辑坐标
				int gridX = 0;
				int gridY = 0;

				gridX = (int) Math.floor(m_boardView.toLogicalX(x));
				gridY = (int) Math.floor(m_boardView.toLogicalY(y));

                if (arg0.getButton() == MouseEvent.BUTTON3) {     //如果按下的是右键,则删除该组件

                    try {
                        GizmoObject deletionSelection = m_boardView.getModel().getGizmoByPosition(gridX, gridY);
                        if (deletionSelection == null) {
                            System.out.println("Your selection contains no gizmo to delete");
                        } else {
                            m_boardView.getModel().removeGizmo(deletionSelection.getName());
                            m_boardView.getModel().refresh();
                        }
                    } catch (Exception e1) {
                        System.out.println("There is nothing in that square!");
                    }
                }
                else {
                    if (window.command.equals("Move")) {              //选择被移动的对象位置
                        moveSelection = m_boardView.getModel()         //获得要移动点Gizmo对象
                                .getGizmoByPosition(gridX, gridY);
                        if (moveSelection == null) {                   //如果被选中对象为空
                            System.out.println("The square you selected has nothing in it, make another selection.");
                        } else {                               //选中该点再选择要移动的位置
                            System.out.println("Now click on place you would like to move to.");
                            window.setCommand("Move Location");
                        }
                    } else if (window.command.equals("Move Location")) {    //已经选择了被移动对象，现在是点击了新的位置
                        try {
                            System.out.println(moveSelection.getName());

                            if (moveSelection.getX() == gridX      //如果点击位置与原位置重叠
                                    && moveSelection.getY() == gridY) {
                                JOptionPane
                                        .showMessageDialog(
                                                window,
                                                "Cannot move as the gizmo already exists at selected position. Please select a different position for the gizmo to move to.",
                                                "Move Gizmo Error...",
                                                JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            m_boardView.getModel().moveGizmo(       //移动该Gizmo到新位置
                                    moveSelection.getName(), gridX, gridY);
                            System.out.println("Moved to (" + gridX + "," + gridY + "). Select another gizmo to move.");
                            m_boardView.getModel().refresh();      //刷新画板
                            window.setCommand("Move");    //处于选择被移动对象状态
                            System.out
                                    .println(m_boardView.getModel()
                                            .getGizmoByPosition(gridX, gridY)
                                            .getName());
                        } catch (NullPointerException e) {
                            moveSelection = null;
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(window, e.getMessage());
                            moveSelection = null;
                            System.out.println(("There is something already there - Choose an empty square."));
                        }
                    }

                    if (window.command.equals("KeyConnect")) {    //选择添加热键对象状态
                        GizmoObject selectedGizmo = m_boardView.getModel().getGizmoByPosition(gridX, gridY);

                        if (selectedGizmo == null) {
                            JOptionPane.showMessageDialog(
                                            window,
                                            "Cannot setup key connection. A gizmo does not exist at the position selected.",
                                            "Key Connection Error...",
                                            JOptionPane.ERROR_MESSAGE);
                        } else {
                            KeyConnectionDialog keyDialog = new KeyConnectionDialog(
                                    window, m_boardView.getModel(), selectedGizmo);
                            keyDialog.setVisible(true);
                        }
                    }

                    if (window.command.equals("KeyDisconnect")) {     //选择取消热键对象状态
                        GizmoObject selectedGizmo = m_boardView.getModel().getGizmoByPosition(gridX, gridY);

                        if (selectedGizmo == null) {
                            JOptionPane
                                    .showMessageDialog(
                                            window,
                                            "Cannot remove key connection. A gizmo does not exist at the position selected.",
                                            "Key Connection Error...",
                                            JOptionPane.ERROR_MESSAGE);
                        } else {
                            boolean foundKey = false;
                            for (KeyConnection key : m_boardView.getModel()
                                    .getKeyConnections()) {
                                for (Entry<Direction, List<BoardAction>> entry : key
                                        .getActions().entrySet()) {
                                    List<BoardAction> actions = entry
                                            .getValue();
                                    for (BoardAction action : actions) {
                                        if (action.getName().equals(
                                                selectedGizmo.getName())) {
                                            key.clearActions(selectedGizmo
                                                    .getName());
                                            JOptionPane
                                                    .showMessageDialog(
                                                            window,
                                                            "Removed key connection from selected gizmo. The key '"
                                                                    + KeyEvent
                                                                    .getKeyText(key
                                                                            .getKey())
                                                                    + "' no longer activates this gizmo.",
                                                            "Key Disconnect...",
                                                            JOptionPane.ERROR_MESSAGE);
                                            foundKey = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!foundKey) {
                                JOptionPane
                                        .showMessageDialog(
                                                window,
                                                "No key has been set for the selected gizmo.",
                                                "Key Disconnect...",
                                                JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }

                    if (window.command.equals("Rotate")) {    //选择要旋转对象状态
                        GizmoObject rotateSelection = m_boardView.getModel().getGizmoByPosition(gridX, gridY);
                        if (rotateSelection == null) {
                            System.out.println("Your selection contains no gizmos! Try again!");
                        } else {
                            try {
                                m_boardView.getModel().rotateGizmo(rotateSelection.getName());
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(window, e.getMessage());
                            }
                            m_boardView.getModel().refresh();
                            System.out.println("The gizmo has been rotated. Left-click another gizmo to perform another rotation.");
                        }
                    }
                       //因为改变了他的功能，但名字没改，所以会有点歧义=_=
                    if (window.command.equals("Scale")) {       //选择要改变大小的组件
                        //改变所选gizmo大小
                        GizmoObject resizeSelection = m_boardView.getModel().getGizmoByPosition(gridX, gridY);
                        if (resizeSelection == null) {                   //如果被选中对象为空
                            System.out.println("The square you selected has nothing in it, make another selection.");
                        } else {                               //尝试改变其大小
                            Sliders s = new Sliders(window, true);   //生成选择大小对话框
                            s.setVisible(true);
                            Point slide = s.getValue();
                           try {
                            String name = resizeSelection.getName();
                               if (resizeSelection instanceof ActiveBall) {
                                   m_boardView.getModel().removeGizmo(name);
                                   m_boardView.getModel().addGizmo( new ActiveBall(),
                                           gridX, gridY, slide.x, slide.y, name);
                               }else if (resizeSelection instanceof CircleBumper) {
                                   m_boardView.getModel().removeGizmo(name);
                                   m_boardView.getModel().addGizmo( new CircleBumper(),
                                           gridX, gridY, slide.x, slide.y, name);
                               }else if (resizeSelection instanceof TriangleBumper) {
                                   m_boardView.getModel().removeGizmo(name);
                                   m_boardView.getModel().addGizmo( new TriangleBumper(),
                                           gridX, gridY, slide.x, slide.y, name);
                               }else if (resizeSelection instanceof SquareBumper) {
                                   m_boardView.getModel().removeGizmo(name);
                                   m_boardView.getModel().addGizmo( new SquareBumper(),
                                           gridX, gridY, slide.x, slide.y, name);
                               }else if (resizeSelection instanceof TrapezoidBumper) {
                                   m_boardView.getModel().removeGizmo(name);
                                   m_boardView.getModel().addGizmo( new TrapezoidBumper(),
                                           gridX, gridY, slide.x, slide.y, name);
                               }else if (resizeSelection instanceof LeftFlipper) {
                                   m_boardView.getModel().removeGizmo(name);
                                   m_boardView.getModel().addGizmo( new LeftFlipper(),
                                           gridX, gridY, slide.x, slide.y, name);
                               }else if (resizeSelection instanceof Absorber) {
                                   m_boardView.getModel().removeGizmo(name);
                                   m_boardView.getModel().addGizmo( new Absorber(),
                                           gridX, gridY, slide.x, slide.y, name);
                               }else if (resizeSelection instanceof Portal) {
                                   m_boardView.getModel().removeGizmo(name);
                                   m_boardView.getModel().addGizmo( new Portal(),
                                           gridX, gridY, slide.x, slide.y, name);
                               }

                            m_boardView.getModel().refresh();
                           } catch (NullPointerException e) {}
                             catch (Exception e) {         //如果和其他位置有冲突，提示
                            JOptionPane.showMessageDialog(window, e.getMessage());
                            System.out.println(e);
                           }
                        }

                    }

                    if (window.command.equals("Connect")) {     //选择被关联对象状态
                        connectionSelection = m_boardView.getModel().getGizmoByPosition(gridX, gridY);
                        if (connectionSelection != null) {
                            System.out.println("Now select the gizmo it should be connected to...");
                            window.setCommand("Connect To...");
                            m_boardView.getModel().refresh();
                        } else {
                            System.out.println("Your selection contains no gizmo, try another square.");
                        }

                    } else if (window.command.equals("Connect To...")) {  //选择关联对象状态
                        GizmoObject connectionSelection2 = m_boardView
                                .getModel().getGizmoByPosition(gridX, gridY);
                        if (connectionSelection2 != null) {
                            window.setCommand("Connect");
                            String msg;
                            try {
                                m_boardView.getModel().connectGizmo(
                                        connectionSelection.getName(),
                                        connectionSelection2.getName());
                                msg = connectionSelection.getName()
                                        + " has now been connected to "
                                        + connectionSelection2.getName()
                                        + ". Add another connection.";
                            } catch (Exception e) {
                                msg = e.getMessage();
                            }

                            window.setCommand("Connect");
                            System.out.println(msg);
                            connectionSelection = null;
                            connectionSelection2 = null;
                        } else {
                            System.out.println("There is no gizmo in the grid. Select a different square or right click to cancel the connect function");
                        }
                        m_boardView.getModel().refresh();
                    }

                    if (window.command.equals("Disconnect")) {  //选择被取消关联对象状态
                        disConnectionSelection = m_boardView.getModel()
                                .getGizmoByPosition(gridX, gridY);

                        if (disConnectionSelection == null) {
                            System.out.println("No gizmo at the selected position. Please select a gizmo at a valid position.");
                        } else {
                            System.out.println("Now select the gizmo it should be connected to...");
                            window.setCommand("Disconnect From...");
                            m_boardView.getModel().refresh();
                        }

                    } else if (window.command.equals("Disconnect From...")) {  //选择取消关联对象状态
                        GizmoObject disConnectionSelection2 = m_boardView
                                .getModel().getGizmoByPosition(gridX, gridY);
                        String msg;

                        if (disConnectionSelection2 == null) {
                            msg = "No gizmo to disconnect from at the selected position. Please select a gizmo at a valid position.";
                        } else {
                            try {
                                m_boardView.getModel().disconnectGizmo(
                                        disConnectionSelection.getName(),
                                        disConnectionSelection2.getName());
                                msg = disConnectionSelection.getName()
                                        + " has now been disconnected from "
                                        + disConnectionSelection2.getName()
                                        + ". Remove another connection.";

                            } catch (Exception e) {
                                msg = e.getMessage();
                            }
                            window.setCommand("Disconnect");
                        }

                        System.out.println(msg);
                        disConnectionSelection = null;
                        disConnectionSelection2 = null;

                        m_boardView.getModel().refresh();
                    }

                    if (window.command.equals("Circle")) {     //选择添加球形位置
                        try {
                            m_boardView.getModel().addGizmo(new CircleBumper(),
                                    gridX, gridY,
                                    StringUtils.generateUniqueName());
                            m_boardView.getModel().refresh();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(window, e.getMessage());
                            System.out.println(e);
                        }
                    }

                    if (window.command.equals("Triangle")) {    //选择添加三角形位置
                        try {
                            m_boardView.getModel().addGizmo(
                                    new TriangleBumper(), gridX, gridY,
                                    StringUtils.generateUniqueName());
                            m_boardView.getModel().refresh();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(window, e.getMessage());
                            System.out.println(e);
                        }
                    }

                    if (window.command.equals("Square")) {    //选择添加方形位置
                        try {
                            m_boardView.getModel().addGizmo(new SquareBumper(),
                                    gridX, gridY,
                                    StringUtils.generateUniqueName());
                            m_boardView.getModel().refresh();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(window, e.getMessage());
                            System.out.println(e);
                        }
                    }

                    if (window.command.equals("Trapezoid")) {    //选择添加梯形位置
                        try {
                            m_boardView.getModel().addGizmo(new TrapezoidBumper(),
                                    gridX, gridY,
                                    StringUtils.generateUniqueName());
                            m_boardView.getModel().refresh();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(window, e.getMessage());
                            System.out.println(e);
                        }
                    }


                    if (window.command.equals("Block")) {          //选择添加滑块位置
                        try {
                            m_boardView.getModel().addGizmo(new Block(),
                                    gridX, gridY,
                                    StringUtils.generateUniqueName());
                            m_boardView.getModel().refresh();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(window, e.getMessage());
                            System.out.println(e);
                        }
                    }

                    if (window.command.equals("Absorber")) {      //选择添加吸收器位置
                        Sliders s = new Sliders(window, true);   //生成选择大小对话框
                        s.setVisible(true);
                        Point slide = s.getValue();
                        try {
                            String name = StringUtils.generateUniqueName();  //为其生成一个唯一的名字
                            m_boardView.getModel().addGizmo(new Absorber(),
                                    gridX, gridY, slide.x, slide.y, name);
                            m_boardView.getModel().refresh();
                        } catch (NullPointerException e) {
                        } catch (Exception e) {         //如果和其他位置有冲突，提示
                            JOptionPane.showMessageDialog(window, e.getMessage());
                            System.out.println(e);
                        }
                    }

                    if (window.command.equals("Left Flipper")) {   //选择放置左旋转挡板位置
                        try {
                            m_boardView.getModel().addGizmo(new LeftFlipper(),
                                    gridX, gridY,
                                    StringUtils.generateUniqueName());
                            m_boardView.getModel().refresh();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(window, e.getMessage());
                            System.out.println(e);
                        }
                    }

                    if (window.command.equals("Right Flipper")) {   //选择放置右旋转挡板位置
                        try {
                            m_boardView.getModel().addGizmo(new RightFlipper(),
                                    gridX, gridY,
                                    StringUtils.generateUniqueName());
                            m_boardView.getModel().refresh();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(window, e.getMessage());
                            System.out.println(e);
                        }
                    }

                    if (window.command.equals("Ball")) {    //选择放置小球位置
                        try {
                            m_boardView.getModel().addGizmo(new ActiveBall(),
                                    gridX, gridY,
                                    StringUtils.generateUniqueName());
                            m_boardView.getModel().refresh();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(window, e.getMessage());
                            System.out.println(e);
                        }
                    }

                    if (window.command.equals("Portal")) {    //选择放置传送门位置
                        try {
                            m_boardView.getModel().addGizmo(new Portal(),
                                    gridX, gridY,
                                    StringUtils.generateUniqueName());
                            m_boardView.getModel().refresh();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(window, e.getMessage());
                            System.out.println(e);
                        }
                    }
                }
				System.out.println("Grid co ords are (" + gridX + "," + gridY + ").");
			}
		}
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
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void update(Observable o, Object arg) {  //作为观察者，实现的函数
		// 当Board发出变化信号时
		// 更新score和lives
		int score = window.getBoardView().getModel().getScore();
		int lives = window.getBoardView().getModel().getLives();

		if (lives == 0) {
			window.showBuildMode();

			window.getBoardView().getModel().setMode(Mode.BUILD_MODE);

			// 重置棋盘
			window.getBoardView().getModel().resetGame();

			window.getBoardView().getModel().refresh();

			System.out.println("Build Mode!");

			playSeq.stop();

			if (window.isMusicPlaying()) {
				buildSeq.playMusic();
			}

			// 判断是否为高分
			boolean high = window.getBoardView().getModel().highscore(score);

			if (high) {

				String name = "";
				boolean con = true;

				while (con) {
					name = (String) JOptionPane
							.showInputDialog(
									window,
									"Enter your name:\n"
											+ "WARNING: Tabs are not allowed. Do not leave empty.",
									"High Score!", JOptionPane.PLAIN_MESSAGE,
									null, null, "Player A");

					if (name == null)
						con = false;
					else if (name.equals("") || name.contains("\t"))
						con = true;
					else
						con = false;
				}

				if (name != null) {
					window.getBoardView().getModel().addHigh(name, score);
					window.fillScores();
					window.writeScores();
				}
			}
			// 返回Build Mode模式，并提示命已用尽
			JOptionPane.showMessageDialog(window,
					"You have run out of lives. Returning to build mode.\n"
							+ "Your score: " + score + "\n", "Balls depleted!",
					JOptionPane.WARNING_MESSAGE);

		}

		window.showScore(Integer.toString(score));
		window.showLives(Integer.toString(lives));
	}
}