package view;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

public class FileView {

    private Scanner scanner = new Scanner(System.in);
    // 显示文件和文件夹的列表
    public void displayFileList(List<String> fileList) {
        System.out.println("当前目录下的文件和文件夹:");
        if (fileList.isEmpty()) {
            System.out.println("目录为空");
        } else {
            for (String fileName : fileList) {
                System.out.println(fileName);
            }
        }
        System.out.println();
    }

    // 显示文件内容
    public void displayFileContent(String content) {
        System.out.println("文件内容:");
        System.out.println(content);
        System.out.println();
    }

    // 显示操作成功的信息
    public void displayMessage(String message) {
        System.out.println(message);
    }

    // 显示错误信息
    public void displayError(String errorMessage) {
        System.err.println("错误: " + errorMessage);
    }

    // 获取文件名
    public String getFileName() {
        System.out.print("请输入文件或文件夹的名称: ");

        return scanner.nextLine();
    }

    // 获取新文件内容
    public String getFileContent() {
        System.out.print("请输入新内容: ");
        return scanner.nextLine();
    }

    // 显示操作结果
    public void displayResult(String message) {
        System.out.println(message);
    }

    // 显示当前工作目录
    public void displayCurrentDirectory(File currentDirectory) {
        System.out.println("当前工作目录: " + currentDirectory);
    }




    // 获取用户输入的新文件名
    public String getNewFileName() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入新文件名: ");
        return scanner.nextLine();
    }

//    // 显示重命名成功的消息
//    public void displayRenameSuccess(String oldName, String newName) {
//        System.out.println("文件 \"" + oldName + "\" 已成功重命名为 \"" + newName + "\".");
//    }
//
//    // 显示错误信息
//    public void displayRenameError(String message) {
//        System.out.println("重命名失败: " + message);
//    }

    //利用方法重载将上述两个方法合并为一个方法，便于代码维护
    public void displayRenameResult(String oldName, String newName) {
        System.out.println("文件 \"" + oldName + "\" 已成功重命名为 \"" + newName + "\".");
    }

    // 方法重载：显示错误信息
    public void displayRenameResult(String message) {
        System.out.println("重命名失败: " + message);
    }



    //压缩功能*********************、
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public String getFilePath() throws IOException {
        System.out.println("请输入文件或文件夹路径:");
        return reader.readLine();
    }


    //压缩功能*********************
}
