package com.e.maiplaceapp.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.e.maiplaceapp.R;

public class AddToCartDialog extends DialogFragment {
    private static final String TAG = "AddToCartDialog";

    public interface onQuantitySend {
        void sendQuantity(String quantity);
    }

    public onQuantitySend mOnQuantitySend;

    private EditText editTextQuantity;
    private Button mDialogCancel;
    private Button mDialogOkay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_layout_dialog, container, false);
        mDialogCancel = view.findViewById(R.id.btnDialogCancel);
        mDialogOkay = view.findViewById(R.id.btnDialogOkay);
        editTextQuantity = view.findViewById(R.id.foodQuantity);

        mDialogCancel.setOnClickListener(v -> {
            Log.d(TAG, "onClick : closing the dialog");
            getDialog().dismiss();
        });

        mDialogOkay.setOnClickListener(v -> {
            Log.d(TAG, "onClick : capturing input");
            String quantity = editTextQuantity.getText().toString();
            if (!quantity.isEmpty()) {
                mOnQuantitySend.sendQuantity(quantity);
            }
            getDialog().dismiss();
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOnQuantitySend = (onQuantitySend) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(TAG + " you must implement onQuantitySend Interface.");
        }
    }
}
