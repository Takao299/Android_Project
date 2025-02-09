package com.example.spbtex.ui.reservation;

import java.time.LocalDate;
import java.util.List;

public class CalendarDays {

    private List<Integer> offDayList;
    private List<LocalDate> tempOnDayList;
    private List<LocalDate> tempOffDayList;

    public List<Integer> getOffDayList() {
        return offDayList;
    }

    public void setOffDayList(List<Integer> offDayList) {
        this.offDayList = offDayList;
    }

    public List<LocalDate> getTempOnDayList() {
        return tempOnDayList;
    }

    public void setTempOnDayList(List<LocalDate> tempOnDayList) {
        this.tempOnDayList = tempOnDayList;
    }

    public List<LocalDate> getTempOffDayList() {
        return tempOffDayList;
    }

    public void setTempOffDayList(List<LocalDate> tempOffDayList) {
        this.tempOffDayList = tempOffDayList;
    }

    public CalendarDays() {
    }

    public CalendarDays(List<Integer> offDayList, List<LocalDate> tempOnDayList, List<LocalDate> tempOffDayList) {
        this.offDayList = offDayList;
        this.tempOnDayList = tempOnDayList;
        this.tempOffDayList = tempOffDayList;
    }
}
