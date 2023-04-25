package io.nuls.api.task;

import io.nuls.core.model.DoubleUtils;
import junit.framework.TestCase;
import org.junit.Test;

public class StatisticalTaskTest   {

    @Test
    public void test(){
        double monthly = StatisticalTask.getDeflationRatio(41095890410959d);

        double d = DoubleUtils.mul(365, monthly);
        d = DoubleUtils.div(d, 30, 0);
        System.out.println(d);
        double annualizedReward = DoubleUtils.mul(100, DoubleUtils.div(d, 5155868000000000d, 4), 2);
        System.out.println(annualizedReward);
    }
}