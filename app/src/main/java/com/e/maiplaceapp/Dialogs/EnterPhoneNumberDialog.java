package com.e.maiplaceapp.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.e.maiplaceapp.R;

public class EnterPhoneNumberDialog extends AppCompatDialogFragment  {

    private EditText mEnterPhoneNumber;

    private sendInputPhoneNumber listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.enter_phone_number_dialog, null);

        builder.setView(view)
                .setCancelable(false)
                .setTitle("Enter your phone number")
                .setNegativeButton("cancel", (dialogInterface, i) -> {})
                .setPositiveButton("ok", (dialogInterface, i) -> {
                    String phoneNumber = mEnterPhoneNumber.getText().toString();
                    listener.applyPhoneNumber(phoneNumber);
                });
        mEnterPhoneNumber = view.findViewById(R.id.enterPhoneNumber);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (sendInputPhoneNumber) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement sendInputtedCodeListener");
        }
    }

    public interface sendInputPhoneNumber {
        void applyPhoneNumber(String code);
    }
}


