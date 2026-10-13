package com.example.smartcampusrecruitment.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.model.ContactItem;
import com.example.smartcampusrecruitment.model.dto.RecruiterDTO;
import com.example.smartcampusrecruitment.model.dto.SeekerDTO;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{
    public List<ContactItem> items=new ArrayList<>();
    private OnItemClickListener listener;

    public void setNewData(List<ContactItem> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onItemClick(ContactItem item, int position);
    }
    public static ContactAdapter fromRecruiters(List<RecruiterDTO> list) {
        List<ContactItem> items = new ArrayList<>();
        for (RecruiterDTO dto : list) {
            items.add(new ContactItem(
                    dto.getName(),
                    dto.getTouxiang(),
                    dto.getCompany(),
                    null,
                    dto.getPhone()));
        }
        return new ContactAdapter(items);
    }

    public static ContactAdapter fromSeekers(List<SeekerDTO> list) {
        List<ContactItem> items = new ArrayList<>();
        for (SeekerDTO dto : list) {
            items.add(new ContactItem(
                    dto.getName(),
                    dto.getTouxiang(),
                    null,
                    dto.getGraduationSchool(),
                    dto.getPhone()));
        }
        return new ContactAdapter(items);
    }
    public ContactAdapter(List<ContactItem> items) {
        this.items = items;
    }


    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {
        ContactItem item = items.get(position);
        holder.text_name.setText(item.getName());
        String company = item.getCompany();
        String school  = item.getSchool();
        if (TextUtils.isEmpty(company) && !TextUtils.isEmpty(school)) {
            holder.text_cors.setText(school);
        } else if (!TextUtils.isEmpty(company) && TextUtils.isEmpty(school)) {
            holder.text_cors.setText(company);
        } else {
            holder.text_cors.setText("");   // 或者“暂无信息”
        }
        String base64 = item.getTouxiang();
        if (!TextUtils.isEmpty(base64)) {
            byte[] bytes = Base64.decode(base64, Base64.URL_SAFE);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.img_touxiang.setImageBitmap(bitmap);
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item, position));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_touxiang;
        TextView text_name,text_cors;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_touxiang=itemView.findViewById(R.id.img_touxiang_contact);
            text_name=itemView.findViewById(R.id.text_name_contact);
            text_cors=itemView.findViewById(R.id.text_cors_contact);

        }
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
