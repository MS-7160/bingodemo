import java.io.File;

public class CreateResourceDirs {
    public static void main(String[] args) {
        String basePath = "C:\\Users\\Manson\\AndroidStudioProjects\\bingodemo\\app\\src\\main\\res";
        
        File valuesLong = new File(basePath, "values-long");
        File valuesSw360dp = new File(basePath, "values-sw360dp");
        
        if (valuesLong.mkdirs()) {
            System.out.println("Created: " + valuesLong.getAbsolutePath());
        }
        
        if (valuesSw360dp.mkdirs()) {
            System.out.println("Created: " + valuesSw360dp.getAbsolutePath());
        }
    }
}
