package co.qingyu.dbtransfer;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jackie
 * @since 2.0
 */
public class MinioTest {

    private final static Logger log = LoggerFactory.getLogger(MinioTest.class);


    /**
     * 节点地址
     */
    private final String endpoint = "http://192.168.1.57:29000/";

    /**
     * 访问key
     */
    private final String accessKey = "j1Yx3dZpRU1IbMz4GP0s";

    /**
     * 访问密钥
     */
    private final String secretKey = "SAU4UIc8E0ThZ0qHRGSDyFGO2Ng8bWmvLBB5J3e5";

    private final String bucket = "maternal-file-system";


    @Test
    @DisplayName("获取对象信息")
    public void getObjectsTest() {
        MinioClient minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();


    }

    @Test
    @DisplayName("获取所有对象信息")
    public void listObjectsTest() throws Exception {
        MinioClient minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucket).recursive(true).build());
        for (Result<Item> next : results) {
            Item item = next.get();
            if (item.isDir()) {
                log.info("name:{},size:{}", item.objectName(), item.size());
//                minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object().build());

            } else {
                log.info("name:{},size:{},last modified:{} ", item.objectName(), item.size(), item.lastModified());
            }

        }
    }

    @Test
    @DisplayName("存储文件")
    public void putObjectsTest() {
        MinioClient minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();


    }


}
