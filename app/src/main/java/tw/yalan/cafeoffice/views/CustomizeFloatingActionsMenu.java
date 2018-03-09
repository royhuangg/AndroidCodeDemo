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

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

/**
 * Created by Alan Ding on 2017/1/31.
 */
public class CustomizeFloatingActionsMenu extends FloatingActionsMenu {

    public CustomizeFloatingActionsMenu(Context context) {
        super(context);
    }

    public CustomizeFloatingActionsMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomizeFloatingActionsMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public AddFloatingActionButton getAddFloattingButton() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            if (v != null && v instanceof AddFloatingActionButton) {
                return (AddFloatingActionButton) v;
            }
        }
        return null;

    }
}
