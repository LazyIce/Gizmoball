package parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import controller.BoardAction;
import controller.KeyConnection;
import controller.KeyConnection.Direction;

import model.*;

import sun.awt.AWTAccessor;
import utils.StringUtils;

/** 解析器*/
public class GizmoParser
{	
	/* board模型 */
	private Board m_board;
	
	/* Map的元素对<组件，组件的操作> */
	private Map<String, Command> m_commands;

	private Board.Format m_format;
	
	public GizmoParser (Board board)
	{
		m_board = board;
		
		m_format = Board.Format.STANDARD;
		
		init();
	}
	
	private void init()
	{
		m_commands = new HashMap<String, Command>();
		
		/* Gizmo游戏文件中的组件命令格式*/
		m_commands.put("Square", new GizmoCommand());
		m_commands.put("Circle", new GizmoCommand());
		m_commands.put("Triangle", new GizmoCommand());
		m_commands.put("Trapezoid", new GizmoCommand());
		m_commands.put("RightFlipper", new GizmoCommand());
		m_commands.put("LeftFlipper", new GizmoCommand());
		m_commands.put("Block",new GizmoCommand());
		m_commands.put("Portal", new GizmoCommand());
		m_commands.put("Absorber", new AbsorberCommand());
		m_commands.put("Ball", new BallCommand());
		m_commands.put("Rotate", new RotateCommand());
		m_commands.put("Scale", new ScaleCommand());
		m_commands.put("Move", new MoveCommand());
		m_commands.put("Connect", new ConnectCommand());
		m_commands.put("KeyConnect", new KeyConnectCommand());
		m_commands.put("Gravity", new GravityCommand());
		m_commands.put("Friction", new FrictionCommand());
	}
	
	public void load (File file, Board.Format format) throws FileNotFoundException, IOException, NullPointerException, Exception
	{
		if (file == null)
			throw new NullPointerException("The specified file object was not initialised. Please initialise the object.");
		
		m_format = format;

		if (!file.exists())
			throw new IOException(String.format("The file %s does not exist. Please specify a valid file.", file.getName()));
		
		LineNumberReader br = new LineNumberReader(new InputStreamReader(new FileInputStream(file)));
		
		parse(br, file.getPath());
	}

	/*解析加载进来的文件流 */
	private void parse (LineNumberReader br, String fileName) throws IOException
	{
		boolean containsText = false;

		//判断读取的行是否为空行
		String line = null;
		while ( (line = br.readLine()) != null ) {  //读取一行
			if (line.trim().equals(""))   //去掉字符收尾空格
				continue;
			else
				containsText = true;
			
			int lineNo = br.getLineNumber();   //获取行号

			String[] list = line.split("\\s+");	//以空格为分隔符分割字符串
			String cmd = list[0];

			//判断是否对应解析格式
			if (m_commands.containsKey(cmd)) {
				if (list.length == 1)
				    System.out.println("There is a error!");
				else
					m_commands.get(cmd).handle(cmd, Arrays.copyOfRange(list, 1, list.length), lineNo);
			} else
                System.out.println("There is a error!");

		}
		br.close();

		if (!containsText) {
			throw new IOException(String.format("The file %s contains no text. Please include text which follows the gizmoball file format.", fileName));
		}
	}

	//保存文件
	public void save (File file, Board.Format format) throws IOException, NullPointerException, Exception
	{
		if (file == null) {
			throw new NullPointerException ("The file passed to GizmoParser.save() was not initialised. Please initialise the object.");
		}
		
		if (format.equals(Board.Format.STANDARD) && m_board.getFormat().equals(Board.Format.EXTENDED)) {
			throw new Exception(String.format("Cannot save file %s as the board contains gizmos which are not supported in %s format.", file.getName(), format.toString().toLowerCase()));
		} else {
			m_format = format;
		}
		
		if (format.equals(Board.Format.STANDARD)) {
			if (!file.getPath().endsWith(".gizmos")) {
				file = new File(file.getPath() + ".gizmos");
			}
		} else {
			if (!file.getPath().endsWith(".egizmos")) {
				file = new File(file.getPath() + ".egizmos");
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		/* 写入Gizmo组件的定义 */
		for (GizmoObject gizmo : m_board.getGizmos()) {
			/* 边界碰撞器不写入文件 */
			if (gizmo instanceof BorderBumper)
				continue;
			
			/* 基本模式中传送门也不写入文件 */
			if (gizmo instanceof Portal && m_format.equals(Board.Format.STANDARD))
				continue;
			if (gizmo instanceof ActiveBall) {
				bw.write(gizmo.getType() + " " + gizmo.getName() + " " + gizmo.getX() + " " + gizmo.getY() + " " + gizmo.getVelocity().x() + " " + gizmo.getVelocity().y() + "\n");
			} else if (gizmo instanceof Absorber) {
				bw.write(gizmo.getType() + " " + gizmo.getName() + " " + gizmo.getX() + " " + gizmo.getY() + " " + (gizmo.getL()+gizmo.getX()) + " " + (gizmo.getH()+gizmo.getY()) + "\n");
			} else {
				bw.write(gizmo.getType() + " " + gizmo.getName() + " " + gizmo.getX() + " " + gizmo.getY() + "\n");	
			}
		}
		//每个Gizmo组件信息写一行
		bw.write("\n");
		
		for (GizmoObject gizmo : m_board.getGizmos()) {
			/* 写入Gizmo组件间的关联信息 */
			List<GizmoObject> connectedGizmos = gizmo.getConnectedGizmos();
			for (GizmoObject g : connectedGizmos) {
				bw.write("Connect " + gizmo.getName() + " " + g.getName() + "\n");
			}
			
			/* 写入Gizmo组件处于哪个旋转状态 */
			for (int i = 0; i < gizmo.getRotateCount(); i++) {
				bw.write("Rotate " + gizmo.getName() + "\n\n");
			}
		}
		
		bw.write("\n");
		
		/* 写入Gizmo组件关联的按键操作 */
		for (KeyConnection key : m_board.getKeyConnections()) {
			for (Entry<Direction, List<BoardAction>> entry : key.getActions().entrySet()) {
				for (BoardAction b : entry.getValue()) {
					bw.write("KeyConnect key " + key.getKey() + " " + entry.getKey().toString().toLowerCase() + " " + b.getName() + "\n");
				}
			}
			bw.write("\n");
		}
		bw.close();
	}
	
	private abstract class Command
	{
		/* 处理读入的指令 */
		public abstract void handle (String cmd, String[] args, int lineNo);
	}
	
	/* <gizmoOp> <name> <int-pair>
	 * <gizmoOp> ::= Square | Circle | Triangle | Trapezoid |RightFlipper | LeftFlipper
	 */
	private class GizmoCommand extends Command
	{		
		@Override
		public void handle (String cmd, String[] args, int lineNo)
		{
			if (args.length < 3 || args.length > 3) {
                System.out.println("There is a error!");
				return;
			}
			
			String name = null;
			Integer x = null;
			Integer y = null;
			
			if (StringUtils.isValidIdentifier(args[0]))
				name = args[0];
			else
                System.out.println("There is a error!");
			
			if ( (x = StringUtils.isInteger(args[1])) == null )
                System.out.println("There is a error!");
			
			if ( (y = StringUtils.isInteger(args[2])) == null )
                System.out.println("There is a error!");
			
			if (x != null && y != null && name != null) {
				try {
					if (cmd.equals("Square"))       { m_board.addGizmo(new SquareBumper(), x, y, name);   return; }
					if (cmd.equals("Circle"))       { m_board.addGizmo(new CircleBumper(), x, y, name);   return; }
					if (cmd.equals("Triangle"))     { m_board.addGizmo(new TriangleBumper(), x, y, name); return; }
					if (cmd.equals("Trapezoid")) {m_board.addGizmo(new TrapezoidBumper(),x,y,name);return;}
					if (cmd.equals("RightFlipper")) { m_board.addGizmo(new RightFlipper(), x, y, name);   return; }
					if (cmd.equals("LeftFlipper"))	{ m_board.addGizmo(new LeftFlipper(), x, y, name);    return; }
					if (cmd.equals("Block")) {m_board.addGizmo(new Block(), x, y, name); return;}

					if (cmd.equals("Portal")) {
						if (m_format.equals(Board.Format.STANDARD)) {
                            System.out.println("There is a error!");
							return;
						} else {
							m_board.addGizmo(new Portal(), x, y, name);
							return;
						}
					}
				} catch (Exception e) {
                    System.out.println("There is a error!");
				}
			}
		}
	}
	
	/*
	 * Absorber <name> <int-pair> <int-pair>
	 * <int-pair> ::= INTEGER INTEGER
	 */
	private class AbsorberCommand extends Command
	{
		@Override
		public void handle(String cmd, String[] args, int lineNo)
		{
			if (args.length < 5 || args.length > 5) {
                System.out.println("There is a error!");
				return;
			}
			
			String name = null;
			Integer x1 = null;
			Integer x2 = null;
			Integer y1 = null;
			Integer y2 = null;
			
			if (StringUtils.isValidIdentifier(args[0]))
				name = args[0];
			else
                System.out.println("There is a error!");
			
			if ( (x1 = StringUtils.isInteger(args[1])) == null)
				System.out.println("There is a error!");

			if ( (y1 = StringUtils.isInteger(args[2])) == null)
                System.out.println("There is a error!");
			
			if ( (x2 = StringUtils.isInteger(args[3])) == null)
                System.out.println("There is a error!");
			
			if ( (y2 = StringUtils.isInteger(args[4])) == null)
                System.out.println("There is a error!");
			
			if (x1 != null && x2 != null && y1 != null && y2 != null)
				try {
					m_board.addAbsorber(new Absorber(), x1.intValue(), y1.intValue(), x2.intValue(), y2.intValue(), name);
				} catch (Exception e) {
                    System.out.println("There is a error!");
				}
		}
	}


	/*
	 * Move <name> <number-pair>
	 * <number-pair> ::= <int-pair> | <float-pair>
	 * <int-pair> ::= INTEGER INTEGER
	 * <float-pair> ::= FLOAT FLOAT
	 */
	private class MoveCommand extends Command
	{	
		@Override
		public void handle(String cmd, String[] args, int lineNo)
		{
			if (args.length < 3 || args.length > 3) {
				System.out.println("There is a error!");
				return;
			}
			
			String name = null;
			Number x = null;
			Number y = null;
			
			if (StringUtils.isValidIdentifier(args[0]))
				name = args[0];
			else
                System.out.println("There is a error!");

			if ( ((x = StringUtils.isInteger(args[1])) == null) && ((y = StringUtils.isInteger(args[2])) == null) ) {
				if ( ((x = StringUtils.isFloat(args[1])) == null) && ((y = StringUtils.isFloat(args[2])) == null) )
                    System.out.println("There is a error!");
				else {
					try {
						m_board.moveGizmo(name, x.doubleValue(), y.doubleValue());
					} catch (Exception e) {
                        System.out.println("There is a error!");
					}
				}
			} else {
				try {
					m_board.moveGizmo(name, x.intValue(), y.intValue());
				} catch (Exception e) {
                    System.out.println("There is a error!");
				}
			}
		}
	}
	
	/*
	 * Connect <name> <name>
	 * <name> ::= IDENTIFIER
	 */
	private class ConnectCommand extends Command
	{
		@Override
		public void handle(String cmd, String[] args, int lineNo)
		{	
			if (args.length < 2 || args.length > 2) {
				System.out.println("There is a error!");
				return;
			}
			
			String gizmo1 = null;
			String gizmo2 = null;
			
			if (StringUtils.isValidIdentifier(args[0]))
				gizmo1 = args[0];
			else
                System.out.println("There is a error!");

			if (StringUtils.isValidIdentifier(args[1]))
				gizmo2 = args[1];
			else
                System.out.println("There is a error!");
			
			if (gizmo1 != null && gizmo2 != null) {
				try {
					m_board.connectGizmo(gizmo1, gizmo2);
				} catch (Exception e) {
                    System.out.println("There is a error!");
				}
			}
		}
	}
	
	/*
	 * KeyConnect <keyid> <name>
	 * <keyid> ::= "key" KEYNUM "down" |
	 * "key" KEYNUM "up"
	 */
	private class KeyConnectCommand extends Command
	{
		@Override
		public void handle(String cmd, String[] args, int lineNo) {
			if (args.length < 4 || args.length > 4) {
                System.out.println("There is a error!");
				return;
			}
			
			if (!args[0].equals("key")) {
                System.out.println("There is a error!");
			}
			
			Integer num = null;
			String direction = args[2];
			String identifier = args[3];
			
			if ( (num = StringUtils.isInteger(args[1])) == null )
                System.out.println("There is a error!");
			
			if ( !(args[2].equals("up") || args[2].equals("down")) ) {
                System.out.println("There is a error!");
			}
			
			if (direction.equals("up") || direction.equals("down") && num != null) {
				try {
					Direction d;
					if (direction.equals("up")) {
						d = Direction.UP;
					} else {
						d = Direction.DOWN;
					}
					
					List<KeyConnection> connections = m_board.getKeyConnections();
					KeyConnection connection = null;
					for (KeyConnection key : connections) {
						if (key.equals(new KeyConnection(num))) {
							connection = key;
							break;
						}
					}
					
					if (connection != null) {
						if (m_board.getGizmoByName(identifier) == null) {
                            System.out.println("There is a error!");
						} else {
							connection.addAction(d, m_board.getGizmoByName(identifier));
						}
					} else {
						if (m_board.getGizmoByName(identifier) == null) {
                            System.out.println("There is a error!");
						} else {
							m_board.addKeyConnection(new KeyConnection(num, d, m_board.getGizmoByName(identifier)));
						}	
					}
				} catch (Exception e) {
                    System.out.println("There is a error!");
				}
			}
		}
	}
	
	/*
	 * Ball <name> <float-pair> <float-pair>
	 * <float-pair> ::= FLOAT FLOAT
	 */
	private class BallCommand extends Command
	{	
		@Override
		public void handle(String cmd, String[] args, int lineNo)
		{
			if (args.length < 5 || args.length > 5) {
                System.out.println("There is a error!");
				return;
			}
			
			String name = null;
			Float x = null;
			Float y = null;
			Float vx = null;
			Float vy = null;
			
			if (StringUtils.isValidIdentifier(args[0]))
				name = args[0];
			else
                System.out.println("There is a error!");
			
			if ( (x = StringUtils.isFloat(args[1])) == null )
                System.out.println("There is a error!");

			if ( (y = StringUtils.isFloat(args[2])) == null )
                System.out.println("There is a error!");

			if ( (vx = StringUtils.isFloat(args[3])) == null)
                System.out.println("There is a error!");
			
			if ( (vy = StringUtils.isFloat(args[4])) == null)
                System.out.println("There is a error!");
			
			if (x != null && y != null && vx != null && vy != null) {
				try {
					m_board.addBall(new ActiveBall(), x.doubleValue(), y.doubleValue(), vx.doubleValue(), vy.doubleValue(), name);
				} catch (Exception e) {
                    System.out.println("There is a error!");
				}
			}
		}
	}
	
	/*
	 * Delete <name>
	 * <name> ::= IDENTIFIER
	 */
	private class ScaleCommand extends Command
	{
		@Override
		public void handle(String cmd, String[] args, int lineNo)
		{
			if (args.length < 1 || args.length > 1) {
                System.out.println("There is a error!");
				return;
			}
			
			String name = args[0];
			if (StringUtils.isValidIdentifier(name)) {
				try {
					m_board.removeGizmo(name);
				} catch (Exception e) {
                    System.out.println("There is a error!");
				}
			} else
				System.out.println("There is a error!");
		}
	}
	
	/*
	 * Rotate <name>
	 * <name> ::= IDENTIFIER
	 */
	private class RotateCommand extends Command
	{
		@Override
		public void handle(String cmd, String[] args, int lineNo)
		{
			if (args.length < 1 || args.length > 1) {
                System.out.println("There is a error!");
				return;
			}
			
			String name = args[0];
			if (StringUtils.isValidIdentifier(name)) {
				try {
					m_board.rotateGizmo(name);
				} catch (Exception e) {
                    System.out.println("There is a error!");
				}
			} else
                System.out.println("There is a error!");
		}
	}
	
	/*
	 * Gravity FLOAT
	 */
	private class GravityCommand extends Command
	{
		@Override
		public void handle(String cmd, String[] args, int lineNo)
		{
			if (args.length < 1 || args.length > 1) {
                System.out.println("There is a error!");
				return;
			}
			
			Float g = null;
			
			if ( (g = StringUtils.isFloat(args[0])) == null)
                System.out.println("There is a error!");
			
			if (g != null)
				m_board.setGravity(g.doubleValue());
		}
	}
	
	/*
	 * Friction FLOAT FLOAT
	 */
	private class FrictionCommand extends Command
	{
		@Override
		public void handle(String cmd, String[] args, int lineNo)
		{
			if (args.length < 2 || args.length > 2) {
                System.out.println("There is a error!");
				return;
			}
			
			Float mu = null;
			Float mu2 = null;
			
			if ( (mu = StringUtils.isFloat(args[0])) == null )
                System.out.println("There is a error!");

			if ( (mu2 = StringUtils.isFloat(args[1])) == null )
                System.out.println("There is a error!");

			if (mu != null && mu2 != null)
				m_board.setFriction(mu.doubleValue(), mu2.doubleValue());
		}
	}

}