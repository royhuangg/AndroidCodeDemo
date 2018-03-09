package tw.yalan.cafeoffice.activitys.filter;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grasea.grandroid.mvp.UsingPresenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tw.yalan.cafeoffice.Config;
import tw.yalan.cafeoffice.R;
import tw.yalan.cafeoffice.adapter.FastFilterSettingRecyclerAdapter;
import tw.yalan.cafeoffice.common.BaseActivity;
import tw.yalan.cafeoffice.dialog.OpenTimePickerDialog;
import tw.yalan.cafeoffice.filter.FilterRule;
import tw.yalan.cafeoffice.views.FilterTabItemController;

@UsingPresenter(value = CafeFilterPresenter.class, singleton = false)
public class CafeFilterActivity extends BaseActivity<CafeFilterPresenter> {

    @BindViews({R.id.slider_wifi, R.id.slider_quiet, R.id.slider_price, R.id.slider_site, R.id.slider_coffee, R.id.slider_tasty, R.id.slider_music})
    List<LinearLayout> sliderLayouts;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindArray(R.array.filter_item)
    String[] names;
    @BindArray(R.array.filter_icon_item)
    TypedArray icons;

    ArrayList<FilterTabItemController> filterPickerControllerArrayList;
    @BindView(R.id.radio_time)
    RadioGroup radioTime;
    @BindView(R.id.radio_socket)
    RadioGroup radioSocket;
    @BindView(R.id.recyclerSettings)
    RecyclerView recyclerSettings;
    @BindView(R.id.cb_opening)
    AppCompatCheckBox cbOpening;
    @BindView(R.id.cb_current_time)
    AppCompatCheckBox cbCurrentTime;
    @BindView(R.id.cb_stand_up)
    AppCompatCheckBox cbStandUp;
    @BindView(R.id.layout_sliders)
    NestedScrollView layoutSliders;
    @BindView(R.id.tvTimeStart)
    TextView tvTimeStart;
    @BindView(R.id.tvTimeEnd)
    TextView tvTimeEnd;
    @BindView(R.id.tabSortLeft)
    TextView tabSortLeft;
    @BindView(R.id.tabSortRight)
    TextView tabSortRight;
    @BindView(R.id.layoutCurrentTime)
    RelativeLayout layoutCurrentTime;
    FastFilterSettingRecyclerAdapter adapter;
    //    @BindView(R.id.shadow)
//    View shadow;
    ArrayList<FilterRule> filterRules = new ArrayList<>();

    @BindArray(R.array.filter_item_1)
    String[] tabText1;
    @BindArray(R.array.filter_item_2)
    String[] tabText2;
    @BindArray(R.array.filter_item_3)
    String[] tabText3;
    @BindArray(R.array.filter_item_4)
    String[] tabText4;
    @BindArray(R.array.filter_item_5)
    String[] tabText5;
    @BindArray(R.array.filter_item_6)
    String[] tabText6;
    @BindArray(R.array.filter_item_7)
    String[] tabText7;
    ArrayList<String[]> tabTextList = new ArrayList<>();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private FilterRule limitTimeRule = new FilterRule(FilterRule.Type.LIMITED_TIME, 0.0);
    private FilterRule sockedRule = new FilterRule(FilterRule.Type.SOCKET, 0.0);
    private final View.OnClickListener listener = view -> {
        if (view instanceof TextView && cbCurrentTime.isChecked()) {
            TextView tv = (TextView) view;
            String timeString = tv.getText().toString();
            String timeOtherString = "00:00";
            if (view == tvTimeStart) {
                timeOtherString = tvTimeEnd.getText().toString();
            } else if (view == tvTimeEnd) {
                timeOtherString = tvTimeStart.getText().toString();
            }
            Calendar calendarOther = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(sdf.parse(timeString));
                calendarOther.setTime(sdf.parse(timeOtherString));
                if (view == tvTimeStart) {
                    OpenTimePickerDialog openTimePickerDialog = OpenTimePickerDialog.newInstance(2, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                            calendarOther.get(Calendar.HOUR_OF_DAY), calendarOther.get(Calendar.MINUTE), 0, true);
                    openTimePickerDialog.setOnClickSetButtonBody((forAll, dayOfWeek, startHour, startMinute, endHour, endMinute) -> {
                        Calendar newTime = Calendar.getInstance();
                        newTime.set(Calendar.HOUR_OF_DAY, startHour);
                        newTime.set(Calendar.MINUTE, startMinute);
                        tvTimeStart.setText(sdf.format(newTime.getTime()));
                        newTime.set(Calendar.HOUR_OF_DAY, endHour);
                        newTime.set(Calendar.MINUTE, endMinute);
                        tvTimeEnd.setText(sdf.format(newTime.getTime()));

                        return null;
                    });
                    openTimePickerDialog.show(getSupportFragmentManager(), "openTimePickerDialog");
                } else {
                    OpenTimePickerDialog openTimePickerDialog = OpenTimePickerDialog.newInstance(2, calendarOther.get(Calendar.HOUR_OF_DAY), calendarOther.get(Calendar.MINUTE),
                            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 1, true);
                    openTimePickerDialog.setOnClickSetButtonBody((forAll, dayOfWeek, startHour, startMinute, endHour, endMinute) -> {
                        Calendar newTime = Calendar.getInstance();
                        newTime.set(Calendar.HOUR_OF_DAY, startHour);
                        newTime.set(Calendar.MINUTE, startMinute);
                        tvTimeStart.setText(sdf.format(newTime.getTime()));
                        newTime.set(Calendar.HOUR_OF_DAY, endHour);
                        newTime.set(Calendar.MINUTE, endMinute);
                        tvTimeEnd.setText(sdf.format(newTime.getTime()));
                        return null;
                    });
                    openTimePickerDialog.show(getSupportFragmentManager(), "openTimePickerDialog");
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    };

    @OnClick(R.id.tv_restore)
    public void onClickReset() {
        getPresenter().onClickResetToDefault();
    }

    @OnClick(R.id.btn_search)
    public void onClickSearch() {
        int[] defaultValue = new int[filterPickerControllerArrayList.size() + 5];
        for (int i = 0; i < filterPickerControllerArrayList.size(); i++) {
            FilterTabItemController sliderController = filterPickerControllerArrayList.get(i);
            int value = sliderController.getValue();
            FilterRule rule = null;
            switch (i) {
                case 0://WIFI
                    if (value == 2) {
                        rule = new FilterRule(FilterRule.Type.WIFI, 4.0);
                    } else if (value == 1) {
                        rule = new FilterRule(FilterRule.Type.WIFI, 3.0);
                    } else {
                        rule = new FilterRule(FilterRule.Type.WIFI, 0.0);
                    }
                    break;
                case 1://QUIET
                    if (value == 2) {
                        rule = new FilterRule(FilterRule.Type.QUIET, 4.0);
                    } else if (value == 1) {
                        rule = new FilterRule(FilterRule.Type.QUIET, 3.0);
                    } else {
                        rule = new FilterRule(FilterRule.Type.QUIET, 0.0);
                    }
                    break;
                case 2://PRICE
                    if (value == 2) {
                        rule = new FilterRule(FilterRule.Type.PRICE, 4.0);
                    } else if (value == 1) {
                        rule = new FilterRule(FilterRule.Type.PRICE, 3.0);
                    } else {
                        rule = new FilterRule(FilterRule.Type.PRICE, 0.0);
                    }
                    break;
                case 3://SEAT
                    if (value == 2) {
                        rule = new FilterRule(FilterRule.Type.SEAT, 4.0);
                    } else if (value == 1) {
                        rule = new FilterRule(FilterRule.Type.SEAT, 3.0);
                    } else {
                        rule = new FilterRule(FilterRule.Type.SEAT, 0.0);
                    }
                    break;
                case 4://TASTY
                    if (value == 2) {
                        rule = new FilterRule(FilterRule.Type.TASTY, 4.0);
                    } else if (value == 1) {
                        rule = new FilterRule(FilterRule.Type.TASTY, 3.0);
                    } else {
                        rule = new FilterRule(FilterRule.Type.TASTY, 0.0);
                    }
                    break;
                case 5://TASTY_FOOD
                    if (value == 2) {
                        rule = new FilterRule(FilterRule.Type.TASTY_FOOD, 4.0);
                    } else if (value == 1) {
                        rule = new FilterRule(FilterRule.Type.TASTY_FOOD, 3.0);
                    } else {
                        rule = new FilterRule(FilterRule.Type.TASTY_FOOD, 0.0);
                    }
                    break;
                case 6://MUSIC
                    if (value == 2) {
                        rule = new FilterRule(FilterRule.Type.TASTY, 4.0);
                    } else if (value == 1) {
                        rule = new FilterRule(FilterRule.Type.TASTY, 3.0);
                    } else {
                        rule = new FilterRule(FilterRule.Type.TASTY, 0.0);
                    }
                    break;
            }
            filterRules.add(rule);
            defaultValue[i] = value;
        }
        defaultValue[7] = radioTime.getCheckedRadioButtonId();
        defaultValue[8] = radioSocket.getCheckedRadioButtonId();
        defaultValue[9] = cbStandUp.isChecked() ? 1 : 0;
        defaultValue[10] = cbOpening.isChecked() ? 1 : 0;
        defaultValue[11] = cbCurrentTime.isChecked() ? 1 : 0;

        parseRadioGroupSearchResult();
        filterRules.add(new FilterRule(FilterRule.Type.STANDING_DESK, cbStandUp.isChecked() ? 1.0 : 0));
        if (cbOpening.isChecked()) {
            filterRules.add(new FilterRule(FilterRule.Type.OPENING, cbOpening.isChecked() ? 1.0 : 0));
        } else if (cbCurrentTime.isChecked()) {
            filterRules.add(new FilterRule(FilterRule.Type.CUSTOM_TIME_OPENING, cbCurrentTime.isChecked() ? 1.0 : 0));
        }

        getPresenter().onClickSearch(defaultValue, tabSortLeft.isActivated() ? 0 : 1);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Config.EXTRA_DATA, filterRules);
        if (cbCurrentTime.isChecked()) {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            String[] splitStart = tvTimeStart.getText().toString().split("[:]");
            String[] splitEnd = tvTimeEnd.getText().toString().split("[:]");
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);
            start.set(Calendar.HOUR_OF_DAY, Integer.valueOf(splitStart[0]));
            start.set(Calendar.MINUTE, Integer.valueOf(splitStart[1]));

            end.set(Calendar.SECOND, 0);
            end.set(Calendar.MILLISECOND, 0);
            end.set(Calendar.HOUR_OF_DAY, Integer.valueOf(splitEnd[0]));
            end.set(Calendar.MINUTE, Integer.valueOf(splitEnd[1]));
            bundle.putLong(Config.EXTRA_TIME_START, start.getTimeInMillis());
            bundle.putLong(Config.EXTRA_TIME_END, end.getTimeInMillis());
        }
        bundle.putInt(Config.EXTRA_ID_CHOOSE, tabSortLeft.isActivated() ? 0 : 1);
        intent.putExtra(Config.EXTRA_DATA, bundle);
        setResult(RESULT_OK, intent);
        finish();
    }


    public void parseRadioGroupSearchResult() {
        switch (radioTime.getCheckedRadioButtonId()) {
            case R.id.radio_time_1:
                Iterator<FilterRule> iterator = filterRules.iterator();
                while (iterator.hasNext()) {
                    FilterRule next = iterator.next();
                    if (next.getType() == FilterRule.Type.LIMITED_TIME) {
                        iterator.remove();
                    }
                }
                break;
            case R.id.radio_time_2:
                if (!filterRules.contains(limitTimeRule)) {
                    limitTimeRule.setValue(3.0);
                    filterRules.add(limitTimeRule);
                } else {
                    limitTimeRule.setValue(3.0);
                }
                break;
            case R.id.radio_time_3:
                if (!filterRules.contains(limitTimeRule)) {
                    limitTimeRule.setValue(1.0);
                    filterRules.add(limitTimeRule);
                } else {
                    limitTimeRule.setValue(1.0);
                }
                break;
            case R.id.radio_time_4:
                if (!filterRules.contains(limitTimeRule)) {
                    limitTimeRule.setValue(2.0);
                    filterRules.add(limitTimeRule);
                } else {
                    limitTimeRule.setValue(2.0);
                }
                break;
        }

        switch (radioSocket.getCheckedRadioButtonId()) {
            case R.id.radio_socket_1:
                if (filterRules.contains(sockedRule)) {
                    filterRules.remove(sockedRule);
                }
                break;
            case R.id.radio_socket_2:
                if (!filterRules.contains(sockedRule)) {
                    sockedRule.setValue(3.0);
                    filterRules.add(sockedRule);
                } else {
                    sockedRule.setValue(3.0);
                }
                filterRules.add(new FilterRule(FilterRule.Type.SOCKET, 3.0));
                break;
            case R.id.radio_socket_3:
                if (!filterRules.contains(sockedRule)) {
                    sockedRule.setValue(1.0);
                    filterRules.add(sockedRule);
                } else {
                    sockedRule.setValue(1.0);
                }
                break;
        }
    }

    @Override
    public void initViews() {
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_white_arrow_back);
        toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
            playTransition();
        });
        getSupportActionBar().setTitle(R.string.title_filter_set);
        tabTextList.add(tabText1);
        tabTextList.add(tabText2);
        tabTextList.add(tabText3);
        tabTextList.add(tabText4);
        tabTextList.add(tabText5);
        tabTextList.add(tabText6);
        tabTextList.add(tabText7);
        filterPickerControllerArrayList = new ArrayList<>();
        for (int i = 0; i < sliderLayouts.size(); i++) {
            LinearLayout layout = sliderLayouts.get(i);
            FilterTabItemController filterPickerItemController = new FilterTabItemController(layout);
            filterPickerItemController.setup(icons.getResourceId(i, 0), names[i], tabTextList.get(i), 0);
            filterPickerControllerArrayList.add(filterPickerItemController);
        }
        icons.recycle();

        adapter = new FastFilterSettingRecyclerAdapter(this, new ArrayList()
                , new FastFilterSettingRecyclerAdapter.SimpleItemConfig()) {
            @Override
            public void onItemClick(ViewHolder holder, int position, ItemObject data) {
                super.onItemClick(holder, position, data);
                switch (data.getSetting()) {
                    case ForWorkAndStudy:
                        for (int i = 0; i < filterPickerControllerArrayList.size(); i++) {
                            FilterTabItemController filterTabItemController = filterPickerControllerArrayList.get(i);
                            if (i == 0 || i == 3 || i == 6) {
                                filterTabItemController.setValue(2);
                            } else {
                                filterTabItemController.setValue(1);
                            }
                        }
                        break;
                    case HighCP:
                        for (int i = 0; i < filterPickerControllerArrayList.size(); i++) {
                            FilterTabItemController filterTabItemController = filterPickerControllerArrayList.get(i);
                            if (i == 2 || i == 4 || i == 6) {
                                filterTabItemController.setValue(2);
                            } else {
                                filterTabItemController.setValue(1);
                            }
                        }
                        break;
                    case AVG4:
                        for (FilterTabItemController filterTabItemController : filterPickerControllerArrayList) {
                            filterTabItemController.setValue(2);
                        }
                        break;
                }

            }
        };
        adapter.getList().add(new FastFilterSettingRecyclerAdapter.ItemObject(FastFilterSetting.ForWorkAndStudy));
        adapter.getList().add(new FastFilterSettingRecyclerAdapter.ItemObject(FastFilterSetting.HighCP));
        adapter.getList().add(new FastFilterSettingRecyclerAdapter.ItemObject(FastFilterSetting.AVG4));
        recyclerSettings.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        tvTimeStart.setOnClickListener(listener);
        tvTimeEnd.setOnClickListener(listener);
        tabSortLeft.setOnClickListener(view -> {
            if (tabSortLeft.isActivated()) {
                return;
            }
            tabSortLeft.setActivated(true);
            tabSortRight.setActivated(false);
        });
        tabSortRight.setOnClickListener(view -> {
            if (tabSortRight.isActivated()) {
                return;
            }
            tabSortRight.setActivated(true);
            tabSortLeft.setActivated(false);
        });

    }


    public void setupDefaultValue(int[] values, int sortBy) {
        tabSortLeft.setActivated(sortBy == 0);
        tabSortRight.setActivated(sortBy == 1);

        cbOpening.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                cbCurrentTime.setChecked(false);
            }
        });
        cbCurrentTime.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                cbOpening.setChecked(false);
            }
            tvTimeStart.setEnabled(checked);
            tvTimeEnd.setEnabled(checked);
            tvTimeStart.setAlpha(checked ? 1f : 0.5f);
            tvTimeEnd.setAlpha(checked ? 1f : 0.5f);

        });
        tabSortLeft.setActivated(true);
        for (int i = 0; i < filterPickerControllerArrayList.size(); i++) {
            FilterTabItemController sliderController = filterPickerControllerArrayList.get(i);
            sliderController.setValue(values[i]);
        }
        radioTime.check(values[7]);
        radioSocket.check(values[8]);
        cbStandUp.setChecked(values[9] == 1);
        cbOpening.setChecked(values[10] == 1);
        cbCurrentTime.setChecked(values[11] == 1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playTransition();
    }

    public void playTransition() {
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_bottom);
    }

}
