package com.github.mylibdemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mylibdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:cl
 * 创建日期：2025/5/16
 * 描述:
 */
public class SecondActivity extends AppCompatActivity {


   private RecyclerView rvContent;

   private StringAdapter adapter;

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_second);
      rvContent = findViewById(R.id.rvContent);

      initRv();
   }

   private void initRv(){
      adapter = new StringAdapter();
      rvContent.setLayoutManager(new LinearLayoutManager(this));
      rvContent.setAdapter(adapter);

      List<String> dataList = new ArrayList<>();
      for (int index = 0 ;index < 50; index++){
         dataList.add("我是内容 "+index);
      }

      adapter.setDataList(dataList);
   }


   protected static class StringAdapter extends RecyclerView.Adapter<StringAdapter.ViewHolder>{

      private final List<String> dataList = new ArrayList<>();


      public void setDataList(List<String> dataList){
         this.dataList.clear();
         if (dataList != null && !dataList.isEmpty()){
            this.dataList.addAll(dataList);
         }

         notifyDataSetChanged();
      }

      @NonNull
      @Override
      public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_string_content,parent,false);
         return new ViewHolder(view);
      }

      @Override
      public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         holder.tvContent.setText(dataList.get(position));
      }

      @Override
      public int getItemCount() {
         return dataList.size();
      }

      static class ViewHolder extends RecyclerView.ViewHolder{

         private final TextView tvContent;

         public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
         }
      }
   }
}
