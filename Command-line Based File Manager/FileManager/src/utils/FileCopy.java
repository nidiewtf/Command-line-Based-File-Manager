package utils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileCopy {
    //拷贝文件
    public boolean copyFile(String filePath, String destPath) {
        boolean flag = false;
        File file = new File(filePath);
        Path fullPath = Paths.get(destPath, file.getName());
        File copyFile = fullPath.toFile();

        // 获取文件名及扩展名
        String fileFullName = file.getName();
        String filePriName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
        String fileExtenName = fileFullName.substring(fileFullName.lastIndexOf("."));

        // 如果目标文件存在，则在文件名后添加 "-副本"
        if (copyFile.exists()) {
            copyFile = new File(destPath + filePriName + "-副本" + fileExtenName);
        }

        // 创建输入输出流对象
        try (FileInputStream fIn = new FileInputStream(file);
             BufferedInputStream bIn = new BufferedInputStream(fIn);
             FileOutputStream fOut = new FileOutputStream(copyFile);
             BufferedOutputStream bOut = new BufferedOutputStream(fOut)) {

            // 记录开始时间
            long startTime = System.currentTimeMillis();

            // 获取文件总大小
            long totalBytes = file.length();
            long copiedBytes = 0;
            byte[] buffer = new byte[1024];
            int length;

            // 读取并写入文件数据
            while ((length = bIn.read(buffer)) != -1) {
                bOut.write(buffer, 0, length);
                copiedBytes += length;

                // 计算并显示进度
                int progress = (int) ((copiedBytes * 100) / totalBytes);
                System.out.print("\r拷贝进度: " + progress + "%");
            }
            bOut.flush();

            // 记录结束时间并计算耗时
            long endTime = System.currentTimeMillis();
            System.out.println("\n文件拷贝完成！耗时: " + (endTime - startTime) + " 毫秒");

            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    //拷贝文件夹
    public boolean copyDirectory(String dirPath, String destPath) {
        boolean flag = false;
        File sourceDir = new File(dirPath);
        File destDir = new File(destPath, sourceDir.getName());

        // 检查源目录是否存在
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            System.err.println("源目录不存在或不是目录：" + dirPath);
            return false;
        }

        // 如果目标目录已存在，则更改目标目录名称以避免覆盖
        if (destDir.exists()) {
            destDir = new File(destPath, sourceDir.getName() + "-副本");
        }

        // 创建目标目录
        destDir.mkdirs();

        // 获取源目录下所有文件和子目录的列表
        File[] files = sourceDir.listFiles();
        if (files == null) {
            System.err.println("无法读取源目录内容：" + dirPath);
            return false;
        }

        // 记录拷贝开始时间
        long startTime = System.currentTimeMillis();

        // 遍历源目录下的每个文件和子目录
        for (File file : files) {
            File newDestFile = new File(destDir, file.getName());
            String sourceFilePath = file.getAbsolutePath();
            String destinationFilePath = newDestFile.getAbsolutePath();

            // 如果是文件，则复制文件并显示进度
            if (file.isFile()) {
                System.out.println("正在复制文件：" + file.getName());
                if (!copyFile(sourceFilePath, destDir.getAbsolutePath())) {
                    return false;
                }
            }
            // 如果是目录，则递归复制子目录
            else if (file.isDirectory()) {
                System.out.println("正在复制目录：" + file.getName());
                if (!copyDirectory(sourceFilePath, newDestFile.getParent())) {
                    return false;
                }
            }
        }

        // 记录拷贝结束时间并计算耗时
        long endTime = System.currentTimeMillis();
        System.out.println("目录拷贝完成！总耗时: " + (endTime - startTime) + " 毫秒");

        flag = true;
        return flag;
    }
}
