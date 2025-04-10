package com.infinity.ai.platform.repository;

import com.infinity.ai.platform.entity.NpcChatData;
import com.infinity.ai.platform.entity.NpcPlayerChatData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NpcPlayerChatDataRepository extends JpaRepository<NpcPlayerChatData, Long> {

    Page<NpcChatData> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT u FROM NpcPlayerChatData u WHERE u.npcId = :ids and u.playerId = :playerId order by created_at desc")
    List<NpcPlayerChatData> findByNpcIdAndPlayerId(@Param("ids") Long ids, @Param("playerId") Long playerId, Pageable pageable);
}
