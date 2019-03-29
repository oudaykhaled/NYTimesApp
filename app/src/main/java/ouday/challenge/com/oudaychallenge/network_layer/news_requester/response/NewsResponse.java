package ouday.challenge.com.oudaychallenge.network_layer.news_requester.response;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ouday.challenge.com.oudaychallenge.entities.Result;

public class NewsResponse {

@SerializedName("status")
@Expose
private String status;
@SerializedName("copyright")
@Expose
private String copyright;
@SerializedName("num_results")
@Expose
private double numResults;
@SerializedName("results")
@Expose
private List<Result> results = null;

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

public String getCopyright() {
return copyright;
}

public void setCopyright(String copyright) {
this.copyright = copyright;
}

public double getNumResults() {
return numResults;
}

public void setNumResults(double numResults) {
this.numResults = numResults;
}

public List<Result> getResults() {
return results;
}

public void setResults(List<Result> results) {
this.results = results;
}

}