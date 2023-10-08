package com.panda.sport.rcs.mgr.paid.matrix.bean;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  TODO
 * @Date: 2019-10-04 20:24
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class OddsFieldsTemplateVo {
    private Integer id;
    private Integer cid;
    private String  name;
    private String code;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }
}
