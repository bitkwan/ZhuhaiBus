package huajiteam.zhbus.zhdata;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by KelaKim on 2016/5/4.
 */
public class BusLineInfo extends BusData  {
    private String Id;
    private String Name;
    private String LineNumber;
    private int Direction;
    private String FromStation;
    private String ToStation;
    private String BeginTime;
    private String EndTime;
    private String Price;
    private String Interval;
    private String Description;
    private int StationCount;

    public String getID() {
        return this.Id;
    }

    public String getName() {
        return this.Name;
    }

    public String getLineNumber() {
        return this.LineNumber;
    }

    public int getDirection() {
        return this.Direction;
    }

    public String getFromStation() {
        return this.FromStation;
    }

    public String getToStation() {
        return this.ToStation;
    }

    public String getBeginTime() {
        return this.BeginTime;
    }

    public String getEndTime() {
        return this.EndTime;
    }

    public String getPrice() {
        return this.Price;
    }

    public String getInterval() {
        return this.Interval;
    }

    public String getDescription() {
        return this.Description;
    }

    public int getStationCount() {
        return this.StationCount;
    }

    public String getDisplayData() {
        return "线路名称: " + this.Name
                + "\n线路: " + this.LineNumber
                + "\n方向: " + this.Direction
                + "\n起始站: " + this.FromStation
                + "\n终点站: " + this.ToStation
                + "\n首班车发车时间: " + this.BeginTime
                + "\n末班车发车时间: " + this.EndTime
                + "\n票价: ￥" + this.Price
				/*+ "\n间隔: " + this.Interval
				+ "\n描述: " + this.Description*/
                + "\n途径站点个数: " + this.StationCount;
    }
}
