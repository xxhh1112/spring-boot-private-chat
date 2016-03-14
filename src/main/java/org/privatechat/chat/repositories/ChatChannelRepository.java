package org.privatechat.chat.repositories;

import org.privatechat.chat.models.ChatChannel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface ChatChannelRepository extends CrudRepository<ChatChannel, String> {
  @Query(" FROM"
      + "    ChatChannel c"
      + "  WHERE"
      + "    c.userIdOne IN (:userIdOne, :userIdTwo) "
      + "  AND"
      + "    c.userIdTwo IN (:userIdOne, :userIdTwo)")
  public List<ChatChannel> findExistingChannel(
      @Param("userIdOne") long userIdOne, @Param("userIdTwo") long userIdTwo);
  
  @Query(" FROM"
      + "    ChatChannel c"
      + "  WHERE"
      + "    c.uuid IS :uuid")
  public ChatChannel getChannelDetails(@Param("uuid") String uuid);
  
  @Query(" SELECT"
      + "    uuid"
      + "  FROM"
      + "    ChatChannel c"
      + "  WHERE"
      + "    c.userIdOne IN (:userIdOne, :userIdTwo)"
      + "  AND"
      + "    c.userIdTwo IN (:userIdOne, :userIdTwo)")
  public String getChannelUuid(
      @Param("userIdOne") long userIdOne, @Param("userIdTwo") long userIdTwo);
}