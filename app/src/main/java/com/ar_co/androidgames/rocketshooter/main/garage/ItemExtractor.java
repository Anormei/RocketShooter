package com.ar_co.androidgames.rocketshooter.main.garage;

import com.ar_co.androidgames.rocketshooter.main.parts.Part;

public interface ItemExtractor {
        void attachItem(Part part, float x, float y);

        boolean hasItem();
}
