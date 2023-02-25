package com.example.syq.nfcpro00.tools.items;

import android.view.View;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Class Name CardItem
 * Created by Gorio on 2018/3/20.
 *
 * @author Gorio
 * @date 2018/3/20
 */

public class CardItem {
//需要添加一个onClickListener来给页面中的Button中添加

    private int mTitleResource;
    private int mTextResource;
    private int mButtonText;
    private String mImageColor;
    private View.OnClickListener onClickListener;
    public CardItem(int title, int text) {
        mTitleResource = text;
        mTextResource = title;
    }

    public CardItem(int mTitleResource, int mTextResource, int mButtonText, String mImageColor, View.OnClickListener onClickListener) {
        this.mTitleResource = mTitleResource;
        this.mTextResource = mTextResource;
        this.mButtonText = mButtonText;
        this.mImageColor = mImageColor;
        this.onClickListener = onClickListener;
    }

    public int getmTitleResource() {
        return mTitleResource;
    }

    public int getmTextResource() {
        return mTextResource;
    }

    public int getmButtonText() {
        return mButtonText;
    }

    public String getmImageColor() {
        return mImageColor;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }
}
