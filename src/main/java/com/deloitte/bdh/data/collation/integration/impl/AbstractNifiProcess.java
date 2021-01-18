package com.deloitte.bdh.data.collation.integration.impl;

import com.deloitte.bdh.common.http.HttpClientUtil;
import com.deloitte.bdh.common.properties.BiProperties;
import com.deloitte.bdh.common.redis.RedisClusterUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.collation.enums.NifiEnum;
import com.deloitte.bdh.data.collation.integration.NifiProcessService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Map;

public abstract class AbstractNifiProcess implements NifiProcessService {
    @Resource
    protected BiProperties biProperties;
    @Autowired
    private RedisClusterUtil redisClusterUtil;

    @Override
    public String getToken() throws Exception {
        String nifiToken = redisClusterUtil.STRINGS.get(NifiEnum.REDIS_ACCESS_TOKEN.getKey() + biProperties.getNifiUserName());
        if (StringUtil.isEmpty(nifiToken)) {
            Map<String, Object> req = Maps.newHashMap();
            req.put("username", biProperties.getNifiUserName());
            req.put("password", biProperties.getNifiPwd());
            nifiToken = HttpClientUtil.httpPostRequest(biProperties.getNifiUrl() + NifiEnum.ACCESS_TOKEN.getKey(), req);
            redisClusterUtil.STRINGS.set(NifiEnum.REDIS_ACCESS_TOKEN.getKey() + biProperties.getNifiUserName(), nifiToken);
            redisClusterUtil.KEYS.expired(NifiEnum.REDIS_ACCESS_TOKEN.getKey() + biProperties.getNifiUserName(), Integer.parseInt(biProperties.getExpiredTime()));
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
