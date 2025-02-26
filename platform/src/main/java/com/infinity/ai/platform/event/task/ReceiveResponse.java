package com.infinity.ai.platform.event.task;

import com.infinity.ai.domain.model.Goods;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class ReceiveResponse {
    List<Goods> goodsList;
    List<Integer> taskIdList;
    Set<Integer> catIds;
}
