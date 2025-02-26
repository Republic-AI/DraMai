package com.infinity.ai.platform.manager;

import com.infinity.ai.platform.application.Config;
import com.infinity.ai.platform.constant.NFTConstant;
import com.infinity.ai.platform.constant.NFTType;
import com.infinity.common.msg.platform.npc.NFTData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.core.TransactionInstruction;
import org.p2p.solanaj.programs.AssociatedTokenProgram;
import org.p2p.solanaj.programs.TokenProgram;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.ConfirmedTransaction;
import org.p2p.solanaj.rpc.types.SignatureStatuses;
import org.p2p.solanaj.rpc.types.TokenAccountInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NFTManager {

    @Getter
    private static final NFTManager instance = new NFTManager();

    @Getter
    private Map<Integer, NFTData> nftMap = new ConcurrentHashMap<>();

    private String solanaPrivateKey;

    private RpcClient client;


    public void init() {
        try {
            solanaPrivateKey = FileUtils.readFileToString(FileUtils.getFile(Config.getInstance().getGameDataPath() + "/SolanaPrivateKey"), "UTF-8");
            client = new RpcClient("https://dimensional-autumn-firefly.solana-mainnet.quiknode.pro/484ba0e3ab51f63384c08ace0d3110fa5709b408");
        } catch (Exception e) {
            log.error("read SolanaPrivateKey error", e);
        }
        List<String> nftList = getNFTList();
        int index = 0;
        for (NFTType nftType : NFTType.values()) {
            String mint = nftList.size() > index ? nftList.get(index) : "";
            index++;
            nftMap.put(nftType.getId(), new NFTData(nftType.getId(), NFTConstant.INIT, "", mint, null, 0));
        }
    }

    public boolean reciveNFT(NFTData nftData, String address) {
        String signture;
        ConfirmedTransaction confirmedTransaction;
        SignatureStatuses signatureStatuses;
        try {
             int count = 2;
            do {
                signture = tranformNFT(nftData, address);
                Thread.sleep(3000);
                confirmedTransaction = client.getApi().getTransaction(signture);
                signatureStatuses = client.getApi().getSignatureStatuses(Arrays.asList(signture), true);
                count--;
            } while (confirmedTransaction == null && count > 0);
        } catch (Exception e) {
            log.error("Error occurred during NFT transfer: " , e);
        }
        return false;
    }

    public String tranformNFT(NFTData nftData, String address) {
        try {
            boolean health = client.getApi().getHealth();
            // 初始化 Solana RPC 客户端
            // 发送方和接收方的公钥
            String senderPrivateKey = solanaPrivateKey; // 替换为发送方的私钥
            Account senderAccount = Account.fromBase58PrivateKey(senderPrivateKey);
            PublicKey senderPublicKey = senderAccount.getPublicKey();
            PublicKey receiverPublicKey = new PublicKey(address); // 替换为接收方的公钥
            // NFT 的 Mint 地址
            PublicKey mintKey = new PublicKey(nftData.getMint()); // 替换为 NFT 的 Mint 地址

            // 如果接收方没有 ATA 账号，创建 ATA 账号
            TransactionInstruction idempotent = AssociatedTokenProgram.createIdempotent(
                    senderPublicKey,
                    receiverPublicKey,
                    mintKey
            );

            // 计算 ATA 账号地址
            List<byte[]> seeds = Arrays.asList(
                    senderPublicKey.toByteArray(),
                    TokenProgram.PROGRAM_ID.toByteArray(),
                    mintKey.toByteArray()
            );
            PublicKey senderTokenAccount = PublicKey.findProgramAddress(
                    seeds,
                    AssociatedTokenProgram.PROGRAM_ID
            ).getAddress();

            List<byte[]> destSeeds = Arrays.asList(
                    receiverPublicKey.toByteArray(),
                    TokenProgram.PROGRAM_ID.toByteArray(),
                    mintKey.toByteArray()
            );
            PublicKey receiverTokenAccount = PublicKey.findProgramAddress(
                    destSeeds,
                    AssociatedTokenProgram.PROGRAM_ID
            ).getAddress();

            // 创建转账指令
            TransactionInstruction transferInstruction = TokenProgram.transfer(
                    senderTokenAccount,
                    receiverTokenAccount,
                    1, // 转账数量，NFT 通常为 1
                    senderPublicKey
            );

            // 创建交易并添加指令
            Transaction transaction = new Transaction();
            transaction.addInstruction(idempotent);
            transaction.addInstruction(transferInstruction);

            // 发送交易
            return client.getApi().sendTransaction(transaction, senderAccount);
        } catch (Exception e) {
            log.error("Error occurred during NFT transfer: " , e);
        }
        return null;
    }

    public List<String> getNFTList() {
        /*List<String> ret = new ArrayList<>();
        PublicKey accountPublicKey = new PublicKey("DXTW6i9iS15gm3XjzZHzfcEdCzikTfVczw9kmJhZLmRJ");
        // 获取账户中的 NFT 元数据
        try {
            TokenAccountInfo tokenAccountInfo = client.getApi().getTokenAccountsByOwner(accountPublicKey, Map.of("programId", "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA"), Map.of());
            for (TokenAccountInfo.Value value : tokenAccountInfo.getValue()) {
                if (Integer.parseInt(value.getAccount().getData().getParsed().getInfo().getTokenAmount().getAmount()) > 0) {
                    ret.add(value.getAccount().getData().getParsed().getInfo().getMint());
                }
            }
        } catch (RpcException e) {
            throw new RuntimeException(e);
        }
        return ret;*/
        return new ArrayList<>();
    }
}
