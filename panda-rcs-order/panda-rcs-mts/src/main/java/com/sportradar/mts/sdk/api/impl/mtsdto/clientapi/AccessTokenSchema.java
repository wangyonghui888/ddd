package com.sportradar.mts.sdk.api.impl.mtsdto.clientapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"access_token", "expires_in", "not-before-policy", "refresh_expires_in", "refresh_token", "session_state", "token_type", "scope"})
public class AccessTokenSchema
  implements Serializable
{
  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("expires_in")
  private Integer expiresIn;
  @JsonProperty("not-before-policy")
  private Integer notBeforePolicy;
  @JsonProperty("refresh_expires_in")
  private Integer refreshExpiresIn;
  @JsonProperty("refresh_token")
  private String refreshToken;
  @JsonProperty("session_state")
  private String sessionState;
  @JsonProperty("token_type")
  private String tokenType;
  @JsonProperty("scope")
  private String scope;
  
  @JsonProperty("code")
  private String code;
  
  private static final long serialVersionUID = 126597019580134904L;
  
  public AccessTokenSchema() {}
  
  public AccessTokenSchema(String accessToken, Integer expiresIn, Integer notBeforePolicy, Integer refreshExpiresIn, String refreshToken, String sessionState, String tokenType, String scope) {
    this.accessToken = accessToken;
    this.expiresIn = expiresIn;
    this.notBeforePolicy = notBeforePolicy;
    this.refreshExpiresIn = refreshExpiresIn;
    this.refreshToken = refreshToken;
    this.sessionState = sessionState;
    this.tokenType = tokenType;
    this.scope = scope;
  }

  
  @JsonProperty("access_token")
  public String getAccessToken() { return this.accessToken; }


  
  @JsonProperty("access_token")
  public void setAccessToken(String accessToken) { this.accessToken = accessToken; }


  
  @JsonProperty("expires_in")
  public Integer getExpiresIn() { return this.expiresIn; }


  
  @JsonProperty("expires_in")
  public void setExpiresIn(Integer expiresIn) { this.expiresIn = expiresIn; }


  
  @JsonProperty("not-before-policy")
  public Integer getNotBeforePolicy() { return this.notBeforePolicy; }


  
  @JsonProperty("not-before-policy")
  public void setNotBeforePolicy(Integer notBeforePolicy) { this.notBeforePolicy = notBeforePolicy; }


  
  @JsonProperty("refresh_expires_in")
  public Integer getRefreshExpiresIn() { return this.refreshExpiresIn; }


  
  @JsonProperty("refresh_expires_in")
  public void setRefreshExpiresIn(Integer refreshExpiresIn) { this.refreshExpiresIn = refreshExpiresIn; }


  
  @JsonProperty("refresh_token")
  public String getRefreshToken() { return this.refreshToken; }


  
  @JsonProperty("refresh_token")
  public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }


  
  @JsonProperty("session_state")
  public String getSessionState() { return this.sessionState; }


  
  @JsonProperty("session_state")
  public void setSessionState(String sessionState) { this.sessionState = sessionState; }


  
  @JsonProperty("token_type")
  public String getTokenType() { return this.tokenType; }


  
  @JsonProperty("token_type")
  public void setTokenType(String tokenType) { this.tokenType = tokenType; }


  
  @JsonProperty("scope")
  public String getScope() { return this.scope; }


  
  @JsonProperty("scope")
  public void setScope(String scope) { this.scope = scope; }

  @JsonProperty("code")
  public String getCode() { return this.code; }
  
  @JsonProperty("code")
  public void setCode(String code) { this.code = code; }

  
  public String toString() { return ToStringBuilder.reflectionToString(this); }



  
  public int hashCode() { return (new HashCodeBuilder()).append(this.accessToken).append(this.expiresIn).append(this.notBeforePolicy).append(this.refreshExpiresIn).append(this.refreshToken).append(this.sessionState).append(this.tokenType).append(this.scope).toHashCode(); }


  
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof AccessTokenSchema)) {
      return false;
    }
    AccessTokenSchema rhs = (AccessTokenSchema)other;
    return (new EqualsBuilder()).append(this.accessToken, rhs.accessToken).append(this.expiresIn, rhs.expiresIn).append(this.notBeforePolicy, rhs.notBeforePolicy).append(this.refreshExpiresIn, rhs.refreshExpiresIn).append(this.refreshToken, rhs.refreshToken).append(this.sessionState, rhs.sessionState).append(this.tokenType, rhs.tokenType).append(this.scope, rhs.scope).isEquals();
  }
}
