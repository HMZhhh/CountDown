package com.sl.android.countdown;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.anim.AppFloatDefaultAnimator;
import com.lzf.easyfloat.anim.DefaultAnimator;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.lzf.easyfloat.interfaces.OnInvokeView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.os.VibrationEffect.DEFAULT_AMPLITUDE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DownFloat";
    public static int SWITCH = 1;
    private final String timeParent = "yyyy-MM-dd HH:mm:ss:SS";
    @BindView(R.id.tv_show_down_time)
    TextView tvShowDownTime;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.tv_year)
    TextView tvYear;
    @BindView(R.id.tv_month)
    TextView tvMonth;
    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_branch)
    TextView tvBranch;
    @BindView(R.id.tv_second)
    TextView tvSecond;
    @BindView(R.id.tv_millisecond)
    TextView tvMillisecond;
    @BindView(R.id.tv_start_down)
    TextView tvStartDown;
    @BindView(R.id.tv_time_10)
    TextView tv_time_10;
    @BindView(R.id.tv_time_12)
    TextView tv_time_12;
    @BindView(R.id.tv_time_20)
    TextView tv_time_20;
    @BindView(R.id.tv_time_jd)
    TextView tv_time_jd;
    @BindView(R.id.tv_time_tm)
    TextView tv_time_tm;
    @BindView(R.id.tv_time_tb)
    TextView tv_time_tb;
    private String year;
    private String month;
    private String day;
    private String time;
    private String branch;
    private String second;
    private String millisecond;

    private String defaultYear = "2021";
    private String defaultMonth = "1";
    private String defaultDay = "6";
    private String defaultTime = "20";
    private String defaultBranch = "0";
    private String defaultSecond = "0";
    private String defaultMillisecond = "0";

    private long showDay;
    private long showTime;
    private long showBranch;
    private long showSecond;
    private long showMillisecond;
    private long difference;

    private View view;
    private TextView floatTextView;
    private Vibrator vibrator;
    private boolean hasVibrator;

    private String defaultUrl = "http://www.ntsc.ac.cn";//中国科学院国家授时中心
    private long sysDifInternet = 0;//系统与网络的时间差
    private CountDownTimerUtil countDownTimer = new CountDownTimerUtil(difference, 20) {
        @Override
        public void onTick(long millisUntilFinished) {

            showDay = millisUntilFinished / 1000 / 86400;
            showTime = millisUntilFinished / 1000 / 3600 % 24;
            showBranch = millisUntilFinished / 1000 / 60 % 60;
            showSecond = millisUntilFinished / 1000 % 60;
            showMillisecond = millisUntilFinished % 1000 / 10;

            String value = String.valueOf((int) (millisUntilFinished / 1000));
            String downEndTime = "剩余:" + showDay + "天" + showTime + "时" + showBranch + "分" + showSecond + "秒" + showMillisecond + "毫秒";
            if (null != floatTextView) {
                floatTextView.setText(downEndTime);
            }
        }

        @Override
        public void onFinish() {
            if (null != floatTextView) {
                floatTextView.setText("over");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initFloatView();

    }

    /**
     * //关闭或者停止振动器
     * abstract void cancel()：
     * //判断硬件是否有振动器
     * abstract boolean hasVibrator()
     * //控制手机振动为milliseconds毫秒
     * void vibrate(long milliseconds)
     * //指定手机以pattern指定的模式振动!比如:pattern为new int[200,400,600,800],
     * //就是让他在200,400,600,800这个时间交替启动与关闭振动器! 而第二个则是重复次数,
     * //如果是-1的只振动一次,如果是0的话则一直振动
     * void vibrate(long[] pattern,int repeat):
     */
    private void initView() {
        vibrator = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        hasVibrator = vibrator.hasVibrator();
    }

    private void initFloatView() {
        EasyFloat.with(this)
                // 设置浮窗xml布局文件，并可设置详细信息
                .setLayout(R.layout.float_down_layout, new OnInvokeView() {
                    @Override
                    public void invoke(View view) {

                    }
                })
                // 设置浮窗显示类型，默认只在当前Activity显示，可选一直显示、仅前台显示、仅后台显示
                .setShowPattern(ShowPattern.ALL_TIME)
//                 设置吸附方式，共15种模式，详情参考SidePattern
//                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                // 设置浮窗的标签，用于区分多个浮窗
                .setTag("DownFloat")
                // 设置浮窗是否可拖拽，默认可拖拽
                .setDragEnable(true)
                // 系统浮窗是否包含EditText，仅针对系统浮窗，默认不包含
                .hasEditText(false)
                // 设置浮窗固定坐标，ps：设置固定坐标，Gravity属性和offset属性将无效
                .setLocation(100, 200)
                // 设置浮窗的对齐方式和坐标偏移量
                .setGravity(Gravity.END, 0, 200)
                // 设置宽高是否充满父布局，直接在xml设置match_parent属性无效
                .setMatchParent(false, false)
                // 设置Activity浮窗的出入动画，可自定义，实现相应接口即可（策略模式），无需动画直接设置为null
                .setAnimator(new DefaultAnimator())
                // 设置系统浮窗的出入动画，使用同上
                .setAppFloatAnimator(new AppFloatDefaultAnimator())
                // 设置系统浮窗的不需要显示的页面

                // 设置系统浮窗的有效显示高度（不包含虚拟导航栏的高度），基本用不到，除非有虚拟导航栏适配问题
//    .setDisplayHeight(OnDisplayHeight { context -> DisplayUtils.rejectedNavHeight(context) })
                // 浮窗的一些状态回调，如：创建结果、显示、隐藏、销毁、touchEvent、拖拽过程、拖拽结束。
                // ps：通过Kotlin DSL实现的回调，可以按需复写方法，用到哪个写哪个
                .registerCallbacks(new OnFloatCallbacks() {
                    @Override
                    public void createdResult(boolean b, String s, View view) {
                        Log.e(TAG, "createdResult");
                        MainActivity.this.view = view;
                    }

                    @Override
                    public void dismiss() {
                        Log.e(TAG, "dismiss");
                    }

                    @Override
                    public void drag(View view, MotionEvent motionEvent) {
                        Log.e(TAG, "drag");
                    }

                    @Override
                    public void dragEnd(View view) {
                        Log.e(TAG, "dragEnd");
                    }

                    @Override
                    public void hide(View view) {
                        Log.e(TAG, "hide");
                    }

                    @Override
                    public void show(View view) {
                        Log.e(TAG, "show");
                    }

                    @Override
                    public void touchEvent(View view, MotionEvent motionEvent) {
                        Log.e(TAG, "show");
                    }
                }).show();
    }

    @OnClick({R.id.tv_time_10, R.id.tv_time_12, R.id.tv_time_20,
            R.id.tv_time_jd, R.id.tv_time_tm, R.id.tv_time_tb,
            R.id.tv_start_down, R.id.tv_show_down_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_time_10:
                defaultTime = "10";
                tvTime.setText(defaultTime);
                break;
            case R.id.tv_time_12:
                defaultTime = "12";
                tvTime.setText(defaultTime);
                break;
            case R.id.tv_time_20:
                defaultTime = "20";
                tvTime.setText(defaultTime);
                break;
            case R.id.tv_time_jd:
                defaultUrl="https://www.jd.com/";
                break;
            case R.id.tv_time_tm:
                defaultUrl="https://www.tmall.com/";
                break;
            case R.id.tv_time_tb:
                defaultUrl="https://www.taobao.com/";
                break;
            case R.id.tv_start_down:
                sysDifInternet = 0;
                checkInput();
                break;
            case R.id.tv_show_down_time:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getTimeByIntenet();
                    }
                }).start();

                break;
        }
    }

    private void checkInput() {

        String downEndTime;
        year = tvYear.getText().toString().trim();
        year = TextUtils.isEmpty(year) ? defaultYear : year;

        month = tvMonth.getText().toString().trim();
        month = TextUtils.isEmpty(month) ? defaultMonth : month;

        day = tvDay.getText().toString().trim();
        day = TextUtils.isEmpty(day) ? defaultDay : day;

        time = tvTime.getText().toString().trim();
        time = TextUtils.isEmpty(time) ? defaultTime : time;

        branch = tvBranch.getText().toString().trim();
        branch = TextUtils.isEmpty(branch) ? defaultBranch : branch;

        second = tvSecond.getText().toString().trim();
        second = TextUtils.isEmpty(second) ? defaultSecond : second;

        millisecond = tvMillisecond.getText().toString().trim();
        millisecond = TextUtils.isEmpty(millisecond) ? defaultMillisecond : millisecond;

        downEndTime = year + "-" + month + "-" + day + " " + time + ":" + branch + ":" + second + ":" + millisecond;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeParent);
        Date date = null;
        try {
            date = simpleDateFormat.parse(downEndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long nowEndTime = date.getTime();
        long nowStartTime = System.currentTimeMillis();
        nowStartTime += sysDifInternet;
        difference = nowEndTime - nowStartTime;
        if (difference <= 550) {
            //关闭开关
            shock();
            return;
        }

        floatTextView = view.findViewById(R.id.tv_show_down);
        countDownTimer.setMillisInFuture(difference);
        countDownTimer.start();
    }

    private void getTimeByIntenet() {
        Dialog dialog = new Dialog(this);


        URL url = null;//取得资源对象
        try {
            url = new URL(defaultUrl);
//            url = new URL("https://www.jd.com/");
//            url = new URL("https://www.jd.com/");
            URLConnection uc = url.openConnection();//生成连接对象
            long nowSys = System.currentTimeMillis();
            uc.connect(); //发出连接
            long ld = uc.getDate(); //取得网站日期时间
            Log.d(TAG, "系统日期时间:"+ld);
            Log.d(TAG, "网站日期时间:"+nowSys);
            sysDifInternet = ld - nowSys;
            Log.d(TAG, "网络与系统时间差值:"+sysDifInternet);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    checkInput();
                }
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 震动
     */
    private void shock() {
        SWITCH = 0;
        if (null != vibrator && hasVibrator) {
            long[] pattern = {1000, 1000, 2000, 50};   // 停止 开启 停止 开启
            AudioAttributes audioAttributes = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, DEFAULT_AMPLITUDE));
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION) //key
                        .build();
                vibrator.vibrate(pattern, -1, audioAttributes);
            } else {
                vibrator.vibrate(pattern, -1);    //重复两次上面的pattern 如果只想震动一次，index设为-1
            }
        }
    }
}
