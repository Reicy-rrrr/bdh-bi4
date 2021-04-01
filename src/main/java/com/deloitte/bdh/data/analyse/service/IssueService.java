package com.deloitte.bdh.data.analyse.service;

import com.deloitte.bdh.data.analyse.model.request.CopyDeloittePageDto;
import com.deloitte.bdh.data.analyse.model.request.IssueDeloitteDto;

import java.util.Map;

public interface IssueService {

    Map<String, String> copyDeloittePage(CopyDeloittePageDto dto);

    Map<String, String> issueDeloittePage(IssueDeloitteDto dto);
}
