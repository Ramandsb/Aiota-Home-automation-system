package in.tagplug.tagplug;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by admin pc on 16-12-2015.
 */
public class Schedule_adapter extends RecyclerView.Adapter<Schedule_adapter.MyviewHolder> {

    private ArrayList<DataItems> infoList = new ArrayList<>();
    private LayoutInflater mInflater;
    Context context;
    DataItems currentItem;
    int pos=0;

    public Schedule_adapter(Context context) {
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
         currentItem = infoList.get(position);
        holder.devicename.setText(currentItem.getAppid());
        holder.starttime.setText("Start :"+currentItem.getStart_hour()+":"+currentItem.getStart_min());
        holder.endtime.setText("End :"+currentItem.getEnd_hour()+":"+currentItem.getEnd_min());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseOperations dop = new DatabaseOperations(context);
                dop.deleteScheduler(dop,currentItem.getUnique_stamp());
//                infoList.clear();
            setData(dop.readScheduleData(dop, currentItem.getTitle()));
                Intent i = new Intent(context,ScheduleActivity.class);
                i.putExtra("device_name", currentItem.getAppid());
                context.startActivity(i);
                ((Activity)context).finish();




            }
        });


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
