package apt.hacktogether.adapter;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
        @Bind(R.id.ll_pending_members) public LinearLayout ll_pendingMembers;

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
        getPendingMembers(myGroup, holder);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // card clicked
                Utils.gotoEditGroupActivity(mContext, myGroup.getObjectId());
            }
        });

        return convertView;
    }

    private void getMembers(final ParseObject myGroup, final ViewHolder holder){
        ParseRelation<ParseUser> membersRelation = myGroup.getRelation(Common.OBJECT_GROUP_MEMBERS);
        membersRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> members, ParseException e) {
//                Log.d("WILL getChildCount ", String.valueOf(holder.ll_Members.getChildCount()));
                // remove first. Otherwise, will have repeated icons.
                holder.ll_Members.removeAllViews();
//                Log.d("After remove, getChildCount ", String.valueOf(holder.ll_Members.getChildCount()));

                for (ParseUser member: members){
                    ParseFile imgFile = member.getParseFile(Common.OBJECT_USER_PROFILE_PIC);

                    CircleImageView imgProfile = new CircleImageView(mContext);
                    LinearLayout.LayoutParams imgProfile_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    imgProfile_params.setMargins(0, 0, 10, 10);
                    imgProfile.setLayoutParams(imgProfile_params);
                    imgProfile.getLayoutParams().height = 80;
                    imgProfile.getLayoutParams().width = 80;
                    imgProfile.setImageResource(R.drawable.ic_account_circle_black_48dp);
                    Picasso.with(mContext)
                            .load(imgFile.getUrl())
                            .into(imgProfile);

                    holder.ll_Members.addView(imgProfile);
                }

            }
        });
    }

    private void getPendingMembers(final ParseObject myGroup, final ViewHolder holder){
        ParseRelation<ParseUser> pendingMembersRelation = myGroup.getRelation(Common.OBJECT_GROUP_PENDINGMEMBERS);
        pendingMembersRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> pendingMembers, ParseException e) {
                // remove first. Otherwise, will have repeated icons.
                holder.ll_pendingMembers.removeAllViews();

                for (ParseUser pendingMember: pendingMembers){
                    ParseFile imgFile = pendingMember.getParseFile(Common.OBJECT_USER_PROFILE_PIC);

                    CircleImageView imgProfile = new CircleImageView(mContext);
                    LinearLayout.LayoutParams imgProfile_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    imgProfile_params.setMargins(0, 0, 10, 0);
                    imgProfile.setLayoutParams(imgProfile_params);
                    imgProfile.getLayoutParams().height = 80;
                    imgProfile.getLayoutParams().width = 80;
                    imgProfile.setImageResource(R.drawable.ic_account_circle_black_48dp);
                    Picasso.with(mContext)
                            .load(imgFile.getUrl())
                            .into(imgProfile);
                    Utils.toGrayScale(imgProfile);

                    holder.ll_pendingMembers.addView(imgProfile);
                }

            }
        });
    }
}
