package com.deloitte.bdh.data.collation.integration.impl;

import com.deloitte.bdh.common.http.HttpClientUtil;
import com.deloitte.bdh.common.redis.RedisClusterUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.collation.enums.NifiEnum;
import com.deloitte.bdh.data.collation.integration.NifiProcessService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public abstract class AbstractNifiProcess implements NifiProcessService {
    @Value("${nifi.url}")
    protected String URL;
    private static final Integer expiredTime = 60 * 24 * 7;

    @Autowired
    private RedisClusterUtil redisClusterUtil;

    @Override
    public String getToken() throws Exception {
        //todo 数据库读取用户和密码
        String username = "login/bidw@NIFI.COM";
        String password = "REQ51sRHZ";
        String nifiToken = null;
//        String nifiToken = redisClusterUtil.STRINGS.get(NifiEnum.REDIS_ACCESS_TOKEN.getKey() + username);
        if (StringUtil.isEmpty(nifiToken)) {
            Map<String, Object> req = Maps.newHashMap();
            req.put("username", username);
            req.put("password", password);
            nifiToken = HttpClientUtil.httpPostRequest(URL + NifiEnum.ACCESS_TOKEN.getKey(), req);
            redisClusterUtil.STRINGS.set(NifiEnum.REDIS_ACCESS_TOKEN.getKey() + username, nifiToken);
            redisClusterUtil.KEYS.expired(NifiEnum.REDIS_ACCESS_TOKEN.getKey() + username, expiredTime);
        }
        return nifiToken;
    }

    protected Map<String, Object> setHeaderAuthorization() throws Exception {
        String token = this.getToken();
        Map<String, Object> reqHeader = Maps.newHashMap();
        reqHeader.put("Authorization", "Bearer " + token);
        return reqHeader;
    }

}
