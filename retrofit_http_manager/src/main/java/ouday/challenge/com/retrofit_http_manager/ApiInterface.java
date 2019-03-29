package ouday.challenge.com.retrofit_http_manager;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

/**
 * This interface is necessary for Retrofit Implementation
 */
public interface ApiInterface {

    @Headers({"Content-Type: application/json" })
    @POST
    Call<String> postHTTP(@Url String url, @Body String requestBody, @HeaderMap Map<String, String> header);

    @Headers({"Content-Type: application/json" })
    @GET
    Call<String> getHTTP(@Url String url);

    @Headers({"Content-Type: application/json" })
    @DELETE
    Call<String> deleteHTTP(@Url String url);

    @Headers({"Content-Type: application/json" })
    @PUT
    Call<String> putHTTP(@Url String url, @Body String requestBody);

    @POST
    @FormUrlEncoded
    Call<String> postFormData(@Url String url, @FieldMap Map<String,String> params, @HeaderMap Map<String, String> header);

}