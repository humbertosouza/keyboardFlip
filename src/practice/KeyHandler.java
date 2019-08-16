/**
 * Practice - Keyboard Handler
 */
package practice;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author humberto
 * 
 * KeyHandler transforms the traditional keyboard to a transformed keyboard output.
 * 2019-08-16
 * 
 * It is intended to run under Java 6.
 *
 */
public class KeyHandler {
	
	Map<String,String> old2NewKeyboard = new HashMap<String, String>();

	// Keyboard string map
	String  keyboardString = "";
	// Keyboard array map
	String[][] keyboardArray = {{"1","2","3","4","5","6","7","8","9","0"},
			{"q","w","e","r","t","y","u","i","o","p"},
			{"a","s","d","f","g","h","j","k","l",";"},
			{"z","x","c","v","b","n","m",",",".","/"}};

	/**
	 * @param args
	 *  arg1: moves in CSV. E.g. H,V,5,V,-12 ...
	 *  arg2: text file to translate: textFile.txt
	 *  
	 *  Output: printed translated text
	 */
	public static void main(String[] args) {
		
		// Default Inputs
		String sampleText = "Hello World";
		String[] decodedArgs = {"H","V","H","5","V","-12"};
		
		//Initialize object
		KeyHandler keyHandler = new KeyHandler();


		int processedTextSuccess = 0;
		if (args.length != 0 && args.length == 2) {
		
			String[] keyMoves = keyHandler.processMoves(args[0]);
			processedTextSuccess = keyHandler.processTextFile(keyMoves, args[1]);
			
		} else {
			
			System.out.println("Wrong number of parameters issued.\n  Parameters expected:\n    moves.csv - file containing the moves.\n    bigtext.txt - Text file which the content will be updated according to the new map.");
			System.out.println("Args found: " + Arrays.toString(args));
			System.out.println("Demoing transformations and issuing the default transformation for the input \"Hello World\" ");
			System.out.println(" ");
			
			keyHandler.demo(keyHandler);
			keyHandler.processTextFile(decodedArgs, sampleText);
			processedTextSuccess = 1;
		
		}
		
		System.out.println("Status: " + processedTextSuccess);

	}
	
	/**
	 * Process the text according to the keyboard moves, processing the text line by line 	
	 * @param keyMoves
	 * @param textInput
	 * @return
	 */
	public int processTextFile(String[] keyMoves, String textInput) {
		
		BufferedReader reader;
		String[][] baseKeyboard = this.getKeybArr();
		String[][] keyboard = this.prepareKeyboard(keyMoves, baseKeyboard);
		
		int success = 0;
		try {
			
			reader = new BufferedReader(new FileReader(textInput));
			String line = reader.readLine().toLowerCase();
			
			while (line != null) {
				System.out.println(line);
				String newLine = this.processLine(line);
				System.out.println(newLine);
		
				// read next line
				line = reader.readLine();
			}
			
			reader.close();
			
		} catch (IOException e) {
			System.out.println("File not found. Processing existing text.\n");
			String newLine = this.processLine(textInput);
			System.out.println(newLine);
			success = 1;
			
		}
				
		return success;
	}
	
	
	/**
	 * Process each line of the text, transforming from the old mapping to the new keyboard mapping
	 * It assumes that the original text is provided using the original keyboard set.
	 * 
	 * @param keyboard array of the keyboard map
	 * @param line  line of text
	 * @return String text
	 */
	private String processLine( String line) {

		String output = "";
		// Chosen StringBuffer in case we create a multi-threaded version of this module
		// StringBuild could be faster, but not threadsafer. Pure string is slow.
		StringBuffer newLine = new StringBuffer();
		
		int size = line.length();
		for(int i=0; i< size; i++) {
			char charat = line.charAt(i);
			String newChar = this.old2NewKeyboard.getOrDefault(String.valueOf(charat), String.valueOf(charat));
			newLine.append(newChar);

		}
		
		return newLine.toString();
		
	}

	private String[][] prepareKeyboard(String[] keyMoves, String[][] refKeyboard) {
		
		String[][] tempKeyboard = refKeyboard;
		int flip = 0;
		boolean flag = false; // Flag = false - text; flag = true - number
		for(String move : keyMoves) {
			try {
				//System.out.println("move: " + move);
				flip = Integer.parseInt(move);
				flag = true;
			}catch(Exception e){
				//System.out.println("Not a number. Continuing");
				flag = false;
				flip = 0;
			}
			if(flag) {
				tempKeyboard = this.flip(tempKeyboard, flip);
				//System.out.println("NTemp keyboard: \n" + this.buildKeyboardString(tempKeyboard));
			}
			if(!flag && move.equals("V")) {
				tempKeyboard = this.verticalFlip(tempKeyboard);
				//System.out.println("VTemp keyboard: \n" + this.buildKeyboardString(tempKeyboard));
				
			}
			if(!flag && move.equals("H")) {
				tempKeyboard = this.horizontalFlip(tempKeyboard);
				//System.out.println("HTemp keyboard: \n" + this.buildKeyboardString(tempKeyboard));
			}

		}
		
		System.out.println("Resulting keyboard: \n" + this.buildKeyboardString(tempKeyboard) + "\n");
		System.out.println("Original keyboard: \n" + this.getMap() + "\n");
		
		this.old2NewKeyboard.clear();
		
		for(int i=0; i< 4; i++) {

			for(int j=0; j< 10; j++) {
				
				this.old2NewKeyboard.put( refKeyboard[i][j], tempKeyboard[i][j]);

			}
		}

		return tempKeyboard;
	}

	/**
	 * Process the input moves for the text flip
	 * @param moveFile
	 * @return
	 */
	public String[] processMoves(String moveFile) {
		
		ArrayList<String> moves = new ArrayList<String>();
		
		try {
			List<String> allLines = Files.readAllLines(Paths.get(moveFile));
			for (String line : allLines) {
				String itens[] = line.split(",");
				for(String item: itens){
					moves.add(item);					
				}
			}
			//System.out.println("Move file content:\n" + allLines);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		Object[] returnsObj = moves.toArray();
		String[] returns = new String[returnsObj.length];
		int i = 0;
		
		for(Object soleReturn : returnsObj) {
			returns[i] = (String) soleReturn;
			i++;
		}
		
		return returns;
			
	}
	
	/**
	 * Demo moves
	 * @param keyHandler
	 */
	public void demo(KeyHandler keyHandler) {
		
		String[][] keybArr = keyHandler.getKeybArr();
		
		keybArr = keyHandler.getKeybArr();
		
		//Test the individual commands
		String keyb= keyHandler.getMap();
		
		System.out.println("Base Keybd:	" + keyb);

		System.out.println("Hor:	 	" + keyHandler.buildKeyboardString(keyHandler.horizontalFlip(keybArr)));
		System.out.println("Ver:		" + keyHandler.buildKeyboardString(keyHandler.verticalFlip(keybArr)));
		
		System.out.println("Flip -12:	" + keyHandler.buildKeyboardString(keyHandler.flip(keybArr,-12)));		
		System.out.println("Flip 5:		" + keyHandler.buildKeyboardString(keyHandler.flip(keybArr,5)));
		System.out.println("Flip -50:	" + keyHandler.buildKeyboardString(keyHandler.flip(keybArr,-50)));
		System.out.println("Flip -10:	" + keyHandler.buildKeyboardString(keyHandler.flip(keybArr,-10)));
		System.out.println("Flip 50:	" + keyHandler.buildKeyboardString(keyHandler.flip(keybArr,50)));
		System.out.println("Flip 99:	" + keyHandler.buildKeyboardString(keyHandler.flip(keybArr,99)));
		System.out.println("Flip 1000:	" + keyHandler.buildKeyboardString(keyHandler.flip(keybArr,1000)));
		System.out.println("Flip -1000:	" + keyHandler.buildKeyboardString(keyHandler.flip(keybArr,-1000)));
		
	}

	/**
	 * Initialize keyboard
	 */
	public KeyHandler() {

		keyboardString = this.buildKeyboardString(keyboardArray);		

	}

	public String getMap() {

		return keyboardString;

	}

	public String[][] getKeybArr() {

		return keyboardArray;

	}

	/**
	 * Proceed with the horizontal flip
	 * @param keyboard
	 * @return new keyboard position array
	 */
	public String[][] horizontalFlip(String[][] keyboard) {

		String newKeyboard[][] = new String[4][10];
		int auxh = 0;
		int auxv = 0;
		for(int i=0; i< 4; i++) {			

			for(int j=9; j> -1; j--) {

				newKeyboard[i][j] = keyboard[auxh][auxv];
				auxv++;

			}
			auxv = 0;
			auxh++;
			
		}	

		return newKeyboard;
	}

	/**
	 * Proceed with the vertical flip
	 * @param keyboard
	 * @return new keyboard position array
	 */
	public String[][] verticalFlip(String[][] keyboard) {

		String newKeyboard[][] = new String[4][10];
		int auxh = 0;

		for(int i=3; i> -1; i--) {

			for(int j=0; j< 10; j++) {

				newKeyboard[auxh][j] = keyboard[i][j];	
			}

			auxh++;
		}	

		return newKeyboard;
	}

	/**
	 * Proceed lateral flip in numbers
	 * @param keyboard
	 * @param digits - number of moves to the right (positive) or left (negative)
	 * @return new keyboard position array
	 */
	public String[][] flip(String[][] keyboard, int digits) {
		//TODO: Still being implemented. Unsafe code
		
		String newKeyboard[][] = new String[4][10];

		int hDeplacement = digits % 10;
		int lines = (digits / 10) ;
		//Todo: implement this for support values greater than 40 and -40.
		int times = (digits / 4); 

		if(lines < 0) {
			lines = (times+1) * -1; 
		}
		if(lines > 4) {
			lines = lines % 4;
		}

		if(hDeplacement < 0) {
			hDeplacement = 10 + hDeplacement; 
		}


		int auxh = lines;
		int auxv = hDeplacement;
		int j = 0;
		int it = 0;
		String currentChar = "";

		for(int i= 0; i< 40; i++) {

			j = i % 10;
			it = (i / 10);
			if(auxv >9) {
				auxv = 0;
				auxh++;
				if(auxh > 3) {
					auxh = 0;
				}
			}
			//newKeyboard[auxh][auxv] = keyboard[it][j];
			currentChar = keyboard[auxh][auxv];
			newKeyboard[it][j] = keyboard[auxh][auxv];
			auxv++;


		}	

		return newKeyboard;
	}

	// Not optimized for Java 6. 
	// From Java 8, we could initialize the JVM with -XX:+UseStringDeduplication	
	/**
	 * Build new Keyboard String from the keyboard array
	 *	- Not optimized for Java 6. It could be Stringbuffer, but the content is too small.
	 *  - From Java 8, we could initialize the JVM with -XX:+UseStringDeduplication	
 	 * @param keyboardArray
	 * @return
	 */
	public String buildKeyboardString(String[][] keyboardArray) {

		String keyboardStringTemp = "";

		for(int i=0; i< 4; i++) {

			for(int j=0; j< 10; j++) {
				
				keyboardStringTemp = keyboardStringTemp + keyboardArray[i][j];

			}

		}

		return keyboardStringTemp;
	}


}
