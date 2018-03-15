/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.sliding.adaptive.thread.pool.report.momentum;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author george-toma
 */
public class ThreadPoolMomentum {

    private final LocalDateTime momentumDateTime;
    private final double momentum;
    private final int threadPoolSize;

    public ThreadPoolMomentum(LocalDateTime momentumDateTime, double momentum, int threadPoolSize) {
        this.momentumDateTime = momentumDateTime;
        this.momentum = momentum;
        this.threadPoolSize = threadPoolSize;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.momentumDateTime);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.momentum) ^ (Double.doubleToLongBits(this.momentum) >>> 32));
        hash = 41 * hash + this.threadPoolSize;
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
        if (Double.doubleToLongBits(this.momentum) != Double.doubleToLongBits(other.momentum)) {
            return false;
        }
        if (this.threadPoolSize != other.threadPoolSize) {
            return false;
        }
        return Objects.equals(this.momentumDateTime, other.momentumDateTime);
    }

}
