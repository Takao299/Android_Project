package com.example.spbtex.ui.facility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.spbtex.R;
import com.example.spbtex.dataload.AttachedFile;
import com.example.spbtex.dataload.ShowAsyncTask;
import com.example.spbtex.sqlite.DbOpenHelper;
import com.example.spbtex.sqlite.FilesModel;
import com.example.spbtex.ui.reservation.ReservationActivity;

import java.io.File;
import java.util.List;

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.ViewHolder>{

    private List<Facility> facilityList;
    private Context context;

    //あらかじめローカルに用意した画像
    int[] imageList = {
            R.drawable.room1,
            R.drawable.room2,
            R.drawable.room3,
            R.drawable.bike,
            R.drawable.running
    };

    public CardRecyclerAdapter(Context context,List<Facility> facilityList) {
        super();
        this.facilityList = facilityList;
        this.context = context;
    }

    public CardRecyclerAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, @SuppressLint("RecyclerView") final int position) { //final int position

        //画像表示
        if(DbOpenHelper.STAND_ALONE){
            //サーバーに接続しない場合、あらかじめローカルに用意した画像を使用
            vh.imageView.setImageResource(imageList[position]);
        }else{
            //サーバーから内部ストレージに保存した画像を表示
            FilesModel filesModel = new FilesModel(context);
            //facilityのIDとAttachedFileの外部キーが一致した画像情報を取得。施設に関連付けられた画像である
            List<AttachedFile> attachedFileList = filesModel.searchAllData(facilityList.get(position).getId());
            if(!attachedFileList.isEmpty()){
                //最初の1枚のみ取得
                AttachedFile af = attachedFileList.get(0);
                String f_name = af.getCreateTime() + af.getFileName();
                File file = new File(context.getFilesDir(), f_name);
                if (file.exists()) {
                    ShowAsyncTask task = new ShowAsyncTask();
                    task.setContext(context);
                    task.setFileName(f_name);
                    task.setImageView(vh.imageView);
                    task.setWait(false);
                    task.execute();
                }
            }
        }

        //他テキスト表示
        vh.textView_f_name.setText(facilityList.get(position).getName());
        vh.textView_amount.setText(context.getResources().getString(R.string.fc_amount)+ facilityList.get(position).getAmount().toString());
        vh.textView_memo.setText(facilityList.get(position).getMemo());
        vh.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, facilityList.get(position).getName().toString(),Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, ReservationActivity.class);
                intent.putExtra("EXTRA_DATA_ID", facilityList.get(position).getId().toString());
                intent.putExtra("EXTRA_DATA_NAME", facilityList.get(position).getName().toString());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.layout_recycler, parent, false);
        return new ViewHolder(v);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView_f_name;
        TextView textView_amount;
        TextView textView_memo;
        LinearLayout layout;

        public ViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.imageView);
            textView_f_name = (TextView)v.findViewById(R.id.textView_f_name);
            textView_amount = (TextView)v.findViewById(R.id.textView_amount);
            textView_memo = (TextView)v.findViewById(R.id.textView_memo);
            layout = (LinearLayout)v.findViewById(R.id.layout);
        }
    }
}

