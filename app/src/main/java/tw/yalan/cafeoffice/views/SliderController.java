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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.yalan.cafeoffice.R;

/**
 * Created by Alan Ding on 2017/1/30.
 */
public class SliderController {
    LinearLayout sliderLayout;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.discrete)
    DiscreteSeekBar discrete;
    @BindView(R.id.tv_value)
    TextView tvValue;

    public SliderController(LinearLayout sliderLayout) {
        this.sliderLayout = sliderLayout;
        ButterKnife.bind(this, sliderLayout);
    }

    public void setProgress(int progress) {
        discrete.setProgress(progress);
    }

    public void setup(@DrawableRes int iconRes, String title, int defaultProgress) {
        ivIcon.setImageResource(iconRes);
        tvName.setText(title);
        discrete.setNumericTransformer(new SyncTextViewNumericTransformer(tvValue));
        discrete.setProgress(defaultProgress);
    }

    public void setMax(int max) {
        discrete.setMax(max);
    }

    public void setMin(int min) {
        discrete.setMin(min);
    }

    public int getProgress() {
        return discrete.getProgress();
    }

    public String getValue() {
        return tvValue.getText().toString();
    }


}
