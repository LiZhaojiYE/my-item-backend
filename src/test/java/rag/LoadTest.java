package rag;

import com.lzj.MyItemApp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/** @author 周航宇 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyItemApp.class)
public class LoadTest {

    @Resource
    private VectorStore redisVectorStore;

    //@Test
    public void loadKnowledge() {
        // 使用 TikaDocumentReader 读取文档
        List<Document> documents = new TikaDocumentReader(new ClassPathResource("rag/knowledge.txt")).read();
        log.info("✅读取 {} 条文档", documents.size());

        // 使用 TokenTextSplitter 切分文档
        List<Document> chunks = TokenTextSplitter.builder()
                .build()
                .apply(documents);
        log.info("✅切分 {} 条文档", chunks.size());

        // 存入向量数据库，这个过程会自动调用 embeddingModel 将文本变成向量再存入向量库
        redisVectorStore.add(chunks);
        log.info("✅存储 {} 条文档", chunks.size());
    }
}
