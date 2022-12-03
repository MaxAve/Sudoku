import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Frame extends JFrame {
	public static int mouseClickX;
	public static int mouseClickY;
	
	Frame() {
		this.add(new Panel());
		this.setTitle("Desktop Sudoku");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		
		this.addMouseListener(new MouseListener() {
		    @Override
		    public void mouseClicked(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {
		        mouseClickX = e.getX();
		        mouseClickY = e.getY();
			}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
	}
}