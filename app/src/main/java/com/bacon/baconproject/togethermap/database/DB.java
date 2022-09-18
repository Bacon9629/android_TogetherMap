package com.bacon.baconproject.togethermap.database;

import com.bacon.baconproject.togethermap.history_data.HistoryTrip;

public class DB {
    public interface OnComplete<TResult>{
        void onComplete(TResult item);
    }

    public interface onNewJourneyUploadComplete{
        void onComplete(HistoryTrip trip);
    }

    public interface onJoinJourney{
        void joinSuccess();
        void joinFailed();
    }

    public interface onDataChange<TResult>{
        void onChange(TResult item);
    }

    public interface onCheck{
        void onCheck(boolean check);
    }
}
