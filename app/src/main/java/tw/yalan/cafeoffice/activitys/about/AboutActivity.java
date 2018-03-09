package tw.yalan.cafeoffice.activitys.about;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.yalan.cafeoffice.R;
import tw.yalan.cafeoffice.activitys.loadtohome.AnimationSplashActivity;

public class AboutActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnSpecial)
    Button btnSpecial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_white_arrow_back);
        toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        getSupportActionBar().setTitle(R.string.title_about_us);
        RxView.clicks(btnSpecial)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> {
                    startActivity(new Intent(AboutActivity.this, AnimationSplashActivity.class));
                });
    }
}
