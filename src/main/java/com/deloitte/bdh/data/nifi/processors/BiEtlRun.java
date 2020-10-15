package com.deloitte.bdh.data.nifi.processors;


import com.deloitte.bdh.common.util.JsonUtil;
import com.deloitte.bdh.data.enums.RunStatusEnum;
import com.deloitte.bdh.data.nifi.dto.RunContext;
import com.deloitte.bdh.data.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BiEtlRun extends AbStractProcessors<RunContext> {
    @Autowired
    private BiProcessorsService processorsService;

    @Override
    public RunContext positive(RunContext context) throws Exception {
        logger.info("开始执行创建 BiEtlRun.positive，参数:{}", JsonUtil.obj2String(context));
        switch (context.getMethod()) {
            case VIEW:
                processorsService.preview(context);
                break;
            case RUN:
                processorsService.runState(context.getModel().getProcessGroupId(), RunStatusEnum.RUNNING.getKey(), true);
                break;
            case STOP:
                processorsService.stopAndClearSync(context.getModel().getProcessGroupId(), context.getModel().getCode());
                break;
            default:
        }
        return null;
    }

    @Override
    public void reverse(RunContext context) throws Exception {
        logger.info("开始执行创建 BiEtlRun.reverse，参数:{}", JsonUtil.obj2String(context));
        switch (context.getMethod()) {
            case VIEW:
                logger.info("BiEtlRun.reverse.VIEW : nothing");
                break;
            case RUN:
                logger.info("BiEtlRun.reverse.RUN : nothing");
                break;
            case STOP:
                logger.info("BiEtlRun.reverse.STOP : nothing");
                break;
            default:

        }
    }

    @Override
    protected void end(RunContext context) throws Exception {
        switch (context.getMethod()) {
            case VIEW:
                processorsService.stopAndClearAsync(context.getModel().getProcessGroupId(), context.getModel().getCode());
                break;
            case RUN:
                logger.info("BiEtlRun.end.RUN : nothing");
                break;
            case STOP:
                logger.info("BiEtlRun.end.STOP : nothing");
                break;
            default:

        }
    }

    @Override
    protected void validateContext(RunContext context) throws Exception {
    }

}
