package qianfeng.a9_4listvideo;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/10/27 0027.
 */
public class MyAdapter extends BaseAdapter {

    private List<VideoBean> list;
    private Context context;
    private LayoutInflater inflater;

    private MediaPlayer player;
    private int currentPosition = -1;


    public MyAdapter(List<VideoBean> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        player = new MediaPlayer();

        // 异步准备完毕后回调此方法
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item, parent, false);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.iv);
            holder.surfaceView = (SurfaceView) convertView.findViewById(R.id.surface_view);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final VideoBean bean = list.get(position);
        // 获取从网上json字符串得来的宽高的值，待会重新设置iv和surfaceView的宽高的值
        int width = bean.getWidth();
        int height = bean.getHeight();

        ViewGroup.LayoutParams lp = holder.iv.getLayoutParams();
        lp.width = width;
        lp.height = height;

        // 如果这个布局参数是从iv上直接获取(iv.getLayoutParams())的话，
        // 调用这个方法就可以直接重新测量iv的布局参数并重新生成iv的宽高。
        holder.iv.requestLayout();

        // 这个布局参数是从iv上直接获取的，那么我在surfaceView上要使用它，就不能用requstLayout(),而要使用setLayoutParams()
        holder.surfaceView.setLayoutParams(lp); // 这样一样能达到重新利用网络的json获得的宽高来重新设置surfaceView的宽高这样的效果。

        // 用Picasso下载图片到holder.iv上
        Picasso.with(context).load(bean.getImageUrl()).into(holder.iv);

        Object tag = holder.iv.getTag(); // 这个iv是最新加载进来的iv，是用户你能看到的最底部的iv
        if(tag != null)// 什么情况下tag==null? 就是加载第一屏数据的时候,这个！=null表明这个tag是经过回收之后得到的
        {
            Integer tag1 = (Integer) tag;
            if(tag1 == currentPosition && tag1 != position)// 如果你点击了最底部这个iv,但是这个iv就是你想播放的，那就 tag1 != position，保证你想播放的能播放而不会被停止，而在其他地方被回收的那个item的视频是会被停止的，注意这个getTat是在setTag之前就执行了。
            {
                player.stop();
                //然后更新当前播放播放视频的位置，为-1，即不播放，即回收了item播放视频之后，就不播放，等用户下次点击的时候再播放
                currentPosition = -1;// currentPosition:无论何时都是表示要立即播放的视频
            }
        }

        // 要弄懂非常重要的一点是：position究竟是什么？
        holder.iv.setTag(position);// position:是每调用一个getView拿到的position,其实每调用一次getView，就会调用public Object getItem(int position) 来获取这个list的position
        if(currentPosition == position) // position是你getView，你能看到的最后一个item的position，并不是预加载的position
        {
            holder.iv.setVisibility(View.INVISIBLE);
            holder.surfaceView.setVisibility(View.VISIBLE); // surfaceView只能是用View.INVISIBLE来隐藏，不能用GONE，因为一用GONE，SurfaceView会被回收，而surface会被认为是0，然后holder也画不了东西。
            player.reset(); // 重置player里面的setData
            // MediaPlayer和Video同步
            player.setDisplay(holder.surfaceView.getHolder());

            try {
                player.setDataSource(bean.getVideoUrl());
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else
        {
            holder.iv.setVisibility(View.VISIBLE);
            holder.surfaceView.setVisibility(View.INVISIBLE);
        }

        holder.title.setText(bean.getTitle());

        // 点击iv，只是做一件事，就是告诉系统我要播放哪个视频，如果视频正在播放，那就停止之前的，加载之后点击的iv对应的视频。
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player.isPlaying())
                {// 如果player正在播放，你点击iv，就让刚刚播放视频停止，然后立即执行用户点击了的下一个视频，
                    // 并把currentPosition重新给值，并刷新页面，等待视频播放。
                    player.stop();
                }else
                {
                   //如果点击的iv不是当前正在播放视频的iv
                    //就将这个iv的postion赋给currentPosition，然后重新调用getView方法，if(currentPosition == position)就会执行
                    Integer tag = (Integer) holder.iv.getTag();
                    currentPosition = tag; // 告诉系统我要播放哪个视频
                    notifyDataSetChanged();// 调用这个方法会重新调用一次getView()
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView title;
        ImageView iv;
        SurfaceView surfaceView;
    }
}
