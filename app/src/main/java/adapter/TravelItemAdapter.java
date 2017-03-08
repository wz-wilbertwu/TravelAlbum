package adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import model.TravelItem;
import wilbert.com.travelalbum.R;

/**
 * Created by wilbert on 2016/11/29.
 */
public class TravelItemAdapter extends RecyclerView.Adapter<TravelItemAdapter.ViewHolder>{
    private IOnItemClick iOnItemClick;
    private List travelItemList;
    public void setDataSet(List newDataSet) {
        this.travelItemList = newDataSet;
    }
    public TravelItemAdapter(List travelItemList, IOnItemClick iOnItemClick) {
        this.travelItemList = travelItemList;
        this.iOnItemClick = iOnItemClick;
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
        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.textView = (TextView)itemView.findViewById(R.id.itemTextView);
        }
    }
}
