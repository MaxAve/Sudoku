import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Panel extends JPanel implements ActionListener {
	static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	static final int SCREEN_WIDTH = (int)screenSize.getWidth();
	static final int SCREEN_HEIGHT = (int)screenSize.getHeight();
	static final int DELAY = 100;
	static Timer timer;
	static Random random;
	static String screen;
	static long startTime = System.currentTimeMillis();
	static long elapsedTime = System.currentTimeMillis() - startTime;
	static long elapsedSeconds = elapsedTime / 1000;
	static long secondsDisplay = elapsedSeconds % 60;
	static long elapsedMinutes = elapsedSeconds / 60;
	static long minutesDisplay = elapsedMinutes % 60;
	static long elapsedHours = elapsedMinutes / 60;
	static String formattedTime = "";
	public static ArrayList<Button> buttons = new ArrayList<>();
	private static int lastClickedSquareX;
	private static int lastClickedSquareY;

	public static UI MenuScreen;
	public static UI.UIButton playButton;
	public static UI.UIButton helpButton;
	public static UI.UIButton settingsButton;
	public static UI.UIText menuTitle;
	public static UI.UIText gameTitle;
	
	Panel() {
		random = new Random();
		timer = new Timer(DELAY, this);
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(new Color(250, 250, 250));
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		timer.start();
		changeScreen("menu");

		// User Interface
		MenuScreen = new UI();

		playButton = new UI.UIButton(SCREEN_WIDTH/2, SCREEN_HEIGHT - 200, 500, 100, "Play");
		playButton.lMargin = 190;
		playButton.uMargin = 65;
		MenuScreen.add(playButton);

		helpButton = new UI.UIButton(SCREEN_WIDTH/2, SCREEN_HEIGHT - 350, 500, 100, "Instructions");
		helpButton.lMargin = 100;
		helpButton.uMargin = 65;
		MenuScreen.add(helpButton);

		settingsButton = new UI.UIButton(SCREEN_WIDTH/2, SCREEN_HEIGHT - 500, 500, 100, "Settings");
		settingsButton.lMargin = 200;
		settingsButton.uMargin = 65;
		MenuScreen.add(settingsButton);

		gameTitle = new UI.UIText("SUDOKU", SCREEN_WIDTH/20);
		gameTitle.setXPosition("mid");
		gameTitle.y = SCREEN_HEIGHT/10;
		MenuScreen.add(gameTitle);

		menuTitle = new UI.UIText("Menu", SCREEN_WIDTH/20);
		menuTitle.setXPosition("mid");
		menuTitle.y = SCREEN_HEIGHT/10 + 200;
		MenuScreen.add(menuTitle);
	}
	
	public static void startNewGame() {
		// Complete game reset
		startTime = System.currentTimeMillis();
		Sudoku.clueCoordinates.clear();
		buttons.clear();
		Sudoku.resetGrid();
		Sudoku.placeClues();

		// Game Buttons
		for(int i = 0; i < 9; i++) {
			new Button(SCREEN_WIDTH/2 - SCREEN_HEIGHT/15*4 - SCREEN_HEIGHT/15/2 - (int)(3*4.5) + i*(SCREEN_HEIGHT/15+3) - SCREEN_HEIGHT/15/2, SCREEN_HEIGHT-SCREEN_HEIGHT/15*3, SCREEN_HEIGHT/15, SCREEN_HEIGHT/15, "num_button", i+1);
		}
		new Button(SCREEN_WIDTH/2 - SCREEN_HEIGHT/15*4 - SCREEN_HEIGHT/15/2 - (int)(3*4.5) - SCREEN_HEIGHT/15/2, SCREEN_HEIGHT-SCREEN_HEIGHT/15*3+SCREEN_HEIGHT/15+SCREEN_HEIGHT/50, SCREEN_HEIGHT/15*9+30, SCREEN_HEIGHT/15, "notes", 0);
		new Button(10, SCREEN_HEIGHT/15/2+10, (int)(SCREEN_HEIGHT/5.5), SCREEN_HEIGHT/15, "exit_button", 1);
	}
	
	public static void changeScreen(String newScreen) {
		screen = newScreen;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw(Graphics g) {
		if (screen.equals("game")) {
			// Grid
			drawGrid(SCREEN_HEIGHT/15, 3, g);
			
			// Buttons
			drawButtons(g);
			
			// Notes
			for(Note note : Sudoku.notes) {
				note.drawNote(g);
			}
		} else if (screen.equals("menu")) {
			// UI
			MenuScreen.renderUI(g);
		}
	}
	
	// Draws the grid in the CENTER of the screen (or at least close to it)
	public static void drawGrid(int cellSize, int spacing, Graphics g) {
		final int X = SCREEN_WIDTH/2 - cellSize*4 - cellSize/2 - (int)(spacing*4.5);
		final int Y = cellSize*2;
		
		// Outline
		g.setColor(Color.black);
		g.fillRect(X-spacing-cellSize/2, Y-spacing-cellSize/2, 9*cellSize+10*spacing, 9*cellSize+10*spacing);
		
		// Background
		g.setColor(new Color(220, 220, 220));
		g.fillRect(X-cellSize/2, Y-cellSize/2, 9*cellSize+8*spacing, 9*cellSize+8*spacing);
		
		g.setColor(Color.black);
		// Lines (vertical)
		g.fillRect(X+3*(cellSize+spacing)-cellSize/2-spacing, Y-cellSize/2, spacing, Y+7*cellSize+8*spacing);
		g.fillRect(X+6*(cellSize+spacing)-cellSize/2-spacing, Y-cellSize/2, spacing, Y+7*cellSize+8*spacing);
		// Lines (horizontal)
		g.fillRect(X-cellSize/2, Y+3*(cellSize+spacing)-cellSize/2-spacing, Y+7*cellSize+8*spacing, spacing);
		g.fillRect(X-cellSize/2, Y+6*(cellSize+spacing)-cellSize/2-spacing, Y+7*cellSize+8*spacing, spacing);
		
		// Draws grid
		g.setColor(Color.white);
		for(int i = 0; i < Sudoku.grid.length; i++) {
			for(int j = 0; j < Sudoku.grid.length; j++) {				
				g.setColor(Color.white);
				g.fillRect(X+j*(cellSize+spacing)-cellSize/2, Y+i*(cellSize+spacing)-cellSize/2, cellSize, cellSize);
				
				// Place numbers on grid
				if(!Sudoku.useNotes && Frame.mouseClickX > X+j*(cellSize+spacing)-cellSize/2 && Frame.mouseClickX < X+j*(cellSize+spacing)+cellSize/2 && Frame.mouseClickY > Y+i*(cellSize+spacing)-cellSize && Frame.mouseClickY < Y+i*(cellSize+spacing)+cellSize) {
					boolean isPlacingNumberAllowed = true; // Players always do what theyre not allowed to do, so we need to make sure they dont clear the clues
					for(int[] coordinates : Sudoku.clueCoordinates) {
						if(i == coordinates[0] && j == coordinates[1]) {
							isPlacingNumberAllowed = false;
						}
					}
					if(isPlacingNumberAllowed) {
						if(Sudoku.selectedNum != Sudoku.grid[i][j]) {
							Sudoku.grid[i][j] = Sudoku.selectedNum;
							
							if(Sudoku.isMoveLegal(j, i)) {
								g.setColor(new Color(0, 175, 255, 100));
								g.fillRect(X+j*(cellSize+spacing)-cellSize/2, Y+i*(cellSize+spacing)-cellSize/2, cellSize, cellSize);
							} else {
								g.setColor(new Color(255, 0, 0, 100));
								g.fillRect(X+j*(cellSize+spacing)-cellSize/2, Y+i*(cellSize+spacing)-cellSize/2, cellSize, cellSize);
							}
						} else {
							Sudoku.grid[i][j] = 0;
						}
						lastClickedSquareX = j;
						lastClickedSquareY = i;
					}
					
					// Reset mouse click coordinates
					Frame.mouseClickX = -1;
					Frame.mouseClickY = -1;
				}
				
				// Draw numbers
				g.setFont(new Font("Arial", Font.BOLD, (int)(cellSize/1.5)));
				if(Sudoku.grid[i][j] > 0) {
					g.setColor(new Color(0, 125, 205));
					g.drawString(""+Sudoku.grid[i][j], X+j*(cellSize+spacing)-cellSize/4, Y+i*(cellSize+spacing)+cellSize/4+2);
					g.setColor(new Color(0, 175, 255));
					g.drawString(""+Sudoku.grid[i][j], X+j*(cellSize+spacing)-cellSize/4, Y+i*(cellSize+spacing)+cellSize/4);
				}
			}
		}
		
		// Draws numbers in black if the number is a clue
		for(int[] coordinates : Sudoku.clueCoordinates) {
			int i = coordinates[0];
			int j = coordinates[1];
			g.setColor(Color.black);
			g.drawString(""+Sudoku.grid[i][j], X+j*(cellSize+spacing)-cellSize/4, Y+i*(cellSize+spacing)+cellSize/4+2);
			g.setColor(new Color(80, 80, 80));
			g.drawString(""+Sudoku.grid[i][j], X+j*(cellSize+spacing)-cellSize/4, Y+i*(cellSize+spacing)+cellSize/4);
		}
		
		// Draws +
		g.setColor(new Color(0, 175, 255, 40));
		for(int i = 0; i < Sudoku.grid[0].length; i++) {
			g.fillRect(X+i*(cellSize+spacing)-cellSize/2, Y+lastClickedSquareY*(cellSize+spacing)-cellSize/2, cellSize, cellSize);
		}
		for(int i = 0; i < Sudoku.grid[0].length; i++) {
			g.fillRect(X+lastClickedSquareX*(cellSize+spacing)-cellSize/2, Y+i*(cellSize+spacing)-cellSize/2, cellSize, cellSize);
		}
		
		// Text
		g.setFont(new Font("Arial", Font.BOLD, SCREEN_HEIGHT/45));
		if(Sudoku.lang.equals("en")) {
			g.setColor(new Color(0, 125, 205));
			g.drawString("Difficulty: " + Sudoku.difficulty.toUpperCase(), X-cellSize/2+(int)(1.5*cellSize), Y-cellSize+1);
			g.setColor(new Color(0, 175, 255));
			g.drawString("Difficulty: " + Sudoku.difficulty.toUpperCase(), X-cellSize/2+(int)(1.5*cellSize), Y-cellSize);
		} else if(Sudoku.lang.equals("ua")) {
			g.setColor(new Color(0, 125, 205));
			g.drawString("Важкіcть: " + Sudoku.difficulty.toUpperCase(), X-cellSize/2+(int)(1.5*cellSize), Y-cellSize+1);
			g.setColor(new Color(0, 175, 255));
			g.drawString("Важкіcть: " + Sudoku.difficulty.toUpperCase(), X-cellSize/2+(int)(1.5*cellSize), Y-cellSize);
		} else if(Sudoku.lang.equals("de")) {
			g.setColor(new Color(0, 125, 205));
			g.drawString("Schwerigkeit: " + Sudoku.difficulty.toUpperCase(), X-cellSize/2+(int)(1.5*cellSize), Y-cellSize+1);
			g.setColor(new Color(0, 175, 255));
			g.drawString("Schwerigkeit: " + Sudoku.difficulty.toUpperCase(), X-cellSize/2+(int)(1.5*cellSize), Y-cellSize);
		}
		
		// Time
		if(Sudoku.lang.equals("en")) {
			g.setColor(new Color(0, 125, 205));
			g.drawString("Time: " + formattedTime , X-cellSize/2+(int)(5.5*cellSize), Y-cellSize+1);
			g.setColor(new Color(0, 175, 255));
			g.drawString("Time: " + formattedTime, X-cellSize/2+(int)(5.5*cellSize), Y-cellSize);
		} else if(Sudoku.lang.equals("ua")) {
			g.setColor(new Color(0, 125, 205));
			g.drawString("Чаc: " + formattedTime , X-cellSize/2+(int)(5.5*cellSize), Y-cellSize+1);
			g.setColor(new Color(0, 175, 255));
			g.drawString("Чаc: " + formattedTime, X-cellSize/2+(int)(5.5*cellSize), Y-cellSize);
		} else if(Sudoku.lang.equals("de")) {
			g.setColor(new Color(0, 125, 205));
			g.drawString("Zeit: " + formattedTime , X-cellSize/2+(int)(5.5*cellSize), Y-cellSize+1);
			g.setColor(new Color(0, 175, 255));
			g.drawString("Zeit: " + formattedTime, X-cellSize/2+(int)(5.5*cellSize), Y-cellSize);
		}
	}
	
	static int buttonClickX, buttonClickY, clickedButtonWidth, clickedButtonHeight;
	public void drawButtons(Graphics g) {
		for(Button button : buttons) {
			g.setFont(new Font("Arial", Font.BOLD, (int)(SCREEN_HEIGHT/15/1.5)));
			if(button.type.equals("num_button")) {
				g.setColor(new Color(0, 75, 155));
				g.fillRoundRect(button.x, button.y-button.hitboxHeight/2+button.hitboxHeight/10, button.hitboxWidth, button.hitboxHeight, 15, 15);
				g.setColor(new Color(0, 175, 255));
				g.fillRoundRect(button.x, button.y-button.hitboxHeight/2, button.hitboxWidth, button.hitboxHeight, 15, 15);
				g.setColor(Color.white);
				g.drawString(button.ID+"", button.x+button.hitboxWidth/4, button.y+button.hitboxHeight/4);
			} else if(button.type.equals("exit_button")) {
				g.setColor(new Color(0, 75, 155));
				g.fillRoundRect(button.x, button.y-button.hitboxHeight/2+button.hitboxHeight/10, button.hitboxWidth, button.hitboxHeight, 15, 15);
				g.setColor(new Color(0, 175, 255));
				g.fillRoundRect(button.x, button.y-button.hitboxHeight/2, button.hitboxWidth, button.hitboxHeight, 15, 15);
				g.setColor(Color.white);
				
				if(Sudoku.lang.equals("en")) {
					g.drawString("Exit", button.x+button.hitboxWidth/4, button.y+button.hitboxHeight/4);
				} else if(Sudoku.lang.equals("ua")) {
					g.setFont(new Font("Arial", Font.BOLD, (int)(SCREEN_HEIGHT/15/2)));
					g.drawString("Вийти", button.x+button.hitboxWidth/4, button.y+button.hitboxHeight/5);
				} else if(Sudoku.lang.equals("de")) {
					g.setFont(new Font("Arial", Font.BOLD, (int)(SCREEN_HEIGHT/15/3.5)));
					g.drawString("Verlassen", button.x+button.hitboxWidth/4, button.y+button.hitboxHeight/8);
				}
			} else if(button.type.equals("notes")) {
				g.setColor(new Color(0, 75, 155));
				g.fillRoundRect(button.x, button.y-button.hitboxHeight/2+button.hitboxHeight/10, button.hitboxWidth, button.hitboxHeight, 15, 15);
				g.setColor(new Color(0, 175, 255));
				g.fillRoundRect(button.x, button.y-button.hitboxHeight/2, button.hitboxWidth, button.hitboxHeight, 15, 15);
				
				g.setColor(Color.white);
				FontMetrics metrics = getFontMetrics(g.getFont());
				if(Sudoku.lang.equals("en")) {
					g.drawString("Notes", button.x+button.hitboxWidth/2-metrics.stringWidth("Notes")/2, button.y+button.hitboxHeight/4);
				} else if(Sudoku.lang.equals("ua")) {
					g.drawString("Hoтaтки", button.x+button.hitboxWidth/2-metrics.stringWidth("Hoтaтки")/2, button.y+button.hitboxHeight/5);
				} else if(Sudoku.lang.equals("de")) {
					g.drawString("Notize", button.x+button.hitboxWidth/2-metrics.stringWidth("Notize")/2, button.y+button.hitboxHeight/8);
				}
				
				if(Sudoku.useNotes) {
					g.setColor(new Color(255, 255, 255, 120));
					g.fillRoundRect(button.x, button.y-button.hitboxHeight/2, button.hitboxWidth, button.hitboxHeight, 15, 15);
				}
			}
		}
		
		g.setColor(new Color(255, 255, 255, 120));
		g.fillRoundRect(buttonClickX, buttonClickY-clickedButtonHeight/2, clickedButtonWidth, clickedButtonHeight, 15, 15);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Update time
		elapsedTime = System.currentTimeMillis() - startTime;
		elapsedSeconds = elapsedTime / 1000;
		secondsDisplay = elapsedSeconds % 60;
		elapsedMinutes = elapsedSeconds / 60;
		minutesDisplay = elapsedMinutes % 60;
		elapsedHours = elapsedMinutes / 60;
		formatTime(); // Format time
		
		// Buttons
		for(Button button : buttons) {
			if (screen.equals("game")) {
				if(button.type.equals("num_button")) {
					button.onClick(() -> {
						Sudoku.selectedNum = button.ID;
						buttonClickX = button.x;
						buttonClickY = button.y;
						clickedButtonWidth = button.hitboxWidth;
						clickedButtonHeight = button.hitboxHeight;
					});
				} else if(button.type.equals("exit_button")) {
					button.onClick(() -> {
						changeScreen("menu");
						buttonClickX = button.x;
						buttonClickY = button.y;
						clickedButtonWidth = button.hitboxWidth;
						clickedButtonHeight = button.hitboxHeight;
					});
				} else if(button.type.equals("notes")) {
					button.onClick(() -> {
						Sudoku.useNotes = !Sudoku.useNotes;
					});
				}
			}
			if(button.type.equals("play_button")) {
				button.onClick(() -> {
					changeScreen("game");
					buttonClickX = button.x;
					buttonClickY = button.y;
					clickedButtonWidth = button.hitboxWidth;
					clickedButtonHeight = button.hitboxHeight;
				});
			}
		}

		// UI
		if (screen.equals("menu")) {
			// Play button
			playButton.onClick(()->{
				changeScreen("game");
				Sudoku.resetGrid();
				startNewGame();
				Sudoku.notes.clear();
			});
		}
		
		// Notes
		if(Sudoku.useNotes) {
			if(Frame.mouseClickX > 0 && Frame.mouseClickY > 0) {
				boolean deleteNote = false;
				for(Note note : Sudoku.notes) {
					if(Frame.mouseClickX > note.x && Frame.mouseClickX < note.x+Note.size && Frame.mouseClickY > note.y && Frame.mouseClickY < note.y+Note.size*2) {
						if(note.num == Sudoku.selectedNum) {
							for(int i = 0; i < Sudoku.notes.size(); i++) {
								if(Sudoku.notes.get(i).ID == note.ID) {
									Sudoku.notes.remove(i);
								}
							}
							deleteNote = true;
							break;
						}
					}
				}
				if(!deleteNote) {
					new Note(Frame.mouseClickX-Note.size, Frame.mouseClickY-Note.size, Sudoku.selectedNum);
				}
				Frame.mouseClickX = -1;
				Frame.mouseClickY = -1;
			}
		}
		
		repaint();
	}
	
	// Formats the time and saves the value to the formattedTime variable
	static void formatTime() {
		// We need to reset the String, if we don't, we will get a super long string that will eventually crash the program
		formattedTime = "";
		
		if(elapsedHours < 10) {
			formattedTime += "0";
			formattedTime += elapsedHours;
		} else {
			formattedTime += elapsedHours;
		}
		formattedTime += ":";
		if(minutesDisplay < 10) {
			formattedTime += "0";
			formattedTime += minutesDisplay;
		} else {
			formattedTime += minutesDisplay;
		}
		formattedTime += ":";
		if(secondsDisplay < 10) {
			formattedTime += "0";
			formattedTime += secondsDisplay;
		} else {
			formattedTime += secondsDisplay;
		}
	}
	
	// Keyboard controls
	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_1:
				Sudoku.selectedNum = 1;
				break;
			case KeyEvent.VK_2:
				Sudoku.selectedNum = 2;
				break;
			case KeyEvent.VK_3:
				Sudoku.selectedNum = 3;
				break;
			case KeyEvent.VK_4:
				Sudoku.selectedNum = 4;
				break;
			case KeyEvent.VK_5:
				Sudoku.selectedNum = 5;
				break;
			case KeyEvent.VK_6:
				Sudoku.selectedNum = 6;
				break;
			case KeyEvent.VK_7:
				Sudoku.selectedNum = 7;
				break;
			case KeyEvent.VK_8:
				Sudoku.selectedNum = 8;
				break;
			case KeyEvent.VK_9:
				Sudoku.selectedNum = 9;
				break;
			}
		}
	}
}