package qianfeng.a9_4listvideo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = ((ListView) findViewById(R.id.lv));

        List<VideoBean> list = parseJson();
        MyAdapter adapter = new MyAdapter(list, this);
        lv.setAdapter(adapter);
    }

    private List<VideoBean> parseJson() {
        StringBuffer json = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("video1.json")));
            String str;
            while ((str = br.readLine()) != null) {
                json.append(str);
            }
            return parseJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<VideoBean> parseJson(StringBuffer json) {
        List<VideoBean> list = new ArrayList<>();
        try {
            JSONArray data = new JSONObject(json.toString()).getJSONObject("data").getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                if (data.getJSONObject(i).has("ad")) {
                    continue;
                }
                JSONObject group = data.getJSONObject(i).getJSONObject("group");
                String videoUrl = group.getJSONObject("360p_video").getJSONArray("url_list").getJSONObject(0).getString("url");
                String title = group.getString("title");
                int video_height = group.getInt("video_height");
                int video_width = group.getInt("video_width");
                String imageUrl = group.getJSONObject("medium_cover").getJSONArray("url_list").getJSONObject(0).getString("url");
                list.add(new VideoBean(videoUrl, video_width, video_height, imageUrl, title));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

}
