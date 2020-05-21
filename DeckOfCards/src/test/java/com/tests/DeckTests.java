package com.tests;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.restassured.path.json.JsonPath;
import static io.restassured.RestAssured.*;
import java.util.Random;
import com.objects.Deck;

public class DeckTests {
	static final String DECK_API = "https://deckofcardsapi.com/api/deck/";
	static final String NEW_DECK_ENDPOINT = "new";
	static final String DRAW_CARDS_ENDPOINT = "{deck_id}/draw/";

	static final int FULL_DECK_COUNT = 52;
	static final int JOKER_COUNT = 2;

	Deck testDeck;

	public JsonPath drawRequest(int cards_draw_count) {
		return given().
					pathParam("deck_id",  testDeck.deck_id).
					queryParam("count", cards_draw_count).
					log().all().
				when().
					get(DECK_API + DRAW_CARDS_ENDPOINT).
				then().
					log().all().
					statusCode(HttpStatus.SC_OK).
					extract().body().jsonPath();
	}

	@BeforeTest
	public void beforeAll() {
		JsonPath jsonPath = given().
					log().all().
				when().
					get(DECK_API + NEW_DECK_ENDPOINT).
				then().
					log().all().
					statusCode(HttpStatus.SC_OK).
					extract().body().jsonPath();
		testDeck = jsonPath.getObject("$", Deck.class);		
	}

	@Test(priority=1)
	public void newDeck() {
		testDeck.verifyNewDeck(FULL_DECK_COUNT);
	}

	@Test(priority=5)
	public void newDeckWithJokers() {
		JsonPath jsonPath = given().
					queryParam("jokers_enabled", true).
					log().all().
				when().
					get(DECK_API + NEW_DECK_ENDPOINT).
				then().
					log().all().
					statusCode(HttpStatus.SC_OK).
					extract().body().jsonPath();
		Deck testDeckWithJokers = jsonPath.getObject("$", Deck.class);
		testDeckWithJokers.verifyNewDeck(FULL_DECK_COUNT + JOKER_COUNT);
	}


    @Test(priority=2)
	public void drawFromDeck() {
		int cards_draw_count = new Random().nextInt(testDeck.remaining) + 1;
		JsonPath jsonPath = drawRequest(cards_draw_count);
		testDeck = jsonPath.getObject("$", Deck.class);
		testDeck.verifyCards();
		testDeck.verifyRemainingCount(FULL_DECK_COUNT - cards_draw_count);
	}

    @Test(priority=3)
	public void drawNoCardsFromDeck() {
		int cards_draw_count = 0;
		JsonPath jsonPath = drawRequest(cards_draw_count);
		testDeck = jsonPath.getObject("$", Deck.class);
		testDeck.verifyRemainingCount(testDeck.remaining);
	}

    @Test(priority=4)
	public void drawTooManyCardsFromDeck() {
		int cards_draw_count = testDeck.remaining + 1;
		JsonPath jsonPath = drawRequest(cards_draw_count);
		testDeck = jsonPath.getObject("$", Deck.class);
		testDeck.verifyCards();
		testDeck.verifyRemainingCount(0);
		Assert.assertEquals(testDeck.success, false);
		String expectedError = String.format("Not enough cards remaining to draw %d additional", cards_draw_count);
		Assert.assertEquals(jsonPath.getString("error"), expectedError);
	}
}
