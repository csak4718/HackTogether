package apt.hacktogether.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import apt.hacktogether.R;
import apt.hacktogether.utils.Common;
import apt.hacktogether.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by de-weikung on 12/6/15.
 */
public class MyGroupsTabAdapter extends BaseAdapter{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<ParseObject> mList;

    static class ViewHolder {
        @Bind(R.id.txt_group_name) public TextView txtGroupName;
        @Bind(R.id.ll_members) public LinearLayout ll_Members;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public MyGroupsTabAdapter(Context context, List<ParseObject> list) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.card_my_groups_tab, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ParseObject myGroup = mList.get(position);
        holder.txtGroupName.setText(myGroup.getString(Common.OBJECT_GROUP_NAME));
        getMembers(myGroup, holder);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // card clicked
                Utils.gotoEditGroupActivity(mContext, holder.txtGroupName.getText().toString());
            }
        });

        return convertView;
    }

    private void getMembers(ParseObject myGroup, final ViewHolder holder){
        ParseRelation<ParseUser> membersRelation = myGroup.getRelation(Common.OBJECT_GROUP_MEMBERS);
        membersRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> members, ParseException e) {
                for (ParseUser member: members){
                    ParseFile imgFile = member.getParseFile(Common.OBJECT_USER_PROFILE_PIC);

                    CircleImageView imgProfile = new CircleImageView(mContext);
                    imgProfile.getLayoutParams().height = 50;
                    imgProfile.getLayoutParams().width = 50;
                    imgProfile.setImageResource(R.drawable.ic_account_circle_black_48dp);
                    Picasso.with(mContext)
                            .load(imgFile.getUrl())
                            .into(imgProfile);
                    holder.ll_Members.addView(imgProfile);

                }
            }
        });
    }
}
