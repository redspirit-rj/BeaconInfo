package info.redspirit.beaconinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static info.redspirit.beaconinfo.R.id.textView_sub;

/**
 * Created by rj on 2017/01/20.
 */

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.ViewHolder> {
    private String[] list;
    private Integer[] idList;
    private Context context;

    //アクティビティ取得
    private Activity mActivity;
    private LayoutInflater mLayoutInflater;
    private View v;

    public CardRecyclerAdapter(Context context, String[] stringArray, Integer[] idArray) {
        super();
        this.list = stringArray;
        this.idList = idArray;
        this.context = context;
    }


//    public CardRecyclerAdapter(Context context) {
//        super();
//        mLayoutInflater = LayoutInflater.from(context);
//        this.context = context;
//    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, final int position) {
        vh.textView_main.setText(list[position]);
        vh.imageView.setImageResource(R.mipmap.ic_place_white);
        vh.textView_sub.setText("ID: " + String.valueOf(idList[position]));
        vh.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, String.valueOf(idList[position]) + ":" + list[position], Toast.LENGTH_SHORT).show();
                infoView(idList[position]);
            }
        });
    }

    protected void infoView(int id) {
        Intent intent = new Intent(context, InfoActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    public CardRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.layout_recycler, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_main;
        TextView textView_sub;
        LinearLayout layout;
        ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            textView_main = (TextView) v.findViewById(R.id.textView_main);
            textView_sub = (TextView) v.findViewById(R.id.textView_sub);
            layout = (LinearLayout) v.findViewById(R.id.layout);
            imageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }
}
