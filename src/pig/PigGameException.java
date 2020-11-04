/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2020 Gary F. Pollice
 *******************************************************************************/

package pig;

/**
 * Exception for the Pig Game. 
 * @version Oct 21, 2020
 */
public class PigGameException extends RuntimeException
{
	/**
	 * Any Pig Game exception must have a message indicating the problem.
	 * @param message
	 */
	public PigGameException(String message)
	{
		super(message);
	}
}
