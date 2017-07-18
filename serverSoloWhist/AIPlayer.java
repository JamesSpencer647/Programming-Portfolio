package serverSoloWhist;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class AIPlayer extends Player{

	// list of cards that have already been played (by any player) used for choosing which card to play
	private ArrayList<Card> playedCards;
	//A list of potential cards to play
	private List<Card> toPlay;
	
	public AIPlayer(String name) {
		super(name);
		playedCards = new ArrayList<>();
		toPlay = new ArrayList<>();
	}
	
	public void cardPlayed(Card card) {
		playedCards.add(card);
		Collections.sort(playedCards);
	}
	
	//this method is only need for testing purposes
	public ArrayList<Card> getPlayed() {
		return playedCards;
	}
	
	public Card chooseCard(Trick trick){
		
		Card card = null;
		toPlay.clear();
		/*If there is only one card left*/
		if (hand.size() == 1) {
			card = hand.getCard(0);
		}
		else {
			/*first get a count for the suits in hand - most play strategies need these numbers*/
			int[] suitCount = {0,0,0,0};
			
			for (Card c : hand) {
					suitCount[c.getSuit().getVal()]++;
			}
			
			/*if this is the first card to be played*/
			if (trick.getLeadingSuit() == null) {
				/*if I have a card that can win*/
				if (canWinPlayFirst()) {
					
					/*If there is only one card to choose from - play it*/
					if (toPlay.size() == 1)
						card = toPlay.get(0);
					/*if there are multiple cards to choose from*/
					else {
						/*Check */
						int toPlayTrumpCount = 0;
						for (Card c : toPlay) {
							if (c.getSuit() == trick.getTrumpSuit())
								toPlayTrumpCount++;
						}
						/*if all of the potential winning cards are trump cards
						 * - play the lowest rank*/
						if (toPlayTrumpCount == toPlay.size()) {
							card = toPlay.get(0);
						}
						/*if there are some non-trump cards - play the lowest rank card from the
						 * non-trump suit with the most cards*/
						else {
							int suitToPlay = 0, max = 0;
							/*get an index of the suit with the most cards and with cards in toPlay*/
							for (int i = 0; i<4; i++) {
								
								for (Card c : toPlay) {
									
									if (c.getSuit() == Suit.values()[i]) {
										
										if (Suit.values()[i] != trick.getTrumpSuit() && suitCount[i] > max) {
											max = suitCount[i];
											suitToPlay = i;
										}
									}
								}
								
							}
							/*play the first card from toPlay of the chosen suit*/
							for (Card c : toPlay) {
								if (c.getSuit() == Suit.values()[suitToPlay]) {
									card = c;
									break;
								}
							}														
						}
					}
				}
				/*can't play a winning card so play the lowest rank card from the suit there are most of*/
				else {
					int suitToPlay = 0, max = 0;
					/*get an index of the suit with the most cards and with cards in toPlay*/
					for (int i = 0; i<4; i++) {
						
						for (Card c : hand) {
							
							if (c.getSuit() == Suit.values()[i]) {
								
								if (Suit.values()[i] != trick.getTrumpSuit() && suitCount[i] > max) {
									max = suitCount[i];
									suitToPlay = i;
								}
							}
						}
						
					}
					/*play the first card from toPlay of the chosen suit*/
					for (Card c : hand) {
						if (c.getSuit() == Suit.values()[suitToPlay]) {
							card = c;
							break;
						}
					}
				}
			}
			/*if the trick already contains one or more cards*/
			else {
				/*check if a trump card has been played*/
				boolean trumped = false;
				for (Card c : trick) {
					if (c.getSuit() == trick.getTrumpSuit()) {
						trumped = true;
						break;
					}
				}
				
				/*if it's possible to follow suit*/
				if (suitCount[trick.getLeadingSuit().getVal()] > 0) {
					/*5 - if a trump has been played*/
					if (trumped) {
						for (Card c : hand) {
							if (c.getSuit() == trick.getLeadingSuit()) {
								card = c;
								break;
							}
						}
					}
					/*if a trump hasn't been played*/
					else {
						/*find the highest rank card in the trick*/
						Card highest = trick.getTrick().get(0);
						for (Card c : trick) {
							if (c.getRank().getVal() > highest.getRank().getVal()){
								highest = c;
							}
						}
						/*if any cards in hand can beat highest*/
						if (canWinNotFirst(highest)) {
							/*if three cards have already been played - play the lowest card that can win*/
							if (trick.getTrick().size() == 3) {
								card = toPlay.get(0);
							}
							/*otherwise play the highest*/
							else {
								card = toPlay.get(toPlay.size()-1);
							}
						}
						/*if no cards can win the trick - play the lowest card which follows suit*/
						else {
							for (Card c : hand) {
								if (c.getSuit() == trick.getLeadingSuit()) {
									card = c;
									break;
								}
							}
						}
						
					}
				}
				/*if it's not possible to follow suit*/
				else {
					/*if a trump has been played*/
					if (trumped) {
						Card highest = null;
						for (Card c : trick) {
							if ((highest == null && c.getSuit() == trick.getTrumpSuit()) 
									|| (c.getSuit() == trick.getTrumpSuit() && c.getRank().getVal() > highest.getRank().getVal())){
								highest = c;
							}
						}
						/*if the player has a higher trump*/
						if (canWinNotFirst(highest)) {
							/*if three cards have already been played - play the lowest card that can win*/
							if (trick.getTrick().size() == 3) {
								card = toPlay.get(0);
							}
							/*otherwise play the highest*/
							else {
								card = toPlay.get(toPlay.size()-1);
							}
						}
						/*if the player does not have a higher trump - play the lowest rank card from the highest count non-trump suit*/
						else {
							int suitToPlay = 0, max = 0;
							/*get an index of the suit with the most cards*/
							for (int i = 0; i<4; i++) {
								
								for (Card c : hand) {
									
									if (c.getSuit() == Suit.values()[i]) {
										
										if (Suit.values()[i] != trick.getTrumpSuit() && suitCount[i] > max) {
											max = suitCount[i];
											suitToPlay = i;
										}
									}
								}
								
							}
							/*play the first card from toPlay of the chosen suit*/
							for (Card c : hand) {
								if (c.getSuit() == Suit.values()[suitToPlay]) {
									card = c;
									break;
								}
							}
						}
					}
					/*if a trump hasn't been played*/
					else {
						/*check the hand for trump cards*/
						boolean haveTrump = false;
						for (Card c : hand) {
							if (c.getSuit() == trick.getTrumpSuit()) {
								haveTrump = true;
								break;
							}
						}
						/*if the player has a trump card - */
						if (haveTrump) {
							/*if this is the last card to be played - play the lowest trump*/
							if (trick.getTrick().size() == 3) {
								for (Card c : hand) {
									if (c.getSuit() == trick.getTrumpSuit()){
										card = c;
										break;
									}
								}
							}
							/*otherwise play the highest trump*/
							else {
								for (int i=0; i<hand.size(); i++) {
									/*This first if stops an IndexOutOfBoundsException being thrown by the 
									 * else if*/
									if (i == (hand.size() - 1)){
										card = hand.getCard(i);
										break;
									}
									
									else if (hand.getCard(i).getSuit() == trick.getTrumpSuit()
											&& hand.getCard(i+1).getSuit() != trick.getTrumpSuit()) {
										card = hand.getCard(i);
										break;
									}
								}
							}
							
						}
						/*if the player doesn't have a trump card - play the lowest card from the highest count suit*/
						else {
							int suitToPlay = 0, max = 0;
							/*get an index of the suit with the most cards*/
							for (int i = 0; i<4; i++) {
								
								for (Card c : hand) {
									
									if (c.getSuit() == Suit.values()[i]) {
										
										if (Suit.values()[i] != trick.getTrumpSuit() && suitCount[i] > max) {
											max = suitCount[i];
											suitToPlay = i;
										}
									}
								}
								
							}
							/*play the first card from toPlay of the chosen suit*/
							for (Card c : hand) {
								if (c.getSuit() == Suit.values()[suitToPlay]) {
									card = c;
									break;
								}
							}
						}
					}
				}
			}
		}

		return card;
	}	
	
	/*This method will be used to assert whether or not a card
	 * in this players hand can win when going first*/
	private boolean canWinPlayFirst() {
		
		int index = 0;
		
		for (int i=0; i<hand.size(); i++) {
			/*Check for aces*/
			if(hand.getCard(i).getRank() == Rank.ACE) {
				toPlay.add(hand.getCard(i));
			}
			/*See if any cards in hand are the highest remaining card*/
			else {
				/*get an index for the highest rank
				 * this will be used to check for all higher cards in cardsPlayed
				 */
				for (int r=0; r<13; r++) {
					if(hand.getCard(i).getRank() == Rank.values()[r])
						index = r;
				}
				/*hand will be in order*/
				for (Card c : hand) {
					/*check if the card c is the next highest card*/
					if (hand.getCard(i).getSuit() == c.getSuit()
							&& (Rank.values()[index].getVal() - c.getRank().getVal()) == -1 ) {
						index++;
					}
					/*check the cards that have already been played */
					else {
						for (Card c1 : playedCards) {
							if (hand.getCard(i).getSuit() == c1.getSuit()
									&& (Rank.values()[index].getVal() - c1.getRank().getVal()) == -1 ) {
								index++;
							}
						}
						/*re-check the hand card to avoid missing cards*/
						if (hand.getCard(i).getSuit() == c.getSuit()
								&& (Rank.values()[index].getVal() - c.getRank().getVal()) == -1 ) {
							index++;
						}
					}
					
				}
				/*if index is of an ACE then every card above this card has been played*/
				if (Rank.values()[index] == Rank.ACE) {
					toPlay.add(hand.getCard(i));
				}
			}
		}
		Collections.sort(toPlay);
		return toPlay.size()>0;
	}
	
	/*This method will be used to assert whether or not a card
	 * in this players hand can win when not going first
	 *  - assuming the leading suit can be followed*/
	private boolean canWinNotFirst(Card highest) {
		
		for (Card c : hand) {
			if (c.getSuit() == highest.getSuit() && c.getRank().getVal() > highest.getRank().getVal()) {
				toPlay.add(c);
			}
		}
		Collections.sort(toPlay);
				
		return toPlay.size() > 0;
	}
	
}
