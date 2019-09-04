package com.xesi.xenuser.kuryentxtreadbill.helper;

import android.util.Log;

import java.math.BigDecimal;

/**
 * Created by xenuser on 1/13/2017.
 */

public class ComputeConsumption {
    public ComputeConsumption() {
    }

    public double computeInitialConsumption(double consumption, double meterMultiplier, double totalKwhAddOn) {
        return (consumption * meterMultiplier) + totalKwhAddOn;
    }

    public double computeCoreloss(double consumption, double coreLossKWH) {
        return consumption + coreLossKWH;
    }

    public BigDecimal computeSCDiscount(BigDecimal bdTotalPerKwCharge,
                                        BigDecimal lifeLineDiscount, BigDecimal lifeLineSubsidy,
                                        BigDecimal bdRateSC, String isLLSInclToSCD) {
        BigDecimal scDiscount = new BigDecimal(0).subtract(((bdTotalPerKwCharge).add(lifeLineDiscount)).multiply(bdRateSC));
        if (isLLSInclToSCD.equals("Y"))
            scDiscount = new BigDecimal(0).subtract(((bdTotalPerKwCharge).add(lifeLineDiscount).add(lifeLineSubsidy)).multiply(bdRateSC));
        return scDiscount;
    }

    public BigDecimal computeSCDiscountSoreco(BigDecimal bdTotalPerKwCharge,BigDecimal RFSC,
                                        BigDecimal lifeLineDiscount, BigDecimal lifeLineSubsidy) {
        return new BigDecimal(0).subtract(((bdTotalPerKwCharge).subtract(RFSC)).subtract(lifeLineSubsidy).
                multiply(new BigDecimal("1").subtract(lifeLineDiscount)).multiply(new BigDecimal("0.05")));
    }

    public String checkMonthlyAvgConsumption(double consumption, double avgConsumption, int spike, int drop) {
        String choice = "";
        double percentage;
        if (avgConsumption > 0) {
            percentage = (consumption - avgConsumption) / avgConsumption * 100;
            if (percentage > 0) {
                if (percentage > spike) choice = "SPIKE";
            } else {
                percentage = Math.abs(percentage);
                if (percentage > drop) choice = "DROP";
            }
        }
        return choice;
    }
}
