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

import com.e.maiplaceapp.Helpers.SharedPref;
import com.e.maiplaceapp.R;

public class AddToCartDialog extends DialogFragment {
    private static final String TAG = "AddToCartDialog";




    public interface onQuantitySend {
        void sendQuantity(String quantity);
    }



    public onQuantitySend mOnQuantitySend;

    private EditText editTextQuantity;
    private Button mDialogOkay;

    public static AddToCartDialog newInstance() {
        AddToCartDialog frag = new AddToCartDialog();
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_layout_dialog, container, false);
        mDialogOkay = view.findViewById(R.id.btnDialogOkay);
        editTextQuantity = view.findViewById(R.id.foodQuantity);

        // Check if the dialog appear in CartDialog which means that this dialog functionality is to edit the item.
        int selectedItemQuantity = SharedPref.getSharedPreferenceInt(getContext(),"current_quantity", 0);
        if(selectedItemQuantity != 0) {
            editTextQuantity.setText(String.valueOf(selectedItemQuantity));
            // Rebase the current quantity Shared pref.
            SharedPref.setSharedPreferenceInt(getContext(), "current_quantity", 0);
        }


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
