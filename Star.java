public class Star extends Person
{
	public String name;
	public String pictureUrl;
	public String resumeUrl;
	public String sourcePicPath;
	public String sourceResPath;
	public String downloadUrl;
	public String movie;

	public Star(String starName, String sourcePicPath, String sourceResPath,
			String downloadUrl, String bestMovie)
	{
		super(starName, sourcePicPath, sourceResPath, downloadUrl);
		this.name = starName;
		this.sourcePicPath = sourcePicPath;
		this.sourceResPath = sourceResPath;
		this.downloadUrl = downloadUrl;
		this.movie = bestMovie;
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
