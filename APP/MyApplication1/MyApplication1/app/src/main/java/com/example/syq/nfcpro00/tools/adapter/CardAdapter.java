package com.example.syq.nfcpro00.tools.adapter;

import android.support.v7.widget.CardView;

/**
 * Class Name CardAdapter
 * Created by Gorio on 2018/3/20.
 *
 * @author Gorio
 * @date 2018 /3/20
 */
public interface CardAdapter {
    /**
     * The constant MAX_ELEVATION_FACTOR.
     */
    int MAX_ELEVATION_FACTOR = 8;

    /**
     * Gets base elevation.
     *
     * @return the base elevation
     */
    float getBaseElevation();

    /**
     * Gets card view at.
     *
     * @param position the position
     * @return the card view at
     */
    CardView getCardViewAt(int position);

    /**
     * Gets count.
     *
     * @return the count
     */
    int getCount();
}
