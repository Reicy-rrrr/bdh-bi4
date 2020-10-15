package com.deloitte.bdh.data.integration;


import com.deloitte.bdh.data.model.BiEtlConnection;

import java.util.List;

public interface AsyncService {

    void stopAndClearAsync(String processGroupId, String modelCode, List<BiEtlConnection> connectionList) throws Exception;

    void stopAndClearSync(String processGroupId, String modelCode, List<BiEtlConnection> connectionList) throws Exception;

}
