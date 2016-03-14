package org.privatechat.chat.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name="chatChannel")
public class ChatChannel {

  @Id
  @NotNull
  private String uuid;

  @NotNull
  private long userIdOne;

  @NotNull
  private long userIdTwo;

  public ChatChannel(long userIdOne, long userIdTwo) {
    this.uuid = UUID.randomUUID().toString();
    this.userIdOne = userIdOne;
    this.userIdTwo = userIdTwo;
  }

  public ChatChannel() {

  }

  public void setUserIdOne(long userIdOne) {
    this.userIdOne = userIdOne;
  }

  public void setUserIdTwo(long userIdTwo) {
    this.userIdTwo = userIdTwo;
  }

  public long getUserIdOne() {
    return this.userIdOne;
  }

  public long getUserIdTwo() {
    return this.userIdTwo;
  }

  public String getUuid() {
    return this.uuid;
  }
}