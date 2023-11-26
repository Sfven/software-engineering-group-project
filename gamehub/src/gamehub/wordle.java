
import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class wordle extends Application {
	
	double OPACITY = 1;
	int sqSize = 50;
	int keyHeight = 15;
	int keyWidth = 20;
	String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	String guess = "";
	String WotD;
	int place = 0;
	ArrayList<String> greyLetters = new ArrayList<String>();
	ArrayList<String> yellowLetters = new ArrayList<String>();
	ArrayList<String> greenLetters = new ArrayList<String>();
	Font guessType = new Font("Arial", 32);
	Font keyType = new Font("Arial", 12);
	Font keyPress = new Font("Arial Black", 12);
	boolean win = false;
	boolean over = false;
	
	/* 
	public static void main(String[] args) throws IOException {
		launch(args);
	}
	*/

	@Override
	public void start(Stage stage) throws Exception {

		/*
		 * Load up lists
		 */
		ArrayList<String> wordleList = new ArrayList<>(); // List for Wordle Word of the Day
		ArrayList<String> extendedList = new ArrayList<>(); // List for eligible words to be guessed
		BufferedReader reader1 = new BufferedReader(new FileReader("../../english_words_10k_mit.txt"));
		BufferedReader reader2 = new BufferedReader(new FileReader("../../english_words_alpha_dwyl.txt"));
		while (reader1.ready()) {
			String word = reader1.readLine();
			if (word.length() == 5) {
				wordleList.add(word);
			}
		}
		extendedList.addAll(wordleList);
		reader1.close();
		while (reader2.ready()) {
			String word = reader2.readLine();
			if (word.length() == 5) {
				extendedList.add(word);
			}
		}
		reader2.close();
		/*
		 * Pick Word of the Day randomly from wordleList
		 */
		Random rand = new Random();
		WotD = wordleList.get(rand.nextInt(wordleList.size()));
		
		//CHEAT
		//System.out.println(WotD);
		
		/*
		 * Build GUI.
		 * Starting with header text.
		 */
		
		GridPane textGrid = new GridPane();
		Text header = new Text();
		header.setText("GUESS THE WORD");
		header.setFont(new Font("Arial Bold Italic", 28));
		header.setTextAlignment(TextAlignment.CENTER);
		textGrid.setAlignment(Pos.CENTER);
		textGrid.add(header, 0, 0);
		
		/*
		 * Create Wordle Grid. 
		 */
		
		GridPane wordleGrid = new GridPane();
		
		Background bg = new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
		Background clear = new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY));
		Background green = new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY));
		Background yellow = new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY));
		Background grey = new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY));
		Background lightgrey = new Background(new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY));
		for (int row = 0; row < 6; row++) {
			for (int column = 0; column < 5; column++) {
				Rectangle square = new Rectangle(sqSize, sqSize, Color.WHITE);
				square.setStrokeWidth(1);
				square.setStroke(Color.BLACK);
				TextField text = new TextField();
				text.setPrefColumnCount(1);
				text.setBackground(clear);
				text.setPadding(new Insets(10, 10, 10, 10));
				text.setPrefSize(sqSize-20, sqSize-20);
				text.setFont(guessType);
				text.setEditable(false);
				text.setMouseTransparent(true);
				text.setFocusTraversable(true);
				text.textProperty().addListener(new ChangeListener<String>() {
					/*
					 * Set each textbox rules.
					 */
			        @Override
			        public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
			            text.setText(text.getText().toUpperCase()); // always capitalized
			        	if (text.getText().length() > 1) { // can only contain one letter per box
			                String s = text.getText().substring(0, 1);
			                text.setText(s);
			            }
			            if (!letters.contains(text.getText())) { // can only type alphabet keys
			            	text.clear();
			            }
			            if (greyLetters.contains(text.getText())) { // don't think this works. meant to prevent reusing eliminated letters.
			            	text.clear();
			            	//System.out.println("grey");
			            }
			        }
			    });
			
				StackPane stack = new StackPane();
				stack.getChildren().addAll(square, text); // add square element and textfield element to stack
				wordleGrid.add(stack, column, row); // add stack to grid
			}
		}
		
		/*
		 * Create keyboard below wordle grid
		 */
		
		GridPane keyboardGrid = new GridPane();
		String QWERTY = "QWERTYUIOP"; // row one
		String ASDF = "ASDFGHJKL"; // row two
		String ZXCV = "ZXCVBNM"; // row three
		String QWERTY_FULL = "QWERTYUIOPASDFGHJKLZXCVBNM"; // full keyboard letters (effectively a duplicate of 'letters' but whatever)
		
		for (int row = 0; row < 3; row++) {
			String KEYROW;
			if (row == 0) {
				KEYROW = QWERTY;
			}
			else if (row == 1) {
				KEYROW = ASDF;
			}
			else {
				KEYROW = ZXCV;
			}
			for (int keyPlace = 0; keyPlace < KEYROW.length(); keyPlace++) {
				String key = KEYROW.substring(keyPlace, keyPlace+1);
				Rectangle keyRect = new Rectangle(keyHeight, keyWidth, Color.WHITE);
				keyRect.setStrokeWidth(1);
				keyRect.setStroke(Color.WHITE);
				TextField keyF = new TextField();
				keyF.setPrefColumnCount(1);
				keyF.setBackground(clear);
				keyF.setPadding(new Insets(1, 1, 1, 1));
				keyF.setPrefSize(keyHeight, keyWidth);
				keyF.setFont(keyType);
				keyF.setText(key);
				keyF.setEditable(false);
				keyF.setMouseTransparent(true);
				keyF.setFocusTraversable(false);
				
				/*
				 * Make on-screen keys clickable.
				 */
				keyF.setOnMouseClicked(new EventHandler<MouseEvent>() {
						 
					    @Override
					    public void handle(MouseEvent event) {
					    	try {
								Robot robot = new Robot();
								char keyCode = keyF.getText().charAt(0);
								robot.keyPress(java.awt.event.KeyEvent.getExtendedKeyCodeForChar(keyCode));
								robot.keyRelease(java.awt.event.KeyEvent.getExtendedKeyCodeForChar(keyCode));
							} catch (AWTException e) {
								e.printStackTrace();
							}
					    }
				});
				
				keyRect.setOnMouseClicked(new EventHandler<MouseEvent>() {
					 
				    @Override
				    public void handle(MouseEvent event) {
				    	try {
							Robot robot = new Robot();
							char keyCode = keyF.getText().charAt(0);
							robot.keyPress(java.awt.event.KeyEvent.getExtendedKeyCodeForChar(keyCode));
							robot.keyRelease(java.awt.event.KeyEvent.getExtendedKeyCodeForChar(keyCode));
						} catch (AWTException e) {
							e.printStackTrace();
						}
				    }
			});
				
				StackPane stack = new StackPane();
				stack.setAlignment(Pos.CENTER);
				stack.getChildren().addAll(keyRect, keyF);
				keyboardGrid.add(stack, keyPlace, row);
			}
		}
		keyboardGrid.setAlignment(Pos.CENTER);
		
		
		/* BIG LOOP */
		/*
		 * Loop determines behavior for typing into grid, 
		 * passing focus from one box to the next,
		 * and behavior for keys typed, ENTER, BACKSPACE
		 */
		for (int bigCount = 0; bigCount<6; ++bigCount) {
		
			int place = bigCount*5; // iterator through every box
			int num = (bigCount+1)*5; // end of row
			
			TextField firstText = ((TextField) ((Pane) wordleGrid.getChildren().get(0)).getChildren().get(1));
			firstText.setEditable(true);
		
			while (place < num) {
				TextField text = ((TextField) ((Pane) wordleGrid.getChildren().get(place)).getChildren().get(1));
				int next = place+1;
				int prev = place-1;
				int start = bigCount*5;
				int end = ((bigCount+1)*5)-1;
				int jump = end+1;
				if (jump > 29) {
					jump = 29;
				}
				//int thing1 = place / 5;
				if (place == start) {
					prev = place;
				}
				if (place == end) {
					next = place;
				}
				
				TextField nextText = ((TextField) ((Pane) wordleGrid.getChildren().get(next)).getChildren().get(1)); //for after key typed
				TextField prevText = ((TextField) ((Pane) wordleGrid.getChildren().get(prev)).getChildren().get(1)); //for backspace
				//TextField endText = ((TextField) ((Pane) wordleGrid.getChildren().get(end)).getChildren().get(1));
				TextField jumpText = ((TextField) ((Pane) wordleGrid.getChildren().get(jump)).getChildren().get(1)); // for enter pressed (if full word entered)
				
					text.setOnKeyPressed(new EventHandler<KeyEvent>() {
						 
					    @Override
					    public void handle(KeyEvent event) {
					    	
					    	/*
					    	 * Visual cue on on-screen keyboard when key pressed. 
					    	 * Currently adds border/stroke around key
					    	 */
					    	
					    	String keyVal = event.getCode().toString();
				            int keyPos = 0;
				            if (QWERTY_FULL.contains(keyVal)) {
				            	keyPos = QWERTY_FULL.indexOf(keyVal);
				            	//((TextField) ((Pane) keyboardGrid.getChildren().get(keyPos)).getChildren().get(1)).setFont(keyPress);
				            	((Rectangle) ((Pane) keyboardGrid.getChildren().get(keyPos)).getChildren().get(0)).setStroke(Color.BLACK);
				            	//text.setText(keyVal);
				            	//nextText.requestFocus();
				            }
				            
				            
						    /*
						     * Backspace behavior
						     */
					    	if(event.getCode().equals(KeyCode.BACK_SPACE)) {
					        	
					    		if (!over) {
						    		prevText.setEditable(true);
						            prevText.requestFocus();
					    		}
				        	
					    	}		
					    	/*
					    	 * Enter behavior
					    	 * Includes game logic for checking guessed word
					    	 */
					        if(event.getCode().equals(KeyCode.ENTER)) {
					        	guess = "";
					        	
					        	// assemble guess word from the five textfields
					        	for (int i = start; i < num; i++) {
					        		guess = guess.concat(((TextField) ((Pane) wordleGrid.getChildren().get(i)).getChildren().get(1)).getText());
					        	}
					        	guess = guess.toLowerCase(); // wordleList has words in lowercase
					        	System.out.println(guess);
					        	
					        	// check if invalid guess
			        			if (!extendedList.contains(guess)) {
			        				for (int j = start; j < num; j++) {
						        		((TextField) ((Pane) wordleGrid.getChildren().get(j)).getChildren().get(1)).clear();
						        	}
			        				((TextField) ((Pane) wordleGrid.getChildren().get(start)).getChildren().get(1)).requestFocus();
			        				guess = "";
			        			}
			        			// proceed if valid guess
			        			else {
			        				/*
			        				 * Loop through and change to pertinent color (grey, yellow, green)
			        				 * Add to pertinent letter lists (grey, yellow, green)
			        				 */
			        				for (int k = 0; k < guess.length(); k++) {
			        					char guessChar = guess.charAt(k);
			        					String guessCharStr = String.valueOf(guessChar); // same as guessChar but String. 
			        					
			        					if (guessChar == WotD.charAt(k)) { // GREEN LETTERS
			        						// FIXME: k in the line below needs to be replaced with the correct int
			        						int n = start + k; // translates guessChar index in guess to index in wordleGrid
			        						((Rectangle) ((Pane) wordleGrid.getChildren().get(n)).getChildren().get(0)).setFill(Color.GREEN);
			        						//((TextField) ((Pane) wordleGrid.getChildren().get(n)).getChildren().get(1)).setBackground(green);
			        						if (!greenLetters.contains(guessCharStr)) {
			        							greenLetters.add(guessCharStr);
			        							if (yellowLetters.contains(guessCharStr)) {
			        								yellowLetters.remove(guessCharStr);
			        							}
			        						}
			        					}
			        					else if (WotD.contains(guessCharStr)) { // YELLOW LETTERS
			        						// FIXME: k in the line below needs to be replaced with the correct int // note: idk what the heck this was about
			        						int n = start + k; // translates guessChar index in guess to index in wordleGrid
			        						((Rectangle) ((Pane) wordleGrid.getChildren().get(n)).getChildren().get(0)).setFill(Color.YELLOW);
			        						//((TextField) ((Pane) wordleGrid.getChildren().get(n)).getChildren().get(1)).setBackground(yellow);
			        						if (!yellowLetters.contains(guessCharStr)) {
			        							yellowLetters.add(guessCharStr);
			        						}
			        					}
			        					else { // GREY LETTERS
			        						// FIXME: k in the line below needs to be replaced with the correct int
			        						int n = start + k; // translates guessChar index in guess to index in wordleGrid
			        						((Rectangle) ((Pane) wordleGrid.getChildren().get(n)).getChildren().get(0)).setFill(Color.GREY);
			        						//((TextField) ((Pane) wordleGrid.getChildren().get(n)).getChildren().get(1)).setBackground(grey);
			        						if (!greyLetters.contains(guessCharStr)) {
			        							greyLetters.add(guessCharStr);
			        						}
			        					}
			        				}
			        				
			        				/*
			        				 * Move to next line.
			        				 */
			        				jumpText.setEditable(true);
			        				jumpText.requestFocus();
			        				
			        				/*
			        				 * Change color of letters on keyboard
			        				 */
			        				
				        			for (String letter : greyLetters) {
				        					
		        							int letterPos = QWERTY_FULL.indexOf(letter.toUpperCase());
		    				            	((Rectangle) ((Pane) keyboardGrid.getChildren().get(letterPos)).getChildren().get(0)).setFill(Color.GREY);
		    				    			//((TextField) ((Pane) keyboardGrid.getChildren().get(letterPos)).getChildren().get(1)).setBackground(grey);
	        							
			        				}
				        			for (String letter : yellowLetters) {
				        					
		        							int letterPos = QWERTY_FULL.indexOf(letter.toUpperCase());
		    				            	((Rectangle) ((Pane) keyboardGrid.getChildren().get(letterPos)).getChildren().get(0)).setFill(Color.YELLOW);
		    				    			//((TextField) ((Pane) keyboardGrid.getChildren().get(letterPos)).getChildren().get(1)).setBackground(yellow);
		        							
				        			}
				        			for (String letter : greenLetters) {
				        					
		        							int letterPos = QWERTY_FULL.indexOf(letter.toUpperCase());
		    				            	((Rectangle) ((Pane) keyboardGrid.getChildren().get(letterPos)).getChildren().get(0)).setFill(Color.GREEN);
		    				    			//((TextField) ((Pane) keyboardGrid.getChildren().get(letterPos)).getChildren().get(1)).setBackground(green);
	        							
			        				}
			        				
			        				
			        				System.out.println("green: " + greenLetters.toString());
			        				System.out.println("yellow: " + yellowLetters.toString());
			        				System.out.println("grey: " + greyLetters.toString());
			        				
			        				/* 
			        				 * Check if win or game over
			        				 * 
			        				 */
			        				if (guess.equals(WotD)) {
			        					over = true;
			        					win = true;
			        					endGame(wordleGrid, textGrid, win, WotD);
			        				}
			        				else if (((TextField) ((Pane) wordleGrid.getChildren().get(29)).getChildren().get(1)).isFocused()) {
			        					over = true;
			        					win = false;
			        					endGame(wordleGrid, textGrid, win, WotD);
			        				}

			        			}
					        	
					        }
					        
					}
					});
					
					/*
					 * For passing focus to next textfield
					 */
					text.setOnKeyTyped(new EventHandler<KeyEvent>() {
						 
					    @Override
					    public void handle(KeyEvent event) {
					    	//text.setText(event.getText());
					    	if (letters.contains(event.getCharacter().toUpperCase()) && !over) {
					        	nextText.setEditable(true);
					            nextText.requestFocus();
					            
				            //nextText.clear();
					    	}
					    }
					});
					/*
					 * Visual cue for on-screen keyboard when key is released (after being pressed).
					 * Currently removes border/stroke around key.
					 */
					text.setOnKeyReleased(new EventHandler<KeyEvent>() {
						 
					    @Override
					    public void handle(KeyEvent event) {
					    	String keyVal = event.getCode().toString();
				            int keyPos = 0;
				            if (QWERTY_FULL.contains(keyVal)) {
				            	keyPos = QWERTY_FULL.indexOf(keyVal);
				            	//((Rectangle) ((Pane) keyboardGrid.getChildren().get(keyPos)).getChildren().get(0)).setStrokeWidth(1);
				            	//((TextField) ((Pane) keyboardGrid.getChildren().get(keyPos)).getChildren().get(1)).setFont(keyType);
				            	((Rectangle) ((Pane) keyboardGrid.getChildren().get(keyPos)).getChildren().get(0)).setStroke(Color.WHITE);
					    	}
					    }
					});
					
				place++;
				
			}
		
		}//end BIG FOR LOOP
		
		/*
		 * Grid formatting
		 */
		
		textGrid.setPadding(new Insets(10, 10, 10, 10));
		textGrid.setHgap(10);
		textGrid.setVgap(10);
		
		wordleGrid.setPadding(new Insets(10, 10, 10, 10));
		wordleGrid.setHgap(10);
		wordleGrid.setVgap(10);
		
		keyboardGrid.setPadding(new Insets(10, 10, 10, 10));
		keyboardGrid.setHgap(10);
		keyboardGrid.setVgap(10);
		
		/*
		 * Assemble full grid with header text, wordleGrid, and keyboardGrid.
		 */
		
		GridPane grid = new GridPane();
		grid.add(textGrid, 0, 0);
		grid.add(wordleGrid, 0, 1);
		grid.add(keyboardGrid,  0, 2);
		Scene scene = new Scene(grid);
		
		
		
		stage.setMaximized(false);
		stage.setScene(scene);
		stage.setTitle("Wordle");
		stage.show();
		
	}
	
	/*
	 * Upon win condition reached or all six guesses expended, 
	 * set all tiles as uneditable 
	 * and change header to appropriate message.
	 */
	public static void endGame(GridPane wordleGrid, GridPane textGrid, boolean win, String WotD) {
		for (Node box : wordleGrid.getChildren()) {
			((TextField) ((Pane) box).getChildren().get(1)).setEditable(false);
			((TextField) ((Pane) box).getChildren().get(1)).setFocusTraversable(false);
		}
		
		if (win) {
			System.out.println("You win!");
			((Text) ((Pane) textGrid).getChildren().get(0)).setText("YOU WIN!");
			
		}
		else {
			System.out.println("You lose!");
			((Text) ((Pane) textGrid).getChildren().get(0)).setText("YOU LOSE: " + WotD.toUpperCase());
		}
	}

}
