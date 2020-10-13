package cm.deone.corp.tontines.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAaqKJdP0:APA91bFcvbly22wYH5G2lUq3dzXKc5WmpD2oIC7bA79q2li3OONMW0gZUZsmSf7rWYTPDamKfttV8tzo7FlUBcGkGRxfwcVfz5oy3fWKK5yyQ4T4H5y8gjtQK-GYy1DKD_7bVbQYjr2t"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
