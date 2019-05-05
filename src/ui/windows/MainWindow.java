package ui.windows;

import controller.BoardViewController;
import controller.MainWindowController;
import events.ControllerListener;
import events.NewGameActionListener;
import events.SaveMenuActionListener;
import events.LoadMenuActionListener;
import model.Mode;
import model.MusicClip;
import model.Score;
import view.BoardView;
import view.ToolButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

public class MainWindow extends JFrame {

    /********************************************************************/
    public String command = "";
    // 游戏画板
    private BoardView boardView;

    // 是否播放音乐
    private boolean musicPlaying = false;

    // 音乐队列
    private MusicClip build = new MusicClip("sims_build.wav", "Build/");
    private MusicClip play = new MusicClip("shepard.wav", "Play/");
    //跟踪当前播放歌曲
    private int buildSong = 0;
    private int playSong = 0;


    DefaultTableModel scoreModel;

    public void showScore(String s) {scoreField.setText(s);}
    public void showLives(String l) {livesField.setText(l);}

    public void fillScores() {
        scoreModel.setRowCount(0);

        String[] scores = { "", "" };
        ArrayList<Score> scoreList = boardView.getModel().getScores();
        for (int i=0;i<scoreList.size();i++) {
            scores[0] = scoreList.get(i).getName();
            scores[1] = Integer.toString(scoreList.get(i).getScore());
            scoreModel.addRow(scores);
        }
    }
    public void writeScores() {
        boardView.getModel().writeScores();
    }

    public void run() {
        ControllerListener eventListener = new ControllerListener(
                boardView.getModel());
        Timer timer = new Timer(40, eventListener);
        timer.start();
    }

    public BoardView getBoardView() {
        return boardView;
    }

    public void setCommand(String string) {
        command = string;
    }

    public MusicClip getBuildSequencer() {
        return build;
    }

    public MusicClip getPlaySequencer() {
        return play;
    }

    public int getBuildSong(){
        return buildSong;
    }

    public void setBuildSong(int i){
        buildSong = i;
    }

    public int getPlaySong(){
        return playSong;
    }

    public void setPlaySong(int i){
        playSong = i;
    }
    public boolean isMusicPlaying() {
        return musicPlaying;
    }

    public void setMusicPlaying(boolean playing) {
        musicPlaying = playing;
    }

    public JButton getPauseButton() {
        return pause_Play;
    }

    public JButton getBuildButton() {return buildbtn;}

    public JPanel getCardLayoutPanel() { return jPanel1; }

    public void setSoundItem(String temp){
        jMenuItem_Sound.setText(temp);
    }

	public MainWindow(String name) {
        super(name);

    //    	this.setName(name + " - " + newFileName);
        boardView = new BoardView();
        boardView.setFocusable(true);
        boardView.getModel().setMode(Mode.BUILD_MODE);
        BoardViewController controller = new BoardViewController(boardView);

        boardView.addKeyListener(controller);

        this.addMouseListener(new MainWindowController(this));
        this.pack();
        initComponents();
        animationPanel.add(boardView);

        System.out.println(animationPanel.getBounds());
        System.out.println("mainPanel's"+mainPanel.getBounds());
        this.setBounds(0,0,820,520);
        boardView.setBounds(0,0,
                animationPanel.getWidth(),animationPanel.getHeight());
        boardView.setScale();
        System.out.println(boardView.getBounds());
        showBuildMode();

	}



    /***********************************************************下****/
	private void initComponents() {

		mainPanel = new javax.swing.JPanel();
		animationPanel = new javax.swing.JPanel();
		jPanel_Mode = new javax.swing.JPanel();


        jPanel1 = new javax.swing.JPanel();
		jPanel_PlayMode = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		scoreField = new java.awt.TextField();
		livesField = new java.awt.TextField();
		jScrollPane2 = new javax.swing.JScrollPane();
		highScore = new javax.swing.JTable();
		jPanel_BuildMode = new javax.swing.JPanel();
		jPanel_GizmosControl = new javax.swing.JPanel();

		jPanel_Gizmos = new javax.swing.JPanel();

		jMenuBar = new javax.swing.JMenuBar();
		jMenu_Game = new javax.swing.JMenu();
		jMenuItem_New = new javax.swing.JMenuItem();
		jMenuItem_Open = new javax.swing.JMenuItem();
		jMenuItem_Save = new javax.swing.JMenuItem();
		jMenu_Set = new javax.swing.JMenu();
		jMenuItem_Sound = new javax.swing.JMenuItem();
		jMenuItem_Control = new javax.swing.JMenuItem();
		jMenu_Help = new javax.swing.JMenu();
		jMenuItem_Rule = new javax.swing.JMenuItem();
		jMenuItem_About = new javax.swing.JMenuItem();

        /*****************************
         *上
         */

        playbtn.setText(null);
        playbtn.addActionListener(new MainWindowController(this));

        buildbtn.setText(null);
        buildbtn.addActionListener(new MainWindowController(this));

        pause_Play.setText(null);
        pause_Play.addActionListener(new MainWindowController(this));

        jMenuItem_Sound.addActionListener(new MainWindowController(this));
        jMenuItem_New.addActionListener(new NewGameActionListener(this));
        jMenuItem_Open.addActionListener(new LoadMenuActionListener(this,getBoardView().getModel()));
        jMenuItem_Save.addActionListener(new SaveMenuActionListener(this,getBoardView().getModel()));
        ////////////////////////////////////////////////gizmo control///////////////////////////////////

        ToolButton move = new ToolButton("Move", "icons/move_icon.png");
        move.setText(null);
        move.addActionListener(new MainWindowController(this));

        ToolButton rotate = new ToolButton("Rotate", "icons/rotate_icon.png");
        rotate.setText(null);
        rotate.addActionListener(new MainWindowController(this));

        ToolButton delete = new ToolButton("Scale", "icons/scale_icon.png");
        delete.setText(null);
        delete.addActionListener(new MainWindowController(this));

        ToolButton connect = new ToolButton("Connect", "icons/connect_icon.png");
        connect.setText(null);
        connect.addActionListener(new MainWindowController(this));

        ToolButton disconnect = new ToolButton("Disconnect",
                "icons/disconnect_icon.png");
        disconnect.setText(null);
        disconnect.addActionListener(new MainWindowController(this));

        ToolButton keyConnect = new ToolButton("KeyConnect",
                "icons/key_connect_icon.png");
        keyConnect.setText(null);
        keyConnect.addActionListener(new MainWindowController(this));

        ToolButton keyDisconnect = new ToolButton("KeyDisconnect",
                "icons/key_disconnect_icon.png");
        keyDisconnect.setText(null);
        keyDisconnect.addActionListener(new MainWindowController(this));



        ///////////////////////////////////////////gizmos

        ToolButton leftFlipper = new ToolButton("Left Flipper", "icons/left_flipper_icon.png");
        leftFlipper.setText(null);
        leftFlipper.addActionListener(new MainWindowController(this));

        ToolButton rightFlipper = new ToolButton("Right Flipper", "icons/right_flipper_icon.png");
        rightFlipper.setText(null);
        rightFlipper.addActionListener(new MainWindowController(this));

        ToolButton circle = new ToolButton("Circle", "icons/circle_icon.png");
        circle.setText(null);
        circle.addActionListener(new MainWindowController(this));

        ToolButton triangle = new ToolButton("Triangle", "icons/triangle_icon.png");
        triangle.setText(null);
        triangle.addActionListener(new MainWindowController(this));

        ToolButton square = new ToolButton("Square", "icons/square_icon.png");
        square.setText(null);
        square.addActionListener(new MainWindowController(this));

        ToolButton absorber = new ToolButton("Absorber", "icons/absorber_icon.png");
        absorber.setText(null);
        absorber.addActionListener(new MainWindowController(this));

        ToolButton ball = new ToolButton("Ball", "icons/ball_icon.png");
        ball.setText(null);
        ball.addActionListener(new MainWindowController(this));

        ToolButton portal = new ToolButton("Portal", "icons/portal_icon.png");
        portal.setText(null);
        portal.addActionListener(new MainWindowController(this));

        ToolButton trapezoid = new ToolButton("Trapezoid", "icons/trapezoid_icon.png");
        trapezoid.setText(null);
        trapezoid.addActionListener(new MainWindowController(this));

        ToolButton block = new ToolButton("Block", "icons/block_icon.png");
        block.setText(null);
        block.addActionListener(new MainWindowController(this));

        //////////////////////////////////////

        scoreModel = new DefaultTableModel();
        scoreModel.addColumn("Name");
        scoreModel.addColumn("Score");
        fillScores();

        highScore.addMouseListener(new MainWindowController(this));

        /******************
         *下
         */

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        animationPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout animationPanelLayout = new javax.swing.GroupLayout(animationPanel);
        animationPanel.setLayout(animationPanelLayout);
        animationPanelLayout.setHorizontalGroup(
                animationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 507, Short.MAX_VALUE)
        );
        animationPanelLayout.setVerticalGroup(
                animationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 481, Short.MAX_VALUE)
        );

        jPanel_Mode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));


        javax.swing.GroupLayout jPanel_ModeLayout = new javax.swing.GroupLayout(jPanel_Mode);
        jPanel_Mode.setLayout(jPanel_ModeLayout);
        jPanel_ModeLayout.setHorizontalGroup(
                jPanel_ModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_ModeLayout.createSequentialGroup()
                                .addGap(54, 54, 54)
                                .addComponent(playbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buildbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(44, 44, 44))
        );
        jPanel_ModeLayout.setVerticalGroup(
                jPanel_ModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_ModeLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel_ModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(playbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buildbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(19, 19, 19))
        );

        jPanel1.setLayout(new java.awt.CardLayout());

        jLabel1.setText("Score");

        jLabel2.setText("Lives");

        scoreField.setText("textField1");

        livesField.setText("textField2");

    //    pause_Play.setText("jButton1");

        highScore.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null},
                        {null, null},
                        {null, null},
                        {null, null}
                },
                new String [] {
                        "姓名", "分数"
                }
        ) {
            boolean[] canEdit = new boolean [] {
                    false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(highScore);

        javax.swing.GroupLayout jPanel_PlayModeLayout = new javax.swing.GroupLayout(jPanel_PlayMode);
        jPanel_PlayMode.setLayout(jPanel_PlayModeLayout);
        jPanel_PlayModeLayout.setHorizontalGroup(
                jPanel_PlayModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_PlayModeLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel_PlayModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel1))
                                .addGap(21, 21, 21)
                                .addGroup(jPanel_PlayModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel_PlayModeLayout.createSequentialGroup()
                                                .addComponent(livesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(51, 51, 51)
                                                .addComponent(pause_Play, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(scoreField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(83, Short.MAX_VALUE))
                        .addGroup(jPanel_PlayModeLayout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addContainerGap())
        );
        jPanel_PlayModeLayout.setVerticalGroup(
                jPanel_PlayModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_PlayModeLayout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(jPanel_PlayModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(scoreField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(11, 11, 11)
                                .addGroup(jPanel_PlayModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel2)
                                        .addComponent(livesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(pause_Play))
                                .addGap(26, 26, 26)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel_PlayMode, "card1");

        jPanel_GizmosControl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));


        javax.swing.GroupLayout jPanel_GizmosControlLayout = new javax.swing.GroupLayout(jPanel_GizmosControl);
        jPanel_GizmosControl.setLayout(jPanel_GizmosControlLayout);
        jPanel_GizmosControlLayout.setHorizontalGroup(
                jPanel_GizmosControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_GizmosControlLayout.createSequentialGroup()
                                .addGroup(jPanel_GizmosControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel_GizmosControlLayout.createSequentialGroup()
                                                .addGap(24, 24, 24)
                                                .addComponent(move, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(rotate, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(26, 26, 26)
                                                .addComponent(delete, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(keyConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel_GizmosControlLayout.createSequentialGroup()
                                                .addGap(55, 55, 55)
                                                .addComponent(keyDisconnect, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(27, 27, 27)
                                                .addComponent(connect, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(disconnect, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        jPanel_GizmosControlLayout.setVerticalGroup(
                jPanel_GizmosControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_GizmosControlLayout.createSequentialGroup()
                                .addContainerGap(25, Short.MAX_VALUE)
                                .addGroup(jPanel_GizmosControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(move, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(rotate, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(delete, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(keyConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel_GizmosControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(keyDisconnect, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(connect, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(disconnect, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );

        jPanel_Gizmos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

       // circle.setText("Circle");

     //   triangle.setText("jButton4");

     //   square.setText("jButton5");

     //   trapezoid.setText("jButton1");

     //   block.setText("jButton6");
        block.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blockActionPerformed(evt);
            }
        });

     //   leftFlipper.setText("jButton7");

       // rightFlipper.setText("jButton8");

    //    ball.setText("jButton1");

    //    pathway.setText("jButton9");

     //   absorber.setText("jButton10");

//        portal.setText("jButton11");

        javax.swing.GroupLayout jPanel_GizmosLayout = new javax.swing.GroupLayout(jPanel_Gizmos);
        jPanel_Gizmos.setLayout(jPanel_GizmosLayout);
        jPanel_GizmosLayout.setHorizontalGroup(
                jPanel_GizmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_GizmosLayout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(jPanel_GizmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(block, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                        .addGroup(jPanel_GizmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(circle, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                               /* .addComponent(pathway, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)*/))
                                .addGap(28, 28, 28)
                                .addGroup(jPanel_GizmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(leftFlipper, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel_GizmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(triangle, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(absorber, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(29, 29, 29)
                                .addGroup(jPanel_GizmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(jPanel_GizmosLayout.createSequentialGroup()
                                                .addComponent(rightFlipper, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(ball, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel_GizmosLayout.createSequentialGroup()
                                                .addComponent(square, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(27, 27, 27)
                                                .addComponent(trapezoid, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(portal, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                                /*.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)*/)
        );
        jPanel_GizmosLayout.setVerticalGroup(
                jPanel_GizmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_GizmosLayout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(jPanel_GizmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(circle, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(triangle, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(square, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(trapezoid, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                                .addGroup(jPanel_GizmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(ball, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(rightFlipper, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(leftFlipper, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(block, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(36, 36, 36)
                                .addGroup(jPanel_GizmosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                     //   .addComponent(pathway, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(absorber, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(portal, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_BuildModeLayout = new javax.swing.GroupLayout(jPanel_BuildMode);
        jPanel_BuildMode.setLayout(jPanel_BuildModeLayout);
        jPanel_BuildModeLayout.setHorizontalGroup(
                jPanel_BuildModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_BuildModeLayout.createSequentialGroup()
                                .addGroup(jPanel_BuildModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel_GizmosControl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel_Gizmos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jPanel_BuildModeLayout.setVerticalGroup(
                jPanel_BuildModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_BuildModeLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel_Gizmos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel_GizmosControl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel_BuildMode, "card2");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(animationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGap(11, 11, 11)
                                                .addComponent(jPanel_Mode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addContainerGap())))
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(jPanel_Mode, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addComponent(animationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenu_Game.setText("Game");

        jMenuItem_New.setText("New");
        jMenuItem_New.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_NewActionPerformed(evt);
            }
        });
        jMenu_Game.add(jMenuItem_New);

        jMenuItem_Open.setText("Open");
        jMenuItem_Open.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jMenuItem_Open.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jMenuItem_Open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_OpenActionPerformed(evt);
            }
        });
        jMenu_Game.add(jMenuItem_Open);

        jMenuItem_Save.setText("Save");
        jMenuItem_Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_SaveActionPerformed(evt);
            }
        });
        jMenu_Game.add(jMenuItem_Save);

        jMenuBar.add(jMenu_Game);

        jMenu_Set.setText("Set");

        jMenuItem_Sound.setText("Sound Off");
        jMenuItem_Sound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_SoundActionPerformed(evt);
            }
        });
        jMenu_Set.add(jMenuItem_Sound);

        jMenuBar.add(jMenu_Set);

        jMenu_Help.setText("Help");

        jMenuItem_Rule.setText("Instructions");
        jMenuItem_Rule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_RuleActionPerformed(evt);
            }
        });
        jMenu_Help.add(jMenuItem_Rule);

        jMenuItem_About.setText("About");
        jMenuItem_About.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_AboutActionPerformed(evt);
            }
        });
        jMenu_Help.add(jMenuItem_About);

        jMenuBar.add(jMenu_Help);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        /***********************shang*******/


        ComponentListener comp = new ComponentListener() {
            public void componentResized(ComponentEvent e) {

                boardView.setScale();
            }

            public void componentHidden(ComponentEvent e) {
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentShown(ComponentEvent e) {
            }
        };
        highScore.setModel(scoreModel);
        boardView.addComponentListener(comp);
        /**********************xia*************/
        pack();

	}// </editor-fold>

	private void jMenuItem_SoundActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	private void jMenuItem_SaveActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	private void jMenuItem_OpenActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	private void blockActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	private void jMenuItem_NewActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	private void jMenuItem_ControlActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	private void jMenuItem_RuleActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
        JOptionPane
                .showMessageDialog(
                        this,
                        "Instructions: \n\n"
                                + "F1：Toggle debug mode on and off.\n\n"
                                + "F2: Manually cycle one frame at a time\n\n"
                                + "F3：Toggle wireframe mode on and off.\n\n"
                                + "F4：Show the ball direction\n\n"
                                + "F5: Show the ball count");
	}

	private void jMenuItem_AboutActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane
                .showMessageDialog(
                        this,
                        "Gizmoball - Group Project \n\n"
                                + "Group Members：\n\n"
                                + "haiyun xie\n\n"
                                + "bin xie");
	}


	public void showPlayMode(){
        CardLayout c = (CardLayout) jPanel1.getLayout();
        c.show(jPanel1, "card1");
        playbtn.setEnabled(false);
        buildbtn.setEnabled(true);
	}

	public void showBuildMode(){
        CardLayout c = (CardLayout) jPanel1.getLayout();
        c.show(jPanel1, "card2");
        playbtn.setEnabled(true);
        buildbtn.setEnabled(false);
	}


	// Variables declaration - do not modify
	private javax.swing.JButton absorber;
	private javax.swing.JPanel animationPanel;
	private javax.swing.JButton ball;
	private javax.swing.JButton block;
ToolButton buildbtn = new ToolButton("Build Mode", "icons/build_mode_icon.png");
	private javax.swing.JButton circle;
	private javax.swing.JButton connect;
	private javax.swing.JButton delete;
	private javax.swing.JButton disconnect;
	private javax.swing.JTable highScore;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JMenuBar jMenuBar;
	private javax.swing.JMenuItem jMenuItem_About;
	private javax.swing.JMenuItem jMenuItem_Control;
	private javax.swing.JMenuItem jMenuItem_New;
	private javax.swing.JMenuItem jMenuItem_Open;
	private javax.swing.JMenuItem jMenuItem_Rule;
	private javax.swing.JMenuItem jMenuItem_Save;
	private javax.swing.JMenuItem jMenuItem_Sound;
	private javax.swing.JMenu jMenu_Game;
	private javax.swing.JMenu jMenu_Help;
	private javax.swing.JMenu jMenu_Set;
	private javax.swing.JPanel jPanel_BuildMode;
	private javax.swing.JPanel jPanel_Gizmos;
	private javax.swing.JPanel jPanel_GizmosControl;
    private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel_Mode;
	private javax.swing.JPanel jPanel_PlayMode;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JButton keyConnect;
	private javax.swing.JButton keyDisconnect;
	private javax.swing.JButton leftFlipper;
	private java.awt.TextField livesField;
	private javax.swing.JPanel mainPanel;
ToolButton pause_Play = new ToolButton("Pause", "icons/pause_icon.png");
ToolButton playbtn = new ToolButton("Play Mode", "icons/play_mode_icon.png");
	private javax.swing.JButton portal;
	private javax.swing.JButton rightFlipper;
	private javax.swing.JButton rotate;
	private java.awt.TextField scoreField;
	private javax.swing.JButton square;
	private javax.swing.JButton trapezoid;
	private javax.swing.JButton triangle;
	// End of variables declaration
}
