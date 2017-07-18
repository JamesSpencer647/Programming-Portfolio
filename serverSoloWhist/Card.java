package serverSoloWhist;

public class Card implements Comparable<Card>{

    //Fields
	private final Suit suit;

    private final Rank rank;

    //Constructor
    public Card(Suit suit, Rank rank){
         this.suit=suit;
         this.rank=rank;
    }

    //Methods
    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank.toString() +" of " + suit.toString();
    }
    
    public int compareTo(Card other){
    	int result = this.suit.compareTo(other.suit);
    	
    	if(result == 0)
    		result = this.rank.compareTo(other.rank);
    	
    	return result;
    }
    
    @Override 
    public boolean equals(Object obj) {
    	if (obj == null || this.getClass() != obj.getClass())
			return false;
    	
    	Card other = (Card) obj;
    	
    	return this.suit.equals(other.suit)
    			&& this.rank.equals(other.rank);
    }
    
}
