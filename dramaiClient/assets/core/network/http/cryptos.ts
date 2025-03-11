// key 加密了16进制

// import { decrypt, encrypt } from "../../../../../libs/xxtea";

// function s2h(str) {
//   if (str == "") return "";
//   let hex = [];
//   for (var i = 0; i < str.length; i++) {
//     hex.push(str.charCodeAt(i).toString(16));
//   }
//   return hex.join("");
// }

//转16进制
function h2s(str) {
    if (str.length % 2 != 0) {
        console.log('fail');
        return '';
    }
    let a;
    let string = [];
    for (var i = 0; i < str.length; i = i + 2) {
        a = parseInt(str.substr(i, 2), 16);
        string.push(String.fromCharCode(a));
    }
    return string.join('');
}

//key:qlycjXnrKwT1vNFq  转了16进制
var key = h2s('716c79636a586e724b775431764e4671');

const crtptos:any = {  };

crtptos.encrypt = data => {
    return window["encryptToString"](data, key);
};
crtptos.decrypt = data => {
    return window["decryptToString"](data, key);
};

export default crtptos;
