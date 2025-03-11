import  Request from "../utils/Request";
// const currentURL = window.location.href;
let baseUrl = `https://api.baochuangames.com`;
// const baseUrlyh = `http://api2.test.nesh`;
// if (currentURL.indexOf("localhost") != -1||currentURL.indexOf("192.168") != -1) {
//     baseUrl = "http://192.168.254.88:8080/";//192.168.254.44
// } else {
//     baseUrl = "http://13.214.33.171:8080"; 
// }

export const postShare = ()=>Request.post(`api/v1/upload`);
export const getIslogin = () => Request.get(baseUrl);
// export const testrequest = ()=>Request.get(`${baseUrl}/wt-gac/user/getHomeUserInfo`);
// export const getmusicList = (param)=>Request.get(`${baseUrl}/wt-gac/music/getMusicPassList?activityId=${param.activityId}&userId=${param.userId}`);
// export const getuserInfo = ()=>Request.post(`${baseUrl}/wt-cpc/authorized/getUserInfo`);
// export const postCheckLogin = ()=>Request.post(`${baseUrl}wt-cpc/login/checkLogin`);
// export const posttoken = (data)=>Request.post(`${baseUrlyh}/oauth2/health/test`,data,{isForm:true});
