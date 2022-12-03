import java.util.ArrayList;
import java.util.Random;

/*
 * For compile + run:
cd "c:\Users\doks\OneDrive\Desktop\Programming 2022\Java Projects\Sudoku\src\" && javac Sudoku.java && java Sudoku
 */

public class Sudoku {
	private static Random random = new Random();
	public static int[][] grid;
	public static String difficulty = "easy";
	public static int selectedNum = 1;
	public static String lang = "en";
	public static int gamesPlayed = 0;
	public static boolean useNotes = false;
	
	// It is important that players don't erase clues, as that would be kinda stupid ngl
	public static ArrayList<int[]> clueCoordinates = new ArrayList<int[]>();
	public static ArrayList<Note> notes = new ArrayList<>();
	
	public static void main(String[] args) {
		difficulty = "normal";
		lang = "en";
		
		grid = new int[9][9];

		new Frame();
	}

	public static void resetGrid() {
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[0].length; j++) {
				grid[i][j] = 0;
			}
		}
	}
	
	/**
	 * Checks if the number at the coordinates is legal by the standard Sudoku rules.
	 * This method checks vertically, horizontally and most important, it checks the sub-grids in the sudoky grid.
	 * This method utilizes 10 loops in total.
	 * @param x
	 * @param y
	 * @return boolean
	 */
	public static boolean isMoveLegal(int x, int y) {
		// Horizontal checking
		for(int i = 0; i < grid[0].length; i++) {
			for(int j = 0; j < grid[0].length; j++) {
				if(j != i && grid[y][i] != 0 && grid[y][j] != 0) {
					if(grid[y][i] == grid[y][j]) {
						return false;
					}
				} else {
					continue;
				}
			}
		}
		// Vertical checking
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid.length; j++) {
				if(j != i && grid[i][x] != 0 && grid[j][x] != 0) {
					if(grid[i][x] == grid[j][x]) {
						return false;
					}
				} else {
					continue;
				}
			}
		}
		// Checking for same numbers placed in one division
		// This is quite complex as this uses like 6 loops, but all you need to know is that this checks if
		// the same number was placed in one of the nine sub-grids, according to the rules of Sudoku
		for(int a = 0; a < grid.length; a+=3) {
			for(int b = 0; b < grid[0].length; b+=3) {
				// Loops through the sub-grid and adds each number to the 1-DIMENSIONAL ArrayList nums
				ArrayList<Integer> nums = new ArrayList<>();
				for(int i = 0; i < 3; i++) {
					for(int j = 0; j < 3; j++) {
						if(grid[a+i][b+j] != 0) {
							nums.add(grid[a+i][b+j]);
						}
					}
				}
				// Loops through nums to see if one number repeats itself
				// Theoretically, this could be done with the original 2D sub-grid but this is alredy too complicated,
				// so why make this even more complex?
				for(int i = 0; i < nums.size(); i++) {
					for(int j = 0; j < nums.size(); j++) {
						if(i != j) {
							if(nums.get(i) == nums.get(j)) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	public static void placeClues() {
		int maximumNums = 0;
		if(difficulty.equals("easy")) {
			maximumNums = 60;
		} else if(difficulty.equals("normal")) {
			maximumNums = 30;
		} else if(difficulty.equals("hard")) {
			maximumNums = 20;
		}
		
		int totalNums = 0;
		for(int C = 0; C < 1000; C++) { // Im not a fan of C++, but this joke is too good not to add here
			for(int i = 0; i < maximumNums; i++) {
				int x = random.nextInt(grid[0].length);
				int y = random.nextInt(grid.length);
				grid[y][x] = random.nextInt(9)+1;
				for(int j = 0; j < 1000; j++) {
					if(!isMoveLegal(x, y)) {
						grid[y][x] = 0;
						x = random.nextInt(grid[0].length);
						y = random.nextInt(grid.length);
						grid[y][x] = random.nextInt(9)+1;
					} else {
						break;
					}
				}
			}
			
			for(int i = 0; i < grid.length; i++) {
				for(int j = 0; j < grid[0].length; j++) {
					if(grid[i][j] > 0) {
						totalNums++;
					}
				}
			}
			
			if(totalNums >= 17) {
				System.out.println("[Sudoku] Generated new Sudoku grid in " + C + " iterations.");
				System.out.println("[Sudoku] " + totalNums + " grid cells were filled in total.");
				break;
			} else {
				totalNums = 0;
				for(int i = 0; i < grid.length; i++) {
					for(int j = 0; j < grid[0].length; j++) {
						grid[i][j] = 0;
					}
				}
			}
		}
		
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[0].length; j++) {
				if(grid[i][j] != 0 && isMoveLegal(j,i)) {
					clueCoordinates.add(new int[] {i, j});
				}
			}
		}
	}
}