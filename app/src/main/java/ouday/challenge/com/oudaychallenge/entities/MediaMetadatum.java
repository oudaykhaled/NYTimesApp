package ouday.challenge.com.oudaychallenge.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MediaMetadatum {

@SerializedName("url")
@Expose
private String url;
@SerializedName("format")
@Expose
private String format;
@SerializedName("height")
@Expose
private double height;
@SerializedName("width")
@Expose
private double width;

public String getUrl() {
return url;
}

public void setUrl(String url) {
this.url = url;
}

public String getFormat() {
return format;
}

public void setFormat(String format) {
this.format = format;
}

public double getHeight() {
return height;
}

public void setHeight(Integer height) {
this.height = height;
}

public double getWidth() {
return width;
}

public void setWidth(Integer width) {
this.width = width;
}

}