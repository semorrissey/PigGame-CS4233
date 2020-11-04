/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design. The course was
 * taken at Worcester Polytechnic Institute. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Edited by Sean Morrissey Copyright ©2020 Gary
 * F. Pollice
 *******************************************************************************/

package pig;

import static pig.PigGameVersion.*;

/**
 * This class is the main class for the Pig Game programming assignment.
 * 
 * @version Nov 3, 2020
 */
public class PigGame
{
	private int turnScore;
	private int turn;
	private int rollsInTurn;
	private int playerOneScore;
	private int playerTwoScore;
	private Die[] gameDice;
	private int winningScore;
	private int previousTurn;
	private boolean gameOver;
	private PigGameVersion gameType;

	/**
	 * Constructor that takes in the version and dice to be used for the game version.
	 * 
	 * @param version
	 *            the game version
	 * @param scoreNeeded
	 *            the score that a player must achieve (equal to this or greater)
	 * @param dice
	 *            an array of dice to be used for the game.
	 * @throws PigGameException 
	 * 				if any of the arguments are invalid (e.g. scoreNeeded is <= 0)
	 */
	public PigGame(PigGameVersion version, int scoreNeeded, Die... dice)
			throws PigGameException
	{
		turnScore = 0;
		turn = 0;
		playerOneScore = 0;
		playerTwoScore = 0;
		rollsInTurn = 0;
		previousTurn = 0;
		gameOver = false;
		if (dice == null) {
			gameOver = true;
			throw new PigGameException("No die were given.");
		}
		gameDice = dice;
		if (scoreNeeded <= 1) {
			gameOver = true;
			throw new PigGameException(
					"The score needed is too low, please enter something greater than zero");
		}
		winningScore = scoreNeeded;
		gameType = version;

		if (version == TWO_DICE && dice.length < 2) {
			gameOver = true;
			throw new PigGameException(
					"There are not enough dice for this game mode! Please enter at least two dice");
		}

	}

	/**
	 * This method rolls the dice for the play and returns the result.
	 * 
	 * @return the amount rolled. If this returns 0 it means that the player who rolled
	 *         the dice has pigged out and receives a 0 for the turn and the opposing
	 *         player will make the next roll. If the method returns the scoreNeeded, it
	 *         means the player who rolled wins.
	 * @throws PigGameException
	 * 		    if the game is over when this method is called.
	 */
	public int roll()
	{
		if (gameOver) {
			throw new PigGameException(
					"The game is over, you cannot roll. Please create a new game.");
		}
		rollsInTurn++;
		if (gameType == ONE_DIE_DUPLICATE) {
			return rollDupeDice();
		} else if (gameType == TWO_DICE) {
			return rollTwoDice();
		} else {
			return rollStandard();
		}
	}

	/**
	 * This method adds the turn total to the current total for the active player and
	 * switches players. A player must have rolled at least once during the turn before
	 * holding.
	 * 
	 * @throws PigGameException
	 *             if the active player has not rolled at least one time during the turn.
	 * @throws PigGameException
	 *             if the active player tries to hold when the game is over.
	 */
	public void hold()
	{
		if (gameOver) {
			throw new PigGameException(
					"The game is over, you cannot hold. Please create a new game.");
		}
		if (rollsInTurn == 0) {
			throw new PigGameException(
					"The player has not rolled! You must roll once a turn.");
		}

		if (turn == 0) {
			playerOneScore = turnScore;
			rollsInTurn = 0;
			previousTurn = 0;
			turn = 1;
			turnScore = playerTwoScore;
		} else {
			playerTwoScore = turnScore;
			rollsInTurn = 0;
			previousTurn = 0;
			turn = 0;
			turnScore = playerOneScore;
		}
	}

	/**
	 * Performs a roll for the STANDARD game type
	 * 
	 * @return the amount rolled. If this returns 0 it means that the player who rolled
	 *         the dice has pigged out and receives a 0 for the turn and the opposing
	 *         player will make the next roll. If the method returns the scoreNeeded, it
	 *         means the player who rolled wins.
	 */
	private int rollStandard()
	{
		int temp = gameDice[0].roll();
		if (temp == 0) {
			hold();
			return 0;
		} else {
			turnScore += temp + 1;
			if (turnScore >= winningScore) {
				gameOver = true;
				return winningScore;
			} else {
				return turnScore;
			}
		}
	}

	/**
	 * Performs a roll for the TWO_DICE game type
	 * 
	 * @return the amount rolled. If this returns 0 it means that the player who rolled
	 *         the dice has pigged out and receives a 0 for the turn and the opposing
	 *         player will make the next roll. If the total score of the two dice equals
	 *         7, the turn is over and the score does not change, thus returning a 0. If
	 *         the method returns the scoreNeeded, it means the player who rolled wins.
	 */
	private int rollTwoDice()
	{
		int rollOne = gameDice[0].roll();
		int rollTwo = gameDice[1].roll();
		int diceScore = (rollOne + 1) + (rollTwo + 1);

		if (rollOne == 0 || rollTwo == 0) {
			hold();
			return 0;
		} else {
			if (diceScore == 7) {
				return 0;
			}
			turnScore += diceScore;
			if (turnScore > winningScore) {
				gameOver = true;
				return winningScore;
			} else {
				return turnScore;
			}
		}
	}

	/**
	 * Performs a roll for the ONE_DIE_DUPLICATE game type
	 * 
	 * @return the amount rolled. If this returns 0 it means that the player who rolled
	 *         the dice has pigged out and receives a 0 for the turn and the opposing
	 *         player will make the next roll. A 0 can also be returned if consecutive
	 *         rolls are the same, meaning they pigged out and the next player's turn
	 *         begins. If this returns a 1, it means that it was rolled in the first turn
	 *         which counts in this game type. If the method returns the scoreNeeded, it
	 *         means the player who rolled wins.
	 */
	private int rollDupeDice()
	{
		int temp = gameDice[0].roll();
		if (rollsInTurn == 1 && temp == 0) {
			previousTurn = temp + 1;
			turnScore += previousTurn;
			return previousTurn;
		} else {
			if (previousTurn == temp + 1) {
				hold();
				return 0;
			} else if (temp == 0) {
				previousTurn = temp + 1;
				hold();
				return 0;
			} else {
				turnScore += temp + 1;
				if (turnScore > winningScore) {
					previousTurn = temp + 1;
					gameOver = true;
					return winningScore;
				} else {
					return turnScore;
				}
			}
		}
	}

	/**
	 * @return the current turn's score
	 */

	protected int getTurnScore()
	{
		return turnScore;
	}

	/**
	 * @return the current player's turn. If this returns a 0, it is playerOne's turn.\ If
	 *         this returns a 1, it is playerTwo's turn.
	 */
	protected int getTurn()
	{
		return turn;
	}

	/**
	 * @return playerOne's score as of the previous turn. This is updated when hold() is
	 *         called.
	 */
	protected int getPlayerOneScore()
	{
		return playerOneScore;
	}

	/**
	 * @return playerTwo's score as of the previous turn. This is updated when hold() is
	 *         called.
	 */
	protected int getPlayerTwoScore()
	{
		return playerTwoScore;
	}
}
