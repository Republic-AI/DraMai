package com.infinity.ai.platform.repository;


import com.infinity.ai.platform.entity.NpcChatData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NpcChatDataRepository extends JpaRepository<NpcChatData, Long> {

    Page<NpcChatData> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<NpcChatData> findByRoomId(int roomId, Pageable pageable);
}
