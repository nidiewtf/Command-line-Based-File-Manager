package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileZip {
    // 压缩方法
    public void zipFile(String sourceFilePath) throws IOException {
        File sourceFile = new File(sourceFilePath);
        String zipFileName = sourceFile.getParent() + File.separator + sourceFile.getName() + ".zip";
        try (FileOutputStream fos = new FileOutputStream(zipFileName);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            compressFile(zos, sourceFile, sourceFile.getName());
        }
    }

    private void compressFile(ZipOutputStream zos, File file, String basePath) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    compressFile(zos, f, basePath + File.separator + f.getName());
                }
            } else {
                zos.putNextEntry(new ZipEntry(basePath + File.separator));
                zos.closeEntry();
            }
        } else {
            try (FileInputStream fis = new FileInputStream(file)) {
                zos.putNextEntry(new ZipEntry(basePath));
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
            }
        }
    }

    // 解压方法
    public void unzipFile(String zipFilePath) throws IOException {
        // 创建表示 .zip 文件路径的 File 对象
        File zipFile = new File(zipFilePath);

        // 解压目标目录路径，将 .zip 文件名去除扩展名后作为目标文件夹名称
        //String destDirPath = zipFile.getParent() + File.separator + zipFile.getName().replace(".zip", "");
        String destDirPath = zipFile.getParent();
        // 创建目标文件夹
        File destDir = new File(destDirPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        // 创建 ZipInputStream 对象来读取压缩文件内容
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;

            // 逐个读取 Zip 文件中的条目（文件或文件夹）
            while ((entry = zis.getNextEntry()) != null) {
                // 只获取文件名，忽略路径信息
                String fileName = new File(entry.getName()).getName();
                File outputFile = new File(destDirPath, fileName);

                if (entry.isDirectory()) {
                    // 如果条目是一个目录，则创建相应目录
                    outputFile.mkdirs();
                } else {
                    // 如果条目是文件，创建文件并写入内容
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }

                // 关闭当前 ZipEntry
                zis.closeEntry();
            }
        }
    }
}
