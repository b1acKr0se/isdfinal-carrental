package io.b1ackr0se.carrental.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import io.b1ackr0se.carrental.R;

public class PreviewAdapter extends PagerAdapter {

    private Activity activity;
    private ArrayList<String> list;
    private LayoutInflater layoutInflater;

    public PreviewAdapter(Activity activity, ArrayList<String> list) {
        this.activity = activity;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.item_preview_pic, container, false);
        ImageView imageView = (ImageView) layout.findViewById(R.id.image);
        Glide.with(activity).load(list.get(position)).into(imageView);
        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}
