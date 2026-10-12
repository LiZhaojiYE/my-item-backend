package generator;

import com.lzj.MyItemApp;
import com.lzj.util.MyBatisFlexGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/** @author 李兆基 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyItemApp.class)
public class MyBatisFlexGeneratorTest {

    @Test
    public void getGenerator() {
        MyBatisFlexGenerator.builder()
                .setDbName("myitem")
                .setDir("D:\\Idea-Project\\my-item-backend\\src\\main\\java")
                .build()
                .generate();
    }
}
