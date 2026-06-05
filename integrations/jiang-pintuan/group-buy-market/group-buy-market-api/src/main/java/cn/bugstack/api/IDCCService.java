package cn.bugstack.api;

import cn.bugstack.api.response.Response;

/**
 * @description DCC 鍔ㄦ€侀厤缃腑蹇?
 */
public interface IDCCService {

    Response<Boolean> updateConfig(String key, String value);

}
