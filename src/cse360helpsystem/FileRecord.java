package cse360helpsystem; 
/**
 * This class manages the file record
 */
public class FileRecord {
    private String filename; //file name 
    private String fileData; //file data
//constructor 
    public FileRecord(String filename, String fileData) {
        this.filename = filename;
        this.fileData = fileData;
    }
//getter methods 

	public String getFilename() {
		return filename;
	}
	
	public String getFileData() {
		return fileData;
	}

}
