package apt.hacktogether.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import apt.hacktogether.R;
import apt.hacktogether.activity.BrowseActivity;
import apt.hacktogether.activity.MessageActivity;
import apt.hacktogether.utils.Common;
import apt.hacktogether.utils.ParseUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by de-weikung on 12/5/15.
 */
public class PersonTabAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<ParseUser> mList;

    static class ViewHolder {
        @Bind(R.id.img_pic) public CircleImageView imgProfile;
        @Bind(R.id.txt_person_name) public TextView txtPersonName;
        @Bind(R.id.txt_interest) public TextView txtInterest;
        @Bind(R.id.txt_skill) public TextView txtSkill;
        public Button chatRoomButton;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            chatRoomButton = (Button) view.findViewById(R.id.btn_chat_room);
        }
    }

    public PersonTabAdapter(Context context, List<ParseUser> list) {
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
            convertView = mInflater.inflate(R.layout.card_person_tab, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ParseUser hacker = mList.get(position);
        ParseFile imgFile = hacker.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
        Picasso.with(mContext)
                .load(imgFile.getUrl())
                .into(holder.imgProfile);
        holder.txtPersonName.setText(hacker.getString(Common.OBJECT_USER_NICK));
        getHackerInterests(hacker, holder);
        getHackerSkills(hacker, holder);
        holder.chatRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MessageActivity.class);
                ArrayList<String> list = new ArrayList<String>(Arrays.asList(hacker.getObjectId()));
                list.add(ParseUser.getCurrentUser().getObjectId());
                intent.putStringArrayListExtra("mTargetParticipants", list);
                v.getContext().startActivity(intent);
            }
        });

        if (ParseUser.getCurrentUser().getObjectId().equals(hacker.getObjectId())) holder.chatRoomButton.setVisibility(View.GONE);
        return convertView;
    }

    private void getHackerInterests(ParseUser hacker, final ViewHolder holder){
        ParseRelation<ParseObject> interestsRelation = hacker.getRelation(Common.OBJECT_USER_INTERESTS);
        interestsRelation.getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> interests, ParseException e) {
                StringBuilder s = new StringBuilder();
                for (int i = 0; i< interests.size(); i++) {
                    s.append(interests.get(i).getString(Common.OBJECT_INTEREST_NAME));
                    if (i != interests.size()-1) s.append(", ");
                }
                holder.txtInterest.setText(s.toString());
            }
        });
    }

    private void getHackerSkills(ParseUser hacker, final ViewHolder holder){
        ParseRelation<ParseObject> skillsRelation = hacker.getRelation(Common.OBJECT_USER_SKILLS);
        skillsRelation.getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> skills, ParseException e) {
                StringBuilder s = new StringBuilder();
                for (int i = 0; i< skills.size(); i++) {
                    s.append(skills.get(i).getString(Common.OBJECT_SKILL_NAME));
                    if (i != skills.size()-1) s.append(", ");
                }
                holder.txtSkill.setText(s.toString());
            }
        });
    }
}
