package com.deloitte.bdh.data.collation.nifi.template.servie;

import com.deloitte.bdh.data.collation.nifi.template.config.Template;


public interface Transfer {

    String add(String modelGroupId, String templateType, Params params) throws Exception;

    void del(String processGroupId) throws Exception;

    void run(String processGroupId) throws Exception;

    void stop(String processGroupId) throws Exception;

    void clear(String processGroupId) throws Exception;

    interface Params<T extends Template> {
        T params();
    }
}
