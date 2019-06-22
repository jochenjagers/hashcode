package gingerninjas.util.uploader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Round
{

	// "id": "6553823069863936",
	// "name": "Practice round",
	// "problemBlobKey":
	// "blob/AMIfv97cozAcShntqpmmqLLItQLQR5Gx0uCfLT8qilEoM1LUyWEmtRMB04aXFPmyjs1cQ7clc0Hh6xbLkMMIFCXDF7kqiStIL3j1h94EoV8rE-lJ0AVa23bUNN31XAFI8sWXhXBHvqzbhC5RvuBnXoz5ucIIVpdJqR9Yn9eZSRDyR43B5_MNaZK-2buSPtolq5J9UKDmcgwAK65qpQcX7chUzCYbnarWKYgankvh424L-po2N1pjY3WNF-yqidXIsmYn2WWpwBvpqrxhJ7dODo3B-yjoCQhGurnyQ3n5idxg6qgjvaFDJ0hWtVnm8HX_3-9u7oN74qPQMBW2zusDr03QF_dOvNas0-Qm75435WFHboFkwKfizFXed7nzWjbY4kRYOypKfo6b",
	// "scorerLabel": "pizza",
	// "hideScoreboard": true,
	// "adminOnly": false,
	// "start": "2017-01-04T14:00:00.000Z",
	// "end": "2017-02-23T17:30:00.000Z",
	// "dataSets": [
	// {
	// "id": "6310624841695232",
	// "name": "Big",
	// "inputBlobKey":
	// "blob/AMIfv97_hBWBh1Qm2tpWdO8XtKGfQTND8vSM01xTW5ewmQDh6OOwvyr0QNX8dXmAj5niWZQGsCMoRr75UhOL-WcPGFXFb53IOLXdmJ4zd7V8W7-q4LFvdbtvCZenYsvUtAfwQARYWjxwl-W8Nkg1w6bPVvnheWHK7W6MiFAZBdv205enKUXvOoYjD9cwC7KPmZKTcalgXRkvt5IHTxQcLygSgpyco1vlJBu86Erz-EQw2fwkdzQL-6rKE-R9wahwydieJmUzW6z17uH-0Jpr9UuIDINC3zb3dc23GqLL99TVc4-Th_-X-SFaNiLRZuyOTJ2JXGhUUIECse6wcH2WoN_7g-nMnf2f_-7VHIAO9NzmhnSbnoy5JeiAMGyTwXFtViSnU45jVW6P"
	// },
	// {
	// "id": "5708411572322304",
	// "name": "Example",
	// "inputBlobKey":
	// "blob/AMIfv96pt6kItDdj5u97ekc--tGvbtnxHVL7WFedIN5Vh6rxsaUSJIkmkGFZerSU-AOSInxnHqLI3Q2WTACj6sjJzvObWnkHmeUnJnS2y1arbSi461I_4YAha1_Ji-PN87ZiMsSszfKwt65FghHuoqYDIRY8Jg-4C2eU5y8BKJCbmzj1dNzkA2xUzNvU3gOreYGdA_bgYnawmHFVVl8Mtbq3ayn1PfLemYz47uyWZ8IMvVauSe7XkBRWvKD5g6CZ0RvU3NlTC1FEqLJGGbR8KGmsyzAEJ6LE5DHtOT1keynRkfv0lnkcUNRzvasWWfBy-eQ_ATFkK7K0smSGSMTX5N6qhTpew8JFqqPqej6J1e23YS4QbqegufeI_DZN9hcqjklax4vv050_"
	// },
	// {
	// "id": "5184724934852608",
	// "name": "Medium",
	// "inputBlobKey":
	// "blob/AMIfv94N5oO1SVwAVDowWI6yssQTHGJsX3TyzzN9vmC1YPbrRXMVQCzo_1AFHYn94n8YS6XnLYcpRndoI0LZFZjgEGGvxFgWi5FsUIeEK9i1WeBsYKiW7f-_pNaZTqhTGjfn20jUcvhC99_g3IPRWTMoDUqC1dkNrmCoboeCNPAoiW2ZmGz22w8xImsNN38AYMtfimdVZYPhHFg65PuO-C-JD92vZYJh1pWe1eeu605y98OAfniPOzGSei_4C__Hoa329nPUHNbD2wBlDd6BaCQkZnb9vj0QOzViwkbvRg2zeOb58Fv6fJTE30yGgQ6Di7Jv7UWHY_l_6HiBTXwzrI3EA0Cl_10bK7Jun-LLXf0hi3Of3rJoOXs5cWfiWcBPF7aZ9rZiLo4y"
	// },
	// {
	// "id": "6050554908246016",
	// "name": "Small",
	// "inputBlobKey":
	// "blob/AMIfv94KVwZHwXdoM4MFOpzd-0GU7RgvOGAM9XOC_z1qiLvndyMM6tg9wSxgrgvDE9ZyTe4tq-LauutBDziThf5w8ibVi_OKIJf44-RRL9682T1T9LjTv43G7HoYEfbJENnpIS7BcbTZ7i2ouKHmYUOSt5Qi3GQQxbn9r17QLK-L2Vwi0cAHi0XdI2zrEupFjAF5fFstO4Jfk4I2qpBoS-PZ3qOV1ukIlwdy45VAj-_4O1kHqxXiJa-j5yLDEtlvBxq8wAgea3G10_fw2aiU6aCBkMOORg-CAslFBuYBU_fLeWttD2-5EcCTmgo0yeKr2ggXk8pTMKzluZkZPeQCEv-OjmgJuSDNM5nQzl-ykGX20AhmrJKx2gUFHh9jM4e4osRTDNXmnppa"
	// }

	private String				id;
	private String				name;
	private String				problemBlobKey;
	private ArrayList<DataSet>	datasets;
	private Date				start;
	private Date				end;

	public Round(String name, String... dataSets) {
		this.name = name;
		this.datasets = new ArrayList<DataSet>();
		this.start = new Date(0);
		this.end = new Date(2100,1,1);
		for(String d : dataSets) {
			this.datasets.add(new DataSet(d));
		}
	}
	
	public Round(JSONObject obj)
	{
		this.datasets = new ArrayList<>();

		try
		{
			this.id = obj.getString("id");
			this.name = obj.getString("name");
			this.problemBlobKey = obj.getString("problemBlobKey");

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			this.start = format.parse(obj.getString("start"));
			this.end = format.parse(obj.getString("end"));

			JSONArray datasets = obj.getJSONArray("dataSets");
			for(int i = 0; i < datasets.length(); ++i)
			{
				this.datasets.add(new DataSet(datasets.getJSONObject(i)));
			}
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		catch(ParseException e)
		{
			e.printStackTrace();
		}
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getProblemBlobKey()
	{
		return problemBlobKey;
	}

	public ArrayList<DataSet> getDatasets()
	{
		return datasets;
	}

	public String toString()
	{
		String result = "";
		result += "ID:       " + this.id + System.lineSeparator();
		result += "Name:     " + this.name + System.lineSeparator();
		result += "Key:      " + this.problemBlobKey + System.lineSeparator();
		result += "Running:  " + this.isRunning() + System.lineSeparator();
		result += "Datasets: " + System.lineSeparator();
		for(DataSet d : this.datasets)
		{
			result += d.toString() + System.lineSeparator();
		}
		return result;
	}
	
	public boolean isRunning() {
		Date now = new Date();
		now.setHours(now.getHours()-1);
		return (now.after(this.start) && now.before(this.end));
	}

}
