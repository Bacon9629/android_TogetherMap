package com.bacon.baconproject.togethermap.main.historylist;

import com.bacon.baconproject.togethermap.history_data.HistoryTrip;

public class OnCall {
    public interface OnCallMemory{
        void call(HistoryTrip trip);
    }
    public interface OnCallPhoto{
        void call(HistoryTrip trip);
    }
    public interface OnCallSetting{
        void call(HistoryTrip trip);
    }
}
