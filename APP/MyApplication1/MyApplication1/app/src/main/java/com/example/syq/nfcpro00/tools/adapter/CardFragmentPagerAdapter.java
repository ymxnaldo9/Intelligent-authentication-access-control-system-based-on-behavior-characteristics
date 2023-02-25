package com.example.syq.nfcpro00.tools.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;

import com.example.syq.nfcpro00.view.fragment.CardFragment;

import java.util.ArrayList;
import java.util.List;




/**
 * Class Name CardFragmentPagerAdapter
 * Created by Gorio on 2018/3/20.
 *
 * @author Gorio
 * @date 2018 /3/20
 */
public class CardFragmentPagerAdapter extends FragmentStatePagerAdapter implements CardAdapter {
    private List<CardFragment> mFragments;
    private float mBaseElevation;

    /**
     * Instantiates a new Card fragment pager adapter.
     *
     * @param fm            the fm
     * @param baseElevation the base elevation
     */
    public CardFragmentPagerAdapter(FragmentManager fm,float baseElevation){
        super(fm);
        mFragments  = new ArrayList<>();
        mBaseElevation = baseElevation;
        for (int i = 0; i < 5; i++) {
            addCardFragment(new CardFragment());
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return mFragments.size();
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    /**
     * Gets base elevation.
     *
     * @return the base elevation
     */
    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    /**
     * Gets card view at.
     *
     * @param position the position
     * @return the card view at
     */
    @Override
    public CardView getCardViewAt(int position) {
        return mFragments.get(position).getCardView();
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container,position);
        mFragments.set(position,(CardFragment) fragment);
        return fragment;
    }

    /**
     * Add card fragment.
     *
     * @param fragment the fragment
     */
    public void addCardFragment(CardFragment fragment){
        mFragments.add(fragment);
    }
}
