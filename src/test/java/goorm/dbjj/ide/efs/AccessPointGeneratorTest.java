package goorm.dbjj.ide.efs;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Disabled
class AccessPointGeneratorTest {

    @Autowired
    private AccessPointGenerator accessPointGenerator;

    @Test
    void generateAccessPoint() {
        String projectId = "testProjectId";
        String accessPointId = accessPointGenerator.generateAccessPoint(projectId);
        System.out.println(accessPointId);
    }
}