package com.zjucompass.zjucampus3;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjucompass.zjucampus3.widget.ContactListAdapter;

import java.util.List;


public class TeacherListAdapter extends ContactListAdapter
{

    public TeacherListAdapter(Context _context, int _resource,
                       List<Teacher> _items)
    {
        super(_context, _resource, _items);
    }

    private static final String TAG = "TeacherListAdapter";

    @Override
    public void populateDataForRow(View parentView, Teacher item,
                                   int position)
    {
        View view = parentView.findViewById(R.id.infoRowContainer);

        String id = item.getId();
        String name = item.getName();
        String email = item.getEmail();

        ImageView imageView = (ImageView) view.findViewById(R.id.teacher_img);
        TextView nameView = (TextView) view.findViewById(R.id.teacher_name);
        TextView emailView = (TextView) view.findViewById(R.id.teacher_email);

        String imgId = "t_" + id;
        int resId = getContext().getResources().
                getIdentifier(imgId, "drawable", "com.zjucompass.zjucampus3");
        if(resId != 0) {
            imageView.setImageResource(resId);
        }
        else{
            imageView.setImageResource(R.drawable.avatar_default);
        }
        nameView.setText(name);

        if(email.equals("null")){
            email = "";
        }
        emailView.setText(email);
    }

}

