package gingerninjas.qualification;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import gingerninjas.BaseInput;

public class Input extends BaseInput
{
	List<Photo> photos;
	
	
	public Input(File path, String name) throws IOException
	{
		super(path, name);
	}

	
	public List<Photo> getPhotos() {
		return photos;
	}


	@Override
	protected void parse(BufferedReader reader) throws IOException
	{
		String line = null;

		line = reader.readLine();
		
		int rows = Integer.parseInt(line);
		this.photos = new ArrayList<Photo>(rows);
				
		for(int r = 0; r < rows; r++)
		{
			line = reader.readLine();
			logger.debug("endpoint: " + line);
			ArrayList<String> splittedLine = new ArrayList<>(Arrays.asList(line.split(" ")));
			
			
			char orientation = splittedLine.get(0).charAt(0);
			// remove orientation
			splittedLine.remove(0);
			// remove tag count
			splittedLine.remove(0);
			
			this.photos.add(new Photo(r, orientation, splittedLine));
			
		}
	}

	public void reset()
	{
	}

	@Override
	public String toString()
	{
		return "Input [#photos "+this.photos.size()+"]";
	}

	public static void main(String[] args) throws IOException
	{
		
		
		Input test = new Input(new File("Online Qualification Round/"), "A - Example");
		
		LogManager.getLogger().info("A - Example Size: " + test.photos.size());
		for(Photo p : test.photos) {
			LogManager.getLogger().info(p);
		}
	}
}
