/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design. The course was
 * taken at Worcester Polytechnic Institute. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Edited by Sean Morrissey Copyright ©2020 Gary
 * F. Pollice
 *******************************************************************************/

package pig;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static pig.PigGameVersion.*;
import java.util.*;

/**
 * TestCases for the PigGame
 * 
 * @version Oct 21, 2020
 */
class PigGameTest
{
	/**
	 * Implementation of Die that picks a number 1-6 to roll
	 * 
	 * @version Nov 3, 2020
	 */
	static class Dice implements Die
	{
		private List<Integer> numbersToPick = new ArrayList(
				List.of(0, 1, 2, 3, 4, 5));

		/**
		 * Constructor that creates a Dice object
		 */
		public Dice()
		{
		}

		/**
		 * This method "rolls" the die by returning a number from 1 to 6
		 * 
		 * @return the first number from a "shuffled" list of numbers from 1 to 6.
		 */
		public int roll()
		{
			Collections.shuffle(numbersToPick);
			List<Integer> subList = numbersToPick.subList(0, 1);
			return subList.get(0);
		}
	}

	/**
	 * Implementation of Die that allows to rig a die
	 * 
	 * @version Nov 3, 2020
	 */
	static class LoadedDie implements Die
	{
		private int loadedVal;   //an individual loaded value defined by the user
		private int[] loadedValues;  //multiple loaded values defined by the user
		private int valueChosen;  // the previous value chosen for a rolle by the die

		/**
		 * Constructor that creates a rigged die to be used for testing. This uses a
		 * default value.
		 */
		public LoadedDie()
		{
			loadedVal = 1;
		}

		/**
		 * Constructor that takes in a value to be used in a rigged dice. The dice will
		 * always roll for this value.
		 * 
		 * @param val
		 *            The value desired for the rigged die.
		 */
		public LoadedDie(int val)
		{
			loadedVal = val;
		}

		/**
		 * Constructor that takes in two values to be used in a rigged dice. The dice will
		 * always alternate between rolls with these values
		 * 
		 * @param val1
		 *            The first value desired for the rigged die.
		 * @param val2
		 *            The second value desired for the rigged die.
		 */
		public LoadedDie(int val1, int val2)
		{
			int[] temp = {
					val1, val2
			};
			loadedValues = temp;
			valueChosen = 0;
		}

		/**
		 * This method "rolls" the die by choosing the rigged number or alertnating
		 * numbers.
		 * 
		 * @return the rigged value for the die. If there is more than one value, choose
		 *         the one that was not chosen previously and return it. If not, return
		 *         the loadedValue which is the rigged number.
		 */
		public int roll()
		{
			if (loadedValues != null) {
				if (valueChosen == loadedValues[0]) {
					valueChosen = loadedValues[1];
					return valueChosen;
				} else {
					valueChosen = loadedValues[0];
					return valueChosen;
				}

			}
			return loadedVal;
		}

		/**
		 * sets the value of the rigged die to a new given value "a".
		 * 
		 * @param a
		 *            The desired value to change the die to.
		 */
		public void setLoadedValue(int a)
		{
			loadedVal = a;
		}
	}

	// #1
	@Test
	void noDice()
	{ // See https://junit.org/junit5/docs/current/user-guide/#writing-tests-assertions
		assertThrows(PigGameException.class, () -> new PigGame(STANDARD, 10, null));
	}

	// #9
	@Test
	void lowScoreNeeded()
	{
		assertThrows(PigGameException.class,
				() -> new PigGame(STANDARD, -1, new LoadedDie()));
	}

	// #10
	@Test
	void testGameOver()
	{
		LoadedDie newDice = new LoadedDie(10);
		PigGame endedGame = new PigGame(STANDARD, 10, newDice);
		endedGame.roll();
		assertThrows(PigGameException.class, () -> endedGame.roll());
		assertThrows(PigGameException.class, () -> endedGame.hold());
	}

	// #11
	@Test
	void holdBeforeRoll()
	{
		LoadedDie newDice = new LoadedDie(10);
		PigGame endedGame = new PigGame(STANDARD, 10, newDice);
		assertThrows(PigGameException.class, () -> endedGame.hold());
	}

	// #12
	@Test
	void notEnoughDice()
	{
		LoadedDie newDice = new LoadedDie(10);
		assertThrows(PigGameException.class,
				() -> new PigGame(TWO_DICE, 10, newDice));
	}

	// #2
	@Test
	void testDiceRoll()
	{
		Dice newDice = new Dice();
		int rolledValue = newDice.roll();
		assertTrue(rolledValue == 0 || rolledValue == 1 || rolledValue == 2
				|| rolledValue == 3 || rolledValue == 4 || rolledValue == 5);
	}

	// #3
	@Test
	void testPlayerRoll()
	{
		Dice newDice = new Dice();
		PigGame game = new PigGame(STANDARD, 10, newDice);
		int prevScore = game.getTurnScore();
		int rollScore = game.roll();
		int newScore = game.getTurnScore();
		assertTrue(prevScore != newScore || rollScore == 0);
	}

	// #4
	@Test
	void testPlayerHold()
	{
		LoadedDie newDice = new LoadedDie();
		PigGame game = new PigGame(STANDARD, 10, newDice);
		// test for player one
		int currTurn = game.getTurn();
		game.roll();
		game.hold();
		assertTrue(currTurn != 1);
		assertTrue(game.getTurnScore() >= 0);
		assertTrue(game.getPlayerOneScore() >= 0);

		// test for player two
		currTurn = game.getTurn();
		game.roll();
		game.hold();
		assertTrue(currTurn != 0);
		assertTrue(game.getTurnScore() >= 0);
		assertTrue(game.getPlayerTwoScore() >= 0);

	}

	// #5
	@Test
	void testWinningState()
	{
		LoadedDie newDice = new LoadedDie(9);
		PigGame game = new PigGame(STANDARD, 10, newDice);
		assertEquals(10, game.roll());
	}

	// #6
	@Test
	void testStandardGame()
	{

		// If a player rolls a 1
		LoadedDie riggedDice = new LoadedDie(0);
		PigGame game = new PigGame(STANDARD, 10, riggedDice);
		game.roll();
		assertEquals(0, game.getPlayerOneScore());

		// If a player wins a game
		LoadedDie riggedDice2 = new LoadedDie();
		PigGame game2 = new PigGame(STANDARD, 10, riggedDice2);
		game2.roll();
		game2.roll();
		game2.roll();
		game2.roll();
		assertEquals(10, game2.roll());

		// if a player holds
		riggedDice.setLoadedValue(1);
		PigGame game3 = new PigGame(STANDARD, 10, riggedDice);
		game3.roll();
		game3.roll();
		game3.hold();
		assertEquals(1, game3.getTurn());
	}

	// #7
	@Test
	void testDoubleDiceGame()
	{
		// Using set digits to remove ambugity for testing

		// If a player rolls a 1
		LoadedDie riggedDice = new LoadedDie(0);
		LoadedDie riggedDice2 = new LoadedDie(1);
		LoadedDie[] setOfDice = {
				riggedDice, riggedDice2
		};
		PigGame game = new PigGame(TWO_DICE, 10, setOfDice);
		game.roll();
		assertEquals(0, game.getPlayerOneScore());

		riggedDice.setLoadedValue(1);
		riggedDice2.setLoadedValue(0);
		PigGame game2 = new PigGame(TWO_DICE, 10, setOfDice);
		game2.roll();
		assertEquals(0, game2.getPlayerOneScore());

		// If a player wins a game
		riggedDice.setLoadedValue(1);
		riggedDice2.setLoadedValue(1);
		PigGame game3 = new PigGame(TWO_DICE, 10, setOfDice);
		game3.roll();
		game3.roll();
		assertEquals(10, game3.roll());

		// if a player holds
		PigGame game4 = new PigGame(TWO_DICE, 10, setOfDice);
		game4.roll();
		game4.roll();
		game4.hold();
		assertEquals(1, game4.getTurn());

		// if the turn score equals 7
		riggedDice.setLoadedValue(2);
		riggedDice.setLoadedValue(4);
		PigGame game5 = new PigGame(TWO_DICE, 10, setOfDice);
		assertEquals(0, game5.roll());
	}

	// #8
	@Test
	void testDuplicateDice()
	{
		// If a player rolls a 1
		LoadedDie riggedDice = new LoadedDie(0);
		PigGame game = new PigGame(ONE_DIE_DUPLICATE, 10, riggedDice);
		game.roll();
		assertEquals(1, game.getTurnScore());

		// if a player rolls a 1 after their first turn
		game.roll();
		assertEquals(0, game.getTurnScore());

		// If a player rolls multiple dice
		LoadedDie riggedDice2 = new LoadedDie(1, 2);
		PigGame game2 = new PigGame(ONE_DIE_DUPLICATE, 10, riggedDice2);
		game2.roll();
		game2.roll();
		assertEquals(7, game2.roll());

		// if a player rolls a one and they did not roll a one previously
		LoadedDie riggedDice3 = new LoadedDie(1, 0);
		PigGame game3 = new PigGame(ONE_DIE_DUPLICATE, 10, riggedDice3);
		game3.roll();
		game3.roll();
		game3.roll();
		assertEquals(0, game3.roll());

		// if a player holds
		PigGame game4 = new PigGame(ONE_DIE_DUPLICATE, 10, riggedDice2);
		game4.roll();
		game4.roll();
		game4.hold();
		assertEquals(1, game4.getTurn());

		// if a player wins
		game2.roll();
		assertEquals(10, game2.roll());
	}

	// #13
	@Test
	void exampleGame()
	{
		//This is the example test from the Canvas Discussion Board for Module 4
		LoadedDie newDie = new LoadedDie(2);
		PigGame newGame = new PigGame(STANDARD, 12, newDie);
		newGame.roll();
		newGame.hold();
		newDie.setLoadedValue(0);
		newGame.roll();
		newDie.setLoadedValue(4);
		newGame.roll();
		assertEquals(12, newGame.roll());
		assertThrows(PigGameException.class, () -> newGame.hold());
	}
}
