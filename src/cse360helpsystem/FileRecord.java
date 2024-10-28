package cse360helpsystem; 
/**
 * This class manages the file record
 */
public class FileRecord {
    private String filename; //file neam 
    private byte[] fileData; //file data
//constructor 
    public FileRecord(String filename, byte[] fileData) {
        this.filename = filename;
        this.fileData = fileData;
    }
//getter methods 

	public String getFilename() {
		return filename;
	}
	
	public byte[] getFileData() {
		return fileData;
	}

}
