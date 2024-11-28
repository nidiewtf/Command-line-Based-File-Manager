package controller;

import model.FileModel;
import utils.FileCopy;
import utils.FileEncryption;
import utils.FileEncryption_xor;
import view.CommandLineView;
import view.FileView;
import java.io.File;
import java.util.*;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileController {
    private FileModel fileModel;
    private FileView fileView;
    private ExecutorService executorService = Executors.newFixedThreadPool(2); // 使用线程池来管理后台任务
    private FileCopy fileCopy;
    private FileEncryption fileEncryption;
    private CommandLineView view;
    private FileEncryption_xor fileEncryption_xor;



    public FileController() throws Exception {
        fileModel = new FileModel();
        fileView = new FileView();
        fileCopy = new FileCopy();
        fileEncryption = new FileEncryption();
        view = new CommandLineView();
        fileEncryption_xor = new FileEncryption_xor();
    }

    // 设置工作目录方法
    public void setCurrentDirectory() {
        Scanner scanner = new Scanner(System.in); // 创建 Scanner 对象用于读取用户输入
        fileView.displayMessage("请输入新的工作目录路径，直接回车使用当前默认路径: ");

        String path = scanner.nextLine(); // 获取用户输入的路径

        // 如果用户没有输入路径，则使用当前默认路径
        if (path.isEmpty()) {
            File currentDirectory = fileModel.getCurrentDirectory();
            path = currentDirectory.getAbsolutePath(); // 使用当前工作目录的绝对路径
        }

        fileModel.setCurrentDirectory(path); // 设置当前工作目录
        fileView.displayMessage("当前工作目录已设置为: " + path); // 显示设置成功的消息
    }

    // 列出文件的方法，包含过滤与排序功能
    public void listFiles() {
        // 获取当前工作目录
        File currentDirectory = fileModel.getCurrentDirectory(); // 获取当前目录为currentDirectory

        // 提示用户输入过滤条件
        fileView.displayMessage("请输入过滤条件（name/size/type/date），或直接回车以列出所有文件: ");
        Scanner scanner = new Scanner(System.in);
        String filterType = scanner.nextLine(); // 获取过滤条件filterType

        // 提示用户输入排序依据
        fileView.displayMessage("请输入排序依据（name/size/date），或直接回车以默认排序: ");
        String sortBy = scanner.nextLine(); // 获取排序依据sortBy

        // 提示用户输入排序顺序
        boolean ascending = true; // 默认升序
        if (!sortBy.isEmpty()) {
            fileView.displayMessage("请选择排序顺序（1: 升序，2: 降序）: ");
            int orderChoice = scanner.nextInt();
            ascending = (orderChoice == 1); // 用户选择1为升序，选择2为降序
            scanner.nextLine(); // 清除输入缓冲区
        }

        List<String> files;

        // 根据用户的输入获取文件列表
        if (!filterType.isEmpty()) {
            // 根据过滤条件获取文件列表
            files = fileModel.listFilesWithFilter(filterType); // 传入当前目录
        } else {
            // 列出当前目录下的所有文件
            String[] fileNames = currentDirectory.list();
            files = (fileNames != null) ? new ArrayList<>(Arrays.asList(fileNames)) : new ArrayList<>(); // 获取所有文件名
        }

        // 根据排序依据进行排序
        if (!sortBy.isEmpty()) {
            sortFiles(files, sortBy, ascending); // 排序文件，传入排序顺序
        }

        // 显示文件列表
        fileView.displayFileList(files);
    }

    // 控制创建文件
    public void createFile() {
        String fileName = fileView.getFileName();
        if (fileModel.createFile(fileName)) {
            fileView.displayResult("文件创建成功！");
        } else {
            fileView.displayResult("文件创建失败");
        }
    }

    // 控制删除文件
    public void deleteFile() {
        String fileName = fileView.getFileName();
        if (fileModel.deleteFile(fileName)) {
            fileView.displayResult("文件删除成功！");
        } else {
            fileView.displayResult("文件删除失败，文件不存在。");
        }
    }

    // 修改文件内容
    public void modifyFile() {
        String fileName = fileView.getFileName();
        String newContent = fileView.getFileContent();
        if (fileModel.modifyFileContent(fileName, newContent)) {
            fileView.displayResult("文件内容修改成功！");
        } else {
            fileView.displayResult("文件修改失败，文件不存在。");
        }
    }

    // 控制创建文件夹
    public void createDirectory() {
        String dirName = fileView.getFileName();
        if (fileModel.createDirectory(dirName)) {
            fileView.displayResult("文件夹创建成功！");
        } else {
            fileView.displayResult("文件夹创建失败，可能文件夹已存在。");
        }
    }

    // 控制删除文件夹
    public void deleteDirectory() {
        String dirName = fileView.getFileName();
        if (fileModel.deleteDirectory(dirName)) {
            fileView.displayResult("文件夹删除成功！");
        } else {
            fileView.displayResult("文件夹删除失败，文件夹不存在或非空。");
        }
    }


    // 文件排序方法
    private void sortFiles(List<String> files, String sortBy, boolean ascending) {
        // 根据用户指定的排序依据进行文件排序
        switch (sortBy.toLowerCase()) { // 将输入的排序依据转换为小写以进行比较
            case "name":
                // 按文件名排序，使用 Collections.sort() 默认的字典顺序
                if (ascending) {
                    Collections.sort(files); // 升序
                } else {
                    Collections.sort(files, Collections.reverseOrder()); // 降序
                }
                break;
            case "size":
                // 按文件大小排序
                if (ascending) {
                    Collections.sort(files, Comparator.comparing(fileName -> {
                        // 使用文件名创建 File 对象，路径来自当前工作目录
                        File file = new File(fileModel.getCurrentDirectory(), fileName);
                        // 获取文件大小并返回用于排序
                        return fileModel.getFileSize(file);
                    }));
                } else {
                    Collections.sort(files, Comparator.comparing(fileName -> {
                        File file = new File(fileModel.getCurrentDirectory(), (String) fileName);
                        return fileModel.getFileSize(file);
                    }).reversed()); // 降序排序
                }
                break;
            case "date":
                // 按最后修改日期排序
                if (ascending) {
                    Collections.sort(files, Comparator.comparing(fileName -> {
                        File file = new File(fileModel.getCurrentDirectory(), fileName);
                        return fileModel.getFileDate(file);
                    }));
                } else {
                    Collections.sort(files, Comparator.comparing(fileName -> {
                        File file = new File(fileModel.getCurrentDirectory(), (String) fileName);
                        return fileModel.getFileDate(file);
                    }).reversed()); // 降序排序
                }
                break;
            default:
                // 如果排序依据不符合已知类型，显示消息并使用默认排序
                fileView.displayMessage("未知的排序依据，使用默认排序。");
        }
    }


    // 显示文件内容的方法
    public void viewFileContent(String filename) {
        String content = fileModel.readFileContent(filename);
        fileView.displayFileContent(content);
    }

    //显示当前工作路径
    public void displayCurrentDirectory(){
        File filef = fileModel.getCurrentDirectory();
        fileView.displayCurrentDirectory(filef);
    }

    //文件重命名功能
    public void renameFile(String oldFilePath) {
        // 获取用户输入的新文件名
        String newFileName = fileView.getNewFileName();

        // 调用模型的重命名方法
        boolean success = fileModel.renameFile(oldFilePath, newFileName);

        // 根据操作结果更新视图
        if (success) {
            fileView.displayRenameResult(oldFilePath, newFileName);
        } else {
            fileView.displayRenameResult("文件重命名失败，检查文件是否存在或路径是否正确.");
        }
    }


    //拷贝**************************************************************************************************
    //未加入线程机制的拷贝方法
//    public boolean copy(String fileName,String destPath) {
//        boolean flag = false;
//        File currentDirectory=fileModel.getCurrentDirectory();
//        String currentDirectoryPath = currentDirectory.getAbsolutePath();//File类型转String类型
//
//        Path fullPath = Paths.get(currentDirectoryPath, fileName);
//        File file = fullPath.toFile();
//        String currPath=file.getAbsolutePath();
//        System.out.println("源文件路径：" + file);
//
//        if (!file.exists()) { // 需要拷贝的文件/文件夹不存在；
//            System.out.println("拷贝失败！需要拷贝的文件/文件夹不存在");
//            return flag;
//        }
//
//
//        // 需要拷贝的文件/文件夹存在；
//        if (file.isFile()) { flag = fileModel.copyFile(currPath, destPath); } // 需要拷贝的是文件，调用copyFile()；
//        else { flag = fileModel.copyDirectory(currPath, destPath); } 			// 需要拷贝的是文件夹，调用copyDirectory()；
//
//        if (flag) {
//            System.out.println("拷贝成功！");
//        }
//        else {
//            System.out.println("拷贝失败！");
//        }
//        return flag;
//    }





    //加入线程机制的拷贝方法
    //此方法用于支持前台或后台执行
    public Future<Boolean> copy(String fileName, String destPath, boolean isBackground) {
        if (isBackground) {
            // 异步执行拷贝任务，返回 Future 用于跟踪任务状态
            return executorService.submit(() -> copyTask(fileName, destPath));
        } else {
            // 同步执行
            copyTask(fileName, destPath);
            return null;
        }
    }

    // 实际的拷贝任务逻辑，提取到单独的函数中
    public Boolean copyTask(String fileName, String destPath) {
        boolean flag = false; // 初始化拷贝成功标志位为 false，表示拷贝尚未完成或失败
        File currentDirectory = fileModel.getCurrentDirectory(); // 获取当前工作目录
        String currentDirectoryPath = currentDirectory.getAbsolutePath(); // 获取当前工作目录的绝对路径

        Path fullPath = Paths.get(currentDirectoryPath, fileName); // 生成文件的完整路径
        File file = fullPath.toFile(); // 将路径转换为 File 对象
        String currPath = file.getAbsolutePath(); // 获取源文件的绝对路径

        // 输出源文件路径到控制台，便于调试或确认路径
        System.out.println("源文件路径：" + file);

        // 检查文件是否存在，不存在则输出失败信息并返回 false
        if (!file.exists()) {
            System.out.println("拷贝失败！需要拷贝的文件/文件夹不存在");
            return false;
        }

        // 根据源是否是文件，调用相应的拷贝方法
        if (file.isFile()) {
            flag = fileCopy.copyFile(currPath, destPath); // 如果是文件，调用文件拷贝方法
        } else {
            flag = fileCopy.copyDirectory(currPath, destPath); // 如果是目录，调用目录拷贝方法
        }

        // 根据拷贝结果输出相应的信息
        if (flag) {
            System.out.println("拷贝成功！"); // 拷贝成功时的提示
        } else {
            System.out.println("拷贝失败！"); // 拷贝失败时的提示
        }

        return flag; // 返回拷贝结果，true 表示成功，false 表示失败
    }





    //拷贝**************************************************************************************************







    //加密**************************************************************************************************
    //加密过程
    public void encryptFile(String fileName) {
        System.out.println("请选择加密方式1AES/2异或xor");
        int choicee = view.getUserChoice();
        if(choicee==1) {
            boolean isSuccess = fileEncryption.encrypFile(fileName);
            if (isSuccess) {
                System.out.println("文件成功加密：" + fileName);
            } else {
                System.out.println("文件加密失败：" + fileName);
            }
        }
        else if(choicee==2) {
            boolean isSuccess = fileEncryption_xor.encrypFile(fileName);
            if (isSuccess) {
                System.out.println("文件成功加密：" + fileName);
            } else {
                System.out.println("文件加密失败：" + fileName);
            }
        }
    }

    // 解密过程
    public void decryptFile(String fileName) {
        System.out.println("请选择解密方式1AES/2异或xor");
        int choiceee = view.getUserChoice();
        if(choiceee == 1) {
            boolean isSuccess = fileEncryption.decrypFile(fileName);
            if (isSuccess) {
                System.out.println("文件成功解密：" + fileName);
            } else {
                System.out.println("文件解密失败：" + fileName);
            }
        }
        else if(choiceee == 2){
            boolean isSuccess = fileEncryption_xor.decrypFile(fileName);
            if (isSuccess) {
                System.out.println("文件成功解密：" + fileName);
            } else {
                System.out.println("文件解密失败：" + fileName);
            }
        }
    }
    //加密**************************************************************************************************
}
