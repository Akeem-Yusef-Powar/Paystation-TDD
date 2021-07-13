/**
 * Testcases for the Pay Station system.
 *
 * This source code is from the book "Flexible, Reliable Software: Using
 * Patterns and Agile Development" published 2010 by CRC Press. Author: Henrik B
 * Christensen Computer Science Department Aarhus University
 *
 * This source code is provided WITHOUT ANY WARRANTY either expressed or
 * implied. You may study, use, modify, and distribute it for non-commercial
 * purposes. For any commercial use, see http://www.baerbak.com/
 */
package edu.temple.cis.paystation;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PayStationImplTest {

    PayStation ps;

    HashMap <Integer,Integer> compareEmpty = new HashMap<Integer,Integer>(){{
        put(5,0);
        put(10,0);
        put(25,0);
    }};

    @Before
    public void setup() {
        ps = new PayStationImpl();
    }

    /**
     * Entering 5 cents should make the display report 2 minutes parking time.
     */
    @Test
    public void shouldDisplay2MinFor5Cents()
            throws IllegalCoinException {
        ps.addPayment(5);
        assertEquals("Should display 2 min for 5 cents",
                2, ps.readDisplay());
    }

    /**
     * Entering 25 cents should make the display report 10 minutes parking time.
     */
    @Test
    public void shouldDisplay10MinFor25Cents() throws IllegalCoinException {
        ps.addPayment(25);
        assertEquals("Should display 10 min for 25 cents",
                10, ps.readDisplay());
    }

    /**
     * Verify that illegal coin values are rejected.
     */
    @Test(expected = IllegalCoinException.class)
    public void shouldRejectIllegalCoin() throws IllegalCoinException {
        ps.addPayment(17);
    }

    /**
     * Entering 10 and 25 cents should be valid and return 14 minutes parking
     */
    @Test
    public void shouldDisplay14MinFor10And25Cents()
            throws IllegalCoinException {
        ps.addPayment(10);
        ps.addPayment(25);
        assertEquals("Should display 14 min for 10+25 cents",
                14, ps.readDisplay());
    }

    /**
     * Buy should return a valid receipt of the proper amount of parking time
     */
    @Test
    public void shouldReturnCorrectReceiptWhenBuy()
            throws IllegalCoinException {
        ps.addPayment(5);
        ps.addPayment(10);
        ps.addPayment(25);
        Receipt receipt;
        receipt = ps.buy();
        assertNotNull("Receipt reference cannot be null",
                receipt);
        assertEquals("Receipt value must be 16 min.",
                16, receipt.value());
    }

    /**
     * Buy for 100 cents and verify the receipt
     */
    @Test
    public void shouldReturnReceiptWhenBuy100c()
            throws IllegalCoinException {
        ps.addPayment(10);
        ps.addPayment(10);
        ps.addPayment(10);
        ps.addPayment(10);
        ps.addPayment(10);
        ps.addPayment(25);
        ps.addPayment(25);

        Receipt receipt;
        receipt = ps.buy();
        assertEquals(40, receipt.value());
    }

    /**
     * Verify that the pay station is cleared after a buy scenario
     */
    @Test
    public void shouldClearAfterBuy()
            throws IllegalCoinException {
        ps.addPayment(25);
        ps.buy(); // I do not care about the result
        // verify that the display reads 0
        assertEquals("Display should have been cleared",
                0, ps.readDisplay());
        // verify that a following buy scenario behaves properly
        ps.addPayment(10);
        ps.addPayment(25);
        assertEquals("Next add payment should display correct time",
                14, ps.readDisplay());
        Receipt r = ps.buy();
        assertEquals("Next buy should return valid receipt",
                14, r.value());
        assertEquals("Again, display should be cleared",
                0, ps.readDisplay());
    }

    /**
     * Verify that cancel clears the pay station
     */
    @Test
    public void shouldClearAfterCancel()
            throws IllegalCoinException {
        ps.addPayment(10);
        ps.cancel();
        assertEquals("Cancel should clear display",
                0, ps.readDisplay());
        ps.addPayment(25);
        assertEquals("Insert after cancel should work",
                10, ps.readDisplay());
    }

    /**
     * Verify that empty returns the total amount since the last call, then sets the total to 0
     */
    @Test
    public void shouldDisplayTotalAmount() //1 & 3
            throws IllegalCoinException {
        // setup payStation with initial amount and test that empty() returns the total amount since the last call
        ps.addPayment(5);
        int emptyValue = ps.empty();
        assertEquals(emptyValue, 5);

        // check that paystation now has a value of 0
        int emptyValueShouldBeZero = ps.empty();
        assertEquals(emptyValueShouldBeZero, 0);

    }
/** coins are added to pay station, cancel returns the same number & value of coins entered then clears the map
    */
    @Test
    public void cancelReturnsMapContainingMixtureOfCoins() //5 & 7
            throws IllegalCoinException{
        HashMap <Integer,Integer> compareMix = new HashMap<Integer,Integer>(){{
           put(5,4);
           put(10,3);
           put(25,3);
        }};

        ps.addPayment(5);
        ps.addPayment(5);
        ps.addPayment(10);
        ps.addPayment(5);
        ps.addPayment(10);
        ps.addPayment(10);
        ps.addPayment(5);
        ps.addPayment(25);
        ps.addPayment(25);
        ps.addPayment(25);// add coins and see if returned map is as expected
        assertEquals(compareMix,ps.cancel());
        assertEquals(compareEmpty,ps.cancel()); // check map is empty

    }


    /**
     test that canceling a payment does not add that canceled payment to the current balance
     **/
    @Test
    public void canceledEntryDoesNotAddToTheAmountReturnedByEmpty() //2
        throws IllegalCoinException {

        // setup initial balance, cancel it right away, and final balance should be reset to 0
        ps.addPayment(10);
        ps.cancel();
        int return_value = ps.empty();
        assertEquals(return_value, 0);

        
        ps.addPayment(10);
        int return_value_2 = ps.empty();
        assertEquals(return_value_2, 10);

    }

    /**
     use addPayment to add only a single coin to the payStation. Then, check that the payStation returns a
     hashmap containg only 1 coin
     **/
    @Test
    public void callToCancelReturnsAMapContainingOneCoinEntered() //4
            throws IllegalCoinException {
        ps.addPayment(5);
        HashMap<Integer, Integer> return_value = ps.cancel();

        // counter logic, checking all values in the hashmap and incrementing for all non-zeros found
        int count = 0;
        for (Integer value: return_value.values()) {
            if (value != 0) {
                count += 1;
            }
        }
        assertEquals(count, 1);
    }

    /**
     *  5c and 25c coins are added 100 times. its compared with the expected map to see if any coins not entered are present
     *
     * */
    @Test
    public void cancelReturnsMapThatDoesNotHaveKeyForCoinNotEntered() throws IllegalCoinException{ //6
        HashMap <Integer,Integer> compare = new HashMap<Integer,Integer>(){{
            put(5,100);
            put(10,0);
            put (25,100);
        }};

for(int i=0;i<100;i++) {
    ps.addPayment(5);
    ps.addPayment(25);
}
        assertEquals(compare,ps.cancel()); // cancel returns the current map
        assertEquals(compareEmpty,ps.viewMap()); // check again to see if cleared correct

    }
/**
 * coins are added, buy is called and then call viewMap to compare a empty map with the actual map returned
 * */
    @Test
    public void buyClearsMap() throws IllegalCoinException { //8

        ps.addPayment(25);
        ps.addPayment(10);
        ps.addPayment(5);
        ps.buy();
        assertEquals(compareEmpty, ps.viewMap());
        ps.addPayment(25);
        ps.addPayment(25);
        ps.addPayment(25);
        ps.buy();
        assertEquals(compareEmpty,ps.viewMap());
    }
}
