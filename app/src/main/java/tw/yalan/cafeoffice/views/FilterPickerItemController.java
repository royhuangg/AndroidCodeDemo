package tw.yalan.cafeoffice.views;

/**
 * Copyright (C) 2016 Alan Ding
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.yalan.cafeoffice.R;

/**
 * Created by Alan Ding on 2017/1/30.
 */
public class FilterPickerItemController {
    LinearLayout sliderLayout;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.btnMinus)
    AppCompatImageButton btnMiner;
    @BindView(R.id.btnPlus)
    AppCompatImageButton btnPlus;
    @BindView(R.id.tv_value)
    TextView tvValue;
    static DecimalFormat df = new DecimalFormat("0.#");
    int progress = 6;
    int max = 10;
    int min = 0;

    public FilterPickerItemController(LinearLayout sliderLayout) {
        this.sliderLayout = sliderLayout;
        ButterKnife.bind(this, sliderLayout);
        btnMiner.setOnClickListener(view -> {
            if (min < progress)
                setProgress(progress - 2);

        });
        btnPlus.setOnClickListener(view -> {
            if (max > progress)
                setProgress(progress + 2);

        });
    }

    public void setup(@DrawableRes int iconRes, String title, int defaultProgress) {
        ivIcon.setImageResource(iconRes);
        tvName.setText(title);
        setProgress(defaultProgress);
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        tvValue.setText(df.format(progress * 0.5));
    }

    public int getProgress() {
        return progress;
    }

    public String getValue() {
        return tvValue.getText().toString();
    }

    public void setValue(String value) {
        tvValue.setText(value);
    }

}
