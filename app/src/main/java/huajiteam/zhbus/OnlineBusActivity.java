package huajiteam.zhbus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import huajiteam.zhbus.zhdata.BusLineInfo;
import huajiteam.zhbus.zhdata.GetBusInfo;
import huajiteam.zhbus.zhdata.OnlineBusInfo;
import huajiteam.zhbus.zhdata.StationInfo;
import huajiteam.zhbus.zhdata.exceptions.BusLineInvalidException;
import huajiteam.zhbus.zhdata.exceptions.HttpCodeInvalidException;

public class OnlineBusActivity extends AppCompatActivity {

    BusLineInfo busLineInfo;
    GetConfig config;
    StationInfo[] stationInfos;
    OnlineBusInfo[] onlineBusInfos;
    Timer timer = new Timer();
    MAdapter mAdapter;

    boolean firstRun = true;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (config.getWaitTime() == 0) {
                        new Thread(new UpdateOnlineBuses(config, busLineInfo)).start();
                    } else {
                        timer.schedule(new UpdateOnlineBuses(config, busLineInfo), 200, config.getWaitTime() * 1000);
                    }
                    break;
                case 1:
                    makeSnackbar("少女祈祷成功");
                    onlineBusInfos = (OnlineBusInfo[]) msg.obj;
                    mAdapter = new MAdapter(OnlineBusActivity.this);
                    ListView listView = (ListView) findViewById(R.id.onlineBusListView);
                    listView.setAdapter(mAdapter);
                    break;
                case 2:
                    if (config.getAutoFlushNotice()) {
                        makeSnackbar("少女祈祷中...");
                    }
                    onlineBusInfos = (OnlineBusInfo[]) msg.obj;
                    mAdapter.notifyDataSetChanged();
                    break;
                case -1:
                    makeAlert("出现了一个错误", "未知错误: " + msg.obj);
                    break;
                case -1001:
                    makeSnackbar(getString(R.string.error_api_invalid));
                    break;
                case -1003:
                    makeSnackbar(getString(R.string.network_error));
                    break;
                case -2:
                    makeAlert("出现了一个错误", "未知错误: " + msg.obj);
                    timer.cancel();
                    break;
                case -2001:
                    makeSnackbar(getString(R.string.error_api_invalid));
                    timer.cancel();
                    break;
                case -2003:
                    makeSnackbar(getString(R.string.network_error));
                    timer.cancel();
                    break;
                default:
                    makeSnackbar("噫");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_bus);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        this.busLineInfo = (BusLineInfo) intent.getSerializableExtra("busLineInfo");
        this.config = (GetConfig) intent.getSerializableExtra("config");

        makeSnackbar("少女祈祷中...");
        new Thread(new GetStation(config, busLineInfo)).start();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!config.getAutoFlushNotice()) {
                    makeSnackbar("少女祈祷中...");
                }
                if (config.getWaitTime() == 0) {
                    new Thread(new UpdateOnlineBuses(config, busLineInfo)).start();
                } else {
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(new UpdateOnlineBuses(config, busLineInfo), 200, config.getWaitTime() * 1000);
                }
            }
        });
    }

    private void makeSnackbar(String content) {
        Snackbar.make(findViewById(R.id.toolbar), content, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void makeAlert(String title, String content) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(getString(R.string.okay),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
    }

    public final class ViewHolder {
        public ImageView statusImg;
        public TextView stationName;
        public TextView onlineBuses;
        public Button moreInformation;
    }

    class GetStation implements Runnable {

        GetConfig config;
        BusLineInfo busLineInfo;

        GetStation(GetConfig config, BusLineInfo busLineInfo) {
            this.config = config;
            this.busLineInfo = busLineInfo;
        }

        @Override
        public void run() {
            try {
                stationInfos = new GetBusInfo().getStationInfo(
                        this.config.getSearchStationUrl() ,
                        this.busLineInfo.getID()
                );
            } catch (HttpCodeInvalidException |
                    StringIndexOutOfBoundsException |
                    JsonSyntaxException |
                    IllegalArgumentException e) {
                mHandler.obtainMessage(-1001).sendToTarget();
                return;
            } catch (UnknownHostException | SocketTimeoutException | ConnectException e) {
                mHandler.obtainMessage(-1003).sendToTarget();
                return;
            } catch (IOException e) {
                mHandler.obtainMessage(-1, e.toString()).sendToTarget();
                return;
            }
            mHandler.obtainMessage(0).sendToTarget();
        }
    }

    class UpdateOnlineBuses extends TimerTask {

        BusLineInfo busLineInfo;
        GetConfig config;

        UpdateOnlineBuses(GetConfig config, BusLineInfo busLineInfo) {
            this.config = config;
            this.busLineInfo = busLineInfo;
        }

        @Override
        public void run() {
            OnlineBusInfo[] onlineBusInfos;
            try {
                onlineBusInfos = new GetBusInfo().getOnlineBusInfo(
                        this.config.getSearchOnlineBusUrl() ,
                        this.busLineInfo.getName() ,
                        this.busLineInfo.getToStation()
                );
            } catch (HttpCodeInvalidException |
                    StringIndexOutOfBoundsException |
                    JsonSyntaxException |
                    IllegalArgumentException e) {
                mHandler.obtainMessage(-2001).sendToTarget();
                return;
            } catch (UnknownHostException | SocketTimeoutException | ConnectException e) {
                mHandler.obtainMessage(-2003).sendToTarget();
                return;
            } catch (IOException e) {
                mHandler.obtainMessage(-2, e.toString()).sendToTarget();
                return;
            }
            if (firstRun) {
                firstRun = false;
                mHandler.obtainMessage(1, onlineBusInfos).sendToTarget();
            } else {
                mHandler.obtainMessage(2, onlineBusInfos).sendToTarget();
            }
        }
    }

    public class MAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return stationInfos.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            final StationInfo stationInfo = stationInfos[position];
            if (convertView == null) {
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.display_online_bus, null);
                viewHolder.statusImg = (ImageView) convertView.findViewById(R.id.onlineBusImg);
                viewHolder.stationName = (TextView) convertView.findViewById(R.id.stationName);
                viewHolder.onlineBuses = (TextView) convertView.findViewById(R.id.onlineBuses);
                viewHolder.moreInformation = (Button) convertView.findViewById(R.id.moreInformationButton);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.stationName.setText(stationInfo.getName());
            for (OnlineBusInfo data : onlineBusInfos) {
                if (data.getCurrentStation() == stationInfo.getName()) {
                    viewHolder.onlineBuses.setText(data.getBusNumber());
                    viewHolder.statusImg.setImageDrawable(getDrawable(R.drawable.menu_favorite));
                    break;
                } else {
                    viewHolder.onlineBuses.setText("");
                    viewHolder.statusImg.setImageDrawable(getDrawable(R.drawable.menu_about));
                }
            }

            final int moreId = viewHolder.moreInformation.getId();

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == moreId) {
                        makeAlert("详细信息",
                                "ID: " + stationInfo.getId() + "\n" +
                                "站名: " + stationInfo.getName()+ "\n" +
                                "经度: " + stationInfo.getLongitude() + "\n" +
                                "纬度: " + stationInfo.getLatitude() + "\n" +
                                "描述: " + stationInfo.getDescription()
                        );
                    } else {
                        makeSnackbar("WTF!!??");
                    }
                }
            };

            viewHolder.moreInformation.setOnClickListener(onClickListener);
            return convertView;
        }
    }
}