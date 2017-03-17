package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import model.TravelItem;
import util.BitmapUti;
import wilbert.com.travelalbum.R;

/**
 * Created by wilbert on 2016/11/29.
 */
public class TravelItemAdapter extends RecyclerView.Adapter<TravelItemAdapter.ViewHolder>{
    private IOnItemClick iOnItemClick;
    private List travelItemList;
    private Context context;
    public void setDataSet(List newDataSet) {
        this.travelItemList = newDataSet;
    }
    public List getTravelItemList() {
        return travelItemList;
    }
    public TravelItemAdapter(Context context, List travelItemList, IOnItemClick iOnItemClick) {
        this.travelItemList = travelItemList;
        this.iOnItemClick = iOnItemClick;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_travel_item_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        if (iOnItemClick != null) {
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iOnItemClick.onItemClick(view);
                }
            });
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TravelItem item = (TravelItem) travelItemList.get(position);
        holder.textView.setText(item.toString());
        float px = 200 * (context.getResources().getDisplayMetrics().densityDpi / 160f);
        Bitmap bitmap = BitmapUti.getBitmapFromUri(context,
                BitmapUti.getCommonUri(item.getImage()), px);
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return travelItemList.size();
    }

    public interface IOnItemClick {
        void onItemClick(View view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView textView;
        public ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.textView = (TextView)itemView.findViewById(R.id.itemTextView);
            this.imageView = (ImageView)itemView.findViewById(R.id.itemImageView);
        }
    }
}
