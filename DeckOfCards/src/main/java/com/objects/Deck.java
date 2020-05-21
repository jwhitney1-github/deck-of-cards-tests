package com.objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.testng.Assert;

public class Deck {
	static final ArrayList<String> enumSuits = new ArrayList<>(Arrays.asList("SPADES", "DIAMONDS", "CLUBS", "HEARTS"));
	static final ArrayList<String> enumValues = new ArrayList<>(Arrays.asList("ACE", "2", "3", "4", "5", "6", "7", "8", "9", "10", "JACK", "QUEEN", "KING"));

		public boolean success;
		public String deck_id;
		public int remaining;
		public boolean shuffled;
		public ArrayList<HashMap> cards;

		public void verifyNewDeck(int expectedCardCount) {
			Assert.assertEquals(success, true, "Request did not succeed.");
			Assert.assertEquals(shuffled, false, "Unexpected shuffle value.");
			Assert.assertTrue(deck_id.length() > 0, String.format("deck_id %s is not valid.", deck_id));
			Assert.assertEquals(remaining, expectedCardCount, String.format("Incorrect remaining card count."));
		}

		public void verifyCards() {
			String suit;
			String value;
			String code;
			String image;
			for(HashMap<String, String> card: cards) {
				suit = card.get("suit");
				value = card.get("value");
				Assert.assertTrue(enumSuits.contains(suit), "Card suit not found.");
				Assert.assertTrue(enumValues.contains(value), "Card value not found.");
				code = card.get("code");
				Assert.assertTrue(this.verifyCode(code, suit, value), String.format("Code %s did not match the specified pattern.", code));
				image = card.get("image");
				Assert.assertTrue(this.verifyImage(code, image), String.format("Image %s did not match the specified file pattern.", image));
			}
		}

		public void verifyRemainingCount(int expectedCardCount) {
			Assert.assertEquals(remaining, expectedCardCount);
		}

		public boolean verifyCode(String code, String suit, String value) {
			if(value.equals("10")) { value = "0"; }
			if(value.equals("JOKER")) { 
				value = "X"; 
				suit = (suit.equals("BLACK")) ? "1" : suit.equals("RED") ? "2" : suit;
			} 
			return (value.substring(0, 1) + suit.substring(0, 1)).equals(code);
		}
		
		public boolean verifyImage(String code, String image) {
			String imageBase = "https://deckofcardsapi.com/static/img/";
			String imageFileType = ".png";
			return image.equals(imageBase+code+imageFileType);
		}

}
