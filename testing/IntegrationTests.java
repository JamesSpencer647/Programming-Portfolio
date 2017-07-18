package testing;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.junit.Test;

import serverSoloWhist.*;

public class IntegrationTests {
	
	/*Check that playCard() set lastPlayed card to the card played,
	 * adds the card to the trick and remove the card from the players hand*/
	@Test 
	public void testPlayCard() {
		Trick tr = new Trick(Suit.HEARTS);
		Player p1 =  new Player("playerOne");
		Card card1 = new Card(Suit.SPADES, Rank.ACE);
		p1.playCard(card1, tr);
		assertEquals("Player.playCard() test", p1.getHand().size() == 0 && 
											   p1.getPlayedCard() == card1 &&
											   tr.getTrick().size() == 1, true);
	}
	
	/*A test for the function deal() which the gameServer will use to 
	 * deal a shuffled deck of cards evenly to 4 Player objects*/
	ArrayList<Player> players = new ArrayList<>();
	ArrayList<PrintWriter> writers = new ArrayList<>();
	Deck deck = new Deck();
	@Test
	public void testDeal() {
		players.add(new Player("PlayerOne"));
		players.add(new Player("PlayerTwo"));
		players.add(new Player("PlayerThree"));
		players.add(new Player("PlayerFour"));
		deal();
		/*Each players ahdn should now contain 13 cards*/
		assertEquals("GameServer deal() test", players.get(0).getHand().size() == 13 &&
											   players.get(1).getHand().size() == 13 &&
											   players.get(2).getHand().size() == 13 &&
											   players.get(3).getHand().size() == 13 &&
											   compareHands(), true);
	}
	
	/*a function to test that each player has different cards in their hand*/
	private boolean compareHands() {
		boolean allDifferent = false;
		
		for (Player p : players) {
			for (Card c : p.getHand()) {
				for (Player pp : players) {
					if (p != pp) {
						for (Card cc : pp.getHand()) {
							allDifferent = !(c.equals(cc));
						}
					}
				}
			}
		}
		
		return allDifferent;
	}
	/*The deal function that GameServer will use*/
	private void deal() {
		deck.shuffle();
		int i = 0;
		for (Card c : deck) {
			players.get(i).getHand().addCard(c);
			i++;
			i = i % 4;
		}
		for (Player p : players) {
			p.getHand().sort();
		}
	}

	/*Test for the legalMove() function that the GameServer will use to check
	 * if a human players card follows suit if they can*/
	@Test
	public void testLegalMove1() {
		/*An illegal move - the player could have followed suit but didn't*/
		Player p1 = new Player("playerOne");
		p1.getHand().addCard(new Card(Suit.CLUBS, Rank.JACK));
		p1.getHand().addCard(new Card(Suit.HEARTS, Rank.TWO));
		assertEquals("GameServer legalMove() false test", legalMove(p1, new Card(Suit.HEARTS, Rank.TWO), Suit.CLUBS), false);
		
	}
	
	@Test
	public void testLegalMove2() {
		/*A legal move - the player could not follow suit*/
		Player p1 = new Player("playerOne");
		p1.getHand().addCard(new Card(Suit.CLUBS, Rank.JACK));
		p1.getHand().addCard(new Card(Suit.HEARTS, Rank.TWO));
		assertEquals("GameServer legalMove() true test", legalMove(p1, new Card(Suit.HEARTS, Rank.TWO), Suit.SPADES), true);
	}
	/*The legalMove() function which takes the player whose turn it is,
	 * the card they are attempting to play and the tricks leadSuit*/
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
	
	/*Test for the winner() function which GameServer will use to identify
	 * the players list index of the player with the highest card*/
	@Test
	public void testWinner() {
		players.add(new Player("PlayerOne"));
		players.add(new Player("PlayerTwo"));
		players.add(new Player("PlayerThree"));
		players.add(new Player("PlayerFour"));
		deal();
		Trick t = new Trick(Suit.CLUBS);
		players.get(0).playCard(new Card(Suit.HEARTS, Rank.JACK), t);
		players.get(1).playCard(new Card(Suit.HEARTS, Rank.TWO), t);
		players.get(2).playCard(new Card(Suit.HEARTS, Rank.KING), t);
		players.get(3).playCard(new Card(Suit.CLUBS, Rank.NINE), t);
		assertEquals("GameServer winner() test", winner(t), 3);
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
