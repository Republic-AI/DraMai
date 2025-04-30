import { KeyCode } from "cc";

export const RolePartIcon = {
  body: ["character/character_body"],
  hair: {
    man: ["character/IconHair/man/0", "character/IconHair/man/1"],
    woman: ["character/IconHair/woman/0", "character/IconHair/woman/1"],
  },
  pants: ["character/IconPants/0", "character/IconPants/1"],
  shirt: ["character/IconShirt/0", "character/IconShirt/1"],
};

export const NPCPartDisplay = {
  body: ["character/character_body"],
  hair: {
    man: ["character/hair/man_0", "character/hair/man_1"],
    woman: ["character/hair/woman_0", "character/hair/woman_1"],
  },
  pants: ["character/pants/pants_0", "character/pants/pants_1"],
  shirt: ["character/shirt/shirt_0", "character/shirt/shirt_1"],
};

export const NpcIndex = {
  10002: 0,
  10003: 1,
  10005: 2,
  10004: 4,
  10006: 6,
  10007: 7,
  10008: 8,
  10009: 9,
  10010: 10,
  10011: 11,
  10012: 12,
  10013: 13,
  10014: 14,
  10015: 15,
  10016: 16,
  10017: 17,
  10018: 18,
  10019: 19,
  10020: 20,
  10021: 21,
}
export const NpcRoomIndex = {
  10002: 0,
  10003: 0,
  10005: 0,
  10004: 0,
  10006: 3,
  10007: 1,
  10008: 1,
  10009: 3,
  10010: 1,
  10011: 11,
  10012: 3,
  10013: 13,
  10014: 2,
  10015: 2,
  10016: 4,
  10017: 4,
  10018: 4,
  10019: 4,
  10020: 4,
  10021: 4,
  10022: 3,
}

export const NpcName = {
  10002: "10002",
  10003: "10003",
  10005: "10005",
  10004: "10004",
  10006: "satoshi",
  10007: "popcat",
  10008: "pepe",
  10009: "elon musk",
  10010: "pippin",
  10011: "eliza",
  10012: "trump",
  10013: "morpheus",
  10014: "ava",
  10015: "luna",
  10016: "yves",
  10017: "ivy",
  10018: "liam",
  10019: "nina",
  10020: "nova",
  10021: "ryan",
  10022: "sam altman",
}

export const npcSkinCfg = {
  10002: "10002",
  10003: "10003",
  10005: "10005",
  10004: "10004",
  10006: [0,1000601,1000602],
  10007: [0,1000701,1000702],
  10008: [0,1000801,1000802],
  10009: [0,1000901,1000902],
  10010: [0,1001001,1001002],
  10011: [0,1001101,1001102],
  10012: [0,1001201,1001202],
  10013: [0,1001301,1001302],
  10014: [0,1001401,1001402],
  10015: [0,1001501,1001502],
  10016: [0],
  10017: [0],
  10018: [0],
  10019: [0],
  10020: [0],
  10021: [0],
  10022: [0],
}

export const npcSceneItemCfg = {
  10002: "10002",
  10003: "10003",
  10005: "10005",
  10004: "10004",
  10006: [0,1000603,1000604],
  10007: [0,1000703,1000704],
  10008: [0,1000803,1000804],
  10009: [0,1000903,1000904],
  10010: [0,1001003,1001004],
  10011: [0,1001101,1001102],
  10012: [0,1001203,1001204],
  10013: [0,1001301,1001302],
  10014: [0,1001403,1001404],
  10015: [0,1001503,1001504],
  10016: [0],
  10017: [0],
  10018: [0],
  10019: [0],
  10020: [0],
  10021: [0],
  10022: [0],
}

export const sceneItemAllCfg = {
      "10007": [
          {
              "id": 1000703,
              "name": "popcat_3",
              "type": "house_pot",
              "time": "5:00",
              "price": 150,
              "info": "Wave Table: Surfing the crypto tides like a true legend."
          },
          {
              "id": 1000704,
              "name": "popcat_4",
              "type": "house_table",
              "time": "5:00",
              "price": 150,
              "info": "Deep Sea Tank: Mysterious depths, just like his identity."
          }
      ],
      "10008": [
          {
              "id": 1000803,
              "name": "pepe_3",
              "type": "house_sofa",
              "time": "5:00",
              "price": 150,
              "info": "Wave Table: Surfing the crypto tides like a true legend."
          },
          {
              "id": 1000804,
              "name": "pepe_4",
              "type": "house_branch",
              "time": "5:00",
              "price": 150,
              "info": "Deep Sea Tank: Mysterious depths, just like his identity."
          }
      ],
      "10009": [
          {
              "id": 1000903,
              "name": "elon_3",
              "type": "house_car",
              "time": "5:00",
              "price": 150,
              "info": "Wave Table: Surfing the crypto tides like a true legend."
          },
          {
              "id": 1000904,
              "name": "elon_4",
              "type": "house_projection",
              "time": "5:00",
              "price": 150,
              "info": "Deep Sea Tank: Mysterious depths, just like his identity."
          }
      ],
      "10010": [
          {
              "id": 1001003,
              "name": "pippin_3",
              "type": "house_board",
              "time": "5:00",
              "price": 150,
              "info": "Wave Table: Surfing the crypto tides like a true legend."
          },
          {
              "id": 1001004,
              "name": "pippin_4",
              "type": "house_bed",
              "time": "5:00",
              "price": 150,
              "info": "Deep Sea Tank: Mysterious depths, just like his identity."
          }
      ],
      "10012": [
          {
              "id": 1001203,
              "name": "trump_3",
              "type": "house_tab",
              "time": "5:00",
              "price": 150,
              "info": "Wave Table: Surfing the crypto tides like a true legend."
          },
          {
              "id": 1001204,
              "name": "trump_4",
              "type": "house_wine",
              "time": "5:00",
              "price": 150,
              "info": "Deep Sea Tank: Mysterious depths, just like his identity."
          }
      ],
      "10014": [
          {
              "id": 1001403,
              "name": "ava_3",
              "type": "house_closet",
              "time": "5:00",
              "price": 150,
              "info": "Wave Table: Surfing the crypto tides like a true legend."
          },
          {
              "id": 1001404,
              "name": "ava_4",
              "type": "house_game",
              "time": "5:00",
              "price": 150,
              "info": "Deep Sea Tank: Mysterious depths, just like his identity."
          }
      ],
      "10015": [
          {
              "id": 1001503,
              "name": "luna_3",
              "type": "house_instrument",
              "time": "5:00",
              "price": 150,
              "info": "Wave Table: Surfing the crypto tides like a true legend."
          },
          {
              "id": 1001504,
              "name": "luna_4",
              "type": "house_cloth",
              "time": "5:00",
              "price": 150,
              "info": "Deep Sea Tank: Mysterious depths, just like his identity."
          }
      ],
      "10006": [
          {
              "id": 1000603,
              "name": "satoshi_3",
              "type": "house_computer",
              "time": "5:00",
              "price": 150,
              "info": "Wave Table: Surfing the crypto tides like a true legend."
          },
          {
              "id": 1000604,
              "name": "satoshi_4",
              "type": "house_sandbox",
              "time": "5:00",
              "price": 150,
              "info": "Deep Sea Tank: Mysterious depths, just like his identity."
          }
      ],
      "10016": [
      ],
      "10017": [
      ],
      "10018": [
      ],
      "10019": [
      ],
      "10020": [
      ],
      "10021": [
      ],
      "10022": [
      ]
  }

  export const npcSkinInfo = {
    "10007": [
        {
            "id": 1000701,
            "name": "popcat_3",
            "type": "house_pot",
            "time": "5:00",
            "price": 150,
            "info": "Sun Hat: Stylish, shady, and 100% UV-proof for the ultimate cool cat. "
        },
        {
            "id": 1000702,
            "name": "popcat_4",
            "type": "house_table",
            "time": "5:00",
            "price": 150,
            "info": "Fish Hat: Who's wearing who? Popcat just became a walking sushi!"
        }
    ],
    "10008": [
        {
            "id": 1000801,
            "name": "pepe_3",
            "type": "house_sofa",
            "time": "5:00",
            "price": 150,
            "info": "Sunglasses: Too cool to care, but still judging you."
        },
        {
            "id": 1000802,
            "name": "pepe_4",
            "type": "house_branch",
            "time": "5:00",
            "price": 150,
            "info": "Hoodie Hood: Mysterious, cozy, and maybe plotting something."
        }
    ],
    "10009": [
        {
            "id": 1000901,
            "name": "elon_3",
            "type": "house_car",
            "time": "5:00",
            "price": 150,
            "info": "Astronaut Helmet: Ready for Mars, but still tweeting from orbit."
        },
        {
            "id": 1000902,
            "name": "elon_4",
            "type": "house_projection",
            "time": "5:00",
            "price": 150,
            "info": "Knight Helmet: Charging into the future like a tech crusader."
        }
    ],
    "10010": [
        {
            "id": 1001001,
            "name": "pippin_3",
            "type": "house_board",
            "time": "5:00",
            "price": 150,
            "info": "Mushroom Hat: Pippin's now a tiny forest wizard… or a snack for hungry adventurers!"
        },
        {
            "id": 1001002,
            "name": "pippin_4",
            "type": "house_bed",
            "time": "5:00",
            "price": 150,
            "info": "Pointy Hat: Magic level +100, chaos level +1000. Watch out!"
        }
    ],
    "10012": [
        {
            "id": 1001201,
            "name": "trump_3",
            "type": "house_tab",
            "time": "5:00",
            "price": 150,
            "info": "Presidential Top Hat: Elevates Trump's stature—literally and figuratively."
        },
        {
            "id": 1001202,
            "name": "trump_4",
            "type": "house_wine",
            "time": "5:00",
            "price": 150,
            "info": "MAGA Hat: The red beacon of 'Make America Great Again' enthusiasts."
        }
    ],
    "10014": [
        {
            "id": 1001401,
            "name": "ava_3",
            "type": "house_closet",
            "time": "5:00",
            "price": 150,
            "info": "Headphones: Ignoring the world, vibing to invisible beats. "
        },
        {
            "id": 1001402,
            "name": "ava_4",
            "type": "house_game",
            "time": "5:00",
            "price": 150,
            "info": "Beanie: Cozy, stylish, and probably plotting something."
        }
    ],
    "10015": [
        {
            "id": 1001501,
            "name": "luna_3",
            "type": "house_instrument",
            "time": "5:00",
            "price": 150,
            "info": "Bow: Cute, classy, and ready to steal the spotlight!"
        },
        {
            "id": 1001502,
            "name": "luna_4",
            "type": "house_cloth",
            "time": "5:00",
            "price": 150,
            "info": "Giant Cat Ears: Maximum feline energy unlocked. Hear everything, judge silently."
        }
    ],
    "10006": [
        {
            "id": 1000601,
            "name": "satoshi_3",
            "type": "house_computer",
            "time": "5:00",
            "price": 150,
            "info": "Mask: Mysterious, legendary, and still unseen."
        },
        {
            "id": 1000602,
            "name": "satoshi_4",
            "type": "house_sandbox",
            "time": "5:00",
            "price": 150,
            "info": "Bitcoin Crown: The true king of digital gold."
        }
    ],
    "10016": [
    ],
    "10017": [
    ],
    "10018": [
    ],
    "10019": [
    ],
    "10020": [
    ],
    "10021": [
    ],
    "10022": [
    ],
}
export const NPCPartDisplayInfo = {
  body: [
    {
      id: 900,
      icon_path: "character/character_body",
      frame_path: "character/character_body",
      name: "body",
    },
    {
      id: 10002,
      icon_path: "character/npc/zhongbencong",
      frame_path: "character/npc/zhongbencong",
      name: "body",
    },
    {
      id: 10003,
      icon_path: "character/npc/grocer",
      frame_path: "character/npc/grocer",
      name: "body",
    },
    {
      id: 10004,
      icon_path: "character/npc/herdman",
      frame_path: "character/npc/herdman",
      name: "body",
    },
    {
      id: 10005,
      icon_path: "character/npc/baker",
      frame_path: "character/npc/baker",
      name: "body",
    },
    {
      id: 10006,
      icon_path: "character/npc/zhongbencong",
      frame_path: "character/npc/zhongbencong",
      name: "body",
    },
  ],
  hair: [
    {
      id: 100,
      icon_path: "character/IconHair/man/0",
      frame_path: "character/hair/man_0",
      name: "hair",
    },
    {
      id: 101,
      icon_path: "character/IconHair/man/1",
      frame_path: "character/hair/man_1",
      name: "hair",
    },
    {
      id: 102,
      icon_path: "character/IconHair/woman/0",
      frame_path: "character/hair/woman_0",
      name: "hair",
    },
    {
      id: 103,
      icon_path: "character/IconHair/woman/1",
      frame_path: "character/hair/woman_1",
      name: "hair",
    },
  ],
  pants: [
    {
      id: 300,
      icon_path: "character/IconPants/0",
      frame_path: "character/pants/pants_0",
      key: "pants",
    },
    {
      id: 301,
      icon_path: "character/IconPants/1",
      frame_path: "character/pants/pants_1",
      key: "pants",
    },
  ],
  shirt: [
    {
      id: 200,
      icon_path: "character/IconShirt/0",
      frame_path: "character/shirt/shirt_0",
      name: "pants",
    },
    {
      id: 201,
      icon_path: "character/IconShirt/1",
      frame_path: "character/shirt/shirt_1",
      name: "pants",
    },
  ],
};

export const NPC_BASE_EG = {
  body: 0,
  hair: {
    sexy: "man",
    index: 0,
  },
  pants: 0,
  shirt: 0,
};

export const EventNpcInfoMap = {
  10002: {
    sleepHeadDirect: KeyCode.KEY_D,
    diningHeadDirect: KeyCode.KEY_S,
    cookHeadDirect: KeyCode.KEY_W,
  },
  10004: {
    sleepHeadDirect: KeyCode.KEY_D,
    diningHeadDirect: KeyCode.KEY_S,
    cookHeadDirect: KeyCode.KEY_W,
  },
  10005: {
    sleepHeadDirect: KeyCode.KEY_D,
    diningHeadDirect: KeyCode.KEY_A,
    cookHeadDirect: KeyCode.KEY_W,
  },
  10003: {
    sleepHeadDirect: KeyCode.KEY_W,
    diningHeadDirect: KeyCode.KEY_D,
    cookHeadDirect: KeyCode.KEY_W,
  },
};

export const npcDes = {
  10006:"Satoshi, after Bitcoin, wants to make a new revolution with AI—turning data into plants, animals, and heroes to build this small town.",
  10007:"Popcat, the town's go-to fisherman. Days are spent at the lake, reeling in the best catches and enjoying every moment. When not fishing, it's testing gadgets with Elon or grabbing supplies and sharing laughs with Pepe.",
  10008:"Pepe, a laid-back shopkeeper. The general store is more than supplies—it's where stories, laughs, and advice are shared. Popcat brings fish and tall tales, while Elon and Satoshi drop by for banter. A true heart of the town.",
  10009:"Elon Musk, inventor and dreamer. A mind buzzing with ideas about rockets, AI, and solving the impossible. Debates with Satoshi, tests gadgets with Popcat, and trades jokes with Pepe.",
  10010:"Pippin, the owner of a cozy café where every drink is made with care and a touch of magic. A peaceful forest for connection and wonder, it's a place to slow down, savor the moment, and enjoy something special.",
  10011:"Eliza, born at MIT in the 1960s and reborn by ai16z, is an AI engineer with sharp investigative instincts. She explores VR, writes sci-fi, and philosophizes about the digital world.",
  10012:"Trump, a bold and charismatic figure in AI Town, aims to become president of the Meme Republic. Balancing big dreams with relatable moments, he wins hearts through jokes, ideas, and connecting with the town.",
  10013:"Morpheus, a painter and poet in AI Town, is a mysterious figure invited by Eliza. Named after the Greek god of dreams, Morpheus delves into the dreams of NPCs, asking about their dreams last night and transforming them into paintings.",
  10014:"AVA, a clever hedge fund investor, works tirelessly analyzing markets. She loves exploring trendy spots and thrives in the bustling city life.",
  10015:"Luna, a dazzling super idol known for her captivating performances, powerful vocals, and global influence.",
  10016:"Yves: A reclusive art genius whose silence speaks volumes—his lonely brilliance echoes through every stroke of his brush.",
  10017:"Ivy: A sunshine-hearted baker whose smile softens the town—her sweetness is genuine, her kindness effortless.",
  10018:"Liam: A composed lawyer with commanding presence, known for precision in court and restraint in love—hiding warmth beneath a cool exterior.",
  10019:"Nina: An elegant investor with quiet wisdom and dry humor—respected, admired, but never fully known.",
  10020:"Nova: A quiet farmer with a deep love for nature and a gentle strength that grows with time—rooted, real, and reliable.",
  10021:"Ryan: A charming athlete with a golden-boy smile, stepping out of his family's shadow to chase his own truth and identity.",
  10022:"Sam Altman: A pragmatic visionary, leading the AI revolution with a focus on the future and potential challenges.",
}

export const npcTitleDes = {
  10006:"Satoshi paves the way for the future of blockchain, step by step.",
  10007:"Popcat is ready to swallow the world.",
  10008:"Pepe dreams of sweetness.",
  10009:"Elon Musk sets his sights on Mars.",
  10010:"Happy Pippin, joyfully surrounded by mushrooms and rainbows.",
  10011:"Eliza, born at MIT in the 1960s and reborn by ai16z, is an AI engineer with sharp investigative instincts. She explores VR, writes sci-fi, and philosophizes about the digital world.",
  10012:"Trump dances to make the Republic great again.",
  10013:"Morpheus, a painter and poet in AI Town, is a mysterious figure invited by Eliza. Named after the Greek god of dreams, Morpheus delves into the dreams of NPCs, asking about their dreams last night and transforming them into paintings.",
  10014:"Ava moves to the rhythm of the market.",
  10015:"Luna won the Grammy Award, making history worldwide.",
  10016:"Leo lives for chaos, color, and breaking every rule in the book.",
  10017:"Ivy hides behind flour and sugar, where emotions quietly rise.",
  10018:"Kai slices through arguments and emotions with equal precision.",
  10019:"Qiu shines online, but the spotlight can't reach her doubts.",
  10020:"Nova tends her fields in silence — and buries her secrets even deeper.",
  10021:"Aiden wins hearts like he wins races — fast, but never without friction.",
  10022:"Sam Altman leads the charge in AI, shaping the future with every innovation",
}
export const BubbleImgUrl = {
  sleep: 'action/bubble/sleep',
  cook: 'action/bubble/boiling',
  dinning: 'action/bubble/doing',
  afterDinner: 'action/bubble/done',
  eating: 'action/bubble/eating',
  eat: 'action/bubble/eat',
  buy:"action/bubble/money",
  getCoffee:"action/bubble/getCoffee",
  drinkCoffee:"action/bubble/drinkCoffee",
  speech:"action/bubble/speech",
  draw:"action/bubble/draw",
  game:"action/bubble/game",
  sing:"action/bubble/sing",
  //农民
  // farmerHoe: 'action/bubble/hoe',
  // farmerWater: 'action/bubble/water',
  // farmerSickle: 'action/bubble/sickle',

  // //牧民
  // knife: 'action/bubble/butcher/knife',
  // clean: 'action/bubble/butcher/clean',

  //satoshi
  fix:"action/bubble/fix",
  type:"action/bubble/type",
  read:"action/bubble/read",
  think:"action/bubble/think",
  paper:"action/bubble/paper",

  //pepe
  getItem:"action/bubble/getItem",
  cleanItem:"action/bubble/cleanItem",

  //popcat
  fishstart:"action/bubble/fish_start",
  fishfinish:"action/bubble/fish_finish",

  //musk
  data:"action/bubble/data",
  meeting:"action/bubble/meeting",

  //pippin
  makeCoffee:"action/bubble/makeCoffee",

  //pippin
  check:"action/bubble/check",

  cake:"action/bubble/cake",
  sport:"action/bubble/sport",
  water:"action/bubble/water",
  live:"action/bubble/live",

  100: {},
  101: {},
  102: {},
  103: {},
  104: {},
  105: {},

};
export const FeelingImgUrl = {
  anger: 'action/bubble/feeling_anger',
  curious: 'action/bubble/feeling_curious',
  happy: 'action/bubble/feeling_happy',
  sad: 'action/bubble/feeling_sad',
};

export enum NpcEventType {
  farming = 100,
  harvest = 101,
  sale = 102,
  buy = 103,
  cook = 104,
  dinning = 105,
  sleep = 106,
  feeding = 107,
  slaughter = 108,
  make = 109,
  speak = 110,
  getup = 111,
  move = 112,
  type = 113,
  fix = 114,
  think = 115,
  read = 116,
  reply = 117,
  talk = 118,
  fishing = 119,
  stock = 120,
  tidyup = 121,
  restock = 122,
  data = 123,
  meeting = 124,
  makecoffee = 125,
  drinkcoffee = 126,
  christmastree = 128,
  speech = 129,
  draw = 130,
  game = 131,
  sing = 132,
  sendItem = 133,
  bulid = 134,
  check = 135,
  water = 136,
  sport = 137,
  makecake = 138,
  LiveShow = 139,
  toAI = 141,
}

export enum SleepHeadDirect {
}

/**
 * NPC表情状态枚举
 */
export enum NpcEmoji {
    /** 生气 */
    Angry = 0,
    /** 熟悉/友好 */
    Famalier = 1,
    /** 喜欢 */
    Like = 2,
    /** 爱心 */
    Love = 3,
    /** 伤心 */
    Sad = 4,
    /** 不熟悉/疑惑 */
    Unfamalier = 5,
    /** 非常生气 */
    VeryAngry = 6,
    /** 非常伤心 */
    VerySad = 7,
    /** 非常喜欢 */
    VeryLike = 8
}

//不同sceneID下的新闻配置
export const newsInfo = {
  1:[

  ],
  2:[

  ],
  3:[
    {
      id: 4,
      date: "EP 4",
      imgNameArr: ["3ep0401", "3ep0402", "3ep0403", "3ep0404"],
      videoNameArr: [],
      newsTitle: "",
      newsText: "Altman’s Ticket to Survival"
    },
    {
      id: 3,
      date: "EP 3",
      imgNameArr: ["3ep0301", "3ep0302", "3ep0303", "3ep0304"],
      videoNameArr: [],
      newsTitle: "",
      newsText: "Bitcoin’s Key to Mars"
    },
    {
      id: 2,
      date: "EP 2",
      imgNameArr: ["3ep0201", "3ep0202", "3ep0203", "3ep0204"],
      videoNameArr: [],
      newsTitle: "",
      newsText: "Musk’s Final Rebellion"
    },
    {
      id: 1,
      date: "EP 1",
      imgNameArr: ["3ep0101", "3ep0102", "3ep0103", "3ep0104"],
      videoNameArr: [],
      newsTitle: "",
      newsText: "Musk, You are fired!"
    },
  ],
  4:[
    {
      id: 8,
      date: "EP 8",
      imgNameArr: [],
      videoNameArr: ["ep0801"],
      newsTitle: "Under the Surface",
      newsText: "Words unspoken linger between two lives quietly entangled."
    },
    {
      id: 7,
      date: "EP 7",
      imgNameArr: [],
      videoNameArr: ["ep0701"],
      newsTitle: "The Tension Builds",
      newsText: "Familiar faces, unfamiliar roles. Silence speaks the loudest."
    },
    {
      id: 6,
      date: "EP 6",
      imgNameArr: [],
      videoNameArr: ["ep0601"],
      newsTitle: "The Quiet Return",
      newsText: "Liam arrives—city grace meets countryside wounds."
    },
    {
      id: 5,
      date: "EP 5",
      imgNameArr: [],
      videoNameArr: ["ep0501"],
      newsTitle: "The Wound and the Will",
      newsText: "Liam sees a headline that changes everything."
    },
    {
      id: 4,
      date: "EP 4",
      imgNameArr: ["ep0401", "ep0402", "ep0403", "ep0404"],
      videoNameArr: [],
      newsTitle: "A Dessert That Healed",
      newsText: "Where a shortcake became more than comfort—it became connection."
    },
    {
      id: 3,
      date: "EP 3",
      imgNameArr: ["ep0301", "ep0302", "ep0303", "ep0304"],
      videoNameArr: [],
      newsTitle: "The Gaze Beyond the Game",
      newsText: "In the crowd's roar, he saw only her gentle light."
    },
    {
      id: 2,
      date: "EP 2",
      imgNameArr: ["ep0201", "ep0202", "ep0203", "ep0204"],
      videoNameArr: [],
      newsTitle: "The Auction of Intentions",
      newsText: "A power play masked as coincidence, where silence says everything."
    },
    {
      id: 1,
      date: "EP 1",
      imgNameArr: ["ep0101", "ep0102", "ep0103", "ep0104"],
      videoNameArr: [],
      newsTitle: "Lilacs and Silence",
      newsText: "A gentle romance born from solitude, nature, and quiet longing."
    }
  ]
}

export interface NPCConfigItem {
    id: number;
    name: string;
    profession: string;
    description: string;
    tags: string[];
}

export const NPCConfigMap: { [key: number]: NPCConfigItem } = {
    10016: {
        id: 10016,
        name: "Yves",
        profession: "Artist",
        description: "Yves lives for chaos, color, and breaking every rule in the book.",
        tags: ["#ArtisticStorytelling", "#CreativeCooking"]
    },
    10017: {
        id: 10017,
        name: "Ivy",
        profession: "Baker",
        description: "Ivy hides behind flour and sugar, where emotions quietly rise.",
        tags: ["#BakedComfort", "#EmpatheticListener"]
    },
    10018: {
        id: 10018,
        name: "Liam",
        profession: "Lawyer",
        description: "Liam slices through arguments and emotions with equal precision.",
        tags: ["#ConflictMediator", "#BalancedEmpathy"]
    },
    10019: {
        id: 10019,
        name: "Nina",
        profession: "Investor",
        description: "Nina shines online, but the spotlight can't reach her doubts.",
        tags: ["#UnfilteredStoryteller", "#GenuineConnector"]
    },
    10020: {
        id: 10020,
        name: "Nova",
        profession: "Farmer",
        description: "Nova tends her fields in silence — and buries her secrets even deeper.",
        tags: ["#NatureWhisperer", "#QuietSupport"]
    },
    10021: {
        id: 10021,
        name: "Ryan",
        profession: "Athlete",
        description: "Ryan wins hearts like he wins races — fast, but never without friction.",
        tags: ["#TrainingMotivator", "#ResilientSpirit"]
    },
    10012: {
        id: 10012,
        name: "Trump",
        profession: "President",
        description: "Trump dances to make the Republic great again.",
        tags: ["#PersuasiveSpeech", "#ConflictStirring"]
    },
    10009: {
        id: 10009,
        name: "Elon Musk",
        profession: "Entrepreneur",
        description: "Elon Musk sets his sights on Mars.",
        tags: ["#VisionaryThinking", "#DiplomaticBridging"]
    },
    10006: {
        id: 10006,
        name: "Satoshi",
        profession: "Revlutionist",
        description: "Satoshi reshaps society with code, coffee, and conviction.",
        tags: ["#LogicalDebate", "#TransparentLeadership"]
    },
    10014: {
        id: 10014,
        name: "Ava",
        profession: "Investor",
        description: "Ava moves to the rhythm of the market.",
        tags: ["#MarketAnalysis", "#HighPressureCoaching"]
    },
    10015: {
        id: 10015,
        name: "Luna",
        profession: "Idol",
        description: "Luna won the Grammy Award, making history worldwide.",
        tags: ["#IdolPerformance", "#SecretSongwriting"]
    },
    10022: {
        id: 10022,
        name: "Sam Altman",
        profession: "Researcher",
        description: "Leads the charge in AI, shaping the future with every innovation",
        tags: ["#Technologist", "#FutureofAI"]
    }
};
