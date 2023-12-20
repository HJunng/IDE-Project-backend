package goorm.dbjj.ide.domain.fileDirectory;

import goorm.dbjj.ide.model.dto.FileResponseDto;
import goorm.dbjj.ide.storageManager.FileIoStorageManager;
import goorm.dbjj.ide.storageManager.model.Resource;
import goorm.dbjj.ide.storageManager.model.ResourceDto;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static goorm.dbjj.ide.storageManager.StorageManager.RESOURCE_SEPARATOR;

@Slf4j
@Component
public class FileIoProjectFileService implements ProjectFileService {
    //    private static final String ROOT_DRICETORY = "/home/ubunto/efs/app";

    @Value("${app.efs-root-directory}")
    private  String ROOT_DIRECTORY;

    private String getFullPath(String projectId, String subPath) {
        if (StringUtils.isEmpty(subPath)) {
            log.trace("전체 경로 = {}", ROOT_DIRECTORY + RESOURCE_SEPARATOR + projectId);
            return ROOT_DIRECTORY + RESOURCE_SEPARATOR + projectId; // "/app" + "/" + projectId -> /app/projectId
        }
        log.trace("전체 경로 = {}", ROOT_DIRECTORY + RESOURCE_SEPARATOR + projectId + subPath );
        return ROOT_DIRECTORY + RESOURCE_SEPARATOR + projectId + subPath;
    }

    private final FileIoStorageManager storageManager;

    public FileIoProjectFileService(FileIoStorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @Override
    public void initProjectDirectory(String projectId) {
        log.trace("Service.initProjectDirectory - 첫 로그인 시 프로젝트 디렉토리 생성");
        String directoryPath = getFullPath(projectId, "");
        storageManager.createDirectory(directoryPath);
    }

    @Override
    public void createDirectory(String projectId, String path) {
        log.trace("Service.createDirectory - 디렉토리 생성");
        String fullPath = getFullPath(projectId, path);
        storageManager.createDirectory(fullPath);
    }

    @Override
    public List<ResourceDto> loadProjectDirectory(String projectId) {
        log.trace("Service.loadProjectDirectory - 디렉토리 구조 조회");
        String directoryPath = buildFullPath(projectId);
        Resource directory = loadDirectory(directoryPath);
        return convertResourceListToDtoList(directory.getChildren());
    }

    private String buildFullPath(String projectId) {
        return getFullPath(projectId, "");
    }

    private Resource loadDirectory(String fullPath) {

        return storageManager.loadDirectory(fullPath);
    }

    private List<ResourceDto> convertResourceListToDtoList(List<Resource> resources) {
        List<ResourceDto> resourceDtos = new ArrayList<>();
        if (resources != null) {
            for (Resource resource : resources) {
                resourceDtos.add(convertResourceToResourceDto(resource));
            }
        }
        return resourceDtos;
    }

    private ResourceDto convertResourceToResourceDto(Resource resource) {
        List<ResourceDto> childDtos = null;

        if (resource.isDirectory()) {
            childDtos = new ArrayList<>();
            if (resource.getChildren() != null) {
                for (Resource child : resource.getChildren()) {
                    childDtos.add(convertResourceToResourceDto(child));
                }
            }
        }

        return ResourceDto.builder()
                .id(UUID.randomUUID().toString().substring(0,6))
                .name(resource.getName())
                .type(resource.getResourceType().toString())
                .children(childDtos)
                .build();
    }

    @Override
    public void moveFile(String projectId, String oldPath, String newPath) { // 생성과 삭제 조합 -> oldPath에서 삭제하고 newPath에서 다시 생성 => File 단위 O, Directory 단위 X
        log.trace("Service.moveFile - 파일 이동");
        String fileOldPath = getFullPath(projectId, oldPath);
        String fileNewPath = getFullPath(projectId, newPath);
        Resource resource = storageManager.loadFile(fileOldPath);
        storageManager.saveFile(fileNewPath, resource.getContent());
        storageManager.deleteFile(fileOldPath);
    }



    @Override
    public void saveFile(String projectId, String filePath, String content) {
        log.trace("Service.saveFile - 파일 수정 및 저장");
        String fullPath = getFullPath(projectId, filePath);
        String[] splitedPath = filePath.split(RESOURCE_SEPARATOR);
        String fileName = splitedPath[splitedPath.length - 1];
        storageManager.saveFile(fullPath, content);
    }

    @Override
    public FileResponseDto loadFile(String projectId, String filePath) { // 재귀로 로드 // 관심사 분리
        log.trace("Service.loadFile - 파일 조회");
        String fullPath = getFullPath(projectId, filePath);
        Resource resource = storageManager.loadFile(fullPath);
        return toFileResponseDto(resource, fullPath);

    }


    @Override
    public void deleteFile(String projectId, String filePath) {
        log.trace("Service.deleteFile - 파일 및 디렉토리 삭제");
        String fullPath = getFullPath(projectId, filePath);
        storageManager.deleteFile(fullPath);
    }

    private FileResponseDto toFileResponseDto(Resource resource, String filePath) {

        return FileResponseDto.builder()
                .fileName(resource.getName())
                .content(resource.getContent())
                .filePath(filePath)
                .build();
    }
}


