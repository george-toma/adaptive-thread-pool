/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.sliding.adaptive.thread.pool.report;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * @author george-toma
 */
public class ThreadPoolMomentum {

    private final LocalDateTime optimalMomentumDateTime;
    private final double optimalMomentum;
    private final int optimalThreadPoolSize;

    public ThreadPoolMomentum(LocalDateTime optimalMomentumDateTime, double optimalMomentum, int optimalThreadPoolSize) {
        this.optimalMomentumDateTime = optimalMomentumDateTime;
        this.optimalMomentum = optimalMomentum;
        this.optimalThreadPoolSize = optimalThreadPoolSize;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.optimalMomentumDateTime);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.optimalMomentum) ^ (Double.doubleToLongBits(this.optimalMomentum) >>> 32));
        hash = 41 * hash + this.optimalThreadPoolSize;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ThreadPoolMomentum other = (ThreadPoolMomentum) obj;
        if (Double.doubleToLongBits(this.optimalMomentum) != Double.doubleToLongBits(other.optimalMomentum)) {
            return false;
        }
        if (this.optimalThreadPoolSize != other.optimalThreadPoolSize) {
            return false;
        }
        return Objects.equals(this.optimalMomentumDateTime, other.optimalMomentumDateTime);
    }

}
