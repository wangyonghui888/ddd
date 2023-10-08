package com.panda.sport.rcs.console.dto;


import lombok.Data;

@Data
public class RcsLogFormat {


    private String logType;
    private String oldVal;
    private String uid;
    private String logDesc;
    private DynamicBeanBean dynamicBean;
    private String createTime;
    private String name;
    private String logId;
    private String newVal;


    public static class DynamicBeanBean {
        /**
         * click_case : 触发条件：点击弹窗
         */
        private String click_case;

        public String getClick_case() {
            return click_case;
        }

        public void setClick_case(String click_case) {
            this.click_case = click_case;
        }
    }
}
