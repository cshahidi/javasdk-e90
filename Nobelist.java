public class Nobelist extends Person
{
	public String name;
	public String pictureUrl;
	public String resumeUrl;
	public String bestBook;
	public String nobelYear;
	public String nobelPrize;
	public String sourcePicPath;
	public String sourceResPath;
	public String downloadUrl;

	public Nobelist(String name, String sourcePicPath, String sourceResPath,
			String downloadUrl, String bestBook, String nobelYear,
			String nobelPrize)
	{
		super(name, sourcePicPath, sourceResPath, downloadUrl);
		this.name = name;
		this.bestBook = bestBook;
		this.nobelYear = nobelYear;
		this.nobelPrize = nobelPrize;
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
