package gingerninjas.qualification.jochen;

import gingerninjas.qualification.CacheServer;
import gingerninjas.qualification.Video;

class ServerVideoPair
{
	Video		v;
	CacheServer	s;
	float		p;

	public ServerVideoPair() {
		this.v = null;
		this.s = null;
		this.p = 0;
	}
	
	public ServerVideoPair(Video v, CacheServer s, float p)
	{
		this.v = v;
		this.s = s;
		this.p = p;
	}

	public String toString()
	{
		return s.toString() + " - " + v.toString();
	}

	@Override
	public int hashCode()
	{
		
		return (v.getId() << 16) + s.getId();
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ServerVideoPair other = (ServerVideoPair) obj;
		
		if(s == null)
		{
			if(other.s != null)
				return false;
		}
		else if(!s.equals(other.s))
			return false;
		if(v == null)
		{
			if(other.v != null)
				return false;
		}
		else if(!v.equals(other.v))
			return false;
		return true;
	}
	
	
}