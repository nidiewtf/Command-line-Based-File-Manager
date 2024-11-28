package view;

import java.util.Scanner;

//有关输入指令的视图类
public class CommandLineView {
    private Scanner scanner;

    public CommandLineView() {
        scanner = new Scanner(System.in);
    }

    // 显示欢迎信息和可用功能
    public void displayWelcomeMessage() {
        System.out.println("欢迎使用CY文件管理器!");
        System.out.println("可用功能:");
        System.out.println("1. 查看/设置当前工作目录的路径");
        System.out.println("2. 陈列当前目录的文件和文件夹（可设置过滤与排序） ");
        System.out.println("3. 查看文件内容 ");
        System.out.println("4. 创建/删除文件");
        System.out.println("5. 修改文件内容");
        System.out.println("6. 创建/删除文件夹");
        System.out.println("7. 文件重命名");
        System.out.println("8. 拷贝文件或文件夹");
        System.out.println("9. 加密/解密文件(两种方式)");
        System.out.println("10. 压缩/解压文件或文件夹");
        System.out.println("0. 退出程序 ");
        System.out.println("请输入功能编号: ");
    }

    // 读取用户输入的数字选择
    public int getUserChoice() {
        return Integer.parseInt(scanner.nextLine());
    }

    // 读取用户输入字符
    public String getUserInput() {
        return scanner.nextLine();
    }

    // 显示一般信息
    public void displayMessage(String message) {
        System.out.println(message);
    }

    // 显示错误信息
    public void displayError(String errorMessage) {
        System.err.println("错误: " + errorMessage);
    }

    // 关闭Scanner资源
    public void close() {
        scanner.close();
    }

}
