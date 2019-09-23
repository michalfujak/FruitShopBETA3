package com.shop.fruit.fruitshopbeta3.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shop.fruit.fruitshopbeta3.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    //
    ImageView image_menu_view_item;
    TextView text_menu_item;

    /*
    * Construct
     */
    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);

        image_menu_view_item = (ImageView)itemView.findViewById(R.id.image_menu_product);
        text_menu_item = (TextView)itemView.findViewById(R.id.text_menu_product_name);
    }
}
