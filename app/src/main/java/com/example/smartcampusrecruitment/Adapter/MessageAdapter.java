package com.example.smartcampusrecruitment.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.model.dto.ChatMessageDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private List<ChatMessageDTO> messages;
    private String currentUserPhone;
    private static final int TYPE_SENT   = 0;
    private static final int TYPE_RECEIVED = 1;
    private Map<String, String> avatarMap = new HashMap<>();


    public MessageAdapter(List<ChatMessageDTO> messages, String currentUserPhone) {
        this.messages = messages;
        this.currentUserPhone = currentUserPhone;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = (viewType == TYPE_SENT)
                ? R.layout.item_message_sent
                : R.layout.item_message_received;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        ChatMessageDTO message = messages.get(position);


        // 直接用消息里的头像
        String avatarBase64 = message.getTouxiang();
        setAvatar(holder.img_touxiang, avatarBase64);
        Log.d("AvatarDebug", "position=" + position +
                " 头像长度=" + (avatarBase64 == null ? 0 : avatarBase64.length()));
        holder.tvMessage.setText(message.getContent());
        holder.tvTime.setText(message.getSentAt() != null
                ? message.getSentAt().toString().replace("T", " ")
                : "");
    }

    private void setAvatar(ImageView imgTouxiang, String avatarBase64) {
        if (avatarBase64 == null || avatarBase64.trim().isEmpty()) {
            Log.d("Avatar", "空Base64，使用默认");
            imgTouxiang.setImageResource(R.drawable.touxiang);
            return;
        }

        if (!isValidBase64(avatarBase64)) {
            Log.d("Avatar", "Base64 字符串不合法");
            imgTouxiang.setImageResource(R.drawable.touxiang);
            return;
        }

        try {
            byte[] bytes = Base64.decode(avatarBase64, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (bmp == null) {
                Log.d("Avatar", "解码后Bitmap为空");
                imgTouxiang.setImageResource(R.drawable.touxiang);
            } else {
                Log.d("Avatar", "解码成功，尺寸：" + bmp.getWidth() + "x" + bmp.getHeight());
                imgTouxiang.setImageBitmap(bmp);
            }
        } catch (Exception e) {
            Log.d("Avatar", "解码异常：" + e.getMessage());
            imgTouxiang.setImageResource(R.drawable.touxiang);
        }
    }

    private boolean isValidBase64(String avatarBase64) {
        return avatarBase64 != null && avatarBase64.matches("^[A-Za-z0-9+/]*={0,2}$");
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage , tvTime;
        ImageView img_touxiang;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime=itemView.findViewById(R.id.tvTime);
            img_touxiang=itemView.findViewById(R.id.img_touxiang);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageDTO msg = messages.get(position);
        return msg.getSenderPhone().equals(currentUserPhone) ? TYPE_SENT : TYPE_RECEIVED;
    }
}
