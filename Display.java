import com.googlecode.lanterna.*;
import com.googlecode.lanterna.input.*;
import com.googlecode.lanterna.terminal.*;
import com.googlecode.lanterna.screen.*;
import java.io.IOException;
import java.awt.Color;
import java.lang.Math;

/*  Mr. K's TerminalDemo edited for lanterna 3
 */

public class Display {
	/**Places a given string on the screen.
    *
    *@param x is the x value of the coordinate the string starts from.
		*@param y is the y value of the coordinate the string starts from.
		*@param screen is the screen the method adds the string to.
		*@param str is the string that the method places.
    */
	public static void putString(int x, int y, Screen screen, String str) {
		for (int i = 0; i < str.length(); ++i) {
			screen.setCharacter(x+i, y, new TextCharacter(str.charAt(i)));
		}
	}

	/**Displays a 4x2 square of a single color.
    *
		*@param side is the index of the side of the Rubik's Cube the sticker is on
		*@param side is the index of the specific sticker the method is displaying
    *@param x is the x value of the sticker's upperleftmost coordinate.
		*@param y is the y value of the sticker's upperleftmost coordinate.
		*@param screen is the screen the method displays the sticker on.
		*@param cube is the Cube that the method is displaying a sticker of
		*
    */
	private static void drawSticker(int side, int index, int x, int y,Screen screen,Cube cube) {
		int storeY = y;
		for (int i = 0;i < 2;i++) {
			for (int j = 0; j < 4;j++) {
				screen.setCharacter(x+j,storeY,cube.drawColor(side,index));
			}
			storeY++;
		}

	}

	/**Displays an entire side of the Rubik's Cube, made up of a 3x3 grid of
		*stickers and their corresponding colors.
    *
		*@param side is the index of the side of the Rubik's Cube the method is displaying
    *@param startX is the x value of the upperleftmost coordinate.
		*@param startY is the y value of the upperleftmost coordinate..
		*@param screen is the screen the method displays the side on.
		*@param cube is the Cube that the method is displaying a side of
		*
    */
	private static void drawSide(int side,int startX, int startY,Screen screen,Cube cube) {
		int storeStartY = startY;
		int currentSticker = 0;
		for (int i = 0;i < 3;i++) {
			for (int j = 0;j < 3;j++) {
				drawSticker(side,currentSticker,startX+(4*j),storeStartY,screen,cube);
				currentSticker++;
			}
			storeStartY += 2;
		}
	}

	/**Displays the entire Rubik's Cube, made up of 6 sides and laid out like
		*a sideways cross.
		*
		*@param startX is the x value of the upperleftmost coordinate of the side
		*on the top row, with an index 3.
		*@param startY is the y value of the upperleftmost coordinate of the side
		*on the top row, with an index 3.
		*@param screen is the screen the method displays the Rubik's Cube on
		*@param cube is the Cube that the method is displaying
		*
		*/
	public static void drawCube(int startX, int startY,Screen screen, Cube cube){
		drawSide(3,startX,startY,screen,cube);
		drawSide(4,startX-12,startY+6,screen,cube);
		drawSide(0,startX,startY+6,screen,cube);
		drawSide(2,startX+12,startY+6,screen,cube);
		drawSide(5,startX+24,startY+6,screen,cube);
		drawSide(1,startX,startY+12,screen,cube);
	}

	public static void changeCube(KeyStroke key, Screen screen, Cube cube){
		String keyString = ""+key;
		String toMove = ""+keyString.charAt(keyString.length()-3);
		cube.performMove(toMove);
	}

	private static int[] giveSize(TerminalSize dimensions) {
	int row = dimensions.getRows();
	int col = dimensions.getColumns();
	int[] sizes = new int[] {1,1};
	if (row < 36) {
		sizes[0] = 1;
		sizes[1] = 1;
 	}
	else if (row >= 36 && row < 72) {
		sizes[0] = 2;
		sizes[1] = 1;
	}
	else if (row >= 72 && row < 108) {
		sizes[0] = 4;
		sizes[1] = 2;
	}
	else if (row >= 108 && row < 144) {
		sizes[0] = 6;
		sizes[1] = 3;
	}
	else if (row >= 144 && row < 162) {
		sizes[0] = 8;
		sizes[1] = 4;
	}
	else if (row >= 162 && row < 198) {
		sizes[0] = 9;
		sizes[1] = 5;
	}
	else if (row >= 198 && row < 234) {
		sizes[0] = 11;
		sizes[1] = 6;
	}
	else if (row >= 234) {
		sizes[0] = 13;
		sizes[1] = 7;
	}
	return sizes;	
}

	public static void resizeCube(TerminalSize dimensions,Screen screen){
		int row = dimensions.getRows();
		int col = dimensions.getColumns();
		String errorMessage = "Resize screen";
		if (row < 12 || col < 9) {
			for (int i = 0; i < errorMessage.length(); ++i) {
				screen.setCharacter(0+i,4,new TextCharacter(errorMessage.charAt(i)));
			}
		}
	}

	/**Creates an interactive Screen that allows the user to interact with a
		*simulation of a Rubik's Cube. The user may turn different sides,
		*scramble the cube, customize the puzzle to their liking, and time their
		*attempts to sovle it.
		*
		*@param args is the array of the user's inputs
		*
		*/
	public static void main(String[] args) throws IOException {

		int x = 10;
		int y = 10;

		Screen screen = new DefaultTerminalFactory().createScreen();
		screen.startScreen();

		/**Starts a timer of the time the program has gone on for
		  */
		long tStart = System.currentTimeMillis();
		long lastSecond = 0;

		/**Creates a cube to be simulated
		  */
		Cube cube = new Cube();
		String scramble = cube.generateScramble(20);
		cube.performMoveSet(scramble);
		drawCube(25,5,screen,cube);
		putString(2,0,screen,scramble);
		TerminalSize originalSize = screen.getTerminalSize();
		putString(0,1,screen,""+originalSize);
		while (true) {
			KeyStroke key = screen.pollInput();
			TerminalSize currentSize = screen.getTerminalSize();

			if (currentSize != originalSize) {
				resizeCube(currentSize,screen);
				putString(1,0,screen,""+currentSize);
			}

			if (key != null) {
				if (key.getKeyType() == KeyType.Escape) break;
				else if (key.getKeyType() == KeyType.Character) {
					changeCube(key,screen,cube);
					drawCube(25,5,screen,cube);
				}
			}
			long tEnd = System.currentTimeMillis();
			long millis = tEnd - tStart;
			putString(1, 2, screen, "Milliseconds since start of program: "+millis);
			if (millis / 1000 > lastSecond) {
				lastSecond = millis / 1000;
				putString(1, 3, screen, "Seconds since start of program: "+millis/1000);
			}
			screen.doResizeIfNecessary();
			screen.refresh();
		}
		screen.stopScreen();
	}
}
