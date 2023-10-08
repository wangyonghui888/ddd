import com.panda.sport.rcs.ApiApplication;
import com.panda.sport.rcs.db.service.IUserProfileUserTagChangeRecordService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Test;
import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiApplication.class)
public class UserTagTest {

    @Resource
    private IUserProfileUserTagChangeRecordService service;


//    @Test
//    public void batchRelationHistoryData() {
//       service.batchRelationHistoryData();
//    }
     @Test
     public void test2() throws Exception {

     }
}
