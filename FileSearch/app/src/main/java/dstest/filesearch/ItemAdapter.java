package dstest.filesearch;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2016/8/5.
 */
public class ItemAdapter extends BaseAdapter {

    private Context context;
    private List<AppFileChangeData> datas;

    public static class ViewHolder {
        public TextView packageName;
        public TextView path;
        public TextView time;
    }

    public ItemAdapter(Context context, List<AppFileChangeData> datas){
        this.context = context;
        this.datas = datas;
    }

    public void setItems(List<AppFileChangeData> datas){
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return this.datas.size();
    }

    @Override
    public Object getItem(int i) {
        return this.datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View contentView = view;
        if(view == null){
            contentView = View.inflate(context, R.layout.view_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.packageName = (TextView) contentView.findViewById(R.id.textView);
            viewHolder.path = (TextView) contentView.findViewById(R.id.textView2);
            viewHolder.time = (TextView) contentView.findViewById(R.id.textView3);
            contentView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) contentView.getTag();
        AppFileChangeData data = (AppFileChangeData) getItem(i);
        holder.packageName.setText(data.packageName);
        holder.path.setText(data.path);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(data.time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = sdf.format(data.time);
        holder.time.setText(startTime);
        return contentView;
    }
}
