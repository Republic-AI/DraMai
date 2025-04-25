import { _decorator, assetManager, Button, Component, EditBox, instantiate, JsonAsset, Label, Node, Sprite, SpriteFrame, sys, tween, v3 } from 'cc';
import WebUtils from '../../utils/WebUtils';
import { alert_cb_status } from '../../common/alertPrefab';
import { network } from '../../model/RequestData';
import { observer, socket } from '../App';
import { EventType } from '../../EventType';
import { GlobalConfig } from '../config/GlobalConfig';
//import { Buffer } from 'buffer';

let connection:any = null;
declare var solanaWeb3: any;
declare global {
    interface Window {
      solana?: any;
    }
  }
// if (!(window as any).Buffer) {
//     (window as any).Buffer = Buffer
// }
const { ccclass, property } = _decorator;

@ccclass('createPlayerLayer')
export class createPlayerLayer extends Component {
    @property(Node)
    skinViewNode:Node = null;

    @property(Label)
    lblDes:Label = null;

    @property(Label)
    lblTime:Label = null;

    @property(Sprite)
    imgPlayerBig:Sprite = null;

    @property(Node)
    imgFail:Node = null;

    @property(Label)
    lblPlayerName:Label = null;

    @property(Button)
    btnConfirm:Button = null;

    _data = null;
    _chooseData = null;
    _isConfirming = false;
    protected onLoad(): void {
        observer.on(EventType.CREATEPLAYER,this.createPlayer,this);
        this.node.setScale(v3(0, 0, 1))
        tween(this.node).to(0.17, { scale: v3(1, 1, 1), position: v3(0, 40, 0) }).to(0.066, { position: v3(0, 0, 0) }).start()
    }
    start() {
        this.initData()
    }

    update(deltaTime: number) {
        let isChooseUnUse = false;

        if(this._chooseData && GlobalConfig.instance.CreateIDArr[this._chooseData.ID]){
            isChooseUnUse = true;
        }
        this.btnConfirm.interactable = !isChooseUnUse;
        this.btnConfirm.node.children[0].active = isChooseUnUse;
        if(this._data){
            this._data.forEach((playerInfo,index)=>{
                let isUsed = false;
                if(GlobalConfig.instance.CreateIDArr[playerInfo.ID]){
                    isUsed = true;
                }
                this.skinViewNode.children[index].getChildByName("imgInUse").active = isUsed
                this.skinViewNode.children[index].getChildByName("imgPlayer").active = !isUsed;
                //this.skinViewNode.children[index].getChildByName("imgPlayerChoose").active = !isUsed;
                
            })
        }

    }

    protected onDestroy(): void {
        observer.off(EventType.CREATEPLAYER,this.createPlayer,this);
    }

    onBtnChoose(target,CustomData){
        this.choosePlayer(Number(CustomData));
    }

    createPlayer(playerData){
        let npcData = playerData.data.data.myNpc
        if(npcData.userNo == GlobalConfig.instance.LoginData.data.player.playerId){
            GlobalConfig.instance.isCreatePlayer = true;
            this.onBtnClose();
        }
    }

    async onBtnConfirm(){
        let json = new network.GetAllNPCRequest();
        json.command = 10001;
        json.type = 1;
        json["data"] = {
            model:this._chooseData.ID,
            name:"",
        }
        socket.sendWebSocketBinary(json);
        return;
        if (window.solana && window.solana.isPhantom) {
            console.log('Phantom 钱包已检测到');
            if(this._isConfirming){
                return;
            }
            this._isConfirming = true;
            await assetManager.loadAny({ url: 'https://unpkg.com/@solana/web3.js@latest/lib/index.iife.js', ext: '.js' },async (err, script) => {
                if (err) {
                    console.log("加载 solana-web3.js 失败:", err);
                    return;
                }
                await console.log("solanaWeb3 已加载:", solanaWeb3); // 此时 solanaWeb3 已可用
                //solanaWeb3 = script;
                //connection = new solanaWeb3.Connection("https://rpc.ankr.com/solana");
                connection = new solanaWeb3.Connection("https://dimensional-autumn-firefly.solana-mainnet.quiknode.pro/484ba0e3ab51f63384c08ace0d3110fa5709b408");//new solanaWeb3.Connection("https://solana-api.projectserum.com");
                console.log('Connection established:', connection);
                    //this.checkWallet();
                // const provider = new AnchorProvider(connection, window.solana, {
                //     commitment: "processed",
                // });
    
                // const program = new Program(votingContractIDL  as Idl, PROGRAM_ID, provider);
    
                // console.log("Program 初始化成功：", program);
                // const SYSTEM_PROGRAM_ID = new solanaWeb3.PublicKey('11111111111111111111111111111111');
                const wallet =  await window.solana.connect();
                const fromPubkey = wallet.publicKey;
                const userPublicKey = new solanaWeb3.PublicKey(wallet.publicKey);
                const tokenMintAddress = "8hrZax9eVqdLB1duN2fJ3ji4FwbddPsb96uauEMroBm8";
                const mintPublicKey = new solanaWeb3.PublicKey(tokenMintAddress);
                    // 获取账户信息
                // const accountInfo = await connection.getAccountInfo(userPublicKey);
                // if (!accountInfo) {
                //     console.log("Account not found!");
                //     return;
                // }

                // // 获取数据 Buffer
                // const data = accountInfo.data;

                // // 解码数据
                // const mint = new solanaWeb3.PublicKey(data.slice(0, 32)); // 0-31: Mint 地址
                // const owner = new solanaWeb3.PublicKey(data.slice(32, 64)); // 32-63: Owner 地址

                //     // 检查数据长度
                // if (data.length < 165) {
                //     console.log(`Account data size is too small: ${data.length}`);
                //     return;
                // }
                // // 解析代币余额 (uint64 是 8 字节小端表示)
                // const amountBuffer = data.slice(64, 72); // 64-71: 余额
                // const amount = amountBuffer.readBigUInt64LE(); // 转换为 JavaScript 的大整数

                // console.log("Mint Address:", mint.toBase58());
                // console.log("Owner Address:", owner.toBase58());
                // console.log("Token Amount:", amount.toString());
                // return;
                // const toPubkey = new solanaWeb3.PublicKey("HYVXsrfFNQL453E9W1iK4MR72GorFsnQSS8Ripgd44QE");
                // const payNum = Math.floor(0.1 * 1000000000);
                // const instructionData = new Uint8Array(4 + 8); // 4 字节指令编号 + 8 字节金额
                // writeUInt32LE(instructionData, 2, 0); // 指令编号 "2"
                // writeUInt64LE(instructionData, payNum, 4); // lamports 金额

                // const transferInstruction = new solanaWeb3.TransactionInstruction({
                //     keys: [
                //         { pubkey: fromPubkey, isSigner: true, isWritable: true }, // 付款地址
                //         { pubkey: toPubkey, isSigner: false, isWritable: true },  // 接收地址
                //     ],
                //     programId: SYSTEM_PROGRAM_ID, // 使用 SystemProgram.programId
                //     data: instructionData, // 二进制数据
                // });
                // const balance = await connection.getBalance(wallet.publicKey);
                // console.log("Devnet 上的余额:"+ balance);
                // const transaction = new solanaWeb3.Transaction().add(transferInstruction);
                // const signature = await solanaWeb3.sendAndConfirmTransaction(connection, transaction, [wallet]);
                // const transactionSignature = await connection.confirmTransaction(signature, 'confirmed');
                // console.log("交易已确认：", transactionSignature);
                  // 获取用户的所有代币账户（解析后的版本）
                  console.log("userPublicKey11=======" + userPublicKey.toBase58());
                  console.log("mintPublicKey22=======" + mintPublicKey.toBase58());
                  const ProgramID =  new solanaWeb3.PublicKey("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");
                  const tokenAccounts = await connection.getTokenAccountsByOwner(userPublicKey, {programId: ProgramID});
                console.log("tokenAccounts=======" + JSON.stringify(tokenAccounts));
                // 遍历代币账户，检查是否包含指定的 Mint 地址
                let isHaveMint = false;
                tokenAccounts.value.forEach((account) => {
                    const data = account.account.data;
                    // 检查数据长度是否足够（SPL Token 账户固定为 165 字节）
                    const TOKEN_ACCOUNT_SIZE = 165;
                    if (data.length < TOKEN_ACCOUNT_SIZE) {
                        console.log(`Account data size is too small: ${data.length}`);
                        return;
                    }
            
                    try {
                        // 解析代币账户数据（根据 SPL Token 数据结构）
                        const mint = new solanaWeb3.PublicKey(data.slice(0, 32)); // 0-31: Mint 地址
                        const owner = new solanaWeb3.PublicKey(data.slice(32, 64)); // 32-63: Owner 地址
            
                        // 解析代币余额（uint64 是 8 字节小端格式）
                        const amountBuffer = data.slice(64, 72); // 64-71: 余额
                        const amount = Number(amountBuffer.readBigUInt64LE()); // 转换为 JavaScript 数值
            
                        // 打印解析结果
                        console.log(`Mint: ${mint.toBase58()}, Amount: ${amount}, Owner: ${owner.toBase58()}`);
            
                        // 检查是否与指定的 Token Mint 地址匹配
                        if (mint.toBase58() === tokenMintAddress && amount > 0) {
                            console.log(`User owns the token: ${tokenMintAddress}`);
                            isHaveMint = true;
                        }
                    } catch (error) {
                        console.log("Error decoding account data:", error);
                    }
                });
                if(isHaveMint){
                    let json = new network.GetAllNPCRequest();
                    json.command = 10001;
                    json.type = 1;
                    json["data"] = {
                        model:this._chooseData.ID,
                        name:"",
                    }
                    socket.sendWebSocketBinary(json);
                }
                else{
                    //WebUtils.showToast("create failed");
                    this.imgFail.active = true;
                }
                this._isConfirming = false;
                console.log(`User does not own the token: ${tokenMintAddress}`);

            });
            
        } else {
            let str = "Phantom wallet not detected, click OK to proceed with installation";
            WebUtils.showAlert(str,alert_cb_status.both,()=>{
                let downLoadUrl = "https://phantom.app/download"
                // if(sys.isMobile){

                // }
                // else{
                //     window.location.href = downLoadUrl
                // }
                window.location.href = downLoadUrl
            });
            console.log('未检测到 Phantom 钱包');
        }
    }

    initData(){
        let cfgBundle = assetManager.getBundle("createPlayer");
        cfgBundle.load("json/createCfg",JsonAsset,(err,data:JsonAsset)=>{
            console.log("data======" + JSON.stringify(data.json));
            this._data = data.json;
            this._data.forEach((playerInfo,index)=>{
                this.skinViewNode.children[index].getChildByName("imgPlayer").active = true;
                this.skinViewNode.children[index].getChildByName("imgPlayerNormal").active = true;
                cfgBundle.load("image/player_" + playerInfo.ID+"/spriteFrame",(err,spr:SpriteFrame)=>{
                    this.skinViewNode.children[index].getChildByName("imgPlayer").getComponent(Sprite).spriteFrame = spr;
                })
                if(index == 0){
                    this.choosePlayer(0);
                }
            })
        })
    }

    choosePlayer(chooseIndex:number){
        if(this._data[chooseIndex]){
            this.lblDes.string = this._data[chooseIndex].Intro;
            this.lblTime.string = this._data[chooseIndex].Time;
            this._chooseData = this._data[chooseIndex];
            let splitStr = this._data[chooseIndex].Image.split(".");
            this.lblPlayerName.string = splitStr[0];
            this.skinViewNode.children.forEach((node,index)=>{
                node.getChildByName("imgPlayerChoose").active = chooseIndex == index ? true : false;
            })
            let cfgBundle = assetManager.getBundle("createPlayer");
            cfgBundle.load("image/player_" + this._data[chooseIndex].ID+"/spriteFrame",(err,spr:SpriteFrame)=>{
                this.imgPlayerBig.spriteFrame = spr;
            })
        }
        else{
            
        }
    }

    onBtnClose(){
        tween(this.node).to(0.17, { scale: v3(0, 0, 1), position: v3(0, 40, 0) }).call(() => {
            this.node.destroy()
        }).start()
    }

    async onBtnTest(){
        if (window.solana && window.solana.isPhantom) {
            console.log('Phantom 钱包已检测到');
            await assetManager.loadAny({ url: 'https://unpkg.com/@solana/web3.js@latest/lib/index.iife.js', ext: '.js' },async (err, script) => {
                if (err) {
                    console.log("加载 solana-web3.js 失败:", err);
                    return;
                }
                await console.log("solanaWeb3 已加载:", solanaWeb3); // 此时 solanaWeb3 已可用
                //solanaWeb3 = script;
                //connection = new solanaWeb3.Connection("https://rpc.ankr.com/solana");
                connection = new solanaWeb3.Connection("https://dimensional-autumn-firefly.solana-mainnet.quiknode.pro/484ba0e3ab51f63384c08ace0d3110fa5709b408");//new solanaWeb3.Connection("https://solana-api.projectserum.com");
                console.log('Connection established:', connection);
                    //this.checkWallet();
                // const provider = new AnchorProvider(connection, window.solana, {
                //     commitment: "processed",
                // });
    
                // const program = new Program(votingContractIDL  as Idl, PROGRAM_ID, provider);
    
                // console.log("Program 初始化成功：", program);
                // const SYSTEM_PROGRAM_ID = new solanaWeb3.PublicKey('11111111111111111111111111111111');
                const wallet =  await window.solana.connect();
                const fromPubkey = wallet.publicKey;
                const userPublicKey = new solanaWeb3.PublicKey(wallet.publicKey);
                const tokenMintAddress = "8hrZax9eVqdLB1duN2fJ3ji4FwbddPsb96uauEMroBm8";
                const mintPublicKey = new solanaWeb3.PublicKey(tokenMintAddress);
                    // 获取账户信息
                // const accountInfo = await connection.getAccountInfo(userPublicKey);
                // if (!accountInfo) {
                //     console.log("Account not found!");
                //     return;
                // }

                // // 获取数据 Buffer
                // const data = accountInfo.data;

                // // 解码数据
                // const mint = new solanaWeb3.PublicKey(data.slice(0, 32)); // 0-31: Mint 地址
                // const owner = new solanaWeb3.PublicKey(data.slice(32, 64)); // 32-63: Owner 地址

                //     // 检查数据长度
                // if (data.length < 165) {
                //     console.log(`Account data size is too small: ${data.length}`);
                //     return;
                // }
                // // 解析代币余额 (uint64 是 8 字节小端表示)
                // const amountBuffer = data.slice(64, 72); // 64-71: 余额
                // const amount = amountBuffer.readBigUInt64LE(); // 转换为 JavaScript 的大整数

                // console.log("Mint Address:", mint.toBase58());
                // console.log("Owner Address:", owner.toBase58());
                // console.log("Token Amount:", amount.toString());
                // return;
                // const toPubkey = new solanaWeb3.PublicKey("HYVXsrfFNQL453E9W1iK4MR72GorFsnQSS8Ripgd44QE");
                // const payNum = Math.floor(0.1 * 1000000000);
                // const instructionData = new Uint8Array(4 + 8); // 4 字节指令编号 + 8 字节金额
                // writeUInt32LE(instructionData, 2, 0); // 指令编号 "2"
                // writeUInt64LE(instructionData, payNum, 4); // lamports 金额

                // const transferInstruction = new solanaWeb3.TransactionInstruction({
                //     keys: [
                //         { pubkey: fromPubkey, isSigner: true, isWritable: true }, // 付款地址
                //         { pubkey: toPubkey, isSigner: false, isWritable: true },  // 接收地址
                //     ],
                //     programId: SYSTEM_PROGRAM_ID, // 使用 SystemProgram.programId
                //     data: instructionData, // 二进制数据
                // });
                // const balance = await connection.getBalance(wallet.publicKey);
                // console.log("Devnet 上的余额:"+ balance);
                // const transaction = new solanaWeb3.Transaction().add(transferInstruction);
                // const signature = await solanaWeb3.sendAndConfirmTransaction(connection, transaction, [wallet]);
                // const transactionSignature = await connection.confirmTransaction(signature, 'confirmed');
                // console.log("交易已确认：", transactionSignature);
                  // 获取用户的所有代币账户（解析后的版本）
                  console.log("userPublicKey11=======" + userPublicKey.toBase58());
                  console.log("mintPublicKey22=======" + mintPublicKey.toBase58());
                  const ProgramID =  new solanaWeb3.PublicKey("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");
                  const tokenAccounts = await connection.getTokenAccountsByOwner(userPublicKey, {programId: ProgramID});
                console.log("tokenAccounts=======" + JSON.stringify(tokenAccounts));
                // 遍历代币账户，检查是否包含指定的 Mint 地址
                tokenAccounts.value.forEach((account) => {
                    const data = account.account.data;

                    // 检查数据长度是否足够（SPL Token 账户固定为 165 字节）
                    const TOKEN_ACCOUNT_SIZE = 165;
                    if (data.length < TOKEN_ACCOUNT_SIZE) {
                        console.log(`Account data size is too small: ${data.length}`);
                        return;
                    }
            
                    try {
                        // 解析代币账户数据（根据 SPL Token 数据结构）
                        const mint = new solanaWeb3.PublicKey(data.slice(0, 32)); // 0-31: Mint 地址
                        const owner = new solanaWeb3.PublicKey(data.slice(32, 64)); // 32-63: Owner 地址
            
                        // 解析代币余额（uint64 是 8 字节小端格式）
                        const amountBuffer = data.slice(64, 72); // 64-71: 余额
                        const amount = Number(amountBuffer.readBigUInt64LE()); // 转换为 JavaScript 数值
            
                        // 打印解析结果
                        console.log(`Mint: ${mint.toBase58()}, Amount: ${amount}, Owner: ${owner.toBase58()}`);
            
                        // 检查是否与指定的 Token Mint 地址匹配
                        if (mint.toBase58() === tokenMintAddress && amount > 0) {
                            console.log(`User owns the token: ${tokenMintAddress}`);
                            return true;
                        }
                    } catch (error) {
                        console.log("Error decoding account data:", error);
                    }
                });
                console.log(`User does not own the token: ${tokenMintAddress}`);

            });
            
        } else {
            let str = "Phantom wallet not detected, click OK to proceed with installation";
            WebUtils.showAlert(str,alert_cb_status.both,()=>{
                let downLoadUrl = "https://phantom.app"
                // if(sys.isMobile){

                // }
                // else{
                //     window.location.href = downLoadUrl
                // }
                window.location.href = downLoadUrl
            });
            console.log('未检测到 Phantom 钱包');
        }
    }
}

function writeUInt32LE(byteArray, value, offset = 0) {
    byteArray[offset] = value & 0xff;          // 取最低 8 位
    byteArray[offset + 1] = (value >> 8) & 0xff;  // 取次低 8 位
    byteArray[offset + 2] = (value >> 16) & 0xff; // 取次高 8 位
    byteArray[offset + 3] = (value >> 24) & 0xff; // 取最高 8 位
}

function writeUInt64LE(byteArray, value, offset = 0) {
    const low32 = value % Math.pow(2, 32);     // 低 32 位
    const high32 = Math.floor(value / Math.pow(2, 32)); // 高 32 位

    writeUInt32LE(byteArray, low32, offset);         // 写入低 32 位
    writeUInt32LE(byteArray, high32, offset + 4);    // 写入高 32 位
}


