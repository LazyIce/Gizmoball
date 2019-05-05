package view;

import javax.swing.*;
import java.awt.*;

/**
 * Internal class - Tool Button : Specifies a button of specific size
 * 	for the tool panel.
 */
public class ToolButton extends JButton {
	String originalName;
	public ToolButton(String name) {
		super(name);
		originalName = name;
		System.out.println();
	}

	public ToolButton(String name, String image) {
		super(name);
		originalName = name;
        this.setIcon(new ImageIcon(image));
	}

	@Override 
	public Dimension getPreferredSize(){
		return new Dimension(50,50);
		
	}
	
	public String originalName(){
		return originalName;
	}

	public void setOriginalName(String temp){ originalName = temp ; }

}