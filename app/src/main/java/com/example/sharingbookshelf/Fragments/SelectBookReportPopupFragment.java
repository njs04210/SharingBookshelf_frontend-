package com.example.sharingbookshelf.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharingbookshelf.Activities.CreateBookReportActivity;
import com.example.sharingbookshelf.Activities.MainActivity;
import com.example.sharingbookshelf.Adapters.BookreportsAdapter;
import com.example.sharingbookshelf.Adapters.SelectBookreportAdapter;
import com.example.sharingbookshelf.HttpRequest.RetrofitClient;
import com.example.sharingbookshelf.HttpRequest.RetrofitServiceApi;
import com.example.sharingbookshelf.Models.BookData;
import com.example.sharingbookshelf.Models.BookReportResponse;
import com.example.sharingbookshelf.Models.SelectBookReportResponse;
import com.example.sharingbookshelf.Models.SelectReportData;
import com.example.sharingbookshelf.R;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectBookReportPopupFragment extends DialogFragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private RetrofitServiceApi retrofitServiceApi;
    private ArrayList<SelectBookReportResponse.SelectBookReportData> selectbookList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_select_book_report_popup, container, false);

        Button btn_confirm = v.findViewById(R.id.btn_confirm);
        mRecyclerView = v.findViewById(R.id.rv_selectbookreport);

        recyclerViewSettings();
        getAvailableReportList();

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateBookReportActivity.class);
                startActivity(intent);

                Objects.requireNonNull(getDialog()).dismiss();
            }
        });
        return v;
    }

    private void recyclerViewSettings() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        selectbookList = new ArrayList<>();
        mAdapter = new SelectBookreportAdapter(getActivity(), selectbookList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getAvailableReportList() {
        retrofitServiceApi = RetrofitClient.createService(RetrofitServiceApi.class, MainActivity.getJWT());
        Call<SelectBookReportResponse> call = retrofitServiceApi.getAllBookReports(true);
        call.enqueue(new Callback<SelectBookReportResponse>() {
            @Override
            public void onResponse(Call<SelectBookReportResponse> call, Response<SelectBookReportResponse> response) {
                if (response.body().getCode() == 73) {
                    Log.d("아이북쉐어/독후감", response.body().getMsg());
                } else if (response.body().getCode() == 74) {
                    showAllAvailableReports(response.body().getBooks_NoReport());
                }
            }

            @Override
            public void onFailure(Call<SelectBookReportResponse> call, Throwable t) {

            }
        });
    }

    private void showAllAvailableReports(ArrayList<SelectBookReportResponse.SelectBookReportData> dataSet) {
        mAdapter = new SelectBookreportAdapter(getActivity(), dataSet);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);

        this.selectbookList = dataSet;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            WindowMetrics windowMetrics = getActivity().getWindowManager().getCurrentWindowMetrics();

            Window window = getDialog().getWindow();
            window.setGravity(Gravity.BOTTOM);

            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = windowMetrics.getBounds().width();
            getDialog().getWindow().setAttributes(params);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}