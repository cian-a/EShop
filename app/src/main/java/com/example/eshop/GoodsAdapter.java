package com.example.eshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {
    private List<Goods> goods;
    private Context context;

    public GoodsAdapter(Context context, List<Goods> goodsList) {
        this.context = context;
        this.goods = goodsList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goods goods1 = goods.get(position);
        holder.tv_goodsName.setText(goods1.getName());
        holder.tv_goodsPrice.setText(goods1.getPrice());
        Glide.with(context)
                .load(goods1.getImg())
                .into(holder.iv_goodsImg);
    }

    @Override
    public int getItemCount() {
        return goods != null ? goods.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_goodsName;
        TextView tv_goodsPrice;
        ImageView iv_goodsImg;

        ViewHolder(View view) {
            super(view);
            tv_goodsName = view.findViewById(R.id.tv_goodsName);
            tv_goodsPrice = view.findViewById(R.id.tv_goodsPrice);
            iv_goodsImg = view.findViewById(R.id.iv_goodsImg);


        }
    }
}
