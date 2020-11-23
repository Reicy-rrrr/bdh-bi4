package com.deloitte.bdh.common.cron;


import com.deloitte.bdh.common.json.JsonUtil;
import org.apache.commons.lang.StringUtils;

public class CronUtil {

    /**
     * 方法摘要：构建Cron表达式
     *
     * @param cronDate
     * @return String
     */
    public static String createCronExpression(String cronDate) {
        if (StringUtils.isBlank(cronDate)) {
            throw new RuntimeException("cron实体为空，无法生成表达式");
        }
        TaskScheduleModel model = JsonUtil.StringToClass(cronDate,TaskScheduleModel.class);
        return createCronExpression(model);
    }

    /**
     * 方法摘要：构建Cron表达式
     *
     * @param taskScheduleModel
     * @return String
     */
    public static String createCronExpression(TaskScheduleModel taskScheduleModel) {
        StringBuffer cronExp = new StringBuffer("");

        if (null == taskScheduleModel.getJobType()) {
            throw new RuntimeException("执行周期未配置");
        }

        //每隔几秒
        if (taskScheduleModel.getJobType() == 0) {
            validateSecond(taskScheduleModel);
            cronExp.append("0/").append(taskScheduleModel.getSecond());
            cronExp.append(" ");
            cronExp.append("* ");
            cronExp.append("* ");
            cronExp.append("* ");
            cronExp.append("* ");
            cronExp.append("?");
        }

        //每隔几分钟
        if (taskScheduleModel.getJobType() == 4) {
            validateMin(taskScheduleModel);
            cronExp.append("* ");
            cronExp.append("0/").append(taskScheduleModel.getMinute());
            cronExp.append(" ");
            cronExp.append("* ");
            cronExp.append("* ");
            cronExp.append("* ");
            cronExp.append("?");
        }

        //每天
        if (taskScheduleModel.getJobType() == 1) {
            validateSecond(taskScheduleModel);
            validateMin(taskScheduleModel);
            validateHour(taskScheduleModel);
            cronExp.append(taskScheduleModel.getSecond()).append(" ");
            cronExp.append(taskScheduleModel.getMinute()).append(" ");
            cronExp.append(taskScheduleModel.getHour()).append(" ");
            cronExp.append("* ");//日
            cronExp.append("* ");//月
            cronExp.append("?");//周
        }

        //按每周
        if (taskScheduleModel.getJobType() == 3) {
            validateSecond(taskScheduleModel);
            validateMin(taskScheduleModel);
            validateHour(taskScheduleModel);
            validateWeek(taskScheduleModel);
            cronExp.append(taskScheduleModel.getSecond()).append(" ");
            cronExp.append(taskScheduleModel.getMinute()).append(" ");
            cronExp.append(taskScheduleModel.getHour()).append(" ");

            //一个月中第几天
            cronExp.append("? ");
            //月份
            cronExp.append("* ");
            //周
            Integer[] weeks = taskScheduleModel.getDayOfWeeks();
            for (int i = 0; i < weeks.length; i++) {
                if (i == 0) {
                    cronExp.append(weeks[i]);
                } else {
                    cronExp.append(",").append(weeks[i]);
                }
            }
        }

        //按每月
        if (taskScheduleModel.getJobType() == 2) {
            validateSecond(taskScheduleModel);
            validateMin(taskScheduleModel);
            validateHour(taskScheduleModel);
            validateMonth(taskScheduleModel);
            cronExp.append(taskScheduleModel.getSecond()).append(" ");
            cronExp.append(taskScheduleModel.getMinute()).append(" ");
            cronExp.append(taskScheduleModel.getHour()).append(" ");

            //一个月中的哪几天
            Integer[] days = taskScheduleModel.getDayOfMonths();
            for (int i = 0; i < days.length; i++) {
                if (i == 0) {
                    cronExp.append(days[i]);
                } else {
                    cronExp.append(",").append(days[i]);
                }
            }
            //月份
            cronExp.append(" * ");
            //周
            cronExp.append("?");
        }
        return cronExp.toString();
    }

    /**
     * 方法摘要：生成计划的详细描述
     *
     * @param cronDate
     * @return String
     */
    public static String createDescription(String cronDate) {
        if (StringUtils.isBlank(cronDate)) {
            throw new RuntimeException("cron实体为空，无法生成中文解释");
        }
        TaskScheduleModel model = JsonUtil.StringToClass(cronDate, TaskScheduleModel.class);
        return createDescription(model);
    }

    /**
     * 方法摘要：生成计划的详细描述
     *
     * @param taskScheduleModel
     * @return String
     */
    public static String createDescription(TaskScheduleModel taskScheduleModel) {
        StringBuffer description = new StringBuffer("");

        //按分
        if (taskScheduleModel.getJobType() == 0) {
            description.append("每分钟");
            description.append(taskScheduleModel.getSecond()).append("秒");
            description.append("执行");
        }

        //按小时
        if (taskScheduleModel.getJobType() == 4) {
            description.append("每小时");
            description.append(taskScheduleModel.getMinute()).append("分");
            description.append(taskScheduleModel.getSecond()).append("秒");
            description.append("执行");
        }

        //按每天
        if (taskScheduleModel.getJobType() == 1) {
            description.append("每天");
            description.append(taskScheduleModel.getHour()).append("时");
            description.append(taskScheduleModel.getMinute()).append("分");
            description.append(taskScheduleModel.getSecond()).append("秒");
            description.append("执行");
        }

        //按每周
        if (taskScheduleModel.getJobType() == 3) {
            if (taskScheduleModel.getDayOfWeeks() != null && taskScheduleModel.getDayOfWeeks().length > 0) {
                String days = "";
                for (int i : taskScheduleModel.getDayOfWeeks()) {
                    days += "周" + i;
                }
                description.append("每周的").append(days).append(" ");
            }
            if (null != taskScheduleModel.getSecond()
                    && null != taskScheduleModel.getMinute()
                    && null != taskScheduleModel.getHour()) {
                description.append(",");
                description.append(taskScheduleModel.getHour()).append("时");
                description.append(taskScheduleModel.getMinute()).append("分");
                description.append(taskScheduleModel.getSecond()).append("秒");
            }
            description.append("执行");
        }

        //按每月
        else if (taskScheduleModel.getJobType() == 2) {
            //选择月份
            if (taskScheduleModel.getDayOfMonths() != null && taskScheduleModel.getDayOfMonths().length > 0) {
                String days = "";
                for (int i : taskScheduleModel.getDayOfMonths()) {
                    days += i + "号";
                }
                description.append("每月的").append(days).append(" ");
            }
            description.append(taskScheduleModel.getHour()).append("时");
            description.append(taskScheduleModel.getMinute()).append("分");
            description.append(taskScheduleModel.getSecond()).append("秒");
            description.append("执行");
        }

        return description.toString();
    }

    private static void validateSecond(TaskScheduleModel taskScheduleModel) {
        if (null == taskScheduleModel.getSecond()) {
            throw new RuntimeException("未配置秒");
        }
        if (taskScheduleModel.getSecond() > 60 || taskScheduleModel.getSecond() < 0) {
            throw new RuntimeException("秒的区间不正确");
        }
    }

    private static void validateMin(TaskScheduleModel taskScheduleModel) {
        if (null == taskScheduleModel.getMinute()) {
            throw new RuntimeException("未配置分钟");
        }
        if (taskScheduleModel.getMinute() > 60 || taskScheduleModel.getMinute() < 0) {
            throw new RuntimeException("秒的区间不正确");
        }
    }

    private static void validateHour(TaskScheduleModel taskScheduleModel) {
        if (null == taskScheduleModel.getHour()) {
            throw new RuntimeException("未配置小时");
        }
        if (taskScheduleModel.getHour() > 24 || taskScheduleModel.getHour() < 0) {
            throw new RuntimeException("小时的区间不正确");
        }
    }

    private static void validateWeek(TaskScheduleModel taskScheduleModel) {
        if (null == taskScheduleModel.getDayOfWeeks()) {
            throw new RuntimeException("未配置周的集合");
        }
        for (Integer week : taskScheduleModel.getDayOfWeeks()) {
            if (null == week) {
                throw new RuntimeException("未配置周");
            }
            if (taskScheduleModel.getHour() > 24 || taskScheduleModel.getHour() < 0) {
                throw new RuntimeException("周的区间不正确");
            }
        }
    }

    private static void validateMonth(TaskScheduleModel taskScheduleModel) {
        if (null == taskScheduleModel.getDayOfMonths()) {
            throw new RuntimeException("未配置月的集合");
        }
        for (Integer month : taskScheduleModel.getDayOfMonths()) {
            if (null == month) {
                throw new RuntimeException("未配置月");
            }
            if (taskScheduleModel.getHour() > 31 || taskScheduleModel.getHour() < 0) {
                throw new RuntimeException("月的区间不正确");
            }
        }
    }

    //参考例子
    public static void main(String[] args) {
        //执行时间：每天的12时12分12秒 start
        TaskScheduleModel taskScheduleModel = new TaskScheduleModel();

        //分钟
        taskScheduleModel.setJobType(0);
        taskScheduleModel.setSecond(31);
        String cronExp = createCronExpression(taskScheduleModel);
        System.out.println(cronExp + ":" + createDescription(taskScheduleModel));

        //小时
        taskScheduleModel.setJobType(4);
        taskScheduleModel.setMinute(8);
        String cronExpp = createCronExpression(taskScheduleModel);
        System.out.println(cronExpp + ":" + createDescription(taskScheduleModel));

        //每天
        taskScheduleModel.setJobType(1);
        Integer hour = 12; //时
        Integer minute = 12; //分
        Integer second = 12; //秒
        taskScheduleModel.setHour(hour);
        taskScheduleModel.setMinute(minute);
        taskScheduleModel.setSecond(second);
        String cropExp = createCronExpression(taskScheduleModel);
        System.out.println(cropExp + ":" + createDescription(taskScheduleModel));

        //每周的哪几天执行
        taskScheduleModel.setJobType(3);
        Integer[] dayOfWeeks = new Integer[3];
        dayOfWeeks[0] = 1;
        dayOfWeeks[1] = 2;
        dayOfWeeks[2] = 3;
        taskScheduleModel.setDayOfWeeks(dayOfWeeks);
        cropExp = createCronExpression(taskScheduleModel);
        System.out.println(cropExp + ":" + createDescription(taskScheduleModel));

        //每月的哪几天执行
        taskScheduleModel.setJobType(2);
        Integer[] dayOfMonths = new Integer[3];
        dayOfMonths[0] = 1;
        dayOfMonths[1] = 21;
        dayOfMonths[2] = 13;
        taskScheduleModel.setDayOfMonths(dayOfMonths);
        cropExp = createCronExpression(taskScheduleModel);
        System.out.println("json:" + JsonUtil.readObjToJson(taskScheduleModel));

        System.out.println(cropExp + ":" + createDescription(taskScheduleModel));

    }

}


