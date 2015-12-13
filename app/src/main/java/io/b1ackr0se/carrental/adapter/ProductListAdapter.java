package io.b1ackr0se.carrental.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.etsy.android.grid.util.DynamicHeightImageView;

import java.util.ArrayList;

import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.model.Product;
import io.b1ackr0se.carrental.util.Utility;

public class ProductListAdapter extends ArrayAdapter<Product> implements Filterable {

    private Context context;
    private int resourceId;
    private ArrayList<Product> products;
    private ArrayList<Product> originalList;

    public ProductListAdapter(Context context, int resourceId, ArrayList<Product> list) {
        super(context,resourceId, list);
        this.context = context;
        this.resourceId = resourceId;
        this.products = list;
        originalList = new ArrayList<>(products);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Product getItem(int position) {
        return products.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ViewHolder holder;

        if (row == null) {
            row = LayoutInflater.from(context).inflate(resourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (DynamicHeightImageView) row.findViewById(R.id.image);
            holder.name = (TextView) row.findViewById(R.id.name);
            holder.price = (TextView) row.findViewById(R.id.price);
            row.setTag(holder);
        } else holder = (ViewHolder) row.getTag();

        final Product product = getItem(position);
        Glide.with(context).load(product.getImages().get(0)).into(holder.image);
        holder.image.setHeightRatio(1.0);

        holder.name.setText(product.getName().trim());
        holder.price.setText(Utility.showCurrency(product.getPrice()));

        return row;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                if(charSequence == null || charSequence.length() == 0) {
                    results.values = originalList;
                    results.count = originalList.size();
                } else {
                    ArrayList<Product> filteredRowItems = new ArrayList<>();
                    products = originalList;
                    for (Product product : products) {
                        if(product.getName().trim().toLowerCase().contains(charSequence.toString().trim().toLowerCase()))
                            filteredRowItems.add(product);
                    }
                    results.values = filteredRowItems;
                    results.count = filteredRowItems.size();
                }
                return  results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if(filterResults.count == 0)
                    notifyDataSetInvalidated();
                else {
                    products = (ArrayList<Product>) filterResults.values;
                    notifyDataSetChanged();
                }
            }
        };
    }

    private static class ViewHolder {
        DynamicHeightImageView image;
        TextView name;
        TextView price;
    }

}
