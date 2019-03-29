package ouday.challenge.com.app_repository.httpManager;

import java.net.URLConnection;

/**
 * Wrapper class for any file to be uploaded
 */

public class FileToUpload {
    private String filePath;
    private String fileName;
    private String mime;
    private String fileParamName = "file";

    public static FileToUpload File(String filePath){
        return new FileToUpload(filePath);
    }

    public FileToUpload(String filePath, String fileName, String mime){
        setFileName(fileName);
        setFilePath(filePath);
        setMime(mime);
    }

    public FileToUpload(String filePath){
        setFilePath(filePath);
        setFileName(filePath.substring(filePath.lastIndexOf('/') + 1));
        setMime(findMimeFromExtensiont(this.getFileName().split("\\.")[1]));
    }

    private String findMimeFromExtensiont(String fileExtension) {
        return URLConnection.guessContentTypeFromName(fileExtension);
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMime() {
        return mime;
    }

    public FileToUpload setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public FileToUpload setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public FileToUpload setMime(String mime) {
        this.mime = mime;
        return this;
    }

    public String getFileParamName() {
        return fileParamName;
    }

    public FileToUpload setFileParamName(String fileParamName) {
        this.fileParamName = fileParamName;
        return this;
    }
}