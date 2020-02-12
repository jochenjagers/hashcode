package gingerninjas.util.uploader;

import org.json.JSONObject;

public class DataSet
{
	private String	id;
	private String	name;
	private String	inputBlobKey;
	private int		highScore;

	public DataSet(String name) {
		this.name = name;
	}
	
	public DataSet(JSONObject obj)
	{
		this.id = obj.getString("id");
		this.name = obj.getString("name").replace('–', '-');
		this.inputBlobKey = obj.getString("inputBlobKey");
		this.highScore = 0;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getInputBlobKey()
	{
		return inputBlobKey;
	}

	public int getHighScore()
	{
		return highScore;
	}

	public void setHighScore(int highScore)
	{
		if(highScore > this.highScore)
		{
			this.highScore = highScore;
		}
	}

	public String toString()
	{
		String result = "";
		result += "ID:   " + this.id + System.lineSeparator();
		result += "Name: " + this.name + System.lineSeparator();
		result += "Key:  " + this.inputBlobKey + System.lineSeparator();
		result += "Highscore:  " + this.highScore + System.lineSeparator();
		return result;
	}

	// {
	// "id": "6310624841695232",
	// "name": "Big",
	// "inputBlobKey":
	// "blob/AMIfv97_hBWBh1Qm2tpWdO8XtKGfQTND8vSM01xTW5ewmQDh6OOwvyr0QNX8dXmAj5niWZQGsCMoRr75UhOL-WcPGFXFb53IOLXdmJ4zd7V8W7-q4LFvdbtvCZenYsvUtAfwQARYWjxwl-W8Nkg1w6bPVvnheWHK7W6MiFAZBdv205enKUXvOoYjD9cwC7KPmZKTcalgXRkvt5IHTxQcLygSgpyco1vlJBu86Erz-EQw2fwkdzQL-6rKE-R9wahwydieJmUzW6z17uH-0Jpr9UuIDINC3zb3dc23GqLL99TVc4-Th_-X-SFaNiLRZuyOTJ2JXGhUUIECse6wcH2WoN_7g-nMnf2f_-7VHIAO9NzmhnSbnoy5JeiAMGyTwXFtViSnU45jVW6P"
	// },
}
