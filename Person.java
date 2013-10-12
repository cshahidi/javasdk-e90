public class Person
{
	public String name;
	public String pictureUrl;
	public String resumeUrl;
	public String sourcePicPath;
	public String sourceResPath;
	public String downloadUrl;

	public Person(String name, String sourcePicPath, String sourceResPath,
			String downloadUrl)
	{
		this.name = name;
		this.sourcePicPath = sourcePicPath;
		this.sourceResPath = sourceResPath;
		this.downloadUrl = downloadUrl;
	}

	public void setPictureUrl(String url)
	{
		this.pictureUrl = url;
	}

	public void setResumeUrl(String url)
	{
		this.resumeUrl = url;
	}
}
