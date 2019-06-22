package gingerninjas.qualification;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import gingerninjas.BaseOutput;

public class Output extends BaseOutput
{
	protected Collection<CacheServer> servers;

	public Output(String name, Map<String, Object> args)
	{
		super(name, args);
	}

	public Collection<CacheServer> getServers()
	{
		return servers;
	}

	public void setServers(Collection<CacheServer> servers)
	{
		this.servers = servers;
	}

	@Override
	protected void write(BufferedWriter r) throws IOException
	{
		r.write(servers.size() + "");
		for(CacheServer s : servers)
		{
			String line = s.getId() + "";
			for(Video v : s.getVideos())
			{
				line += (" " + v.getId());
			}
			r.write("\n" + line);
		}
	}
}
