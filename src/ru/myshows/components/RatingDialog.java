package ru.myshows.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import ru.myshows.activity.R;
import ru.myshows.client.MyShowsClient;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 10.06.2011
 * Time: 16:57:26
 * To change this template use File | Settings | File Templates.
 */
public class RatingDialog extends Dialog {

    private Button cancelButton;
    private Button okButton;
    private RatingBar ratingBar;
    private Context context;
    private Handler handler;


    public RatingDialog(Context context, Handler handler) {
        super(context);
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.episode_rating);

        cancelButton = (Button) findViewById(R.id.episode_cancel_button);
        okButton = (Button) findViewById(R.id.episode_ok_button);
        ratingBar = (RatingBar) findViewById(R.id.episode_rating);
        getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rating = (int) ratingBar.getRating();
                Message message = new Message();
                message.arg1 = rating;
                handler.sendMessage(message);
                RatingDialog.this.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RatingDialog.this.dismiss();
            }
        });

    }


}
