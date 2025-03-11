
export const GuideIndex = {
    START_GUIDE_STATE1 :{start: 1, end: 1},// 第一阶段引导1 ok btn
    START_GUIDE_STATE2 : {start: 2, end: 10}, // 第二阶段引导2 - 5 好感度x2、AP、coin   
    //START_GUIDE_STATE3 :{start: 6, end: 9}, // 第三阶段引导6 - 9 mxp、shop、work、catDex
    START_GUIDE_STATE4 : {start: 11, end: 11}, // 第四阶段引导10 share
    START_GUIDE_STATE5 : {start: 12, end: 12}, // 第五阶段引导11 聊天记录log 
    START_GUIDE_STATE6 : {start: 13, end: 13}, // 第六阶段引导12 - 12 work 按钮出现
    //START_GUIDE_STATE7 : {start: 13, end: 17}, // 第七阶段引导13 - 17 work
    START_GUIDE_STATE7 : {start: 14, end: 18}, // 第七阶段引导13 - 17 work

}

/**商品pic路径 */
export const ShopPicMap = {
    1: 'shopGoodsPic/shopItem_1',
    2: 'shopGoodsPic/shopItem_2',
    3: 'shopGoodsPic/shopItem_3',
    4: 'shopGoodsPic/shopItem_4',
    5: 'shopGoodsPic/shopItem_5',
    6: 'shopGoodsPic/shopItem_6',
    7: 'shopGoodsPic/shopItem_7',
    8: 'shopGoodsPic/shopItem_8',
}

export const ToySpineMap = {
    10101003 :{
        atlas: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/can_f/can.atlas',
        json: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/can_f/can.json',
        img: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/can_f/can.png'
    },
    10101004 :{
        atlas: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/Cat_strips_f/Cat_strips.atlas',
        json: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/Cat_strips_f/Cat_strips.json',
        img: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/Cat_strips_f/Cat_strips.png'
    },
    10101005 :{
        atlas: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/sponge_f/sponge.atlas',
        json: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/sponge_f/sponge.json',
        img: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/sponge_f/sponge.png'
    },
    10101006 :{
        atlas: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/Natural_Cat_Stick/Natural_Cat_Stick.atlas',
        json: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/Natural_Cat_Stick/Natural_Cat_Stick.json',
        img: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/Natural_Cat_Stick/Natural_Cat_Stick.png'
    },
    10101007 :{
        atlas: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/Scratch_board/Scratch_board.atlas',
        json: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/Scratch_board/Scratch_board.json',
        img: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/Scratch_board/Scratch_board.png'
    },
    10101008 :{
        atlas: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/mouse/mouse.atlas',
        json: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/mouse/mouse.json',
        img: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine_toy/mouse/mouse.png'
    },
}

export const CatSpineMap = {
    100012 :{
        atlas: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/entp/entp.atlas',
        json: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/entp/entp.json',
        img: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/entp/entp.png'
    },
    100010 :{
        atlas: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/estp/estp.atlas',
        json: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/estp/estp.json',
        img: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/estp/estp.png'
    },
    100009 :{
        atlas: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/infp/infp.atlas',
        json: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/infp/infp.json',
        img: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/infp/infp.png'
    },
    100006 :{
        atlas: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/esfp/esfp.atlas',
        json: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/esfp/esfp.json',
        img: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/esfp/esfp.png'
    },
    100004 :{
        atlas: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/isfp/isfp.atlas',
        json: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/isfp/isfp.json',
        img: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/isfp/isfp.png'
    },
    100001 :{
        atlas: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/isfj/isfj.atlas',
        json: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/isfj/isfj.json',
        img: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/isfj/isfj.png'
    },
    100003 :{
        atlas: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/istj/istj.atlas',
        json: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/istj/istj.json',
        img: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/istj/istj.png'
    },
    100008 :{
        atlas: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/istp/istp.atlas',
        json: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/istp/istp.json',
        img: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/istp/istp.png'
    },
    100015 :{
        atlas: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/entj/entj.atlas',
        json: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/entj/entj.json',
        img: 'https://catoss.s3.ap-southeast-1.amazonaws.com/spine/entj/entj.png'
    },
    //
}

export const ToysInfo = {
    10101003: {
        id: 1,
        store_id: 1,
        cName: "猫罐头",
        eName: "Cat Cans",
        cDes: "“我想一天吃八顿！”",
        eDes: "I want to eat eight cans a day.",
        picUrl: "shopGoodsPic/1_shopItem_1",
        nodeName: 'can',
        nodeIndex: 5,
        audio: 'new_toyCan',
    },
    10101004: {
        id: 2,
        store_id: 1,
        cName: "猫条",
        eName: "Cat Treats",
        cDes: "我吸我吸我吸吸吸！！",
        eDes: "I'm addicted! I can't get enough",
        picUrl: "shopGoodsPic/1_shopItem_2",
        nodeName: 'strips',
        nodeIndex: 4,
        audio: 'new_toycatBar',
    },
    10101005: {
        id: 3,
        store_id: 1,
        cName: "沐浴海绵",
        eName: "Bath Sponge",
        cDes: "我的心情取决于你的手法",
        eDes: "My mood depends on your technique",
        picUrl: "shopGoodsPic/1_shopItem_3",
        nodeName: 'sponge',
        nodeIndex: 0,
        audio: 'new_toyBrush',
    },
    10101006: {			
        id: 4,
        store_id: 1,
        cName: "逗猫棒",
        eName: "Cat Wand",
        cDes: "减肥利器？",
        eDes: "Weight Loss Tool？",
        picUrl: "shopGoodsPic/1_shopItem_4",
        nodeName: 'stick',
        nodeIndex: 2,
        audio: 'new_toystick',
    },
    10101007:  {			
        id: 5,
        store_id: 1,
        cName: "猫抓板",
        eName: "Scratch",
        cDes: "这个指甲刀还不错！",
        eDes: "This nail clipper is pretty good！",
        picUrl: "shopGoodsPic/1_shopItem_5",
        nodeName: 'board',
        nodeIndex: 1,
        audio: 'new_toyboard',
    },
    10101008: {			
        id: 6,
        store_id: 1,
        cName: "老鼠玩具",
        eName: "Mouse Toy",
        cDes: "臣服吧，Jerry们",
        eDes: "Submit, Jerrys！",
        picUrl: "shopGoodsPic/1_shopItem_6",
        nodeName: 'mouse',
        nodeIndex: 3,
        audio: 'new_toymouse',
    },
    10101009: {			
        id: 7,
        store_id: 1,
        cName: "猫窝",
        eName: "Cat Bed",
        cDes: "一个窝只能住一只猫，不然要打架！",
        eDes: "Only one cat per bed, or there will be fights!",
        picUrl: "shopGoodsPic/1_shopItem_7",
        nodeName: '',
        nodeIndex: null,
    },
    101010010: {			
        id: 8,
        store_id: 1,
        cName: "猫蛋",
        eName: "Cat Egg",
        cDes: "你想要什么样的小猫呢？",
        eDes: "What kind of kitten do you want?",
        picUrl: "shopGoodsPic/1_shopItem_8",
        nodeName: '',
        nodeIndex: null,
    }
}

export const TouchCatTips = {
    1: 'Meow~meow~',
    2: '',
    3: 'Will you stay with me forever?',
    4: 'Bring me a mouse to practice with!',
};

export function getTouchCatTip(tipNumber: number, catName: string = null) {
    const baseTip = TouchCatTips[tipNumber];
    return tipNumber === 2 ? `${catName} feel very comfortable` : TouchCatTips[tipNumber];
}

export const TouchUnhappyCatTips = {
    1: "I think I'm starting to lose fur!",
    2: "Get your dirty hands off me!",
    3: "I'm tired, touch me later."
}; 



/**互动道具pic 路径 */

export enum ActionType{
    TALK = 1,
    TOY_PLAY = 2,
    TOUCH = 3
}

export const SpineName = {
    action: 'action', //猫咪动作
    idle: 'idle',
    walk: 'walk',
}

export const namePool = [
    'Whiskers',
    'Shadow',
    'Mittens',
    'Luna',
    'Oliver',
    'Simba',
    'Bella',
    'Tiger',
    'Max',
    'Tigger',
    'Smokey',
    'Chloe',
    'Milo',
    'Kitty',
    'Oscar',
    'Jasper',
    'Nala',
    'Felix',
    'Cleo',
    'Ginger',
]

export const occupationPool =[
    'Teacher',
    'Doctor',
    'Nurse',
    'Engineer',
    'Lawyer',
    'Accountant',
    'Developer',
    'Chef',
    'Police',
    'Firefighter',
    'Pharmacist',
    'Electrician',
    'Plumber',
    'Mechanic',
    'Architect',
    'Journalist',
    'Librarian',
    'Pilot',
    'Dentist',
    'Sales',
]

export const termsTxt  = [
    'Last updated June 18, 2024 Hello and welcome! These Terms of Service (“Terms”) are an agreement formed between you (“You”) and Infinity Ground Limited. (“Company”, “Infinity Ground Limited”, “we” or “us”) and cover the website available atInfinity Ground(the “Website”), and the related applications (the “App”) operated on behalf of Infinity Ground Limited, and together with any content, tools, features and functionality offered on or through our Website and App (the “Services”).These Terms govern your access to and use of the Services. Please read them carefully, as they include important information about your legal rights. Upon accessing and/or employing the Services, you unequivocally indicate your acceptance of these Terms and relinquish all rights to any potential disputes, controversies, or claims arising from uncertainties or disagreements pertaining to these Terms. Should you find these Terms unclear or disagreeable, we kindly urge you to refrain from using the Services.For purposes of these Terms, “you” and “your” means you as the user of the Services. If you use the Services on behalf of a company or other entity then “you” includes you and that entity, and you represent and warrant that (a) you are an authorized representative of the entity with the authority to bind the entity to these Terms, and (b) you agree to these Terms on the entity’s behalf.',
    'Note: these Terms contain an arbitration clause and class action waiver. By agreeing to these Terms, you agree (a) to resolve all disputes with us through binding individual arbitration, which means that you waive any right to have those disputes decided by a judge or jury, and (b) that you waive your right to participate in class actions, class arbitrations, or representative actions. You have the right to opt-out of arbitration as explained below.Use of the Services Registration Obligations You may need to submit a registration form to access certain features of the Services. By registering, you agree to provide and maintain true, accurate, current, and complete information about yourself. If you submit any personal information, it will be governed by our Privacy Policy. Users under 13 years old or EU citizens/residents under 16 years old are not authorized to register. You permanently relinquish all rights to dispute any issues arising from the information you provide. Member Account, Password, and Security You are responsible for maintaining the confidentiality of your password and account and for all activities that occur under them. You agree to: (a) immediately notify Infinity Ground Limited of any unauthorized use or breach of security, (b) ensure you exit from your account at the end of each session, and (c) not license, sell, or transfer your account without our prior written approval. Infinity Ground Limited will not be liable for any loss or damage from your failure to comply.Modifications to Site',
    'Infinity Ground Limited reserves the right to modify or discontinue the Services (or any part thereof) with or without notice. You agree that Infinity Ground Limited will not be liable for any modification, suspension, or discontinuance of the Services.General Practices Regarding Use and Storage Infinity Ground Limited may establish general practices and limits concerning the use of the Services, including the maximum period data will be retained and the maximum storage space allotted on our servers. Infinity Ground Limited has no responsibility for the deletion or failure to store any data or content. We reserve the right to terminate inactive accounts and to change these practices and limits at any time, with or without notice.Intellectual Property Rights User Content You represent and warrant that you own all rights to the content you upload or create using the Services ("User Content"). This includes all copyrights and rights of publicity, or you are otherwise permitted to use them. Your User Content must not infringe any rights of any person or entity. By creating or uploading User Content, you retain all rights and grant Infinity Ground Limited a nonexclusive, worldwide, royalty-free, fully paid-up, transferable, sublicensable, perpetual, irrevocable license to use your User Content for any purpose in any form, medium, or technology. Game Modules and Outputs For any automated AI game module ("Game Module") you create or upload, you own all rights to the Game Module and any outputs it generates ("Outputs"). You grant Infinity Ground Limited and any user who elicits Outputs from your Game Module a nonexclusive, worldwide, royalty-free, fully paid-up, transferable, sublicensable, perpetual, irrevocable license to use the Game Module and Outputs. Users interacting with a Game Module own the rights to the Outputs they elicit and grant Infinity Ground Limited and the creator(s) of the Game Module a similar license to use the Outputs.',
    'Website or Services Content, Software, and Trademarks The Website or Services may contain content or features ("Site Content") protected by intellectual property rights. The ownership and operation of the Website or Services rest with Infinity Ground Limited and its affiliates, licensors, and service providers ("Providers"). You agree not to engage in data mining, robots, scraping, or similar automated data gathering methods. Copying, altering, distributing, selling, leasing, or sublicensing our software or services is prohibited. If blocked from accessing the Website or Services, you agree not to circumvent such blocking. Any use of the Website or Services, or the Site Content, other than as authorized is prohibited. All rights not expressly granted are reserved by Infinity Ground Limited. The Infinity Ground Limited name and logos are trademarks of Infinity Ground Limited. Other trademarks used via the Website or Services may be owned by third parties. Nothing in these Terms grants any license or right to use any Infinity Ground Limited trademarks without prior written permission. All goodwill from the use of Infinity Ground Limited trademarks will benefit us exclusively.Contact Us If you have any questions about our Services, or to report any violations of these Terms or our Acceptable Use Policies, please contact us at Hello@infinityg.ai.'
]