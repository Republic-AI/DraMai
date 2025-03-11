
/**
 * 
 const options = {
  title: 'BW乐园全新升级，开启异世界冒险之旅！', // 分享标题
  desc: '快来收集碎片、解锁图鉴即可获得百变装扮及海量BW福利哦~~', // 分享描述
  link: location.href, // 分享链接，必传，请传一个可以访问的url
  pics: 'https://i0.hdslb.com/bfs/activity-plat/static/20200619/b8f2b74d0482aed61472c7065dc1ed56/LjvZEBwmnu.png', 
  pictureList: [{
    img_height: 100,
    img_size: 100,
    img_src: 'https://i0.hdslb.com/bfs/activity-plat/static/20200619/b8f2b74d0482aed61472c7065dc1ed56/LjvZEBwmnu.png',
    img_width: 100,
  }],
  edit_content: '快来收集碎片、解锁图鉴即可获得百变装扮及海量BW福利哦~~', // 动态预设文案
  success: () => {},
  cancel: () => {}
};
 */
export default {
    doShare(option) {
      const biliShare = window["biliShare"];
      console.log("ddddddddd",biliShare);
      // 希望显示分享面板时调用 showWindow
      let inBiliApp = /BiliApp|BiliComic/i.test(navigator.userAgent);
      if(inBiliApp){
        if(!option.pictureList){
          biliShare.showWindow(option);
        }else{
          biliShare.showWindowTypeImage(option);
        }
        console.log("eeeeeeee");
      }else{
        console.log("fffffffff",option);
        biliShare.showWindow(option);
      }
    }
  }