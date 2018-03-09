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

import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

/**
 * Created by Alan Ding on 2017/1/30.
 */
public class SyncTextViewNumericTransformer extends DiscreteSeekBar.NumericTransformer {
    WeakReference<TextView> textViewWeakReference;
    static DecimalFormat df = new DecimalFormat("0.0");

    public SyncTextViewNumericTransformer(TextView textView) {
        this.textViewWeakReference = new WeakReference<>(textView);
    }

    @Override
    public int transform(int value) {
        return 0;
    }

    @Override
    public String transformToString(int value) {
        double v = (double) value * 0.5;
        String str = df.format(v);
        final TextView view = textViewWeakReference.get();
        if (view != null) {
            view.setText(str);
        }
        return str;
    }

    @Override
    public boolean useStringTransform() {
        return true;
    }
}
