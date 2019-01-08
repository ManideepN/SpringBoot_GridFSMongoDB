
package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

@RestController
@RequestMapping("/api")
public class RestControllerAPIs {

	@Autowired
	GridFsOperations gridOperations;
	
	// this variable is used to store ImageId for other actions like: findOne or delete
	private String imageFileId = "";

	@GetMapping("/save")
	public String saveFiles() throws FileNotFoundException {
		// Define metaData
		DBObject metaData = new BasicDBObject();
		metaData.put("organization", "JavaSampleApproach");
		
		/**
		 * 1. save an image file to MongoDB
		 */
		
		// Get input file
		InputStream imageStream = new FileInputStream("D:\\JSA\\jsa-logo.png");
		
		metaData.put("type", "image");
		
		// Store file to MongoDB
		imageFileId = gridOperations.store(imageStream, "jsa-logo.png", "image/png", metaData).toString();
		System.out.println("ImageFileId = " + imageFileId);

		/**
		 * 2. save text files to MongoDB
		 */
		
		// change metaData
		metaData.put("type", "data");

		// Store files to MongoDb
		gridOperations.store(new FileInputStream("D:\\JSA\\text-1.txt"), "text-1.txt", "text/plain", metaData);
		gridOperations.store(new FileInputStream("D:\\JSA\\text-2.txt"), "text-2.txt", "text/plain", metaData);

		return "Done";
	}
	
	@GetMapping("/retrieve/imagefile")
	public String retrieveImageFile() throws IOException{
		// read file from MongoDB
//		GridFSFindIterable imageFile = gridOperations.find(new Query(Criteria.where("_id").is(imageFileId)));
//		
//		// Save file back to local disk
//		imageFile.writeTo("D:\\JSA\\retrieve\\jsa-logo.png");
		
		GridFsResource[] txtFiles = gridOperations.getResources("*.txt");
//		txtFiles[0].getInputStream().
		System.out.println("Image File Name:" + imageFile.getFilename());
		
		return "Done";
	}
	
	@GetMapping("/retrieve/textfiles")
	public String retrieveTextFiles(){
		/**
		 * get all data files then save to local disk
		 */
		
		// Retrieve all data files
		List<GridFSDBFile> textFiles =new LinkedList<>();
				gridOperations.find(new Query(Criteria.where("metadata.type").is("data")));
		
		// Save all back to local disk
		textFiles.forEach(file->{
			
			try {
				String fileName = file.getFilename();
				
				file.writeTo("D:\\JSA\\retrieve\\"+ fileName);
				
				System.out.println("Text File Name: " + fileName);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		return "Done";
	}
	
	@GetMapping("/delete/image")
	public String deleteFile(){
		// delete image via id
		gridOperations.delete(new Query(Criteria.where("_id").is(imageFileId)));
		
		return "Done";
	}
}
