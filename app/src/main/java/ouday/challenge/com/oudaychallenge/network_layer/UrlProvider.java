package ouday.challenge.com.oudaychallenge.network_layer;

public class UrlProvider {

    public static final String BASE_URL = "http://api.nytimes.com/svc/";
    public static final String API_KEY = "Rk6GfVjL9XA3A5ipo7bjr2fNh80CpeA5";
    public static final String REQUEST_NEWS_URL = BASE_URL + "mostpopular/v2/mostviewed/all-sections/%s.json?api-key=%s"; // period - apiKey

}
