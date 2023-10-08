package com.panda.sport.rcs.mts.sportradar.vo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document("pinnacle_market_updated")
@Data
public class PinnacleMarketUpdated {
  private String eventSeqNum;
  private List<PinnacleMarketUpdatedSelection> selections;
  private String accRestriction;
  private String cashOutAvailability;
  private String competitionId;
  private String expiresAt;
  private String fixtureId;
  private String id;
  private Boolean isLive;
  private String marketSeqNum;
  private String name;
  private String nameTemplateId;
  private String periodId;
  private String pricedAt;
  private String ruleSetId;
  private String ruleSetName;
  private String points;
  private String sportId;
  private String status;
  private String unitId;
  private String unitName;
  private String updatedAt;
  private Long participantId;
  private Long subParticipantId;
  private Date createDateTime = new Date();
}
