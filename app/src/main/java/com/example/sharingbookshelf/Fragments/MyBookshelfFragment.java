package com.example.sharingbookshelf.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sharingbookshelf.Activities.BarcodeActivity;
import com.example.sharingbookshelf.Activities.BookDetailsPopupActivity;
import com.example.sharingbookshelf.Activities.MainActivity;
import com.example.sharingbookshelf.Activities.SelfAddBookPopupActivity;
import com.example.sharingbookshelf.HttpRequest.BookApiRetrofitClient;
import com.example.sharingbookshelf.HttpRequest.RetrofitClient;
import com.example.sharingbookshelf.HttpRequest.RetrofitServiceApi;
import com.example.sharingbookshelf.Models.BookApiResponse;
import com.example.sharingbookshelf.Models.GetUserInfoResponse;
import com.example.sharingbookshelf.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class MyBookshelfFragment extends Fragment {

    private static final int BARCODE_ACTIVITY = 10000;
    private static final int ADDSELF_ACTIVITY = 10001;
    private static final int BOOKPOPUP_ACTIVITY = 10002;

    private Button btn_addBook;
    private CircleImageView civ_profile;
    private TextView tv_nickname;
    private RetrofitServiceApi retrofitServiceApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mybookshelf, container, false);
        initializeView(view);
        setUserView(MainActivity.getMemId()); //사용자화면 구성
        addBookView();

        return view;
    }

    private void initializeView(View v) {
        civ_profile = v.findViewById(R.id.civ_profile);
        tv_nickname = v.findViewById(R.id.tv_nickname);
        btn_addBook = v.findViewById(R.id.btn_AddBook);
    }

    private void addBookView() {
        btn_addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final PopupMenu popupMenu = new PopupMenu(getActivity().getApplicationContext(), view);
                getActivity().getMenuInflater().inflate(R.menu.menu_register_book, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.bookFind_barcode) { // 메뉴 홈페이지 만들고 intent로 수정
                            Intent intent = new Intent(getActivity(), BarcodeActivity.class);
                            getActivity().startActivityForResult(intent, BARCODE_ACTIVITY);
                        } else if (menuItem.getItemId() == R.id.bookFind_ISBN) {
                            Intent intent = new Intent(getActivity(), SelfAddBookPopupActivity.class);
                            getActivity().startActivityForResult(intent, ADDSELF_ACTIVITY);
                        } else if (menuItem.getItemId() == R.id.bookFind_direct) {
                            Toast.makeText(getActivity(), "수동으로 정보 입력", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    @CallSuper
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == BARCODE_ACTIVITY) { //바코드 인식 결과
            if (resultCode == RESULT_OK) {
                String data = intent.getExtras().getString("ISBN");
                if (data != null) {
                    Log.d(MainActivity.MAIN_TAG, data);
                    callBookResponse(data);
                }
            }
        }
        if (requestCode == ADDSELF_ACTIVITY) { //직접 추가 결과
            if (resultCode == RESULT_OK) {
                String data = intent.getExtras().getString("ISBN");
                if (data != null) {
                    Log.d(MainActivity.MAIN_TAG, data);
                    callBookResponse(data);
                }
            }
        }
    }

    /* Kakao Book search API 통신 */
    private void callBookResponse(String ISBN) {
        retrofitServiceApi = BookApiRetrofitClient.createService(RetrofitServiceApi.class);
        Call<BookApiResponse> call = retrofitServiceApi.setBookApiResponse(ISBN, "isbn");
        call.enqueue(new Callback<BookApiResponse>() {
            @Override
            public void onResponse(Call<BookApiResponse> call, Response<BookApiResponse> response) {
                BookApiResponse result = response.body();
                Log.d(MainActivity.MAIN_TAG, "책 api 통신 성공");
                if (result != null) {
                    getBookDetails(result);
                }
            }

            @Override
            public void onFailure(Call<BookApiResponse> call, Throwable t) {
                Log.e(MainActivity.MAIN_TAG, "책 api 통신 실패", t);
            }
        });
    }

    private void getBookDetails(BookApiResponse books) {
        ArrayList<BookApiResponse.Document> documentList = books.documents;
        BookApiResponse.Meta meta = books.metas;
        Intent intent = new Intent(getActivity(), BookDetailsPopupActivity.class);
        intent.putExtra("documentList", documentList);
        intent.putExtra("meta", meta);
        getActivity().startActivityForResult(intent, BOOKPOPUP_ACTIVITY);
    }

    private void setUserView(int memId) {
        retrofitServiceApi = RetrofitClient.createService(RetrofitServiceApi.class, MainActivity.getJWT());
        Call<GetUserInfoResponse> call = retrofitServiceApi.getUserInfo(memId);
        call.enqueue(new Callback<GetUserInfoResponse>() {
            @Override
            public void onResponse(Call<GetUserInfoResponse> call, Response<GetUserInfoResponse> response) {
                GetUserInfoResponse result = response.body();
                Log.d(MainActivity.MAIN_TAG, "현재사용자 : " + result.getNickname() + " 프로필 : " + result.getProfileImg());
                String nickname = result.getNickname();
                String profileImg = result.getProfileImg();
                tv_nickname.setText(nickname);
                if (profileImg != null) {
                    Glide.with(getActivity()).load(profileImg).into(civ_profile);
                }
            }

            @Override
            public void onFailure(Call<GetUserInfoResponse> call, Throwable t) {
                Log.e(MainActivity.MAIN_TAG, "사용자 정보 가져오기 실패", t);
            }
        });
    }
}