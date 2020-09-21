package com.bizbot.bizbot.Support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bizbot.bizbot.R;
import com.bizbot.bizbot.Room.Entity.SupportModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SupportListAdapter extends RecyclerView.Adapter<SupportListAdapter.ViewHolder> {
    private static final String TAG = "SupportLisAdapter";

    private Context context;
    private List<SupportModel> sList;
    private List<SupportModel> filterList;
    private KeywordAdapter kwAdapter;
    private String area = null;
    private String field = null;

    public SupportListAdapter(Context context, List<SupportModel> list, String area, String field){
        this.sList = list;
        this.filterList = list;
        this.context = context;
        this.area = area;
        this.field = field;

        FilterItem(area,field);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView agency;
        TextView term;
        ToggleButton likeBtn;
        RecyclerView keywordRV;

        ViewHolder(View view){
            super(view);
            title = (TextView)view.findViewById(R.id.title);
            agency = (TextView)view.findViewById(R.id.agency);
            term = (TextView)view.findViewById(R.id.term);
            likeBtn = (ToggleButton)view.findViewById(R.id.like);
            keywordRV = (RecyclerView)view.findViewById(R.id.keyword_rv);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_support_list,parent,false);
        ViewHolder vh =new ViewHolder(view);
        return vh;
    }

    //리사이클러뷰 아이템 갯수
    @Override
    public int getItemCount() {
        if(filterList == null)
            return 0;
        return filterList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(filterList.get(position).getPblancNm()); //지원 사업 제목
        holder.agency.setText(filterList.get(position).getJrsdInsttNm()); //접수기관명
        holder.term.setText(filterList.get(position).getReqstBeginEndDe()); //접수기간

        //키워드 리사이클러뷰 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        holder.keywordRV.setLayoutManager(layoutManager);
        kwAdapter = new KeywordAdapter(SlicingWord(filterList,position));
        holder.keywordRV.setAdapter(kwAdapter);

    }


    /**
     * 상단에 쓰이는 단어만 추출
     * @param list : 전체 데이터
     * @param position : 아이템 위치
     * @return 키워드
     */
    public String[] SlicingWord(List<SupportModel>list, int position){
        String[] arr1 = list.get(position).getPldirSportRealmLclasCodeNm().split("@"); //분야
        String[] arr2 = list.get(position).getPldirSportRealmMlsfcCodeNm().split("@"); //
        String[] wordList = new String[arr1.length+arr2.length];
        System.arraycopy(arr1,0,wordList,0,arr1.length);
        System.arraycopy(arr2,0,wordList,arr1.length,arr2.length);

        return wordList;
    }

    /**
     * 아이템 필터
     * @param area : 카테고리 엑티비티에서 선택한 지역 영역
     * @param field : 카테고리 엑티비티에서 선택한 분야 영역
     */
    public void FilterItem(String area, String field){
        List<SupportModel> filtering = new ArrayList<SupportModel>();

        if(area != null && field == null){ //지역 o, 분야 x
            for(SupportModel item:sList){
                if(item.getAreaNm().contains(area))
                    filtering.add(item);
            }
        }else if(area == null && field != null){ //지역 x, 분야 o
            for(SupportModel item:sList){
                if(item.getPldirSportRealmLclasCodeNm().contains(field))
                    filtering.add(item);
            }
        }else if(area != null && field != null){ //지역 o, 분야 o
            for(SupportModel item:sList){
                if(item.getAreaNm().contains(area) && item.getPldirSportRealmLclasCodeNm().contains(field))
                    filtering.add(item);
            }
        }else{
            filtering = sList;
        }

        filterList = filtering;
    }

    /**
     * 리스트에 출력하는 아이템 개수 출력
     * @return 아이템 개수
     */
    public int ItemCount(){
        return filterList.size();
    }

    /**
     * 아이템 정렬
     * @param i : spinner 에서 선택한 아이템 번호
     */
    public void ListSort(int i){
        //todo: default 값 설정, 코드 중복 개선
        switch (i) {
            case 1: //최신순 정렬
                Comparator<SupportModel> newest = (item1, item2) -> {
                    int result = item1.getCreatPnttm().compareTo(item2.getCreatPnttm());
                    return result;
                };
                Collections.sort(filterList,newest);
                notifyDataSetChanged();
                break;
            case 2: //제목순 정렬
                Comparator<SupportModel> title = (item1, item2) -> {
                    int result = item1.getPblancNm().compareTo(item2.getPblancNm());
                    return result;
                };
                Collections.sort(filterList,title);
                notifyDataSetChanged();
                break;
            case 3: //이름순 정렬
                Comparator<SupportModel> agency = (item1, item2) -> {
                    int result = item1.getJrsdInsttNm().compareTo(item2.getJrsdInsttNm());
                    return result;
                };
                Collections.sort(filterList,agency);
                notifyDataSetChanged();
                break;
            case 4: //접수 기간 마감순 정렬
                Comparator<SupportModel> term = (item1, item2) -> {
                    int result = item1.getReqstBeginEndDe().compareTo(item2.getReqstBeginEndDe());
                    return result;
                };
                Collections.sort(filterList,term);
                notifyDataSetChanged();
                break;
            default: //아무것도 선택 안했을 때
                Comparator<SupportModel> reset = (item1, item2) -> {
                    int result = item1.getId() <= item2.getId() ? -1 : 1;
                    return result;
                };
                Collections.sort(filterList,reset);
                notifyDataSetChanged();
        }
    }

    public void setList(List<SupportModel> list){
        this.sList = list;
        this.filterList = list;
        notifyDataSetChanged();
    }
}