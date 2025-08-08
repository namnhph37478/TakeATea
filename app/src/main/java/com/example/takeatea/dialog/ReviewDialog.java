package com.example.takeatea.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.takeatea.R;
import com.example.takeatea.dao.ReviewDAO;
import com.example.takeatea.model.Review;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReviewDialog extends Dialog {

    private final int productId;
    private final int userId;

    public ReviewDialog(Context context, int productId, int userId) {
        super(context);
        this.productId = productId;
        this.userId = userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_user_rating);

        RatingBar ratingBar = findViewById(R.id.ratingBar);
        EditText edtComment = findViewById(R.id.edtComment);
        Button btnSubmit = findViewById(R.id.btnSubmitRating);

        ReviewDAO reviewDAO = new ReviewDAO(getContext());

        btnSubmit.setOnClickListener(v -> {
            String comment = edtComment.getText().toString().trim();
            int rating = (int) ratingBar.getRating();

            if (rating == 0 || comment.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập nội dung và chọn số sao", Toast.LENGTH_SHORT).show();
                return;
            }

            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            Review review = new Review(userId, productId, rating, comment, date);
            boolean success = reviewDAO.insertReview(review);

            if (success) {
                Toast.makeText(getContext(), "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();

                // ✅ Chỉ đóng Activity nếu context là Activity
                if (getContext() instanceof Activity) {
                    ((Activity) getContext()).finish();
                }

                dismiss();
            } else {
                Toast.makeText(getContext(), "Đánh giá thất bại hoặc đã tồn tại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
