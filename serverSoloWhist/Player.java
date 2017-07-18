package serverSoloWhist;

public class Player {

	//Fields
	private String name;
	protected Hand hand;
	protected Card lastCardPlayed;
	private int score;
	
	//Constructor
	public Player(String name) {
		this.name = name;
		hand = new Hand();
		score = 0;
	}
	
	//Methods
	public String getName() {
		return name;
	}
	
	public Hand getHand() {
		return hand;
	}
	
	public Card getPlayedCard() {
		return lastCardPlayed;
	}
	
	public int getScore() {
		return score;
	}
	
	public void incScore() {
		score++;
	}
	
	public void playCard(Card card, Trick trick) {
		/*
		 * card is passed to the method when it is clicked on
		 * set lastCardPlayed to the card
		 * return the Card
		 * 
		 * !!!!The server will have already checked that the card
		 * is a legal move
		 */
		lastCardPlayed = card;
		trick.addCard(card);
		
		hand.remove(card);
		
	}
}
