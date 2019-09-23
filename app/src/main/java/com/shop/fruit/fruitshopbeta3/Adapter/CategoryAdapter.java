package com.shop.fruit.fruitshopbeta3.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shop.fruit.fruitshopbeta3.Modul.Category;
import com.shop.fruit.fruitshopbeta3.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    // Implements
    Context content;
    List<Category> categories;

    /**
     * @construct
     * @param content
     * @param categories
     */
    public CategoryAdapter(Context content, List<Category> categories) {
        this.content = content;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(content).inflate(R.layout.menu_item_layout, null);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int position) {
        // Load Image items
        Picasso.with(content)
                .load(categories.get(position).link_cat)
                .into(categoryViewHolder.image_menu_view_item);
        categoryViewHolder.text_menu_item.setText(categories.get(position).name_cat);

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
