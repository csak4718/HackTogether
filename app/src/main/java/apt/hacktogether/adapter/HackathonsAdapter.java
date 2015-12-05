package apt.hacktogether.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import apt.hacktogether.R;
import apt.hacktogether.utils.Utils;

/**
 * Created by de-weikung on 12/4/15.
 */
public class HackathonsAdapter extends RecyclerView.Adapter<HackathonsAdapter.ViewHolder> {
    private String[] mHackathons;
    private Context mContext;


    public HackathonsAdapter(Context context, String[] arr){
        super();
        mContext = context;
        mHackathons = arr;
    }

    @Override
    public HackathonsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_hackathon, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HackathonsAdapter.ViewHolder holder, final int position) {
        holder.tvHackathonName.setText(mHackathons[position]);
        holder.tvHackathonName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoBrowseActivity(mContext, mHackathons[position]);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mHackathons.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvHackathonName;

        public ViewHolder(View v) {
            super(v);
            tvHackathonName = (TextView) v.findViewById(R.id.tv_hackathon_name);
        }
    }
}
