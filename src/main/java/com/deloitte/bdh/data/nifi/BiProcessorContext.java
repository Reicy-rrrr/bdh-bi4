package com.deloitte.bdh.data.nifi;

import com.deloitte.bdh.data.model.BiProcessors;
import com.deloitte.bdh.data.model.resp.Processor;
import lombok.Data;

import java.util.List;

@Data
public class BiProcessorContext extends BiProcessors {
    List<Processor> processorList;
}
