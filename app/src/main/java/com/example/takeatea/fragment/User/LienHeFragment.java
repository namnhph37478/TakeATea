package com.example.takeatea.fragment.User;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeatea.R;
import com.example.takeatea.adapter.MessageAdapter;
import com.example.takeatea.dao.ContactDAO;
import com.example.takeatea.model.Contact;
import com.example.takeatea.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LienHeFragment extends Fragment {

    private RecyclerView recyclerChat;
    private EditText edtMessage;
    private ImageButton btnSend;
    private ContactDAO contactDAO;
    private MessageAdapter adapter;
    private ArrayList<Contact> messages;
    private SessionManager session;

    private final String[] keywords = {"ngon", "dở", "tệ", "thơm", "vừa miệng", "chất lượng"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_contact, container, false);

        recyclerChat = view.findViewById(R.id.recyclerChat);
        edtMessage = view.findViewById(R.id.edtMessage);
        btnSend = view.findViewById(R.id.btnSend);

        contactDAO = new ContactDAO(getContext());
        session = new SessionManager(getContext());

        int userId = session.getUserId();
        messages = contactDAO.getByUserId(userId);
        adapter = new MessageAdapter(getContext(), messages);

        recyclerChat.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerChat.setAdapter(adapter);

        btnSend.setOnClickListener(v -> {
            String msg = edtMessage.getText().toString().trim();
            if (TextUtils.isEmpty(msg)) return;

            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            Contact userMsg = new Contact(userId, msg, now, true);
            contactDAO.insert(userMsg);
            messages.add(userMsg);
            adapter.notifyItemInserted(messages.size() - 1);
            recyclerChat.scrollToPosition(messages.size() - 1);
            edtMessage.setText("");

            // Trả lời tự động
            String reply = getBotReply(msg);
            Contact botMsg = new Contact(userId, reply, now, false);
            contactDAO.insert(botMsg);
            messages.add(botMsg);
            adapter.notifyItemInserted(messages.size() - 1);
            recyclerChat.scrollToPosition(messages.size() - 1);
        });

        return view;
    }

    private String getBotReply(String msg) {
        msg = msg.toLowerCase();
        for (String keyword : keywords) {
            if (msg.contains(keyword)) {
                return "Cảm ơn bạn đã góp ý về sản phẩm! Chúng tôi sẽ cải thiện.";
            }
        }
        if (msg.contains("giờ") || msg.contains("mở cửa")) {
            return "Chúng tôi mở cửa từ 8:00 đến 22:00 hàng ngày.";
        } else if (msg.contains("ship") || msg.contains("giao hàng")) {
            return "Chúng tôi hỗ trợ giao hàng trong bán kính 5km.";
        } else if (msg.contains("món") || msg.contains("giá rẻ")) {
            return "Bạn có thể xem sản phẩm giá rẻ trong trang chủ.";
        }

        return "Cảm ơn bạn đã nhắn tin! Chúng tôi sẽ phản hồi sớm.";
    }
}
