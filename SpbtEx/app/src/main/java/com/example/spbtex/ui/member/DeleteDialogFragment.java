package com.example.spbtex.ui.member;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.spbtex.R;

public class DeleteDialogFragment extends DialogFragment {

    private UpdateEtcActivity activity;

    public void setActivity(UpdateEtcActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        activity = (UpdateEtcActivity) getActivity();

        return new AlertDialog.Builder(requireActivity())
                //.setTitle("タイトル")
                .setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.dialog_positive, (dialog, id) -> {
                    // このボタンを押した時の処理を書きます。
                    activity.deleteMemberExecute(); //退会実行
                })
                .setNegativeButton(R.string.dialog_negative, null)
                //.setNeutralButton("あとで", null)
                .create();
    }
}