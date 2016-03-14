package org.privatechat.chat.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name="chatMessage")
public class ChatMessage {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @NotNull
  private long authorUserId;

  @NotNull
  private long recipientUserId;

  @NotNull
  private Date timeSent;

  @NotNull
  private String contents;

  public ChatMessage() {}

  public ChatMessage(long authorUserId, long recipientUserId, String contents) {
    this.authorUserId = authorUserId;
    this.recipientUserId = recipientUserId;
    this.contents = contents;
    this.timeSent = new Date();
  }
  
  public long getId() {
    return this.id;
  }
  
  public long getAuthorUserId() {
    return this.authorUserId;
  }
  
  public long getRecipientUserId() {
    return this.recipientUserId;
  }
  
  public Date getTimeSent() {
    return this.timeSent;
  }
  
  public String getContents() {
    return this.contents;
  }
}