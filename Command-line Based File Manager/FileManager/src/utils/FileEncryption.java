package utils;

import model.FileModel;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.Key;

public class FileEncryption {

    private final String Encoding = "UTF-8"; // 默认编码
    private final Key aesKey;

    // 构造方法，初始化AES密钥
    public FileEncryption() throws Exception {
        this.aesKey = loadOrGenerateKey(); // 加载或生成密钥
    }

    // 加载或生成密钥的方法
    private Key loadOrGenerateKey() throws Exception {
        File keyFile = new File("aesKey.key");
        if (keyFile.exists()) {
            // 从文件加载密钥
            byte[] keyBytes = Files.readAllBytes(keyFile.toPath());
            return new SecretKeySpec(keyBytes, "AES");
        } else {
            // 生成新密钥并保存
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            byte[] keyBytes = secretKey.getEncoded();
            Files.write(keyFile.toPath(), keyBytes);
            return new SecretKeySpec(keyBytes, "AES");
        }
    }

    // 加密文件方法
    public boolean encrypFile(String fileName) {
        boolean flag = false;
        File file = new File(fileName);

        if (!file.exists() || !file.isFile()) {
            System.out.println("加密失败，文件不存在或不是文件：" + fileName);
            return false;
        }

        // 获取当前工作目录路径
        FileModel filemodel = new FileModel();
        String currentpath = filemodel.getCurrentDirectory().getAbsolutePath();
        if (currentpath == null || currentpath.isEmpty()) {
            System.err.println("当前目录路径无效！");
            return false;
        }
        System.out.println("当前目录：" + currentpath); // 输出当前目录

        // 生成加密文件的路径
        String encryptedFilePath = currentpath + File.separator + "Encryp-" + "aes-"+file.getName();
        System.out.println("加密文件路径：" + encryptedFilePath); // 输出加密文件路径

        try (FileInputStream fIn = new FileInputStream(file);
             FileOutputStream fOut = new FileOutputStream(encryptedFilePath)) {

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);

            byte[] inputBytes = Files.readAllBytes(file.toPath());
            byte[] outputBytes = cipher.doFinal(inputBytes);

            fOut.write(outputBytes);
            flag = true;
            System.out.println("加密完成！");

        } catch (Exception e) {
            System.err.println("加密过程出错：" + e.getMessage());
            e.printStackTrace();
        }
        return flag;
    }


    // 解密文件方法
    public boolean decrypFile(String fileName) {
        boolean flag = false;
        File encrypFile = new File(fileName);

        if (!encrypFile.exists() || !encrypFile.isFile()) {
            System.out.println("解密失败，文件不存在或不是文件：" + fileName);
            return false;
        }

        // 获取解密文件的输出路径，如果 getParent() 返回 null，使用当前工作目录
        String parentDirectory = encrypFile.getParent();
        if (parentDirectory == null) {
            parentDirectory = System.getProperty("user.dir"); // 使用当前工作目录作为文件父目录
        }

        String decryptedFilePath = parentDirectory + File.separator + "Decryp-" + encrypFile.getName();
        System.out.println("解密文件路径：" + decryptedFilePath); // 打印输出解密文件路径，便于调试

        try (FileInputStream fIn = new FileInputStream(encrypFile);
             FileOutputStream fOut = new FileOutputStream(decryptedFilePath)) {

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);

            byte[] inputBytes = Files.readAllBytes(encrypFile.toPath());
            byte[] outputBytes = cipher.doFinal(inputBytes);

            fOut.write(outputBytes);
            flag = true;
            System.out.println("解密完成！");

        } catch (Exception e) {
            System.err.println("解密过程出错：" + e.getMessage());
            e.printStackTrace();
        }

        return flag;
    }

}
