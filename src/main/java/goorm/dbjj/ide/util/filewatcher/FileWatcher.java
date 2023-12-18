package goorm.dbjj.ide.util.filewatcher;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * FileWatcher
 *
 * FileWatcher은 EFS 스토리지의 디렉터리를 감시하고,
 * 디렉터리에 변경사항이 생길 때 이를 사용자에게 알리는 역할을 합니다.
 * 이를 통해 File의 CRUD 로직에서 생기는 '사용자에게 알리는 책임'을 분리해낼 수 있습니다
 */
@Slf4j
@Component("myFileWatcher") //SslAutoConfiguration에 이미 FileWatcher가 등록되어 있어서 이름을 변경해줘야 합니다.
public class FileWatcher {

    public void watch(String dir) throws Exception {

        FileAlterationObserver observer = new FileAlterationObserver(dir);

        observer.addListener(new FileAlterationListenerAdaptor() {

            @Override
            public void onDirectoryChange(File directory) {
//                System.out.println("modified directory = " + directory);
            }

            @Override
            public void onDirectoryCreate(File directory) {
                sendToUser(extractProjectId(directory.getPath()));
            }

            @Override
            public void onDirectoryDelete(File directory) {
                System.out.println("delete directory = " + directory);
            }

            /**
             * 현재 swap 파일의 생성 역시 감지되는 이슈가 있습니다.
             * @param file The file created (ignored)
             */
            @Override
            public void onFileCreate(File file) {
                sendToUser(extractProjectId(file.getPath()));
            }

            @Override
            public void onFileDelete(File file) {
                sendToUser(extractProjectId(file.getPath()));
            }

        });

        // 1초마다 변경사항을 감지합니다.
        FileAlterationMonitor monitor = new FileAlterationMonitor(1000);
        monitor.addObserver(observer);

        monitor.start();
        log.info("FileWatcher Start, dir : {}", dir);
    }

    /**
     * 프로젝트의 파일이 변경되었을 때, 해당 프로젝트를 사용하는 유저들에게 변경사항을 알립니다.
     * WS로 변경됐음을 전송합니다.
     * @param projectId
     */
    private void sendToUser(String projectId) {
        if(projectId != null) {
            //do nothing
        } else {
            //WS로 변경사항을 전송합니다.
            System.out.println("sendToUser = " + projectId);
        }
    }

    /**
     * EFS 스토리지의 디렉터리 경로에서 프로젝트 아이디를 추출합니다.
     * 로컬에서는 작동하지 않습니다.
     * @param path
     * @return
     */
    private String extractProjectId(String path) {
        try { // 로컬 개발환경에서 NPE 발생을 막아주기 위해 try로 감싸줍니다.
            String[] split = path.split("/");
            return split[5];
        } catch (Exception e) {
            return null;
        }
    }
}