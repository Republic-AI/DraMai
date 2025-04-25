// import { _decorator, assetManager, Component, dragonBones, Node, ScrollView, UITransform, v2, v3, view } from 'cc';

// import { AnchorProvider, Program ,Idl } from '@project-serum/anchor';
// import votingContractIDL  from '../resources/libs/voting_contract.json';
// //import { web3 } from '@project-serum/anchor';
// const { ccclass, property } = _decorator;
// //import { Buffer } from 'buffer';
// import { NpcManager } from '../src/NPC/NpcManager';
// // (window as any).Buffer = Buffer;
// // import solanaWeb3 from '@solana/web3.js';
// let connection:any = null;

// let methordID = {
//     initialize:"ed01718369b6b3d7",
//     startNewRound:"71932034a757c999",
//     createPool:"c52a89c8bb58b4af",
//     vote:"7401f21a815a80b9",
//     withdrawWinningPool:"633387dc250f1b96",
//     refundLosingPool:"b8d6d0efd4f2df23",
//     getRoundInfo:"0d9e65c1b78c63f3",
//     getPoolInfo:"cd260a0519152db3",
//     getVoterInfo:"9d3639081bf8e1cf",
//     getAllPoolsInfo:"3b7b552432e1d889"
// }
// //const publicKey = new PublicKey('61EnkzB2XAZcvctyHEQDV8VryQbixwp8XdtsJvEJ1Riq');web
// declare var solanaWeb3: any;
// declare global {
//     interface Window {
//       solana?: any;
//     }
//   }
// function hexToUint8Array(hex) {
//     // 确保字符串长度是偶数
//     if (hex.length % 2 !== 0) {
//         hex = '0' + hex; // 如果长度是奇数，前面补充0
//     }

//     // 将十六进制字符串转换为 Uint8Array
//     const uint8Array = new Uint8Array(hex.length / 2);
//     for (let i = 0; i < hex.length; i += 2) {
//         uint8Array[i / 2] = parseInt(hex.substr(i, 2), 16);  // 每两个字符转为一个字节
//     }
//     return uint8Array;
// }
// function decodeAccountData(data) {
//     // 根据你的合约数据格式解码数据，例如使用 Borsh 或其他方式
//     // 下面是伪代码，需要根据你的合约返回值结构实现
//     return {
//         field1: data.readUInt32LE(0),
//         field2: data.readUInt32LE(4),
//         // 添加其他字段解析...
//     };
// }

// function appendParameters(methodID, poolId, amount) {
//     const poolIdArray = new Uint8Array(1); // u8 参数占用 1 字节
//     poolIdArray[0] = poolId; // 写入 poolId

//     const amountArray = new Uint8Array(8); // u64 参数占用 8 字节
//     const dataView = new DataView(amountArray.buffer);
//     dataView.setBigUint64(0, BigInt(amount), true); // 写入 amount（小端序）

//     // 合并 methodID 和参数
//     const result = new Uint8Array(methodID.length + poolIdArray.length + amountArray.length);
//     result.set(methodID, 0); // 添加 methodID
//     result.set(poolIdArray, methodID.length); // 添加 poolId
//     result.set(amountArray, methodID.length + poolIdArray.length); // 添加 amount

//     return result;
// }
// @ccclass('testScene')
// export class testScene extends Component {

//     _orignOffset = null;
//     protected onLoad(): void {
//         //tdsdsdhis._orignOffset = this.mapView.getScrollOffset();
//         //let dragon =ssss this.node.ssssssgessssssstComponentInChildren(dragonBones.ArmatureDisplay).playTimes(-1)
//     }
//     start() {
//     }

//     update(deltaTime: number) {
        
//     }

//     onBtnGetContentPos(){   
//         //sssconsole.log("mapView.pos========" + JSON.stsssringify(this.mapView.content.position))
//     }

//     onBtnTest1(){
//         // this.node.getComponentInChildren(NpcManager).startSpeech();
//         // return;
//         this.loadSolanaWeb3();
//     }

//     onBtnTest2(){
//         // let offset =  this.mapViessssw.getScrollOffset();
//         // let maxOffset = this.mapView.getMaxSsscrollOffset();
//         // console.log("mapPos======" + JSON.stringify(this.mapModel.position));
//         // console.log("scrolloffset=====" + JSOsssN.stringify(offset));
//         // console.log("maxscrolloffset=====" + JSON.stringify(maxOffset));
//     }

//     // 查询余额
//     async getBalance() {
//         try {
//         // 钱包地址（替换为你的钱包地址）
//         const walletAddress = '61EnkzB2XAZcvctyHEQDV8VryQbixwp8XdtsJvEJ1Riq';

//         // 获取余额
//         connection.getBalance(new solanaWeb3.PublicKey(walletAddress))
//             .then((balance:any) => {
//                 console.log(`Wallet balance: ${balance / solanaWeb3.LAMPORTS_PER_SOL} SOL`);
//             })
//             .catch((err:any) => {
//                 console.log("获取余额失败:", err);
//             });
//             //console.log(`Account balance: ${balance / 10 ** 9} SOL`); // 转换为 SOL 单位
//         } catch (error) {
//             console.log("Error fetching balancesssssssss:", error);
//         }
//     }

//     async loadSolanaWeb3() {
//         // const solanaWeb3 = web3;
//         // const connection = new solanaWeb3.Connection('https://api.devnet.solana.com', 'confirmed');
//         // console.log('Connection established:', connection);
//         assetManager.loadAny({ url: 'https://unpkg.com/@solana/web3.js@latest/lib/index.iife.js', ext: '.js' }, (err, script) => {
//             if (err) {
//                 console.log("加载 solana-web3.js 失败:", err);
//                 return;
//             }
//             console.log("solanaWeb3 已加载:", solanaWeb3); // 此时 solanaWeb3 已可用
//             connection = new solanaWeb3.Connection('https://api.devnet.solana.com', 'confirmed');
//             console.log('Connection established:', connection);
//             this.voteTest()
//                 //this.checkWallet();
//             // const provider = new AnchorProvider(connection, window.solana, {
//             //     commitment: "processed",
//             // });

//             // const program = new Program(votingContractIDL  as Idl, PROGRAM_ID, provider);

//             // console.log("Program 初始化成功：", program);
//         });
//     }

//     async checkWallet(){
//         if (window.solana && window.solana.isPhantom) {
//             console.log('Phantom 钱包已检测到');
//             const wallet =  await window.solana.connect();
//             console.log('钱包已连接:', wallet.publicKey.toString());
//         } else {
//             console.log('未检测到 Phantom 钱包');
//         }
//     }

//     async voteTest(){
//         const PROGRAM_ID = new solanaWeb3.PublicKey('Ppxir5EaV1dpxTa8SoiJpFZDkstJz3FGzjLXmX7NnUQ');
//         const connection = new solanaWeb3.Connection('https://api.devnet.solana.com', 'confirmed');

//         let wallet = null;
//         let secretKey = localStorage.getItem("solanaSecretKey");
//         if(secretKey){
//             let secretArr = Uint8Array.from(JSON.parse(secretKey));
//             wallet = solanaWeb3.Keypair.fromSecretKey(secretArr);
//         }
//         else{
//             wallet = solanaWeb3.Keypair.generate();
//             let secret = Array.from(wallet.secretKey);
//             console.log("Generated Secret Key:", secret);
//             localStorage.setItem("solanaSecretKey",JSON.stringify(secret));
//         }
//         let userPublicKey = null;
//         const poolId = 1; // 示例投票池 ID
//         const amount = 100;
        
//         // 检测和连接 Phantom 钱包
//         if (window.solana && window.solana.isPhantom) {
//             console.log('Phantom 钱包已检测到');
//             // wallet = await window.solana.connect();
//             // userPublicKey = window.solana.publicKey;
//             // console.log('钱包已连接:', wallet.publicKey.toString());
//         } else {
//             console.log('未检测到 Phantom 钱包');
//             return;
//         }
//         //userPublicKey =  new solanaWeb3.Keypair.generate().publicKey;
        
//         // 替换 Buffer 相关操作
//         const stateSeed = new TextEncoder().encode('state');
//         const adminSeed = new TextEncoder().encode('admin');
//         const poolSeed0 = new TextEncoder().encode('pool0');
//         const poolSeed1 = new TextEncoder().encode('pool1');
//         const poolSeed2 = new TextEncoder().encode('pool2');
//         //const poolIdArray = new Uint8Array([poolId]);
//         const amountArray = new Uint8Array(new BigUint64Array([BigInt(amount)]).buffer);
//         const methordIDBuffer = hexToUint8Array("b8e8766c");
//         const params = new Uint8Array([]);
//         const instructionData = new Uint8Array(methordIDBuffer.length + params.length);
//         instructionData.set(methordIDBuffer, 0);
//        instructionData.set(params, methordIDBuffer.length); //new Uint8Array([14, 91, 123, 66, 123, 223, 1, 47]);
//         console.log("buffer====" + JSON.stringify(methordIDBuffer));
//         // 生成状态账户地址
//         const [stateAccount] = await solanaWeb3.PublicKey.findProgramAddress(
//             [stateSeed],
//             PROGRAM_ID
//         );
        
//         // 生成投票池账户地址
//         // const [poolAccount] = await solanaWeb3.PublicKey.findProgramAddress(
//         //     [poolSeed, poolIdArray],
//         //     PROGRAM_ID
//         // );
        
//         // 生成投票者账户地址
//         // const [voterAccount] = await solanaWeb3.PublicKey.findProgramAddress(
//         //     [voterSeed, userPublicKey.toBytes()],
//         //     PROGRAM_ID
//         // );

//                // 假设账户数据是二进制数据，需要解码
//             //    console.log("stateAccount====" + JSON.stringify(stateAccount));
//             //    return;
//             //    const accountInfo = await connection.getAccountInfo(stateAccount);
//             //    const decodedData = decodeAccountData(accountInfo.data);
//             //    console.log("账户数据:", decodedData);
//             //    return;
        
//         // 构造账户列表
//         const accounts = [
//             { pubkey: stateAccount, isSigner: false, isWritable: false},
//             //{ pubkey: window.solana.publicKey, isSigner: true, isWritable: false },
//             // { pubkey: poolAccount, isSigner: false, isWritable: true },
//             // { pubkey: voterAccount, isSigner: false, isWritable: true },
//             // { pubkey: userPublicKey, isSigner: true, isWritable: true },
//             // { pubkey: solanaWeb3.SystemProgram.programId, isSigner: false, isWritable: false },
//         ];

//         // const getStatePDA = async () => {
//         //     return stateAccount;
//         // };

//         // const getVoterPDA = async () => {
//         //     return voterAccount;
//         // };
        
//         // // 创建交易指令
//         const instruction = new solanaWeb3.TransactionInstruction({
//             keys: accounts,
//             programId: PROGRAM_ID,
//             data: instructionData,
//         });
        
//         const balance = await connection.getBalance(stateAccount);
//         console.log("state balance:", balance);   

//         const balance2 = await connection.getBalance(PROGRAM_ID);
//         console.log("PROGRAM_ID balance:", balance2);  

//         const balance3 = await connection.getBalance(wallet.publicKey);
//         console.log("wallet balance:", balance3)
//         //const accountInfo = await connection.getAccountInfo(statePublicKey);
//         // // 构造交易
//         console.log("PROGRAM_ID=======" + JSON.stringify(PROGRAM_ID));
//         console.log("walletPublicKey======" + wallet.publicKey);
//         console.log("stateKey======" + stateAccount);
//         const transaction = new solanaWeb3.Transaction().add(instruction);
//         const provider = window.solana;
//         transaction.feePayer = wallet.publicKey;
//            // 签名交易
//         const latestBlockhash = await connection.getLatestBlockhash("confirmed");
//         console.log('最新区块哈希:', latestBlockhash.blockhash);
//         transaction.recentBlockhash = latestBlockhash.blockhash;
//         transaction.sign(wallet);

        
//         //const signedTransaction = await provider.signTransaction(transaction);
//         //transaction.sign(wallet)
//                 // 模拟交易
//                 // const simulationResult = await connection.simulateTransaction(transaction);
//                 // if (simulationResult.value.err) {
//                 //     console.log("Simulation failed:", simulationResult.value.logs);
//                 //     throw new Error("Transaction simulation failed.");
//                 // }
//                 // return;
//                 try {
//                     // 发送交易
//                     const txId = await connection.sendTransaction(transaction, [wallet]);
//                     await connection.confirmTransaction(txId, 'processed');
//                     console.log("Transaction sent and confirmed:", txId);
//                   } catch (error) {
//                     if (error) {
//                       // 如果是 SendTransactionError，获取日志信息
//                       try {
//                         console.log("error=====" + JSON.stringify(error));
//                         // const txId = error.transactionId;
//                         // if (!txId) {
//                         //   console.log("No transaction ID found in error");
//                         //   return;
//                         // }
//                         // const transactionDetails = await connection.getTransaction(txId, { commitment: 'confirmed' });
//                         // console.log('Transaction details:', transactionDetails);
//                         // // 如果存在日志，可以查看它们
//                         // if (transactionDetails && transactionDetails.meta && transactionDetails.meta.logMessages) {
//                         //   console.log('Logs:', transactionDetails.meta.logMessages);
//                         // }
//                       } catch (logError) {
//                         console.log('Failed to fetch logs:', logError);
//                       }
//                     } else {
//                       console.log('Unexpected error:', error);
//                     }
//                 }
//         // const signature   = await connection.sendTransaction(transaction, [wallet]);
//         // console.log("Transaction Signature:", signature);
//         // const signature = await connection.sendRawTransaction(signedTransaction.serialize(), {
//         //     skipPreflight: false,
//         //     preflightCommitment: "confirmed",
//         // });
//         // console.log("Transaction Signature:", signature);

//         // // 确认交易
//         // const confirmation = await connection.confirmTransaction(signature, "confirmed");
//         // console.log("Transaction Confirmed:", confirmation);
//         // const transactionDetails = await connection.getConfirmedTransaction(signature);
//         // console.log("Transaction Details:", transactionDetails);
//         // 确认交易
//         // const confirmation = await connection.confirmTransaction(signature, "confirmed");
//         // console.log("Transaction Confirmed:", confirmation);
        
//         // // 获取最新区块哈希
//         // try {
//         // } catch (error) {
//         //     console.log('获取区块哈希失败:', error);
//         //     return;
//         // }

//         // try {
//         //     // Phantom 钱包签名
//         //     const signedTransaction = await window.solana.signTransaction(transaction);
//         //     console.log("签名交易:", signedTransaction);
        
//         //     // 将签名后的交易发送到 Solana 网络
//         //     const signature = await connection.sendRawTransaction(signedTransaction.serialize());
//         //     console.log("交易已提交，签名：", signature);
        
//         //     // 等待交易确认
//         //     const confirmation = await connection.confirmTransaction(signature);
//         //     console.log("交易确认:", confirmation);
//         // } catch (error) {
//         //     console.log("交易签名或发送失败:", error);
//         // }
        
//         // 使用 Phantom 钱包
//     }

// }
// // function loadSolanaWeb3CDN(): Promise<void> {
// //     return new Promise((resolve, reject) => {
// //         const script = document.createElement('script');
// //         script.src = 'https://cdn.jsdelivr.net/npm/@solana/web3.js@1.77.3';
// //         script.type = 'text/javascript';
// //         script.async = true;
// //         script.onload = () => {
// //             console.log('Solana Web3.js loaded via CDN');
// //             resolve();
// //         };
// //         script.onerror = (error) => {
// //             console.log('Failed to load Solana Web3.js via CDN:', error);
// //             reject(error);
// //         };
// //         document.head.appendChild(script);
// //     });
// // }

// // initialize().catch((error) => {
// //     console.log('Initialization failed:', error);
// // });



