package com.example.smartcampusrecruitment.seeker.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.gridlayout.widget.GridLayout;

import com.example.smartcampusrecruitment.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SalaryBottomSheet extends BottomSheetDialogFragment {
    private OnSalarySelected listener;
    private int selectedSalary = 10; // 默认 10K
    private int minSalary = 10;   // 默认值
    private int maxSalary = 25;   // 默认值

    public interface OnSalarySelected {
        void onSelected(String range);
    }

    public static SalaryBottomSheet newInstance(OnSalarySelected l,Context ctx) {
        Log.d("SalaryBottomSheet", "newInstance called");
        SalaryBottomSheet sheet = new SalaryBottomSheet();
        sheet.listener = l;
        return sheet;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("SalaryBottomSheet", "onCreateView");
        View v = inflater.inflate(R.layout.bottom_sheet_salary, container, false);
        LinearLayout llLeft = v.findViewById(R.id.llSalaryLeft);
        LinearLayout llRight = v.findViewById(R.id.llSalaryRight);

        Button btnCancel = v.findViewById(R.id.btnCancel);
        Button btnConfirm = v.findViewById(R.id.btnConfirm);

        // 动态生成 1K-260K 按钮
        for (int i = 1; i <= 30; i++) {
            addSalaryButton(llLeft, i);
        }
        // 右列：131K ~ 260K
        for (int i = 5; i <= 30; i++) {
            addSalaryButton(llRight, i);
        }

        btnCancel.setOnClickListener(v2 -> dismiss());
        btnConfirm.setOnClickListener(v2 -> {
            int realMin = Math.min(minSalary, maxSalary);
            int realMax = Math.max(minSalary, maxSalary);
            String range = realMin + "K-" + realMax + "K";
            requireContext().getSharedPreferences("jobinfo", Context.MODE_PRIVATE)
                    .edit()
                    .putString("expected_salary", range)
                    .apply();
            if (listener != null) listener.onSelected(range);
            dismiss();
        });
        return v;
    }
    private void addSalaryButton(LinearLayout parent, int salary) {
        Button btn = new Button(requireContext());
        btn.setText(salary + "K");
        btn.setAllCaps(false);
        btn.setBackgroundResource(R.drawable.bg_salary_selector);
        btn.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 6, 0, 6);
        btn.setLayoutParams(lp);

        btn.setSelected(salary == selectedSalary);
        btn.setOnClickListener(v -> {
            if (parent.getId() == R.id.llSalaryLeft) {
                minSalary = salary;
            } else {
                maxSalary = salary;
            }
            clearAllSelections();
            btn.setSelected(true);
        });
        parent.addView(btn);
    }

    private void clearAllSelections() {
        View v = getView();
        if (v == null) return;
        LinearLayout llLeft  = v.findViewById(R.id.llSalaryLeft);
        LinearLayout llRight = v.findViewById(R.id.llSalaryRight);
        for (int i = 0; i < llLeft.getChildCount(); i++)
            llLeft.getChildAt(i).setSelected(false);
        for (int i = 0; i < llRight.getChildCount(); i++)
            llRight.getChildAt(i).setSelected(false);
    }
}
