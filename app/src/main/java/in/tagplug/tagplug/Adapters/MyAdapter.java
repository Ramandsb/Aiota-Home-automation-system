package in.tagplug.tagplug.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import in.tagplug.tagplug.R;
import in.tagplug.tagplug.pojo.DataItems;

/**
 * Created by admin pc on 12-05-2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyviewHolder> {

    private ArrayList<DataItems> infoList = new ArrayList<>();
    private LayoutInflater mInflater;
    Context context;
    DataItems currentItem;
    int pos=0;

    public MyAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context=context;
    }

    public void setData(ArrayList<DataItems> list) {
        this.infoList = list;
        //update the adapter to reflect the new set of movies
        notifyDataSetChanged();
    }


    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.schedule_recycler, parent, false);
        MyviewHolder viewHolder = new MyviewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyviewHolder holder, final int position) {

        DataItems dataItems= new DataItems();
        dataItems=infoList.get(position);
        dataItems.getDevice_id();
//        dataItems.getDevice_name();
        holder.devicename.setText(dataItems.getDevice_name());
        holder.endtime.setText(dataItems.getDevice_id());

    }


    @Override
    public int getItemCount() {
        return infoList.size();
    }
    public String getCount() {
        String count= Integer.toString(infoList.size());
        return count;
    }

    static class MyviewHolder extends RecyclerView.ViewHolder {
        TextView starttime;
        TextView endtime;
        TextView devicename;
        Button delete;


        public MyviewHolder(View itemView) {
            super(itemView);
            starttime = (TextView) itemView.findViewById(R.id.start_time);
            endtime = (TextView) itemView.findViewById(R.id.end_time);
            devicename = (TextView) itemView.findViewById(R.id.device_name);
            delete = (Button) itemView.findViewById(R.id.delete);

        }
    }
}