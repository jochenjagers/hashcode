package gingerninjas.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ZipUtil
{
	private static final Logger logger = LogManager.getLogger(ZipUtil.class);
	
	public static void zip(File folder, File output, boolean includeParentFolder, FileFilter filter)
	{
		if(filter == null)
			filter = PACKAGE_FILEFILTER;

		File root = folder.getAbsoluteFile();
		String rootFolder = (includeParentFolder ? root.getParentFile().getAbsolutePath() : root.getAbsolutePath());

		logger.info("creating " + output.getName() + " of folder " + folder.getAbsolutePath());

		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try
		{
			fos = new FileOutputStream(output);
			zos = new ZipOutputStream(fos);

			addFiles(zos, rootFolder, root.listFiles(filter), filter);

			zos.closeEntry();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(zos != null)
			{
				try
				{
					zos.flush();
					zos.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			if(fos != null)
			{
				try
				{
					fos.flush();
					fos.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private static void addFiles(ZipOutputStream zos, String rootFolder, File[] files, FileFilter filter)
	{
		byte[] buffer = new byte[1024];
		FileInputStream in = null;
		ZipEntry ze;
		
		for(File f : files)
		{
			if(f.isDirectory())
			{
				addFiles(zos, rootFolder, f.listFiles(filter), filter);
			}
			else
			{
				ze = getZipEntry(f, rootFolder);
				try
				{
					zos.putNextEntry(ze);

					try
					{
						in = new FileInputStream(f);
						int len;
						while((len = in.read(buffer)) > 0)
						{
							zos.write(buffer, 0, len);
						}
					}
					finally
					{
						in.close();
					}

					logger.debug("  zipping " + ze.getName() + " ... OK");
					
				}
				catch(IOException e)
				{
					logger.warn("  zipping " + ze.getName() + " ... ERROR");
				}
			}
		}
	}

	private static ZipEntry getZipEntry(File file, String rootFolder)
	{
		return new ZipEntry(file.getAbsolutePath().substring(rootFolder.length() + 1));
	}

	public static final FileFilter PACKAGE_FILEFILTER = new FileFilter() {
		@Override
		public boolean accept(File pathname)
		{
			if(pathname.isDirectory() && pathname.getName().equalsIgnoreCase("bin"))
				return false;
			if(pathname.isDirectory() && pathname.getName().equalsIgnoreCase(".svn"))
				return false;
			if(pathname.isFile() && pathname.getName().endsWith(".zip"))
				return false;
			if(pathname.isFile() && pathname.getName().endsWith(".pdf"))
				return false;
			if(pathname.isFile() && pathname.getName().endsWith(".log"))
				return false;
			if(pathname.isFile() && pathname.getName().endsWith(".out"))
				return false;
			if(pathname.isFile() && pathname.getName().endsWith(".in"))
				return false;
			
			return true;
		}

	};

	public static void main(String[] args)
	{
		// quick test
		ZipUtil.zip(new File(""), new File(System.currentTimeMillis() + "_without_root.zip"), false, null);
		ZipUtil.zip(new File(""), new File(System.currentTimeMillis() + "_with_root.zip"), true, null);
	}
}
