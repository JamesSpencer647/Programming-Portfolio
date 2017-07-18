package serverSoloWhist;

public enum Suit {
	HEARTS(0),
	CLUBS(1),
	DIAMONDS(2),
	SPADES(3);
	
	final int value;
	private Suit(int value){
		this.value = value;
	}
	
	public int getVal() {
		return this.value;
	}
}
	