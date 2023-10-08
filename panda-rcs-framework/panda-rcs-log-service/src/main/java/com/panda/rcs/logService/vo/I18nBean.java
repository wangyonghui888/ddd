package com.panda.rcs.logService.vo;

import com.panda.rcs.logService.Enum.OperateLogOneEnum;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 多语言对应类
 * @author Z9-jing
 */
@Data
public class I18nBean implements Serializable {
    /*** 中文简体*/
    private String zs;
    /**英文*/
    private String en;
    /*** 简称*/
    private String jc;
    /*** 中文繁体*/
    private String zh;
    /*** 印尼*/
    private String ad;
    /*** 韩语*/
    private String ko;
    /***西班牙*/
    private String es;
    /*** 意大利*/
    private String it_IT;
    /*** 德语*/
    private String de_DE;
    /*** 法语*/
    private String fr_FR;
    /*** 葡萄牙*/
    private String pt;
    /**马来 */
    private String ms;
    /**缅甸语 */
    private String my;
    /** 泰语 */
    private String vi;
     /** 越南语*/
    private String th;

    private String ja;

    private String ru;

    public String getZs() {
        if(this.zs.isEmpty()){
            this.zs= OperateLogOneEnum.NONE.getName();
        }
        return zs;
    }

    public String getEn() {
        if(this.en.isEmpty()){
            this.en= OperateLogOneEnum.NONE.getName();
        }
        return en;
    }



    public String getZh() {
        if(StringUtils.isEmpty(this.zh)){
            this.zh= OperateLogOneEnum.NONE.getName();
        }
        return zh;
    }

    public String getAd() {
        if(StringUtils.isEmpty(this.ad)){
            this.ad= OperateLogOneEnum.NONE.getName();
        }
        return ad;
    }

    public String getKo() {
        if(StringUtils.isEmpty(this.ko)){
            this.ko= OperateLogOneEnum.NONE.getName();
        }
        return ko;
    }

    public String getEs() {
        if(StringUtils.isEmpty(this.es)){
            this.es= OperateLogOneEnum.NONE.getName();
        }
        return es;
    }

    public String getIt_IT() {
        if(StringUtils.isEmpty(this.it_IT)){
            this.it_IT= OperateLogOneEnum.NONE.getName();
        }
        return it_IT;
    }

    public String getDe_DE() {
        if(StringUtils.isEmpty(this.de_DE)){
            this.de_DE= OperateLogOneEnum.NONE.getName();
        }
        return de_DE;
    }

    public String getFr_FR() {
        if(StringUtils.isEmpty(this.fr_FR)){
            this.fr_FR= OperateLogOneEnum.NONE.getName();
        }
        return fr_FR;
    }

    public String getPt() {
        if(StringUtils.isEmpty(this.pt)){
            this.pt= OperateLogOneEnum.NONE.getName();
        }
        return pt;
    }

    public String getMs() {
        if(StringUtils.isEmpty(this.ms)){
            this.ms= OperateLogOneEnum.NONE.getName();
        }
        return ms;
    }

    public String getMy() {
        if(StringUtils.isEmpty(this.my)){
            this.my= OperateLogOneEnum.NONE.getName();
        }
        return my;
    }

    public String getVi() {
        if(StringUtils.isEmpty(this.vi)){
            this.vi= OperateLogOneEnum.NONE.getName();
        }
        return vi;
    }

    public String getTh() {
        if(StringUtils.isEmpty(this.th)){
            this.th= OperateLogOneEnum.NONE.getName();
        }
        return th;
    }

    public String getJa() {
        if(StringUtils.isEmpty(this.ja)){
            this.ja= OperateLogOneEnum.NONE.getName();
        }
        return ja;
    }

    public String getRu() {
        if(StringUtils.isEmpty(this.ru)){
            this.ru= OperateLogOneEnum.NONE.getName();
        }
        return ru;
    }
}
