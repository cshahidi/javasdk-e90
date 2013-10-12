import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import com.amazonaws.services.simpledb.model.ReplaceableAttribute;

public class main
{
	public static void main(String[] args)
	{
		final String bucketName = "trialbucket-09876";
		final String bucketUrl = "https://s3.amazonaws.com/" + bucketName;
		final String suffix = "/";

		// Folders to be created within bucket
		String starsFolder = "stars";
		String nobelistsFolder = "nobelists";
		String imagesFolderName = "images";
		String resumesFolderName = "resumes";
		String starsImagesFolder = starsFolder + suffix + imagesFolderName;
		String starsResumesFolder = starsFolder + suffix + resumesFolderName;
		String nobelistsImagesFolder = nobelistsFolder + suffix
				+ imagesFolderName;
		String nobelistsResumesFolder = nobelistsFolder + suffix
				+ resumesFolderName;
		// put them in an array (TODO:automate this)
		String[] folderNames =
		{
				starsFolder, nobelistsFolder, starsImagesFolder,
				starsResumesFolder, nobelistsImagesFolder,
				nobelistsResumesFolder
		};
		// Simple DB Domains
		String starDomainName = starsFolder;
		String nobelDomainName = nobelistsFolder;

		String j = "jolie";
		String a = "allen";
		String p = "pauling";
		String f = "feynman";
		String n = "neruda";
		String txt = ".txt";
		String jpg = ".jpg";
		final ArrayList<Person> population = new ArrayList<Person>();
		final Star jolie = new Star(
				"Angelina Jolie",
				j + jpg,
				j + txt,
				"http://lissarankin.com/wp-content/uploads/2013/05/angelina-jolie.jpg",
				"Mr and Mrs Smith");
		final Star allen = new Star(
				"Woody Allen",
				a + jpg,
				a + txt,
				"http://foglobe.com/data_images/main/woody-allen/woody-allen-01.jpg",
				"Manhattan");
		final Nobelist pauling = new Nobelist(
				"Linus Pauling",
				p + jpg,
				p + txt,
				"http://upload.wikimedia.org/wikipedia/commons/5/58/L_Pauling.jpg",
				"Linus Pauling and the Golden Pistacchio", "1954 & 1962",
				"Chemistry and Peace");
		final Nobelist feynman = new Nobelist(
				"Richard Feynman",
				f + jpg,
				f + txt,
				"http://upload.wikimedia.org/wikipedia/en/4/42/Richard_Feynman_Nobel.jpg",
				"Surely you are joking mr Feynman", "1965", "Physics");
		final Nobelist neruda = new Nobelist(
				"Pablo Neruda",
				n + jpg,
				n + txt,
				"http://upload.wikimedia.org/wikipedia/commons/0/04/Pablo_Neruda.jpg",
				"Twenty Love Poems", "1971", "Literature");
		population.add(jolie);
		population.add(allen);
		population.add(pauling);
		population.add(feynman);
		population.add(neruda);

		String[] picFiles = new String[population.size()];
		String[] textFiles = new String[population.size()];
		String[] httpaddress = new String[population.size()];
		String[] picAndTxtFiles = new String[2 * population.size()];

		for (int i = 0; i < 2 * population.size(); i++)
		{
			if (i < 5)
			{
				picFiles[i] = population.get(i).sourcePicPath;
				textFiles[i] = population.get(i).sourceResPath;
				httpaddress[i] = population.get(i).downloadUrl;
				picAndTxtFiles[i] = population.get(i).sourcePicPath;
			}
			else
			{
				picAndTxtFiles[i] = population.get(i - population.size()).sourceResPath;
			}
		}

		String[] filePaths = new String[picAndTxtFiles.length];
		File[] files = new File[picAndTxtFiles.length];
		Hashtable<String, File> uploadTable = new Hashtable<String, File>();
		Hashtable<String, ArrayList<ReplaceableAttribute>> tableOfAttributes = new Hashtable<String, ArrayList<ReplaceableAttribute>>();
		tableOfAttributes.put(j, new ArrayList<ReplaceableAttribute>());
		tableOfAttributes.put(a, new ArrayList<ReplaceableAttribute>());
		tableOfAttributes.put(p, new ArrayList<ReplaceableAttribute>());
		tableOfAttributes.put(f, new ArrayList<ReplaceableAttribute>());
		tableOfAttributes.put(n, new ArrayList<ReplaceableAttribute>());

		S3Utils s3Utils = new S3Utils(bucketName);
		SDBUtils sDBUtils = new SDBUtils();
		Properties properties = new Properties();
		final Hashtable<String, Star> moviestars = new Hashtable<String, Star>();

		makeAStar(j, moviestars, jolie, tableOfAttributes.get(j), properties,
				bucketUrl, folderNames, suffix, picFiles, textFiles,
				starsImagesFolder, starsResumesFolder, 0);
		makeAStar(a, moviestars, allen, tableOfAttributes.get(a), properties,
				bucketUrl, folderNames, suffix, picFiles, textFiles,
				starsImagesFolder, starsResumesFolder, 1);

		final Hashtable<String, Nobelist> nobelistas = new Hashtable<String, Nobelist>();
		makeANobelist(p, nobelistas, pauling, tableOfAttributes.get(p),
				properties, bucketUrl, folderNames, suffix, picFiles,
				textFiles, nobelistsImagesFolder, nobelistsResumesFolder, 2);
		makeANobelist(f, nobelistas, feynman, tableOfAttributes.get(f),
				properties, bucketUrl, folderNames, suffix, picFiles,
				textFiles, nobelistsImagesFolder, nobelistsResumesFolder, 3);
		makeANobelist(n, nobelistas, neruda, tableOfAttributes.get(n),
				properties, bucketUrl, folderNames, suffix, picFiles,
				textFiles, nobelistsImagesFolder, nobelistsResumesFolder, 4);
		System.out.println("PROBLEM 1");
		problem1(s3Utils, suffix);
		System.out.println("PROBLEM 2");
		problem2(s3Utils);
		System.out.println("PROBLEM 3");
		System.out.println("PART 1: POPULATING BUCKET");
		problem3(s3Utils, suffix, filePaths, picAndTxtFiles, files,
				uploadTable, folderNames, httpaddress);
		System.out.println("PART 2: POPULATING SDB");

		sDBUtils.createSDBDomain(starDomainName);
		sDBUtils.createSDBDomain(nobelDomainName);
		sDBUtils.listDomains();

		sDBUtils.putAttributes(jolie.name, tableOfAttributes.get(j),
				starDomainName);
		sDBUtils.putAttributes(allen.name, tableOfAttributes.get(a),
				starDomainName);
		sDBUtils.putAttributes(pauling.name, tableOfAttributes.get(p),
				nobelDomainName);
		sDBUtils.putAttributes(feynman.name, tableOfAttributes.get(f),
				nobelDomainName);
		sDBUtils.putAttributes(neruda.name, tableOfAttributes.get(n),
				nobelDomainName);
		for (int i = 0; i < 5; i++)
		{
			if (i < 4)
			{
				System.out.println(sDBUtils
						.getAttributesList(jolie.name, starDomainName)
						.getAttributes().get(i).getName()
						+ ": "
						+ sDBUtils
								.getAttributesList(jolie.name, starDomainName)
								.getAttributes().get(i).getValue());
			}
		}
		// System.out.println("==\nCLEANING UP");

		// cleanup(s3Utils, sDBUtils, starDomainName, nobelDomainName);
	}

	public static void problem1(S3Utils s3Utils, String suffix)
	{
		// PROBLEM 1 (Check S3Utils.java)
		s3Utils.createFolder("someFolder", suffix);
	}

	public static void problem2(S3Utils s3Utils)
	{
		// PROBLEM 2 (Check S3Utils.java)
		s3Utils.deleteBucketContents();
	}

	public static void problem3(S3Utils s3Utils, String suffix,
			String[] filePaths, String[] keys, File[] files,
			Hashtable<String, File> table, String[] folderNames,
			String[] httpaddress)
	{
		// PROBLEM 3

		// Populate our files
		for (int i = 0; i < files.length; i++)
		{
			filePaths[i] = "./src/assets/" + keys[i];
			final File file = new File(filePaths[i]);
			files[i] = file;
			// do not download for indeces higher than five as there
			// is nothing to download
			if (i < 5)
			{
				try
				{
					s3Utils.downloadImage(httpaddress[i], keys[i]);
				}
				catch (Exception e)
				{
					System.out.println("no more downloading required");
					continue;
				}
			}
			table.put(keys[i], files[i]);
		}
		// create the bucket
		s3Utils.createBucket();
		// create the folders
		for (int i = 0; i < folderNames.length; i++)
		{
			s3Utils.createFolder(folderNames[i], suffix);
		}
		// populate the folders
		for (int i = 0; i < table.size(); i++)
		{

			if (i < 2)
			{
				s3Utils.uploadFile(folderNames[2] + suffix + keys[i],
						table.get(keys[i]));
			}
			else if (i < 5)
			{
				s3Utils.uploadFile(folderNames[4] + suffix + keys[i],
						table.get(keys[i]));
			}
			else if (i < 7)
			{
				s3Utils.uploadFile(folderNames[3] + suffix + keys[i],
						table.get(keys[i]));
			}
			else if (i < 10)
			{
				s3Utils.uploadFile(folderNames[5] + suffix + keys[i],
						table.get(keys[i]));
			}
		}
	}

	public static void cleanup(S3Utils s3Utils, SDBUtils sDBUtils,
			String starDomainName, String nobelDomainName)
	{
		// recursively delete everything in the bucket prob2
		problem2(s3Utils);
		// delete the bucket
		s3Utils.deleteBucket();
		// Delete all SimpleDB Domains
		sDBUtils.deleteSDBDomain(starDomainName);
		sDBUtils.deleteSDBDomain(nobelDomainName);
	}

	public static void makeANobelist(String tmpNameString,
			Hashtable<String, Nobelist> nobelistas, Nobelist person,
			ArrayList<ReplaceableAttribute> attributes,
			Properties nobelistProperties, String bucketUrl,
			String[] folderNames, String suffix, String[] picFiles,
			String[] textFiles, String starsImagesFolder,
			String starsResumeFolder, int sourceFileIndex)
	{
		nobelistas.put(tmpNameString, person);
		person.setPictureUrl(bucketUrl + starsImagesFolder + suffix
				+ picFiles[sourceFileIndex]);
		person.setResumeUrl(bucketUrl + suffix + starsResumeFolder + suffix
				+ textFiles[sourceFileIndex]);
		attributes.add(new ReplaceableAttribute(nobelistProperties.fullName,
				nobelistas.get(tmpNameString).name, true));
		attributes.add(new ReplaceableAttribute(nobelistProperties.picUrl,
				nobelistas.get(tmpNameString).pictureUrl, true));
		attributes.add(new ReplaceableAttribute(nobelistProperties.resUrl,
				nobelistas.get(tmpNameString).resumeUrl, true));
		attributes.add(new ReplaceableAttribute(
				nobelistProperties.mostPopularBook, nobelistas
						.get(tmpNameString).bestBook, true));
		attributes.add(new ReplaceableAttribute(
				nobelistProperties.fieldOfNobel,
				nobelistas.get(tmpNameString).nobelPrize, true));
		attributes.add(new ReplaceableAttribute(nobelistProperties.nobelYear,
				nobelistas.get(tmpNameString).nobelYear, true));
	}

	public static void makeAStar(String tmpNameString,
			Hashtable<String, Star> moviestars, Star person,
			ArrayList<ReplaceableAttribute> attributes,
			Properties starProperties, String bucketUrl, String[] folderNames,
			String suffix, String[] picFiles, String[] textFiles,
			String starsImagesFolder, String starsResumeFolder,
			int sourceFileIndex)
	{
		moviestars.put(tmpNameString, person);
		person.setPictureUrl(bucketUrl + starsImagesFolder + suffix
				+ picFiles[sourceFileIndex]);
		person.setResumeUrl(bucketUrl + suffix + starsResumeFolder + suffix
				+ textFiles[sourceFileIndex]);
		attributes.add(new ReplaceableAttribute(starProperties.fullName,
				moviestars.get(tmpNameString).name, true));
		attributes.add(new ReplaceableAttribute(starProperties.picUrl,
				moviestars.get(tmpNameString).pictureUrl, true));
		attributes.add(new ReplaceableAttribute(starProperties.resUrl,
				moviestars.get(tmpNameString).resumeUrl, true));
		attributes.add(new ReplaceableAttribute(
				starProperties.mostPopularMovie,
				moviestars.get(tmpNameString).movie, true));
	}
}
