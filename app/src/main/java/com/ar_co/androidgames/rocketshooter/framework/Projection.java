package com.ar_co.androidgames.rocketshooter.framework;

public class Projection {

    float min;
    float max;

    public Projection() {

    }

    public Projection(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public boolean overlap(Projection p) {
        return (min <= p.max && min >= p.min) || (max >= p.min && max <= p.max);
    }
}
