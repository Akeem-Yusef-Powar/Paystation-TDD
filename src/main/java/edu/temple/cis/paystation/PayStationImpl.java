/**
 * Implementation of the pay station.
 *
 * Responsibilities:
 *
 * 1) Accept payment; 
 * 2) Calculate parking time based on payment; 
 * 3) Know earning, parking time bought; 
 * 4) Issue receipts; 
 * 5) Handle buy and cancel events.
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

import java.util.HashMap;

public class PayStationImpl implements PayStation {

    private int insertedSoFar;
    private int timeBought;
    private int type5;
    private int type10;
    private int type25;

    private HashMap<Integer,Integer> coinsIn  = new HashMap <Integer, Integer>(){{
        put(5,0);
        put(10,0);
        put(25,0);
    }}; // map to add coins inserted, never null

    @Override
    public void addPayment(int coinValue)
            throws IllegalCoinException {
        switch (coinValue) {
            case 5: type5++; coinsIn.put(coinValue,type5);break;
            case 10: type10++; coinsIn.put(coinValue,type10);break;
            case 25: type25++; coinsIn.put(coinValue,type25);break;
            default:
                throw new IllegalCoinException("Invalid coin: " + coinValue);
        }
        insertedSoFar += coinValue;
        timeBought = insertedSoFar / 5 * 2;
    }

    @Override
    public int readDisplay() {
        return timeBought;
    }

    @Override
    public Receipt buy() {
        Receipt r = new ReceiptImpl(timeBought);
        reset();
        coinsIn.put(5,0);
        coinsIn.put(10,0);
        coinsIn.put(25,0); // reset map
        return r;
    }

    @Override
    public HashMap<Integer, Integer> cancel() {
        //System.out.println(coinsIn); // print map
        HashMap<Integer, Integer> temp_holder_before_rest = new HashMap<>(coinsIn);
        coinsIn.put(5,0);
        coinsIn.put(10,0);
        coinsIn.put(25,0); // reset map
        reset();
        return temp_holder_before_rest;
    }


    private void reset() {
        timeBought = insertedSoFar = 0;
    }

    public int empty() {
        int value_to_return = insertedSoFar;
        insertedSoFar = 0;
        return value_to_return;
    }
    @Override
    public HashMap<Integer, Integer> viewMap(){
        return new HashMap<>(coinsIn);
    }
}
