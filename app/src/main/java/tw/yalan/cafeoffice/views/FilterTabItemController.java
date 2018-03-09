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
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import tw.yalan.cafeoffice.R;

/**
 * Created by Alan Ding on 2017/1/30.
 */
public class FilterTabItemController {
    LinearLayout tabGroupLayout;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tabLeft)
    TextView tabLeft;
    @BindView(R.id.tabCenter)
    TextView tabCenter;
    @BindView(R.id.tabRight)
    TextView tabRight;
    @BindViews({R.id.tabLeft, R.id.tabCenter, R.id.tabRight})
    List<TextView> tabs;
    int value;
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (TextView tab : tabs) {
                tab.setActivated(tab == view);
            }
            value = tabs.indexOf(view);
        }
    };

    public FilterTabItemController(LinearLayout tabGroupLayout) {
        this.tabGroupLayout = tabGroupLayout;
        ButterKnife.bind(this, tabGroupLayout);
    }

    public void setup(@DrawableRes int iconRes, String title, String[] tabsText, int defaultValue) {
        ivIcon.setImageResource(iconRes);
        tvName.setText(title);
        for (int i = 0; i < tabs.size(); i++) {
            tabs.get(i).setText(tabsText[i]);
            tabs.get(i).setOnClickListener(onClickListener);
            if (i == Math.min(2, value)) {
                tabs.get(i).setActivated(true);
            }
        }
        value = Math.min(2, value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = Math.min(2, value);
        tabs.get(Math.min(2, value)).callOnClick();
    }
}
