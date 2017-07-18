package soloWhist;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class PlayerClient extends Application{
	
	Socket socket = null;
	static String nameInput = null;
	static BufferedReader in = null;
	static PrintWriter out = null;
	static String message = null;
	static ArrayList<String> cards = new ArrayList<>();
	static ArrayList<String> trick = null;
	static int switchScene;
	
	//Scene objects
	Stage setupStage;
	Scene scene, table;
	BorderPane root;
	HBox b1;
	GridPane grid;
	Label name, serverAddr;
	TextField nameTextField, serverAddrTextField;
	Button submitBtn;
	HBox hbBtn;
	Text inputResponse;
	
	//Where everything after the setup happens
	public void tableScene() {
		
		Scanner sc = new Scanner(System.in);
		int card = -1;
		int round = 0;
		try {
			while (round < 4) {
				
				message = in.readLine();
				System.out.println("Cards received: " + message);
				/*Add each card to cards*/
				String[] cardz = message.replace("[", "").replace("]", "").split(", ");
				for (String c : cardz) {
					cards.add(c);
				}
				/*Playing cards in a round (13 turns)*/
				while (!in.readLine().startsWith("ROUND_OVER")) {
	        		
        			message = in.readLine();
        			//System.out.println("Message received: " + message);
        			if(message.startsWith("YOUR_TURN")){
        				
        				System.out.println("---------------------------------------");
        				System.out.println("Round " + in.readLine());
        				System.out.println("Trump Suit = " + in.readLine());
        				System.out.println("Trick = " + in.readLine());
        				int i = 0;
        				for (String c : cards){
        					System.out.println("" + (i+1) + ": " + c);
        					i++;
        				}
        				
        				while(!message.startsWith("LEGAL_MOVE")) {
        					System.out.println("Pick a card to play: ");
	                    	card = sc.nextInt();
	                    	while(card > cards.size() || card < 1) {
	                    		System.out.println("Illegal Move: Not your card!");
	                    		card = sc.nextInt();
	                    	}
	                    	out.println(cards.get(card-1));
	                    	out.flush();
	                    	
	                    	message = in.readLine();
	                    	if (message.equals("ILLEGAL_MOVE")) {
	                    		System.out.println("You must follow suit if you can!");
	                    	}	                    	
        				}
        				cards.remove(card-1);
        				System.out.println();
        				System.out.println(in.readLine());
        				System.out.println("Tricks Won:\n\t" + in.readLine());
        				System.out.println("\t" + in.readLine());
        				System.out.println("\t" + in.readLine());
        				System.out.println("\t" + in.readLine());
        				
        				
        			}
	        		 
	        	}
					
				System.out.println();
    			System.out.println("Scores:\n\t" + in.readLine());
    			System.out.println("\t" + in.readLine());
    			System.out.println("\t" + in.readLine());
    			System.out.println("\t" + in.readLine());
		    	
				round++;
			}
			switch(in.readLine()) {
				case("WIN") :	
					System.out.println("You Win!");
					break;
				case("DRAW"):	
					System.out.println("You Draw");
					break;
				default:
					System.out.println("You Lose");
			}
			
		} catch (IOException e) {
			System.out.println("Read failed: " + e);
			System.exit(-1);
		} finally {
		
        	try {
        		System.out.println("\n\n\n\nFin--------------");
            	socket.close();
        	} catch(IOException e4) {
        		System.out.println(e4);
        	}
        }
	}
	
	/*This class is for displaying the card, it will be changed to include the use of card faces
	 * at a later stage*/
	class CardRect extends Parent {
		
		CardRect(String card) {
			Rectangle border = new Rectangle(52, 72);
	        Rectangle rect = new Rectangle(50, 70);
	        rect.setFill(Color.WHITE);
	        
	        Text text = new Text(card);
	        text.setWrappingWidth(45);
	        
	         getChildren().add(new StackPane(border, rect, text));
		}
	}

/////////////////////////////////////////////////////////////////////////
	public void start(Stage nullStage) throws Exception{
		
		setupStage = new Stage();
		scene = null;
		root = new BorderPane();
		root.setStyle("-fx-background-color:green");
		
		//these are for root being a gridpane 
		//root.setAlignment(Pos.TOP_LEFT);
		//root.setHgap(100);
		//root.setVgap(20);
		root.setPadding(new Insets(50,50,50,50));
		root.setPrefSize(400,1000);
		//////////////////////////////////////////////////
		HBox b1 = new HBox(2);
		b1.setPrefWidth(800);
		
		//code for the setup stage which will launch the tableStage upon completion
		
		setupStage.setTitle("Solo Whist");
		setupStage.setMaxHeight(200);
		setupStage.setMinWidth(350);
		
		GridPane grid = new GridPane();
		
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10,10,10,10));
		
		name = new Label("Player Name:");
		grid.add(name, 0, 1);
		
		nameTextField = new TextField();
		grid.add(nameTextField, 1, 1);
		
		serverAddr = new Label("Server Address:");
		grid.add(serverAddr, 0, 2);
		
		serverAddrTextField = new TextField();
		grid.add(serverAddrTextField, 1, 2);
		
		/*
		 * Button to enter the name and server address
		 * Causes the name and address to be stored and closes the window 
		 */
		submitBtn = new Button("Submit");
		submitBtn.setOnAction(e -> ButtonClicked(e));
		hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(submitBtn);
        grid.add(hbBtn, 1, 4);
        
        //Text to be displayed if an incorrect server address is input
        inputResponse = new Text();
        inputResponse.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        inputResponse.setFill(Color.RED);
        grid.add(inputResponse, 0, 3, 3, 2);
		
        
        scene = new Scene(grid, 250, 450);
		setupStage.setScene(scene);	
		setupStage.show();
	}
	//////////////////////////////////////////////////////////////////
	public void ButtonClicked(ActionEvent ev) {
		
		nameInput = nameTextField.getText();
		String serverAddrInput = serverAddrTextField.getText();
		if(serverAddrInput.isEmpty()) {serverAddrInput = "localhost";}
		
		//System.out.println(nameInput + "\n" + serverAddrInput);
		
        try {
        	socket = new Socket(serverAddrInput, 8901);
        } 
        catch (Exception e1) {
        	System.out.print("Server Address error: " + e1);
        }
        finally {
        	if(socket != null) {
        		
        		setupStage.close();
        		inputResponse.setText("Connection made: waiting for players.");
            	try{
		    		/*
		    		 * Add wait here so that a fifth client will
		    		 * timeout
		    		 */
		            in = new BufferedReader(new InputStreamReader(
		            		socket.getInputStream()));
		            out = new PrintWriter(socket.getOutputStream(), true);
		            
		            out.println(nameInput);
		            out.flush();

		            message = in.readLine();
		            System.out.println("Message received: " + message);
		            if(nameInput.length() == 0)
		            	nameInput = message.replace("WELCOME", "");
		            
		            //System.out.println("nameInput = " + nameInput);
            		
		        } catch(IOException e3) {
		        	System.out.println(e3);
		        } finally {
		        	
		        	//setupStage.setMaxHeight(500);
		    		//setupStage.setMinWidth(900);
		    		//root.getChildren().add(b1);
		        	//root.setBottom(b1);
		        	
		        	//table = new Scene(root, 500, 500);
		        	//setupStage.setScene(table);
		        	//setupStage.show();
		        	tableScene();
		        }
        	}
        	else {
        		inputResponse.setText("Enter a valid server address.");
        		serverAddrTextField.clear();
        	}
    	}
	}
	//////////////////////////////////////////////////////////////////
	public static void main(String[] args) throws Exception {
	
		Application.launch(args);
	}
}
