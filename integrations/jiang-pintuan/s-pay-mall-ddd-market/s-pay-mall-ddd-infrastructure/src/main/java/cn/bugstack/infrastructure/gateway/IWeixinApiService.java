package cn.bugstack.infrastructure.gateway;

import cn.bugstack.infrastructure.gateway.dto.WeixinQrCodeRequestDTO;
import cn.bugstack.infrastructure.gateway.dto.WeixinQrCodeResponseDTO;
import cn.bugstack.infrastructure.gateway.dto.WeixinTemplateMessageDTO;
import cn.bugstack.infrastructure.gateway.dto.WeixinTokenResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @description 寰俊API鏈嶅姟 retrofit2
 */
public interface IWeixinApiService {

    /**
     * 鑾峰彇 Access token
     * 鏂囨。锛?a href="https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Get_access_token.html">Get_access_token</a>
     *
     * @param grantType 鑾峰彇access_token濉啓client_credential
     * @param appId     绗笁鏂圭敤鎴峰敮涓€鍑瘉
     * @param appSecret 绗笁鏂圭敤鎴峰敮涓€鍑瘉瀵嗛挜锛屽嵆appsecret
     * @return 鍝嶅簲缁撴灉
     */
    @GET("cgi-bin/token")
    Call<WeixinTokenResponseDTO> getToken(@Query("grant_type") String grantType,
                                          @Query("appid") String appId,
                                          @Query("secret") String appSecret);

    /**
     * 鑾峰彇鍑嵁 ticket
     * 鏂囨。锛?a href="https://developers.weixin.qq.com/doc/offiaccount/Account_Management/Generating_a_Parametric_QR_Code.html">Generating_a_Parametric_QR_Code</a>
     * <a href="https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET">鍓嶇鏍规嵁鍑瘉灞曠ず浜岀淮鐮?/a>
     *
     * @param accessToken            getToken 鑾峰彇鐨?token 淇℃伅
     * @param weixinQrCodeRequestDTO 鍏ュ弬瀵硅薄
     * @return 搴旂瓟缁撴灉
     */
    @POST("cgi-bin/qrcode/create")
    Call<WeixinQrCodeResponseDTO> createQrCode(@Query("access_token") String accessToken, @Body WeixinQrCodeRequestDTO weixinQrCodeRequestDTO);

    /**
     * 鍙戦€佸井淇″叕浼楀彿妯℃澘娑堟伅
     * 鏂囨。锛歨ttps://mp.weixin.qq.com/debug/cgi-bin/readtmpl?t=tmplmsg/faq_tmpl
     *
     * @param accessToken              getToken 鑾峰彇鐨?token 淇℃伅
     * @param weixinTemplateMessageDTO 鍏ュ弬瀵硅薄
     * @return 搴旂瓟缁撴灉
     */
    @POST("cgi-bin/message/template/send")
    Call<Void> sendMessage(@Query("access_token") String accessToken, @Body WeixinTemplateMessageDTO weixinTemplateMessageDTO);

}
