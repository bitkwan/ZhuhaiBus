package huajiteam.zhbus.zhdata;

import com.google.gson.Gson;

/**
 * Created by KelaKim on 2016/5/4.
 */
public class GetBusInfoFromJson {
    BusLineInfo[] getBusLineInfoFromJson(String jsonData) {
        return new Gson().fromJson(jsonData
                        .replace("{\"d\":", "")
                        .substring(0, jsonData.length() - 6)
                , BusLineInfo[].class);
    }

    StationInfo[] getStationInfoFromJson(String jsonData) {
        return new Gson().fromJson(jsonData
                        .replace("{\"d\":", "")
                        .substring(0, jsonData.length() - 6)
                , StationInfo[].class);
    }

    OnlineBusInfo[] getOnlineBusInfoFromJson(String jsonData) {
        return new Gson().fromJson(jsonData
                        .replace("{\"d\":", "")
                        .substring(0, jsonData.length() - 6)
                , OnlineBusInfo[].class);
    }
}
