package controller;

import java.io.IOException;

;
import utils.FileZip;
import view.CommandLineView;
import view.FileView;
import java.util.concurrent.Future;

//有关指令的控制类
public class CommandController {
    private FileController fileController;
    private CommandLineView view;
    private FileView fileView;
    private FileZip fileZip;



    public CommandController() throws Exception {
        fileController = new FileController();
        view = new CommandLineView();
        fileView = new FileView();
        fileZip = new FileZip();
    }

    public void start() {//启动调用的方法
        while (true) {
            view.displayWelcomeMessage();//第一步展示欢迎信息
            int choice = view.getUserChoice();//第二步获取用户选择
            processChoice(choice);//第三步解析用户选择
        }
    }

    private void processChoice(int choice) {
        switch (choice) {
            case 1://查看/设置当前工作目录的路径
                view.displayMessage("请选择1查看功能/2设置功能");
                int choice1 = view.getUserChoice();
                switch (choice1) {
                    case 1://查看当前路径
                        fileController.displayCurrentDirectory();
                        break;
                    case 2://设置当前路径
                        fileController.setCurrentDirectory();
                        break;
                }
                break;
            case 2://当前文件夹下内容罗列（包含筛选与排序功能）
                fileController.listFiles();
                break;
            case 3://查看文件内容
                view.displayMessage("请输入文件名: (需带有文件后缀名)");
                String filename = view.getUserInput();
                fileController.viewFileContent(filename);
                break;
            case 4://创建/删除文件
                view.displayMessage("请选择1创建文件/2删除文件");
                int choice2 = view.getUserChoice();
                switch (choice2) {
                    case 1://创建文件
                        fileController.createFile();
                        break;
                    case 2://删除文件
                        fileController.deleteFile();
                        break;
                }
                break;

            case 5://修改文件内容
                fileController.modifyFile();
                break;
            case 6://创建/删除文件夹
                view.displayMessage("请选择1创建文件夹/2删除文件夹");
                int choice3 = view.getUserChoice();
                switch (choice3) {
                    case 1://创建文件夹
                        fileController.createDirectory();
                        break;
                    case 2://删除文件夹
                        fileController.deleteDirectory();
                        break;
                }
                break;
            case 7://文件重命名
                view.displayMessage("请输入待重命名文件的路径(不要带有引号)");
                String lujing = view.getUserInput();
                fileController.renameFile(lujing);
                break;

            case 8://拷贝文件

                //未加入线程机制的拷贝
//                System.out.print("请输入需要拷贝的文件名或文件夹名：");
//                String fileName = view.getUserInput();
//                System.out.print("请输入拷贝的目标地址：");
//                String destfileName = view.getUserInput();
//                fileController.copy(fileName,destfileName);
//                break;


                //加入了线程机制的拷贝
                // 提示用户选择前台或后台执行模式
                view.displayMessage("请选择1前台执行/2后台执行");
                int executionChoice = view.getUserChoice();
                // 根据用户选择，判断是否在后台执行
                boolean isBackground = executionChoice == 2;

                // 提示用户输入需要拷贝的文件或文件夹名
                System.out.println("请输入需要拷贝的文件名或文件夹名：");
                String fileName = view.getUserInput();
                // 提示用户输入拷贝的目标路径
                System.out.println("请输入拷贝的目标地址：");
                String destPath = view.getUserInput();

                // 调用 fileController 的 copy 方法，返回一个 Future 对象，用于跟踪拷贝任务的状态
                Future<Boolean> future = fileController.copy(fileName, destPath, isBackground);

                // 判断是否为后台执行模式并且 future 对象不为空
                if (isBackground && future != null) {
                    // 如果是后台执行，通知用户任务已提交
                    view.displayMessage("任务已在后台执行，可以继续其他操作。");
                } else {
                    // 如果是前台执行模式，则等待任务完成
                    //                        // 获取任务的执行结果，如果成功返回 true，失败返回 false
//                        Boolean result = future.get();
//                        // 根据结果提示用户拷贝是否成功
//                        if (result) {
//                            view.displayMessage("拷贝成功！");
//                        } else {
//                            view.displayMessage("拷贝失败！");
//                        }
                }
                break;


            case 9://加密文件

                view.displayMessage("请选择1加密文件/2解密文件");
                int choice5 = view.getUserChoice();
                switch (choice5) {
                    case 1://加密文件
                        System.out.print("请输入你想要加密的文件名：");
                        String encryptFileName = view.getUserInput();
                        fileController.encryptFile(encryptFileName);
                        break;
                    case 2://解密文件
                        System.out.print("请输入你想要解密的文件名：");
                        String decryptFileName = view.getUserInput();
                        fileController.decryptFile(decryptFileName);
                        break;
                }

                break;
            case 10: // 压缩或解压文件或文件夹
            {
                try {
                    String filePath = fileView.getFilePath();
                    System.out.println("请选择1压缩文件/2解压文件");
                    int choice6 = view.getUserChoice();

                    if (choice6 == 1) {
                        fileZip.zipFile(filePath);
                        fileView.displayResult("压缩完成: " + filePath + ".zip");
                    } else if (choice6 == 2) {
                        fileZip.unzipFile(filePath);
                        fileView.displayResult("解压完成: " + filePath.replace(".zip", ""));
                    } else {
                        fileView.displayError("无效的操作类型");
                    }
                } catch (IOException e) {
                    fileView.displayError("操作失败: " + e.getMessage());
                }
                break;
            }


            case 0://退出程序
                view.close();
                System.exit(0);
                break;
            default:
                view.displayError("无效的选择，请输入有效的编号。");
        }
    }
}
