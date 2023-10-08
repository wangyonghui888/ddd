package com.panda.sport.rcs.common.vo.api.response.danger;

import java.util.List;
import lombok.Data;

@Data
public class PageResult {

  private Integer code;
  private Integer currentPage;
  private List<DangerComboMatchResVO> data;
  private String errorString;
  private String msg;
  private Integer totalCount;

}
