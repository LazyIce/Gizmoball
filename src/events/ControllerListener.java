package events;

import model.Board;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerListener implements ActionListener{

	Board b;
	
	public ControllerListener(Board mB) {
		super();
		b = mB;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (!b.debug)
			b.update();
	}

}
