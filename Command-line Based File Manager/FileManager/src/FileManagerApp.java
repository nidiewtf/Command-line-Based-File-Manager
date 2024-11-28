import controller.CommandController;

//主控制类
public class FileManagerApp {
    public static void main(String[] args) throws Exception {
        System.out.println("欢迎来到文件管理系统！");
        CommandController controller = new CommandController();
        controller.start();
    }
}
