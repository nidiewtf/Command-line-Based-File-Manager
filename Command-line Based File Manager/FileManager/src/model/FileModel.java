package model;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileModel {
    private File currentDirectory; // 当前工作目录的路径

    // 构造方法，初始化当前工作目录为项目根目录
    public FileModel() {
        this.currentDirectory = new File(System.getProperty("user.dir"));
    }

    // 设置当前工作目录
    public void setCurrentDirectory(String path) {
        this.currentDirectory = new File(path);
    }

    // 获取当前工作目录
    public File getCurrentDirectory() {
        return currentDirectory; // 返回当前工作目录的 File 对象
    }

    // 读取文本文件内容
    public String readFileContent(String filename) {
        File file = new File(currentDirectory, filename); // 在当前目录构造指定文件
        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            // 按行读取文件内容
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            return "无法读取文件: " + e.getMessage(); // 异常处理
        }
        return content.toString();
    }

    // 创建文件
    public boolean createFile(String fileName) {
        String invalidChars = "\\ / : * ? \" < > |"; // 非法字符列表
        for (char c : invalidChars.toCharArray()) {
            if (fileName.indexOf(c) >= 0) { // 检查文件名是否合法
                System.out.println("文件名包含非法字符: " + c);
                return false;
            }
        }
        File newFile = new File(currentDirectory, fileName); // 创建文件对象

        try {
            if (!newFile.exists()) { // 判断文件是否已存在
                if (!currentDirectory.exists()) { // 检查目录是否存在
                    System.out.println("当前工作目录不存在!");
                    return false;
                }
                return newFile.createNewFile(); // 创建新文件
            }
            return false; // 文件已存在
        } catch (IOException e) {
            e.printStackTrace();
            return false; // 异常处理
        }
    }

    // 删除文件
    public boolean deleteFile(String fileName) {
        File file = new File(currentDirectory, fileName);
        return file.exists() && file.delete(); // 检查文件是否存在并删除
    }

    // 修改文件内容
    public boolean modifyFileContent(String fileName, String newContent) {
        File file = new File(currentDirectory, fileName);
        try {
            if (file.exists()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(newContent); // 写入新内容
                writer.close();
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false; // 异常处理
        }
    }

    // 创建文件夹
    public boolean createDirectory(String dirName) {
        File newDir = new File(currentDirectory, dirName);
        return newDir.mkdir(); // 创建目录
    }

    // 删除文件夹（如果文件夹为空）
    public boolean deleteDirectory(String dirName) {
        File dir = new File(currentDirectory, dirName);
        if (dir.exists() && dir.isDirectory()) { // 检查目录是否存在
            String[] files = dir.list();
            if (files != null && files.length == 0) { // 确保目录为空
                return dir.delete(); // 删除目录
            }
        }
        return false; // 目录非空或不存在
    }

    // 根据过滤条件列出文件
    public List<String> listFilesWithFilter(String filterType) {
        List<String> filteredFiles = new ArrayList<>();
        File[] files = currentDirectory.listFiles(); // 获取当前目录的所有文件
        if (files != null) {
            Scanner scanner = new Scanner(System.in);
            switch (filterType.toLowerCase()) {
                case "name":
                    System.out.print("请输入要过滤的文件名（模糊匹配）（留下文件名含有输入的文件）:");
                    String nameFilter = scanner.nextLine().toLowerCase();
                    for (File file : files) {
                        if (file.getName().toLowerCase().contains(nameFilter)) {
                            filteredFiles.add(file.getName());
                        }
                    }
                    break;

                case "size":
                    System.out.println("请选择文件大小范围：");
                    System.out.println("1: 0-10KB, 2: 10-20KB, 3: 20-30KB, 4: 30-50KB");
                    System.out.println("5: 50-100KB, 6: 100-500KB, 7: 500KB以上");
                    int sizeChoice = scanner.nextInt();
                    long minSize = 0, maxSize = Long.MAX_VALUE;
                    switch (sizeChoice) {
                        case 1: maxSize = 10 * 1024; break;
                        case 2: minSize = 10 * 1024; maxSize = 20 * 1024; break;
                        case 3: minSize = 20 * 1024; maxSize = 30 * 1024; break;
                        case 4: minSize = 30 * 1024; maxSize = 50 * 1024; break;
                        case 5: minSize = 50 * 1024; maxSize = 100 * 1024; break;
                        case 6: minSize = 100 * 1024; maxSize = 500 * 1024; break;
                        case 7: minSize = 500 * 1024; break;
                        default: System.out.println("无效选择。"); return filteredFiles;
                    }
                    for (File file : files) {
                        long fileSize = getFileSize(file);
                        if (fileSize >= minSize && fileSize <= maxSize) {
                            filteredFiles.add(file.getName() + " (" + fileSize + " bytes)");
                        }
                    }
                    break;

                case "type":
                    System.out.print("请输入类型（file/folder）：");
                    String typeChoice = scanner.nextLine().toLowerCase();
                    for (File file : files) {
                        if ((typeChoice.equals("file") && !file.isDirectory()) ||
                                (typeChoice.equals("folder") && file.isDirectory())) {
                            filteredFiles.add(file.getName() + (file.isDirectory() ? " [目录]" : " [文件]"));
                        }
                    }
                    break;

                case "date":
                    System.out.print("请输入最后修改时间（yyyy-MM-dd HH:mm）：");
                    String dateInput = scanner.nextLine();
                    long dateFilter;
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        dateFilter = sdf.parse(dateInput).getTime();
                    } catch (ParseException e) {
                        System.out.println("时间格式错误，请使用：yyyy-MM-dd HH:mm");
                        return filteredFiles;
                    }
                    for (File file : files) {
                        if (file.lastModified() > dateFilter) {
                            filteredFiles.add(file.getName() + " [最后修改时间: " + getFileDate(file) + "]");
                        }
                    }
                    break;

                default:
                    System.out.println("无效的过滤条件。");
                    break;
            }
        }
        return filteredFiles; // 返回过滤后的文件列表
    }

    // 获取文件大小
    public long getFileSize(File file) {
        return file.length();
    }

    // 获取文件最后修改日期
    public String getFileDate(File file) {
        long lastModified = file.lastModified();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(lastModified));
    }

    // 文件重命名功能
    public boolean renameFile(String oldFilePath, String newFileName) {
        File oldFile = new File(oldFilePath);
        if (oldFile.exists()) { // 检查文件是否存在
            File newFile = new File(oldFile.getParent(), newFileName);
            return oldFile.renameTo(newFile); // 执行重命名操作
        }
        return false; // 文件不存在
    }
}
