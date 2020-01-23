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

public class VerifyCodeDialog extends AppCompatDialogFragment  {

    private EditText mEnterCode;

    private sendInputtedCodeListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.enter_verification_dialog, null);

        builder.setView(view)
                .setTitle("Enter verification code")
                .setNegativeButton("cancel", (dialogInterface, i) -> {})
                .setPositiveButton("ok", (dialogInterface, i) -> {
                    String inputtedCode = mEnterCode.getText().toString();
                    listener.applyTexts(inputtedCode);
                });
        mEnterCode = view.findViewById(R.id.editTextCode);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (sendInputtedCodeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement sendInputtedCodeListener");
        }
    }

    public interface sendInputtedCodeListener {
        void applyTexts(String code);
    }
}


