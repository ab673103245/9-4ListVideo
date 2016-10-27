package qianfeng.a9_4listvideo;

/**
 * Created by 王松 on 2016/10/27.
 */

public class VideoBean {
    private String videoUrl;
    private int width;
    private int height;
    private String imageUrl;
    private String title;

    public VideoBean() {
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public VideoBean(String videoUrl, int width, int height, String imageUrl, String title) {
        this.videoUrl = videoUrl;
        this.width = width;
        this.height = height;
        this.imageUrl = imageUrl;
        this.title = title;
    }
}
