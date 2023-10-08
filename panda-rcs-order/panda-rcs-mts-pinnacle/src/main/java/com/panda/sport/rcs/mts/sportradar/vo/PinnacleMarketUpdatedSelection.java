package com.panda.sport.rcs.mts.sportradar.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
@Data
@NoArgsConstructor
public class PinnacleMarketUpdatedSelection {
  private String id;
  private String maxStake;
  private String name;
  private String participantId;
  private String price;
  private String sortOrder;
  private String status;
  private String type;
  private String points;
  private String altPoints;
}
