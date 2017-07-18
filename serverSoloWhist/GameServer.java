/* GameServer - the server which will control player connections
 * game state, enforce rules and control gameplay*/

package serverSoloWhist;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;


public class GameServer {
	
	private static final int PORT = 8901;
	
	private static List<PrintWriter> writers = new ArrayList<>();
	private static List<BufferedReader> readers = new ArrayList<>();
	
	private static List<Player> players = new ArrayList<>();
	
	private static Deck deck = new Deck();
	private static List<Trick> game = new ArrayList<>();
	
	public static void main(String[] args) {
		
		ServerSocket listener = null;
		Socket clientSocket = null;
		
		/*Create a new server socket*/
		try {
			listener = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Game Server is running");
		
		/*allow up to 4 multi-threaded connections*/
		try {
			while (players.size()<4) {
				clientSocket = listener.accept();
				Handler clientHandler = new Handler(clientSocket);
				clientHandler.start();
			}				
				
		} catch(IOException e) {
					System.out.println("Accept failed:" + e);
		} finally {
				try {
					listener.close();
				} catch (IOException e) {
						System.out.println(e);
				}
			}
	
	
	}
	
	/*Handler class allows the connections to be multithreaded,
	 * alowing multiple simultaneous connections*/
	static class Handler extends Thread {
		
		String name;
		Socket socket;
		Suit suit = Suit.values()[0];
		int turn = 0, round = 0;
		
		public Handler(Socket s) {
			this.socket = s;
		}
		
		public void run() {
			BufferedReader in = null;
		    PrintWriter out = null;
		    Card card = null;
		    int[] tricksWon = {0,0,0,0};
			int i = 0;
			try {
				in = new BufferedReader(new InputStreamReader(
	                    socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				
				/* 
				 * The server will now wait for the client to submit
				 * a name. If the client inputs an empty String as name
				 * they will be named playerN where N is the current
				 * number of players + 1.
				 */
				
				name = in.readLine();
				if(name == null) {
					return;
				}
				if(name.isEmpty()){
					name = "Player " + (writers.size()+1);
				}
				
				
				writers.add(out);
				readers.add(in);
				players.add(new Player(name));
				out.println("WELCOME" + name);
				out.flush();
				
				
				/*Wait for 10 seconds for other human players*/
				System.out.println("Waiting for other players...");
				long startTimer = System.currentTimeMillis();
				while(System.currentTimeMillis() - startTimer < 10000) {
				}
				for (i=0; i<3; i++) {
					if (players.size() < 4) {
						players.add(new AIPlayer("Computer" + (i+1)));
						/*null readers and writers are used so that the index for players, readers 
						 * and writers will always be the same for each human player*/
						writers.add(null);
						readers.add(null);
					}
				}
				
				System.out.println("All players connected");
				int ai = 0, human = 0;
				for (Player p : players) {
					if(p.getClass().getSimpleName().startsWith("Player") ) {
						human++;
					}
					else
						ai++;
				}
				System.out.println("Players - Human: " + human + ", Computer: " + ai);
				
				/*Game setup is complete, the following loops control the game-play
				 * 4 rounds, one for each trump suit.
				 * 13 tricks in each round, 4 cards in each trick*/
				
				while (round < 4) {
					
					deal();
					
					/*This loop is for the 13 tricks*/
					while ( game.size()<13 ){
						for (PrintWriter pw : writers) {
							if (pw != null) {
								pw.println("ROUND_PLAY");
							}
						}
						
						game.add(new Trick(Suit.values()[round]));
						Trick thisTrick = game.get(game.size()-1);
						System.out.println("\tRound " + (game.size()-1) + "\n\tTrump = " + thisTrick.getTrumpSuit());
						
						
						
						/*This loop is for each players turn
						 * continues until the trick contains 4 cards*/
						while ( thisTrick.getTrick().size() < 4){
														
							System.out.println("turn = " + turn);
							
							//if its a human player send the YOUR_TURN message
							if(writers.get(turn) != null) {
								writers.get(turn).println("YOUR_TURN");
								
								//round n
								writers.get(turn).println((game.size()));
								//trump = 
								writers.get(turn).println(thisTrick.getTrumpSuit());
								//trick =
								writers.get(turn).println(thisTrick.getTrick().toString());
								
							}
							
							//control for human players
							if (players.get(turn).getClass().getSimpleName().startsWith("Player"))
							{

								
								//System.out.println("Sent YOUR_TURN to " + players.get(turn).getName());
								
								String cardPlayed = readers.get(turn).readLine();
								
								//Cast cardPlayed to Card
								for (Rank r : Rank.values()) {
									if (r.toString().equals(cardPlayed.split("\\s+")[0])) {
										for (Suit s : Suit.values()) {
											if(s.toString().equals(cardPlayed.split("\\s+")[2])) {
												card = new Card(s,r);
											}
										}
									}
								}
								System.out.println("card = " + card);
								
								/*If this is the first card played then:
								 * It is legal, whatever the card is
								 * and the leading suit must be set*/
								if ( thisTrick.getTrick().size() > 0 ) {
									
									boolean legal = legalMove(players.get(turn), card, thisTrick.getLeadingSuit());
									while(!legal) {
										//System.out.println("card follows suit(" + 
												//thisTrick.getLeadingSuit()+ ") = " + legal);
										//inform the player of the move being illegal
										writers.get(turn).println("ILLEGAL_MOVE");
										//receive their next turn
										cardPlayed = readers.get(turn).readLine();
										//Cast cardPlayed to Card
										for (Rank r : Rank.values()) {
											if (r.toString().equals(cardPlayed.split("\\s+")[0])) {
												for (Suit s : Suit.values()) {
													if(s.toString().equals(cardPlayed.split("\\s+")[2])) {
														card = new Card(s,r);
													}
												}
											}
										}
										//System.out.println("new card = " + card);
										legal = legalMove(players.get(turn), card, thisTrick.getLeadingSuit());
									}	
								}
								
								//a legal move has now been played
								writers.get(turn).println("LEGAL_MOVE");
								//update the game state
								players.get(turn).playCard(card, thisTrick);
								/*update AIPlayers' playedCards list*/
								for (Player p : players) {
									if (p.getClass().getSimpleName() == "AIPlayer") {
										((AIPlayer) p).cardPlayed(card);
									}
								}
								
								writers.get(turn).flush();
								turn = (turn+1)%4;
							}
							//for AIPlayers
							else {
								
								AIPlayer p = (AIPlayer) players.get(turn);
								System.out.println(p.getName() + " chooses " + p.chooseCard(thisTrick));
								p.playCard(p.chooseCard(thisTrick), thisTrick);
								
								turn = (turn+1)%4;
							}
							
							
						}
						System.out.println("Trick ended!");
						
						/*The complete trick is analysed and the winning card is chosen*/
						System.out.println("Trick = " + thisTrick.getTrick());
						for (Player p : players) {
							System.out.println("" + p.getName() + "played: " + p.getPlayedCard());
						}
						int winner = winner(thisTrick);
						System.out.println("WINNER = " + players.get(winner).getName());
						tricksWon[winner]++;
						for (int t : tricksWon) {
							System.out.println(t);
						}
						turn = winner;
						for (PrintWriter pr : writers) {
							if (pr != null) {
								pr.println("Trick: " + thisTrick.getTrick());
								for (i=0;i<4;i++) {
									pr.println(players.get(i).getName() + ": " + tricksWon[i]);
								}
							}
						}
					}
					
					
					System.out.println("Round ended!");
					for (PrintWriter pr : writers) {
						if (pr != null) {
							pr.println("ROUND_OVER");
						}
					}
					/*Find the highest number of tricks won*/
					int mostTricks = 0;
					for (int t : tricksWon) {
						if(t>mostTricks)
							mostTricks = t;
					}
					/*Give anyone with that many trick a point*/
					for (i=0; i<4; i++) {
						if(tricksWon[i] == mostTricks)
							players.get(i).incScore();
					}
					/*Inform all players of the score*/
					for (PrintWriter pr : writers) {
						for (i=0;i<4;i++) {
							if (pr != null) {
								pr.println(players.get(i).getName() + ": " + players.get(i).getScore());
							}
						}
					}
					round++;
					game.clear();
					for (i=0; i<4; i++) {
						tricksWon[i] = 0;
					}
				}
				int winningScore = 0;
				int numWinners = 0;
				/*find the highest score*/
				for (Player p : players) {
					if (p.getScore() > winningScore)
						winningScore = p.getScore();
				}
				/*find the number of winners*/
				for (Player p : players) {
					if (p.getScore() == winningScore)
						numWinners++;
				}
				/*output final result*/
				if (numWinners == 1) {
					for (i=0; i<4; i++) {
						if (writers.get(i) != null) {
							if (players.get(i).getScore() == winningScore)
								writers.get(i).println("WIN");
							else
								writers.get(i).println("LOSE");
						}
					}
				}
				else {
					for (i=0; i<4; i++) {
						if (writers.get(i) != null) {
							if (players.get(i).getScore() == winningScore)
								writers.get(i).println("DRAW");
							else
								writers.get(i).println("LOSE");	
						}
					}
				}
				
				
			} 
			catch (IOException e) {
	            System.out.println(e);
	        } 
			finally {
	            // This client is closing down. Remove its print
	            // writer from the set, and close its socket.
	            
	            if (out != null) {
					writers.remove(out);
					readers.remove(in);
	            }
	            try {
	                socket.close();
	            } 
	            catch (IOException e) {
	            }
	        }
			players.clear();
			game.clear();
		}
		
		private void deal() {
			/*Shuffle the deck and deal the cards at the
			 * beginning of each round*/
			deck.shuffle();
			System.out.println("Dealing cards");
			int i = 0;
			for (Card c : deck) {
				players.get(i).getHand().addCard(c);
				i++;
				i = i % 4;
			}
			for (Player p : players) {
				p.getHand().sort();
				System.out.println(p.getHand());
			}
			System.out.println("All cards dealt");
			for (i=0; i<4; i++) {
				players.get(i).getHand().sort();
				if (writers.get(i) != null) {
					writers.get(i).println(players.get(i).getHand().toString());
				}
			}
		}
		
		private boolean legalMove(Player player, Card card, Suit leadingSuit) {
			boolean legal = true;
			/*If - the cards suit matches the leading suit
			 * the move is legal*/
			if ( card.getSuit() == (leadingSuit) ) {
				legal = true;
			}
			/*Else - If the suit does not match the leading suit but the player
			 * has a card in their hand which does, the move is illegal.
			 * Otherwise its legal*/
			else {
				for (Card c : player.getHand() ) {
					if (c.getSuit() == leadingSuit) {
						legal = false;
					}
				}
			}
			
			return legal;
		}
		
		private int winner(Trick trick) {
			
			Card winCard = trick.getTrick().get(0);
			int winner = 0;

			for (Card c : trick.getTrick()) {
				//if this card is a trump
				if (c.getSuit() == trick.getTrumpSuit()) {
					//but the current winCard is not then this card is the current winCard
					if(winCard.getSuit() != trick.getTrumpSuit()) {
						winCard = c;
					}
					//if the current winCard is a trump but this card has a higher rank
					else if (c.getRank().compareTo(winCard.getRank()) > 0) {
						winCard = c;
					}
				}
				/*if a card is not a trump and the current winning card is there is no need to assess it*/
				else if (c.getSuit() == trick.getLeadingSuit() && winCard.getSuit() != trick.getTrumpSuit()) {
					if (c.getRank().compareTo(winCard.getRank()) > 0) {
						winCard = c;
					}
				}
			}
			
			/* now find out who played winCard and return their position in the player list*/
			for (int i = 0;i<players.size();i++) {
				if (players.get(i).getPlayedCard().equals(winCard)) {
					winner = i;
				}
			}
			return winner;
		}
	}
	
}


