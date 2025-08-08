package com.example.takeatea.fragment.User;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.List;
import java.util.Locale;

public class LienHeFragment extends Fragment {

    // UI
    private RecyclerView recyclerChat;
    private EditText edtMessage;
    private ImageButton btnSend;
    private TextView tvCollectedFeedback;

    // Data
    private final List<Contact> messageList = new ArrayList<>();
    private final List<String> collectedFeedback = new ArrayList<>();
    private MessageAdapter adapter;

    // DAO / session
    private ContactDAO contactDAO;
    private SessionManager sessionManager;
    private int userId;

    // Từ khóa để nhận diện ý kiến/đánh giá của user
    private static final String[] FEEDBACK_KEYWORDS = new String[]{
            "góp ý", "đề xuất", "phản hồi", "chưa ổn", "chưa tốt",
            "ngon", "dở", "nhạt", "mặn", "ngọt", "đậm", "hơi ngọt", "quá ngọt",
            "phục vụ", "đóng gói", "ship", "giao hàng", "đặt hàng"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_contact, container, false);

        // Ánh xạ
        recyclerChat = v.findViewById(R.id.recyclerChat);
        edtMessage = v.findViewById(R.id.edtMessage);
        btnSend = v.findViewById(R.id.btnSend);
        tvCollectedFeedback = v.findViewById(R.id.tvCollectedFeedback);

        // Khởi tạo
        contactDAO = new ContactDAO(requireContext());
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        // RecyclerView
        adapter = new MessageAdapter(requireContext(), messageList);
        recyclerChat.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerChat.setAdapter(adapter);

        // Nạp lịch sử chat
        loadMessages();

        // Gửi tin
        btnSend.setOnClickListener(v1 -> onSend());

        return v;
    }

    private void loadMessages() {
        messageList.clear();
        messageList.addAll(contactDAO.getByUserId(userId));
        adapter.notifyDataSetChanged();
        scrollToBottom();

        // render lại danh sách ý kiến (chỉ tin nhắn của user, có chứa từ khóa)
        collectedFeedback.clear();
        for (Contact c : messageList) {
            if (c.isFromUser() && isFeedbackMessage(c.getMessage())) {
                collectedFeedback.add(c.getMessage());
            }
        }
        renderCollectedFeedback();
    }

    private void onSend() {
        String userMsg = edtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(userMsg)) {
            Toast.makeText(requireContext(), "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
            return;
        }

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // 1) Lưu & hiển thị tin nhắn của USER
        Contact userContact = new Contact(userId, userMsg, now, true); // <- dùng đúng constructor của model
        contactDAO.insert(userContact);
        messageList.add(userContact);
        adapter.notifyItemInserted(messageList.size() - 1);
        scrollToBottom();

        // 2) Nếu là feedback → thu thập lại (CHỈ câu của user)
        if (isFeedbackMessage(userMsg)) {
            collectedFeedback.add(userMsg);
            renderCollectedFeedback();
        }

        // 3) BOT trả lời (offline rules)
        String botReply = getBotReply(userMsg);
        Contact botContact = new Contact(userId, botReply, now, false);
        contactDAO.insert(botContact);
        messageList.add(botContact);
        adapter.notifyItemInserted(messageList.size() - 1);
        scrollToBottom();

        // Clear ô nhập
        edtMessage.setText("");
    }

    private void scrollToBottom() {
        if (adapter.getItemCount() > 0) {
            recyclerChat.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private void renderCollectedFeedback() {
        if (collectedFeedback.isEmpty()) {
            tvCollectedFeedback.setText("Chưa có ý kiến nào.");
            return;
        }
        StringBuilder sb = new StringBuilder("Ý kiến bot đã thu thập:\n");
        for (String f : collectedFeedback) {
            sb.append("- ").append(f).append("\n");
        }
        tvCollectedFeedback.setText(sb.toString().trim());
    }

    /** Rule nhận diện câu có thể coi là ý kiến/feedback của user */
    private boolean isFeedbackMessage(String msg) {
        String lower = msg.toLowerCase(Locale.ROOT);
        for (String k : FEEDBACK_KEYWORDS) {
            if (lower.contains(k)) return true;
        }
        return false;
    }

    /** Trả lời offline đơn giản của bot */
    private String getBotReply(String msg) {
        String lower = msg.toLowerCase(Locale.ROOT);

        // Trả lời theo chủ đề
        if (lower.contains("giờ") || lower.contains("mấy giờ") || lower.contains("mở cửa")) {
            return "Chúng tôi mở cửa từ 8:00 đến 22:00 hàng ngày.";
        }
        if (lower.contains("ship") || lower.contains("giao hàng") || lower.contains("vận chuyển")) {
            return "Chúng tôi hỗ trợ giao hàng trong bán kính 5km.";
        }
        if (lower.contains("giảm giá") || lower.contains("khuyến mãi") || lower.contains("mã")) {
            return "Bạn có thể xem mục Khuyến mãi ở trang chủ để áp dụng mã giảm giá.";
        }
        if (lower.contains("món") || lower.contains("giá rẻ") || lower.contains("gợi ý")) {
            return "Gợi ý: Trà sữa Truyền Thống và Trà sữa Matcha đang bán chạy nhất!";
        }

        // Mặc định
        return "Cảm ơn bạn đã nhắn tin! Chúng tôi sẽ phản hồi sớm.";
    }
}
