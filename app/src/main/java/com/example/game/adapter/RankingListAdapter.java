package com.example.game.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.game.R;
import com.example.game.database.RankingList;
import java.util.List;

public class RankingListAdapter extends BaseAdapter {

    private Context mcontext;

    private LayoutInflater mlayoutInflater;//是一个控件 作用与findViewbyid相似，都是为了寻找某个view。

    public List<RankingList> lists;

    public RankingListAdapter(Context context,List<RankingList> lists){
        this.mcontext = context;
        mlayoutInflater = LayoutInflater.from(context);
        this.lists = lists;
    }

    //静态类，在程序v初始化的时候就创建出来了，预加载，节省资源
    static class ViewHolder{

        public TextView mTvRanking,mTvName,mTvScore;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //getview方法会在第一次加载listviewactivity的第一个item时调用
    //holder就是个缓存打包器，将每个同类型item的打个包，重复使用控件
    //将你写好的布局，作为列表中每一个小部分的布局，把写的xml“关联“到convertView上
    //包含了所有视图的信息，使用的时候直接通过getTag（）获取，使用ViewHolder为了减少程序的内存
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            convertView = mlayoutInflater.inflate(R.layout.layout_ranking_list,null);
            holder = new ViewHolder();
            /*得到各个控件的对象*/
           holder.mTvName = convertView.findViewById(R.id.tv_name);
           holder.mTvRanking = convertView.findViewById(R.id.tv_ranking);
           holder.mTvScore = convertView.findViewById(R.id.tv_score);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        //给控件赋值
        if (lists != null){
            int ranking = position+1;
            RankingList list = lists.get(position);
            holder.mTvRanking.setText(""+ranking+"");
            holder.mTvName.setText(list.getName());
            holder.mTvScore.setText(list.getScore().toString());
            return convertView;
        }
        return null;

    }




}
