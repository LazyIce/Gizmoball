package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class KeyConnection
{	
	public  enum Direction { UP, DOWN };
	
	private int m_key;
	private boolean m_pressed;
	private Map<Direction, List<BoardAction>> m_actions;
	
	public KeyConnection (int key, Direction direction, BoardAction action)
	{
		m_key = key;
		m_pressed = false;
		m_actions = new HashMap<Direction, List<BoardAction>>();
		
		List<BoardAction> actions = new ArrayList<BoardAction>();
		actions.add(action);
		
		m_actions.put(direction, actions);
	}
	
	public KeyConnection (int keyCode)
	{
		m_key = keyCode;
		m_pressed = false;
		m_actions = new HashMap<Direction, List<BoardAction>>();
	}

	public int getKey ()
	{
		return m_key;
	}
	
	public boolean isPressed ()
	{
		return m_pressed;
	}
	
	@Override
	public boolean equals (Object obj)
	{
	    if (this == obj) {
	        return true;
	    }
	    if (obj == null || getClass() != obj.getClass()) {
	        return false;
	    }

	    KeyConnection key = (KeyConnection) obj;
	    return key.getKey() == m_key;
	}
	
	@Override
	public int hashCode ()
	{
		return m_key;
	}

	public void addAction(Direction direction, BoardAction action) throws Exception
	{
		if (m_actions.containsKey(direction)) {        //如果已经有了这个方向的
			if (m_actions.get(direction).contains(action)) {      //如果动作重复了
				throw new Exception ("The specified action " + action.getName() + " already exists for this key connection.");
			} else {
				m_actions.get(direction).add(action);	
			}
		} else {
			m_actions.put(direction, new ArrayList<BoardAction>());
			m_actions.get(direction).add(action);
		}
	}

	public Map<Direction, List<BoardAction>> getActions()
	{
		return m_actions;
	}

	public void setPressed(boolean b)
	{
		m_pressed = b;
	}
	
	public String toString ()
	{	
		return new Integer(m_key).toString();
	}

	public void triggerAllActions()
	{
		for (Entry<Direction, List<BoardAction>> entry : m_actions.entrySet()) {
			for (BoardAction action : entry.getValue()) {
				action.trigger();
			}
		}
	}

	public void triggerAction (Direction direction)
	{
		for (Entry<Direction, List<BoardAction>> entry : m_actions.entrySet()) {
			if (entry.getKey().equals(direction)) {
				for (BoardAction action : entry.getValue()) {
					action.trigger();
					action.triggerConnected();
				}
			}
		}
	}

	public void clearActions(String name)
	{
		for (Entry<Direction, List<BoardAction>> entry : m_actions.entrySet()) {
			for (BoardAction action : entry.getValue()) {
				if (action.getName().equals(name)) {
					entry.getValue().remove(action);
					break;
				}
			}
		}
	}
}