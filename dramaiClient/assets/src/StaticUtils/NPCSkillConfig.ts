export interface NPCSkillInfo {
    skills: string[];
    MBTI: number[];  // [EI, SN, TF, JP] as floating point numbers between 0 and 1
    itemIds: string[];  // 对应NPC拥有的物品ID列表
}

export const NPCSkillMap: { [key: string]: NPCSkillInfo } = {
    "10009": {
        skills: ["#VisionaryThinking", "#DiplomaticBridging"],
        MBTI: [0.6, 0.85, 0.4, 0.3],
        itemIds: ["1000901", "1000902", "1000903", "1000904"]
    },
    "10006": {
        skills: ["#LogicalDebate", "#TransparentLeadership"],
        MBTI: [0.4, 0.9, 0.15, 0.6],
        itemIds: ["1000601", "1000602", "1000603"]
    },
    "10014": {
        skills: ["#MarketAnalysis", "#HighPressureCoaching"],
        MBTI: [0.85, 0.8, 0.9, 0.7],
        itemIds: ["1001401", "1001402", "1001403", "1001404"]
    },
    "10015": {
        skills: ["#IdolPerformance", "#SecretSongwriting"],
        MBTI: [0.3, 0.7, 0.25, 0.55],
        itemIds: ["1001501", "1001502", "1001503", "1001504"]
    },
    "10016": {
        skills: ["#ArtisticStorytelling", "#CreativeCooking"],
        MBTI: [0.8, 0.95, 0.2, 0.1],
        itemIds: ["1001601", "1001602", "1001603", "1001604"]
    },
    "10017": {
        skills: ["#BakedComfort", "#EmpatheticListener"],
        MBTI: [0.3, 0.85, 0.9, 0.6],
        itemIds: ["1001701", "1001702", "1001703"]
    },
    "10018": {
        skills: ["#ConflictMediator", "#BalancedEmpathy"],
        MBTI: [0.4, 0.6, 0.55, 0.9],
        itemIds: ["1001801", "1001802", "1001803"]
    },
    "10019": {
        skills: ["#UnfilteredStoryteller", "#GenuineConnector"],
        MBTI: [0.95, 0.7, 0.5, 0.3],
        itemIds: ["1001901", "1001902", "1001903"]
    },
    "10020": {
        skills: ["#NatureWhisperer", "#QuietSupport"],
        MBTI: [0.9, 1.0, 0.7, 0.8],
        itemIds: ["1002001", "1002002", "1002003"]
    },
    "10021": {
        skills: ["#TrainingMotivator", "#ResilientSpirit"],
        MBTI: [0.7, 0.3, 0.8, 0.9],
        itemIds: ["1002101", "1002102", "1002103", "1002104"]
    },
    "10012": {
        skills: ["#PersuasiveSpeech", "#ConflictStirring"],
        MBTI: [0.9, 0.3, 0.75, 0.4],
        itemIds: ["1001201", "1001202", "1001203"]
    },
    "10022": {
        skills: ["#VisionaryLeadership", "#Technologist"],
        MBTI: [0.5, 0.65, 0.8, 0.7],
        itemIds: ["1002201", "1002202", "1002203"]
    }   
}; 