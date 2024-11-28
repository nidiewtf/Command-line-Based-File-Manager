package utils;

import model.FileModel;

import java.io.*;

//文件加密和解密操作
public class FileEncryption_xor {

    FileModel filemodel; // 文件模型对象，用于获取当前目录等信息
    private String Encoding; // 字符编码方式，用于文件的读取和写入

    // 构造方法，初始化 FileModel 对象和编码方式
    public FileEncryption_xor(){
        this.filemodel = new FileModel(); // 实例化 FileModel 对象
        this.Encoding = (Encoding != null) ? Encoding : "UTF-8"; // 设置编码，默认为 "UTF-8"
    }

    // 加密文件方法
    public boolean encrypFile(String fileName) {
        boolean flag = false; // 标志变量，标识加密是否成功

        // 创建一个 File 对象，表示要加密的文件
        File file = new File(fileName);

        // 输出文件的绝对路径，便于调试
        System.out.println("待加密文件：" + file);

        // 检查文件是否存在且是否为文件类型（而不是目录）
        if (!file.exists() || !file.isFile()) {
            System.out.println("加密失败，文件不存在或不是文件：" + fileName);
            return false; // 文件不存在或不是文件，直接返回 false 表示加密失败
        }

        final byte Key = 11; // 定义加密密钥，使用字节值 11 进行异或运算加密

        // 获取当前工作目录路径
        String currentpath = filemodel.getCurrentDirectory().getAbsolutePath();
        String encryptedFilePath = currentpath + File.separator + "Encryp-" + "xor-" +fileName ; // 生成加密文件的路径

        // 使用 try-with-resources 创建输入输出流，自动管理资源关闭
        try (FileInputStream fIn = new FileInputStream(file); // 创建文件输入流读取源文件
             BufferedReader bReader = new BufferedReader(new InputStreamReader(fIn, this.Encoding)); // 创建带编码的缓冲字符输入流
             FileOutputStream fOut = new FileOutputStream(encryptedFilePath); // 创建文件输出流写入加密后的文件
             BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(fOut, this.Encoding))) { // 创建带编码的缓冲字符输出流

            System.out.println("开始加密...");

            int c; // 用于存储读取到的字符

            // 循环读取源文件的每个字符
            while ((c = bReader.read()) != -1) {
                // 使用异或运算进行简单加密，并将加密后的内容写入加密文件
                bWriter.write(c ^ Key);
            }

            // 刷新输出流，确保所有加密数据写入文件
            bWriter.flush();

            // 标志位设为 true 表示加密成功
            flag = true;
            System.out.println("加密完成！");

        } catch (IOException e) {
            // 捕获和处理 IO 异常，输出错误信息并打印堆栈跟踪
            System.err.println("加密过程出错：" + e.getMessage());
            e.printStackTrace();
        }

        // 返回加密是否成功的标志
        return flag;
    }

    // 解密文件方法
    public boolean decrypFile(String fileName) {
        boolean flag = false; // 标志变量，标识解密是否成功

        // 创建一个 File 对象，表示要解密的文件
        File encrypFile = new File(fileName);

        // 输出文件的绝对路径，便于调试
        System.out.println("待解密文件：" + encrypFile);

        // 检查文件是否存在且是否为文件类型
        if (!encrypFile.exists() || !encrypFile.isFile()) {
            System.out.println("解密失败，文件不存在或不是文件：" + fileName);
            return false; // 文件不存在或不是文件，直接返回 false 表示解密失败
        }

        final byte Key = 11; // 解密密钥，与加密密钥相同

        // 获取当前工作目录路径，生成解密文件路径
        String currentpath = filemodel.getCurrentDirectory().getAbsolutePath();
        String decryptedFilePath = currentpath + File.separator + "Decryp-" + fileName;

        // 使用 try-with-resources 创建输入输出流，自动管理资源关闭
        try (FileInputStream fIn = new FileInputStream(encrypFile); // 创建文件输入流读取加密文件
             BufferedReader bReader = new BufferedReader(new InputStreamReader(fIn, this.Encoding)); // 创建带编码的缓冲字符输入流
             FileOutputStream fOut = new FileOutputStream(decryptedFilePath); // 创建文件输出流写入解密后的文件
             BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(fOut, this.Encoding))) { // 创建带编码的缓冲字符输出流

            System.out.println("开始解密...");
            int c; // 用于存储读取到的字符

            // 循环读取加密文件的每个字符
            while ((c = bReader.read()) != -1) {
                // 使用异或运算进行解密，并将解密后的内容写入解密文件
                bWriter.write(c ^ Key);
            }

            // 刷新输出流，确保所有解密数据写入文件
            bWriter.flush();

            // 标志位设为 true 表示解密成功
            flag = true;
            System.out.println("解密完成！");

        } catch (IOException e) {
            // 捕获和处理 IO 异常，输出错误信息并打印堆栈跟踪
            System.err.println("解密过程出错：" + e.getMessage());
            e.printStackTrace();
        }

        // 返回解密是否成功的标志
        return flag;
    }
}

