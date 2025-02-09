package com.example.spbtex.ui.reservation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarDaysJsonConverter {

    private List<OffDay> offDayList;

    public static class OffDay {
        private Integer intday;
        public Integer getIntday() {return intday;}
        public void setIntday(Integer intday) {this.intday = intday;}
    }

    private List<TempOnDay> tempOnDayList;

    public static class TempOnDay {
        private String tonday;
        public String getTonday() {return tonday;}
        public void setTonday(String tonday) {this.tonday = tonday;}
    }

    private List<TempOffDay> tempOffDayList;

    public static class TempOffDay {
        private String toffday;
        public String getToffday() {return toffday;}
        public void setToffday(String toffday) {this.toffday = toffday;}
    }


    public List<OffDay> getOffDayList() {
        return offDayList;
    }

    public void setOffDayList(List<OffDay> offDayList) {
        this.offDayList = offDayList;
    }

    public List<TempOnDay> getTempOnDayList() {
        return tempOnDayList;
    }

    public void setTempOnDayList(List<TempOnDay> tempOnDayList) {
        this.tempOnDayList = tempOnDayList;
    }

    public List<TempOffDay> getTempOffDayList() {
        return tempOffDayList;
    }

    public void setTempOffDayList(List<TempOffDay> tempOffDayList) {
        this.tempOffDayList = tempOffDayList;
    }

    //CalendarDaysJsonConverter　→　CalendarDays　に変換
    //CalendarDaysインスタンスを入れると３つセットしたCalendarDaysを返す
    public CalendarDays converter(CalendarDays calendarDays){

        List<Integer> offDayList = new ArrayList<>();
        List<LocalDate> tempOnDayList = new ArrayList<>();
        List<LocalDate> tempOffDayList = new ArrayList<>();

        for(OffDay offDay:this.offDayList){
            offDayList.add( offDay.getIntday() );
        }
        for(TempOnDay tempOnDay:this.tempOnDayList){
            tempOnDayList.add( LocalDate.parse(tempOnDay.getTonday()) );
        }
        for(TempOffDay tempOffDay:this.tempOffDayList){
            tempOffDayList.add( LocalDate.parse(tempOffDay.getToffday()) );
        }

        calendarDays.setOffDayList(offDayList);
        calendarDays.setTempOnDayList(tempOnDayList);
        calendarDays.setTempOffDayList(tempOffDayList);
        return calendarDays;
    }
}
