package adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import model.Travel;
import wilbert.com.travelalbum.R;

/**
 * Created by wilbert on 2016/11/27.
 */
public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.ViewHolder>  {
    public interface IOnItemClick {
        void onItemClick(View view);
    }
    private IOnItemClick iOnItemClick;
    private List travelList;
    public void setDataSet(List newDataSet) {
        this.travelList = newDataSet;
    }
    public TravelAdapter(List travelList, IOnItemClick iOnItemClick) {
        this.travelList = travelList;
        this.iOnItemClick = iOnItemClick;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_travel_view, parent, false);
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
        holder.textView.setText(((Travel)travelList.get(position)).getTitle());
    }

    @Override
    public int getItemCount() {
        return travelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public View view;
        public ViewHolder(View t) {
            super(t);
            view = t;
            textView = (TextView) t.findViewById(R.id.itemTextView);
        }
    }
}
