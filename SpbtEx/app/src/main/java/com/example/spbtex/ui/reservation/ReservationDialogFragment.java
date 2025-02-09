package com.example.spbtex.ui.reservation;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.spbtex.R;

public class ReservationDialogFragment extends DialogFragment {

    private ReservationActivity activity;

    public void setActivity(ReservationActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        activity = (ReservationActivity) getActivity();

        return new AlertDialog.Builder(requireActivity())
                //.setTitle("タイトル")
                .setMessage(getText(R.string.reservation_dialog_message)
                        +"\n "+getText(R.string.reservation_facility_label)+" : "+activity.f_name
                        +"\n "+getText(R.string.reservation_date_label)+" : "+activity.r_date
                        +"\n "+getText(R.string.reservation_time_label)+" : "+activity.r_time
                )
                .setPositiveButton(R.string.dialog_positive, (dialog, id) -> {
                    // このボタンを押した時の処理を書きます。
                    activity.reservationConfirmed(); //予約実行
                })
                .setNegativeButton(R.string.dialog_negative, null)
                //.setNeutralButton("あとで", null)
                .create();
    }
}
