package serverSoloWhist;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Hand implements Iterable<Card>{
	
	//Fields
	private List<Card> hand;
	
	//Constructor
	public Hand() {
		hand = new ArrayList<Card>();
	}
	
	//Methods
	public void addCard(Card c){
		hand.add(c);
	}
	
	public Card getCard(int i){
		return hand.get(i);
	}
	
	public void sort()	{
		Collections.sort(hand);
	}
	
	public int size() {
		return hand.size();
	}
	
	public void remove(Card card) {
		hand.remove(card);
	}
	
	//clear method just for testing, cards are removed when played
	public void clear() {
		hand.clear();
	}
	
	@Override
	public String toString() {
		return hand.toString();
	}
	
	public Iterator<Card> iterator() {
		return hand.iterator();
	}
}
