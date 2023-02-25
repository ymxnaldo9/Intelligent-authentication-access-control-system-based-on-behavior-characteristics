package com.example.syq.nfcpro00.tools.adapter;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.syq.nfcpro00.R;
import com.example.syq.nfcpro00.tools.items.CardItem;

import java.util.ArrayList;
import java.util.List;





/**
 * Class Name CardPagerAdapter
 * Created by Gorio on 2018/3/20.
 *
 * @author Gorio
 * @date 2018/3/20
 */
public class CardPagerAdapter extends PagerAdapter implements CardAdapter {
    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;

    public CardPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(CardItem item){
        mViews.add(null);
        mData.add(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addCardItems(List<CardItem> items){
        items.forEach(this::addCardItem);
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
        return mViews.get(position);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return mData.size();
    }

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
     * required for a PagerAdapter to function properly.
     *
     * @param view Page View to check for association with <code>object</code>
     * @param object Object to check for association with <code>view</code>
     * @return true if <code>view</code> is associated with the key object <code>object</code>
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * Create the page for the given position.  The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * {@link #finishUpdate(ViewGroup)}.
     *
     * @param container The containing View in which the page will be shown.
     * @param position  The page position to be instantiated.
     * @return Returns an Object representing the new page.  This does not
     * need to be a View, but can be some other container of the page.
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter,container,false);
        container.addView(view);
        bind(mData.get(position),view);
        CardView cardView = view.findViewById(R.id.cardView);
        if (mBaseElevation == 0){
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position,cardView);
        return view;
    }

    /**
     * Remove a page for the given position.  The adapter is responsible
     * for removing the view from its container, although it only must ensure
     * this is done by the time it returns from {@link #finishUpdate(View)}.
     *
     * @param container The containing View from which the page will be removed.
     * @param position  The page position to be removed.
     * @param object    The same object that was returned by
     *                  {@link #instantiateItem(View, int)}.
     * @deprecated Use {@link #destroyItem(ViewGroup, int, Object)}
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
        mViews.set(position,null);
    }
    private void bind(CardItem item, View view) {
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView contentTextView =  view.findViewById(R.id.contentTextView);
        Button button = view.findViewById(R.id.adapterButton);
        ImageView imageView = view.findViewById(R.id.image);
        if (item.getmImageColor() == null){
            imageView.setColorFilter(android.graphics.Color.parseColor("#f30a0a"));
        }
        else {
            imageView.setColorFilter(android.graphics.Color.parseColor(item.getmImageColor()));
        }
        button.setText(item.getmButtonText());
        button.setOnClickListener(item.getOnClickListener());
        titleTextView.setText(item.getmTitleResource());
        contentTextView.setText(item.getmTextResource());
    }
}
