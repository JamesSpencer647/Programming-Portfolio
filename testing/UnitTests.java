package testing;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import serverSoloWhist.*;

public class UnitTests {

	//Card method tests
	Card card1 = new Card(Suit.SPADES, Rank.ACE);
	Card card2 = new Card(Suit.SPADES, Rank.KING); 
	
	@Test
	public void testa1() {
		assertEquals("Card.getSuit() test", card1.getSuit(), Suit.SPADES);
	}
	
	@Test
	public void testa2() {
		assertEquals("Card.getRank() test", card1.getRank(), Rank.ACE);
	}

	@Test
	public void testa3() {
		assertEquals("Card.toString() test", card1.toString(), "ACE of SPADES");
	}
	
	@Test
	public void testa4() {
		assertEquals("Card.equals() true test", card1.equals(new Card(Suit.SPADES, Rank.ACE)), true);
	}
	
	@Test
	public void testa5() {
		assertEquals("Card.equals() false test", card1.equals(card2), false);
	}
	
	@Test
	public void testa6() {
		assertEquals("Card.compareTo() -1 test", card2.compareTo(card1), -1);
	}
	
	@Test
	public void testa7() {
		assertEquals("Card.compareTo() 0 test", card1.compareTo(card1), 0);
	}
	
	@Test
	public void testa8() {
		assertEquals("Card.compareTo() 1 test", card1.compareTo(card2), 1);
	}
	
	//Deck method tests
	Deck d1 = new Deck();
	
	@Test
	public void testb1() {
		assertEquals("Deck.getCard() test",d1.getCard(51), card1);
	}
	
	//Hand method tests
	Hand h1 = new Hand();
	@Test
	public void testc1() {
		h1.addCard(card1);
		assertEquals("Hand.addCard() & getCard() test", h1.getCard(0).equals(card1), true);
	}
	
	@Test
	public void testc2() {
		h1.addCard(card2);
		h1.sort();
		assertEquals("Hand.sort() test", h1.getCard(0).equals(card2), true);
	}
	
	//Trick method tests
	Trick t1 = new Trick(Suit.HEARTS);
	
	@Test
	public void testd1() {
		t1.addCard(card1);
		assertEquals("Trick.addCard() & getTrick() test", t1.getTrick().get(0).equals(card1), true);
	}
	
	@Test 
	public void testd2() {
		t1.setLeadingSuit(Suit.SPADES);
		assertEquals("Trick.setLeadingSuit() and getLeadingSuit() test", t1.getLeadingSuit(), Suit.SPADES);
	}
	
	@Test 
	public void testd3() {
		assertEquals("Trick.getTrumpSuit() test", t1.getTrumpSuit(), Suit.HEARTS);
	}

	//Player method tests
	Player p1 = new Player("playerOne");
	
	@Test 
	public void teste1() {
		assertEquals("Player.getName() test", p1.getName(), "playerOne");
	}
	
	@Test 
	public void teste2() {
		p1.getHand().addCard(card1);
		assertEquals("Player.getHand() test", p1.getHand().size(), 1);
	}
	
	@Test 
	public void teste3() {
		assertEquals("Player.getScore() test", p1.getScore(), 0);
	}
	
	@Test
	public void teste4() {
		p1.incScore();
		p1.incScore();
		assertEquals("Player.incScore() test", p1.getScore(), 2);
	}
	
	/*Player playCard() method is in the integration test case as it implements multiple
	 * methods from different classes*/
	
	
	//AIPlayer method tests
	/*for the AIPlayer there is no need to test the methods from the Player class
	 * as they are all inherited, even playCard() is the same*/
	AIPlayer ai = new AIPlayer("computerOne");
	
	@Test
	public void testf1() {
		ai.cardPlayed(card1);
		ai.cardPlayed(card2);
		ArrayList<Card> cards = new ArrayList<>();
		//these are in order because cardPlayed() sorts the list
		cards.add(card2);
		cards.add(card1);
		assertEquals("AIPlayer.cardPlayed() test", ai.getPlayed().equals(cards), true);
	}
	
	/*AIPlayer has a method chooseCard() which takes the current trick as
	 * a parameter and returns the card to be played from the AIPlayer's hand.
	 * This is used along with playCard like this - ai.playCard(chooseCard(trick),trick)*/
	
	/*The binary decision tree has 14 possible decision outcomes which are coded
	 * in chooseCard()*/
	
	Card h2 = new Card(Suit.HEARTS, Rank.TWO);
	Card h3 = new Card(Suit.HEARTS, Rank.THREE);
	Card hk = new Card(Suit.HEARTS, Rank.KING);
	Card ha = new Card(Suit.HEARTS, Rank.ACE);
	Card c2 = new Card(Suit.CLUBS, Rank.TWO);
	Card c3 = new Card(Suit.CLUBS, Rank.THREE);
	Card ck = new Card(Suit.CLUBS, Rank.KING);
	Card ca = new Card(Suit.CLUBS, Rank.ACE);
	Card d2 = new Card(Suit.DIAMONDS, Rank.TWO);
	Card d3 = new Card(Suit.DIAMONDS, Rank.THREE);
	Card dk = new Card(Suit.DIAMONDS, Rank.KING);
	Card da = new Card(Suit.DIAMONDS, Rank.ACE);
	Card s2 = new Card(Suit.SPADES, Rank.TWO);
	Card s3 = new Card(Suit.SPADES, Rank.THREE);
	Card sk = new Card(Suit.SPADES, Rank.KING);
	Card sa = new Card(Suit.SPADES, Rank.ACE);
	Trick t = new Trick(Suit.HEARTS);
	
	@Test
	public void cC01() {
		/*1 - YYYY: empty trick, multiple cards that can win, all are trumps*/
		ai.getHand().addCard(c2);
		ai.getHand().addCard(s2);
		ai.getHand().addCard(hk);
		ai.getHand().addCard(ha);		
		//The AI methods assume the hand is sorted, the server sorts al hands before assigning them to players
		ai.getHand().sort();
		
		/*Expected output: KING of HEARTS
		 * because it is the lowest ranked trump card that can win*/
		assertEquals("AIPlayer.chooseCard() test 1", ai.chooseCard(t), hk);
		
		ai.getHand().clear();
	}
	
	public void cC02() {
		/*2 - YYYN: empty trick, multiple cards that can win, not all trumps*/
		ai.getHand().addCard(ha);
		ai.getHand().addCard(da);
		ai.getHand().addCard(sk);
		ai.getHand().addCard(sa);
		ai.getHand().sort();
		
		/*Expected output: KING of SPADES
		 * because all 4 cards can win but there are more spades and that is the lowest one*/
		assertEquals("AIPlayer.chooseCard() test 1", ai.chooseCard(t), sk);
		
		ai.getHand().clear();
	}
	
	public void cC03() {
		/*3 - YYN: empty trick, only one card that can win*/
		ai.getHand().addCard(s2);
		ai.getHand().addCard(sa);
		ai.getHand().sort();
		
		/*Expected output: ACE of SPADES*/
		assertEquals("AIPlayer.chooseCard() test 1", ai.chooseCard(t), sa);
		
		ai.getHand().clear();
	}
	
	public void cC04() {
		/*4 - YN: empty trick, no cards capable of winning*/
		ai.getHand().addCard(c2);
		ai.getHand().addCard(c3);
		ai.getHand().addCard(ck);
		ai.getHand().addCard(d3);
		ai.getHand().sort();
		
		/*Expected output: TWO of CLUBS
		 * because it is the lowest card of the suit there are most of*/
		assertEquals("AIPlayer.chooseCard() test 1", ai.chooseCard(t), c2);
		
		ai.getHand().clear();
	}
	
	public void cC05() {
		/*5 - NYY: can follow suit, trump has been played*/
		ai.getHand().addCard(s3);
		ai.getHand().addCard(sk);
		ai.getHand().addCard(c2);
		ai.getHand().addCard(d3);
		ai.getHand().sort();
		t.addCard(sa);
		t.setLeadingSuit(Suit.SPADES);
		t.addCard(h3);
		
		/*expected output: THREE of SPADES
		 * because leading suit must be followed, can't win and that is the lowest one*/
		assertEquals("AIPlayer.chooseCard() test 1", ai.chooseCard(t), s3);
		
		ai.getHand().clear();
		t.getTrick().clear();
	}
	
	public void cC06() {
		/*6 - NYNYY: can follow suit, no trump cards played, have cards that can win, my card is last*/
		ai.getHand().addCard(new Card(Suit.SPADES, Rank.TEN));
		ai.getHand().addCard(sa);
		ai.getHand().addCard(sk);
		ai.getHand().addCard(d3);
		ai.getHand().sort();
		t.addCard(s2);
		t.setLeadingSuit(Suit.SPADES);
		t.addCard(s3);
		t.addCard(d2);
		
		/*expected output: TEN of SPADES
		 * because leading suit must be followed and that is the lowest card that can win*/
		assertEquals("AIPlayer.chooseCard() test 1", ai.chooseCard(t), new Card(Suit.SPADES, Rank.TEN));
		
		ai.getHand().clear();
		t.getTrick().clear();
	}
	
	public void cC07() {
		ai.getHand().addCard(new Card(Suit.SPADES, Rank.TEN));
		ai.getHand().addCard(sa);
		ai.getHand().addCard(sk);
		ai.getHand().addCard(d3);
		ai.getHand().sort();
		t.addCard(s2);
		t.setLeadingSuit(Suit.SPADES);
		t.addCard(s3);
		
		/*expected output: ACE of SPADES
		 * because leading suit must be followed and that is the highest card that can win*/
		assertEquals("AIPlayer.chooseCard() test 1", ai.chooseCard(t), sa);
		
		ai.getHand().clear();
		t.getTrick().clear();
	}
	
	public void cC08() {
		/*8 - NYNN: can follow suit, no trumps played, don't have card that can win*/
		ai.getHand().addCard(new Card(Suit.SPADES, Rank.TEN));
		ai.getHand().addCard(s2);
		ai.getHand().addCard(d3);
		ai.getHand().sort();
		t.addCard(sa);
		t.setLeadingSuit(Suit.SPADES);
		t.addCard(s3);
		
		/*expected output: TWO of SPADES
		 * because leading suit must be followed, player can't win and that is the lowest*/
		assertEquals("AIPlayer.chooseCard() test 1", ai.chooseCard(t), s2);
		
		ai.getHand().clear();
		t.getTrick().clear();
	}
	
	public void cC09() {
		/*9 - NNYYY: can't follow suit, trump been played, can beat it, my card is last*/
		ai.getHand().addCard(new Card(Suit.HEARTS, Rank.TEN));
		ai.getHand().addCard(ha);
		ai.getHand().addCard(hk);
		ai.getHand().addCard(d3);
		ai.getHand().sort();
		t.addCard(sa);
		t.setLeadingSuit(Suit.SPADES);
		t.addCard(new Card(Suit.HEARTS, Rank.NINE));
		t.addCard(s2);

		/*expected output: TEN of HEARTS
		 * because leading suit can't be followed and that is the lowest trump that can win*/
		assertEquals("AIPlayer.chooseCard() test 1", ai.chooseCard(t), new Card(Suit.HEARTS, Rank.TEN));
		
		ai.getHand().clear();
		t.getTrick().clear();
	}
	
	public void cC10() {
		/*10 - NNYYN: can't follow suit, trump been played, can beat it, my card is not last*/
		ai.getHand().addCard(new Card(Suit.HEARTS, Rank.TEN));
		ai.getHand().addCard(ha);
		ai.getHand().addCard(hk);
		ai.getHand().addCard(d3);
		ai.getHand().sort();
		t.addCard(sa);
		t.setLeadingSuit(Suit.SPADES);
		t.addCard(new Card(Suit.HEARTS, Rank.NINE));
		
		/*expected output: ACE of HEARTS
		 * because leading suit can't be followed and that is the highest trump that can win*/
		assertEquals("AIPlayer.chooseCard() test 1", ai.chooseCard(t), ha);
		
		ai.getHand().clear();
		t.getTrick().clear();
	}
	
	public void cC11() {
		/*11 - NNYN: can't follow suit, trump been played, can't beat it*/
		ai.getHand().addCard(dk);
		ai.getHand().addCard(c2);
		ai.getHand().addCard(c3);
		ai.getHand().addCard(hk);
		ai.getHand().addCard(d3);
		ai.getHand().sort();
		t.addCard(sa);
		t.setLeadingSuit(Suit.SPADES);
		t.addCard(ha);
		t.addCard(s2);
		
		/*expected output: TWO of CLUBS
		 * because leading suit can't be followed and don't have a trump that can win
		 * so play the lowest card from the highest quantity suit in hand*/
		assertEquals("AIPlayer.chooseCard() test 1", ai.chooseCard(t), c2);
		
		ai.getHand().clear();
		t.getTrick().clear();
	}
	
	public void cC12() {
		/*12 - NNNYY: can't follow suit, no trumps played, have trump cards, my card is last*/
		ai.getHand().addCard(new Card(Suit.HEARTS, Rank.TEN));
		ai.getHand().addCard(ha);
		ai.getHand().addCard(hk);
		ai.getHand().addCard(d3);
		ai.getHand().sort();
		t.addCard(sa);
		t.setLeadingSuit(Suit.SPADES);
		t.addCard(s2);
		t.addCard(s3);
		
		/*expected output: TEN of HEARTS
		 * because leading suit can't be followed and that is the lowest trump that can win*/
		assertEquals("AIPlayer.chooseCard() test 1", ai.chooseCard(t), new Card(Suit.HEARTS, Rank.TEN));
		
		ai.getHand().clear();
		t.getTrick().clear();
	}
	
	public void cC13() {
		/*13 - NNNYN: can't follow suit, no trumps played, have trump cards, my card is not last*/
		ai.getHand().addCard(new Card(Suit.HEARTS, Rank.TEN));
		ai.getHand().addCard(ha);
		ai.getHand().addCard(hk);
		ai.getHand().addCard(d3);
		ai.getHand().sort();
		t.addCard(sa);
		t.setLeadingSuit(Suit.SPADES);
		t.addCard(s2);
		
		/*expected output: ACE of HEARTS
		 * because leading suit can't be followed and that is the lowest trump that can win*/
		assertEquals("AIPlayer.chooseCard() test 1", ai.chooseCard(t), ha);
		
		ai.getHand().clear();
		t.getTrick().clear();
	}
	
	public void cC14() {
		/*14 - NNNN: can't follow suit, no trumps played, don't have any trumps*/
		ai.getHand().addCard(ca);
		ai.getHand().addCard(c2);
		ai.getHand().addCard(c3);
		ai.getHand().addCard(d2);
		ai.getHand().addCard(dk);
		ai.getHand().addCard(d3);
		ai.getHand().sort();
		t.addCard(sa);
		t.setLeadingSuit(Suit.SPADES);
		t.addCard(s2);
		
		/*expected output: TWO of CLUBS
		 * because leading suit can't be followed and that is the lowest trump that can win*/
		assertEquals("AIPlayer.chooseCard() test 1", ai.chooseCard(t), c2);
		
		ai.getHand().clear();
		t.getTrick().clear();
	}
	
	
	
	
	
	
	
}
