package huajiteam.zhbus;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.Streams;

import java.io.Serializable;
import java.util.ArrayList;

import huajiteam.zhbus.zhdata.BusLineInfo;
import huajiteam.zhbus.zhdata.GetBusInfoFromJson;

/**
 * Created by KelaKim on 2016/5/11.
 */
public class FavoriteConfig implements Serializable {
    private BusLineInfo[] busLineInfos;
    private Activity activity;

    FavoriteConfig(Activity activity) {
        this.activity = activity;
        reloadData();
    }

    public BusLineInfo[] getBusLineInfos() {
        return this.busLineInfos;
    }

    public ArrayList<BusLineInfo> getBusLineInfoArray() {
        ArrayList<BusLineInfo> arrayList = new ArrayList<BusLineInfo>();
        for (BusLineInfo i : busLineInfos) {
            arrayList.add(i);
        }
        return arrayList;
    }

    public void setFavoriteJson(String favoriteJson) {
        this.busLineInfos = new GetBusInfoFromJson().getBusLineInfoFromJson(favoriteJson);
        saveData(favoriteJson);
    }

    public void setFavoriteJson(BusLineInfo[] busLineInfos) {
        this.busLineInfos = busLineInfos;
        saveData(busLineInfos);
    }

    public void setFavoriteJson(ArrayList<BusLineInfo> oldInfo) {
        int count = oldInfo.size();
        BusLineInfo[] busLineInfos = new BusLineInfo[count];
        for (int i = 0; i < count; i++) {
            busLineInfos[i] = oldInfo.get(i);
        }
        this.busLineInfos = busLineInfos;
        saveData(busLineInfos);
    }

    public void reloadData() {
        SharedPreferences favorites = activity.getSharedPreferences("favorite_list", 0);
        String favoriteJson = favorites.getString("favorite_json", "[]");
        this.busLineInfos = new GetBusInfoFromJson().getBusLineInfoFromJson(favoriteJson);
    }

    public void saveData(String json) {
        SharedPreferences favorites = activity.getSharedPreferences("favorite_list", 0);
        SharedPreferences.Editor editor = favorites.edit();
        editor.putString("favorite_json", json);
        editor.apply();
    }

    public void saveData(BusLineInfo[] busLineInfos) {
        String json = new Gson().toJson(busLineInfos, BusLineInfo[].class);
        saveData(json);
    }

    public void saveData(ArrayList<BusLineInfo> oldInfo) {
        int count = oldInfo.size();
        BusLineInfo[] busLineInfos = new BusLineInfo[count];
        for (int i = 0; i < count; i++) {
            busLineInfos[i] = oldInfo.get(i);
        }
        this.busLineInfos = busLineInfos;
        String json = new Gson().toJson(busLineInfos, BusLineInfo[].class);
        saveData(json);
    }

    public void addData(BusLineInfo busLineInfo) {
        ArrayList<BusLineInfo> tmpData = this.getBusLineInfoArray();
        tmpData.add(busLineInfo);
        saveData(tmpData);
    }

    public void clearAllData() {
        this.setFavoriteJson("[]");
    }
}
