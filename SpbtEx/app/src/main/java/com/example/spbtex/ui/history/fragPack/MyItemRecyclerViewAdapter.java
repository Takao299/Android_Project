package com.example.spbtex.ui.history.fragPack;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.spbtex.ui.history.DetailActivity;
import com.example.spbtex.ui.history.HistoryData;
import com.example.spbtex.databinding.FragmentItemBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<HistoryData> mValues; //各所PlaceholderItem→Reservationに置き換え

    private int tab_number = 0; //追加

    public MyItemRecyclerViewAdapter(List<HistoryData> items, int tab_number) {
        mValues = items;
        this.tab_number = tab_number;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mLineView.setTag(position);
        holder.mIdView.setText(String.valueOf(position+1));
        holder.mContentView.setText(
                ""+mValues.get(position).getRday()
                +" "+mValues.get(position).getRstart()+"~"+mValues.get(position).getRend()
        );
        holder.mContentView2.setText(mValues.get(position).getFacilityName());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {   //fragment_item.xmlのandroid:id=と紐づける
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mContentView2;
        public final LinearLayout mLineView;

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            mIdView = binding.number; //itemNumber;
            mContentView = binding.content;
            mContentView2 = binding.content2; //追加
            mLineView = binding.line; //追加 一列全ての項目をまとめて押下できるように
            binding.line.setOnClickListener(new ListItemClickListener()); //追加
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    //追加　項目をクリックできるように
    private class ListItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag(); //mLineView.getTag()
            Intent intent = new Intent(view.getContext(), DetailActivity.class);
            intent.putExtra("EXTRA_DATA_ID", mValues.get(position).getId().toString());
            intent.putExtra("EXTRA_DATA_F_NAME", mValues.get(position).getFacilityName());
            //intent.putExtra("EXTRA_DATA_M_NAME", mValues.get(position).getMemberName());
            intent.putExtra("EXTRA_DATA_RDAY", mValues.get(position).getRday());
            intent.putExtra("EXTRA_DATA_RSTART", mValues.get(position).getRstart());
            intent.putExtra("EXTRA_DATA_REND", mValues.get(position).getRend());
            //intent.putExtra("EXTRA_DATA_DELETE", mValues.get(position).getDeleteDateTime());
            String isPast = "false";
            if(tab_number!=0) isPast = "true";
            intent.putExtra("EXTRA_DATA_ISPAST", isPast);
            view.getContext().startActivity(intent);
        }
    }
}