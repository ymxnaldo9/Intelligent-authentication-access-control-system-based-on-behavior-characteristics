package com.example.syq.nfcpro00.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.syq.nfcpro00.R;
import com.example.syq.nfcpro00.tools.adapter.CardAdapter;
import com.sdsmdg.tastytoast.TastyToast;


/**
 * Class Name CardFragment
 * Created by Gorio on 2018/3/20.
 *
 * @author Gorio
 * @date 2018/3/20
 */
public class CardFragment extends Fragment {
    private CardView mCardView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adapter, container, false);CardView cardView = (CardView) view.findViewById(R.id.cardView);

        mCardView = view.findViewById(R.id.cardView);
        Button mButton = view.findViewById(R.id.fragmentButton);
        mButton.setOnClickListener((v)-> TastyToast.makeText(getContext(),"点击", TastyToast.LENGTH_SHORT,TastyToast.INFO));
        mCardView.setMaxCardElevation(mCardView.getCardElevation()
                * CardAdapter.MAX_ELEVATION_FACTOR);
        return view;
    }

    public CardView getCardView() {
        return mCardView;
    }


}
