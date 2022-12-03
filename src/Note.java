import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;

public class Note {
	public static int size = Panel.SCREEN_HEIGHT/30;
	int x, y;
	int num;
	public final int ID;
	public Note(int x, int y, int num) {
		this.x = x;
		this.y = y;
		this.num = num;
		this.ID = new Random().nextInt(1000000000);
		Sudoku.notes.add(this);
	}
	public void drawNote(Graphics g) {
		if(Sudoku.useNotes) {
			g.setColor(new Color(240, 233, 168));
			g.fillRect(this.x, this.y, size, size);
			g.setColor(new Color(158, 154, 112));
			g.drawRect(this.x, this.y, size, size);
			g.setColor(new Color(0, 0, 0));
			g.setFont(new Font("Arial", Font.PLAIN, size/2));
			g.drawString(""+this.num, this.x+size/4, y+size-size/4);
		}
	}
}