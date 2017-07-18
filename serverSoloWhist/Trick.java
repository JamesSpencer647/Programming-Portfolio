package serverSoloWhist;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Trick implements Iterable<Card>{

	//Fields
	private List<Card> trick;
	private Suit leadingSuit;
	private Suit trumpSuit;
	
	//Constructor
	public Trick(Suit trump) {
		trick = new ArrayList<Card>();
		trumpSuit = trump;
		leadingSuit = null;
	}
	
	//Methods
	public void addCard(Card c) {
		if(trick.size() == 0) {
			leadingSuit = c.getSuit();
		}
		trick.add(c);
	}
	
	public List<Card> getTrick() {
		return trick;
	}
	
	public void setLeadingSuit(Suit s) {
		leadingSuit = s;
	}
	
	public Suit getLeadingSuit() {
		return leadingSuit;
	}
	
	public Suit getTrumpSuit() {
		return trumpSuit;
	}
	
	public Iterator<Card> iterator() {
		return trick.iterator();
	}
}
