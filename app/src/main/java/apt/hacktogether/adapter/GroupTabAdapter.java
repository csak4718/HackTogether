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
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by de-weikung on 12/5/15.
 */
public class GroupTabAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<ParseObject> mList;

    static class ViewHolder {

        @Bind(R.id.txt_group_name) public TextView txtGroupName;
        @Bind(R.id.ll_members) public LinearLayout ll_Members;
        @Bind(R.id.txt_group_interests) public TextView txtGroupInterests;
        @Bind(R.id.txt_look_for_skills) public TextView txtLookForSkills;
        @OnClick(R.id.btn_chat_room) void goMessage(){
//            TODO
        }

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public GroupTabAdapter(Context context, List<ParseObject> list) {
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
        ViewHolder holder;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.card_group_tab, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ParseObject group = mList.get(position);
        holder.txtGroupName.setText(group.getString(Common.OBJECT_GROUP_NAME));
        getGroupInterests(group, holder);
        getLookForSkills(group, holder);
        getMembers(group, holder);

        return convertView;
    }

    private void getGroupInterests(ParseObject group, final ViewHolder holder){
        ParseRelation<ParseObject> groupInterestsRelation = group.getRelation(Common.OBJECT_GROUP_GROUPINTERESTS);
        groupInterestsRelation.getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> interests, ParseException e) {
                StringBuilder s = new StringBuilder();
                for (int i = 0; i< interests.size(); i++) {
                    s.append(interests.get(i).getString(Common.OBJECT_INTEREST_NAME));
                    if (i != interests.size()-1) s.append(", ");
                }
                holder.txtGroupInterests.setText(s.toString());
            }
        });
    }

    private void getLookForSkills(ParseObject group, final ViewHolder holder){
        ParseRelation<ParseObject> lookForSkillsRelation = group.getRelation(Common.OBJECT_GROUP_LOOKFORSKILLS);
        lookForSkillsRelation.getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> skills, ParseException e) {
                StringBuilder s = new StringBuilder();
                for (int i = 0; i< skills.size(); i++) {
                    s.append(skills.get(i).getString(Common.OBJECT_SKILL_NAME));
                    if (i != skills.size()-1) s.append(", ");
                }
                holder.txtLookForSkills.setText(s.toString());
            }
        });
    }

    private void getMembers(ParseObject group, final ViewHolder holder){
        ParseRelation<ParseUser> membersRelation = group.getRelation(Common.OBJECT_GROUP_MEMBERS);
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
