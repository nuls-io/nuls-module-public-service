package io.nuls.api.rpc.rest.utils;

import junit.framework.TestCase;

public class BinancePriceProviderTest extends TestCase {

    public static void main(String[] args) {
        BinancePriceProvider priceProvider = new BinancePriceProvider("https://api.binance.com");
        System.out.println(priceProvider.queryPrice("NULS"));
    }
}