package com.example.teller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class Custom_dialog_library extends AppCompatDialogFragment {

    private EditText input_lib_name;
    private Custom_Dialog_library_Listener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dailog_layout,null);
        builder.setView(view).setTitle("Enter Library Name").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //When you click apply
                String input_lib_name_text = input_lib_name.getText().toString();
                listener.applyText(input_lib_name_text);
            }
        });

        input_lib_name = view.findViewById(R.id.input_lib_name);


        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
            listener = (Custom_Dialog_library_Listener) context;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public interface Custom_Dialog_library_Listener{
        void applyText(String input_lib_name);
    }
}
