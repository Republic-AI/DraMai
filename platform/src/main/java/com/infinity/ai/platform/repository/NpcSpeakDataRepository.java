package com.infinity.ai.platform.repository;


import com.infinity.ai.platform.entity.NpcChatData;
import com.infinity.ai.platform.entity.NpcSpeakData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NpcSpeakDataRepository extends JpaRepository<NpcSpeakData, Long> {
}
