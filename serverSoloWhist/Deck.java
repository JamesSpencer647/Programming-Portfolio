package serverSoloWhist;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Deck implements Iterable<Card>{
	
	//Fields
	private List<Card> deck;
	
	//Constructor
	public Deck() {
		deck = new ArrayList<Card>();
		
		for(Suit s : Suit.values()){
			for(Rank v : Rank.values())
				deck.add(new Card(s, v));
		}
	}
	
	//Methods
	public void shuffle(){
		Collections.shuffle(deck);
	}
		
	public Card getCard(int i){
		return deck.get(i);
	}
	
	@Override
	public String toString() {
		return deck.toString();
	}
	
	public Iterator<Card> iterator() {
		return deck.iterator();
	}
}
