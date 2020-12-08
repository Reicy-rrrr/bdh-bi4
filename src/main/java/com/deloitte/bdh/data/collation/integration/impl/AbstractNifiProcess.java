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
    @Value("${nifi.transfer.url}")
    protected String url;
    @Value("${nifi.transfer.username}")
    protected String username;
    @Value("${nifi.transfer.password}")
    protected String password;
    @Value("${nifi.transfer.expiredTime}")
    protected String expiredTime;


    @Autowired
    private RedisClusterUtil redisClusterUtil;

    @Override
    public String getToken() throws Exception {
        String nifiToken = redisClusterUtil.STRINGS.get(NifiEnum.REDIS_ACCESS_TOKEN.getKey() + username);
        if (StringUtil.isEmpty(nifiToken)) {
            Map<String, Object> req = Maps.newHashMap();
            req.put("username", username);
            req.put("password", password);
            nifiToken = HttpClientUtil.httpPostRequest(url + NifiEnum.ACCESS_TOKEN.getKey(), req);
            redisClusterUtil.STRINGS.set(NifiEnum.REDIS_ACCESS_TOKEN.getKey() + username, nifiToken);
            redisClusterUtil.KEYS.expired(NifiEnum.REDIS_ACCESS_TOKEN.getKey() + username, Integer.parseInt(expiredTime));
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
