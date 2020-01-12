package com.e.maiplaceapp.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.e.maiplaceapp.R;

public class DeliverTypeDialog extends DialogFragment {
    private static final String TAG = "DeliverTypeDialog";

    public interface onSelectTypeSend {
        void sendType(String type);
    }

    public onSelectTypeSend mOnSelectedTypeSend;

    private Button mDialogOkay;
    private RadioButton selectedDeliverType;
    private RadioGroup radioDeliverType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_deliver_type_dialog, container, false);
        mDialogOkay = view.findViewById(R.id.btnDialogOkay);
        radioDeliverType = view.findViewById(R.id.radioDeliverType);


        mDialogOkay.setOnClickListener(v -> {
            Log.d(TAG, "onClick : capturing input");
            int selectedType = radioDeliverType.getCheckedRadioButtonId();
            selectedDeliverType = view.findViewById(selectedType);
            mOnSelectedTypeSend.sendType(selectedDeliverType.getText().toString());
            getDialog().dismiss();
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOnSelectedTypeSend = (onSelectTypeSend) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(TAG + " you must implement onQuantitySend Interface.");
        }
    }
}
