package in.tagplug.tagplug;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by admin pc on 05-05-2016.
 */
public class CustomGridViewAdapter extends ArrayAdapter<DataItems> {
    Context context;
    int layoutResourceId;
    ArrayList<DataItems> data = new ArrayList<DataItems>();

    public CustomGridViewAdapter(Context context, int layoutResourceId, ArrayList<DataItems> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        Log.d("constructor","der");
    }
    public void setData(ArrayList<DataItems> list){
        data=list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.item_text);
            holder.imageItem = (ImageView) row.findViewById(R.id.item_image);
            row.setTag(holder);
            DataItems item = data.get(position);
            holder.txtTitle.setText(item.getTitle());
            String imagename= item.getImage();
            Log.d("valuesi want",item.getTitle()+"//"+item.getImage()+"//"+item.getAppid());
            if (imagename.equals("ac")){
                holder.imageItem.setImageResource(R.drawable.ac);
            }else if (imagename.equals("fan")){
                holder.imageItem.setImageResource(R.drawable.fan);
            }else if (imagename.equals("bulb")){
                holder.imageItem.setImageResource(R.drawable.bulb);
            }else  if (imagename.equals("lamp")){
                holder.imageItem.setImageResource(R.drawable.lamp);
            }

            Log.d("getViewif0","der");
        } else {
            holder = (RecordHolder) row.getTag();
            Log.d("getViewELSE","der");
        }

        return row;
    }

    static class RecordHolder {
        TextView txtTitle;
        ImageView imageItem;
    }
}
