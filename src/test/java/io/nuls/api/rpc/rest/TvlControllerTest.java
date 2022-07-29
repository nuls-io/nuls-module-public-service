package io.nuls.api.rpc.rest;

import junit.framework.TestCase;

/**
 * @author Niels
 */
public class TvlControllerTest extends TestCase {

    public void testGetNulsPriceFromEx() {
        Double value = TvlController.getNulsPriceFromEx(TvlController.binancePriceProvider);
        System.out.println(value);
        value = TvlController.getNulsPriceFromEx(TvlController.huobiPriceProvider);
        System.out.println(value);
        value = TvlController.getNulsPriceFromEx(TvlController.okexPriceProvider);
        System.out.println(value);
        value = TvlController.getNulsPriceFromEx(TvlController.dexPriceProvider);
        System.out.println(value);
    }
}