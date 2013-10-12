import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.amazonaws.services.s3.model.MultiObjectDeleteException.DeleteError;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3Utils
{
	private Credentials localFile;
	private AWSCredentials awsCredentials;
	private AmazonS3Client s3client;
	private static ArrayList<KeyVersion> bucketList;
	private String bucketName;

	public S3Utils(String bucketName)
	{
		this.localFile = new Credentials();
		this.awsCredentials = this.localFile.loadMyCredentials();
		this.s3client = new AmazonS3Client(this.awsCredentials);
		this.bucketList = new ArrayList<KeyVersion>();
		this.bucketName = bucketName;
	}

	public void createBucket()
	{
		if (!this.s3client.doesBucketExist(this.bucketName))
		{
			final CreateBucketRequest bucketReq = new CreateBucketRequest(
					this.bucketName);
			try
			{
				this.s3client.createBucket(bucketReq);
				System.out.println("Bucket " + this.bucketName
						+ " succesfully created");
			}
			catch (AmazonClientException e)
			{
				System.out.println("Something went wrong");
				System.err.println("Caught AmazonClientException: "
						+ e.getMessage());
			}
		}
		else
		{
			System.out
					.println("A bucket with the Name you supplied already exists");
		}
	}

	public void deleteBucket()
	{
		if (!this.s3client.doesBucketExist(this.bucketName))
		{
			System.out
					.println("The bucket you requested to delete does not seem to exist. Please check if you have already deleted this bucket");
		}
		else
		{
			final DeleteBucketRequest bucketDel = new DeleteBucketRequest(
					this.bucketName);
			try
			{
				this.s3client.deleteBucket(bucketDel);
				System.out.println("Bucket " + this.bucketName
						+ " succesfully deleted");
			}
			catch (AmazonClientException e)
			{
				System.out.println("Something went wrong");
				System.err.println("Caught AmazonClientException: "
						+ e.getMessage());
			}
		}
	}

	public void createFolder(String foldername, String suffix)
	{
		if (this.s3client.doesBucketExist(this.bucketName))
		{
			// Create metadata for your folder & set content-length to 0
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(0);
			// Create empty content
			InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
			// Create a PutObjectRequest passing the foldername suffixed by /
			PutObjectRequest putObjectRequest = new PutObjectRequest(
					this.bucketName, foldername + suffix, emptyContent,
					metadata);
			// Send request to S3 to create folder
			try
			{
				this.s3client.putObject(putObjectRequest);
				System.out.println(foldername + " => created");
			}
			catch (AmazonClientException e)
			{
				System.out.println("Something went wrong");
				System.err.println("Caught AmazonClientException: "
						+ e.getMessage());
			}
		}
		else
		{
			// System.out.println("Bucket does not exist");
		}
	}

	public void uploadFile(String key, File file)
	{
		if (this.s3client.doesBucketExist(this.bucketName))
		{
			try
			{
				// Grant Public Read Permissions
				PutObjectRequest objReq = new PutObjectRequest(this.bucketName,
						key, file)
						.withCannedAcl(CannedAccessControlList.PublicRead);
				// Upload
				this.s3client.putObject(objReq);
				System.out.println(key + " succesfully uploaded");
			}
			catch (AmazonClientException e)
			{
				System.out.println("Something went wrong");
				System.err.println("Caught AmazonClientException: "
						+ e.getMessage());
			}
		}
		else
		{
			System.out.println("Bucket does not exist");
		}
	}

	public void getBucketList()
	{
		if (this.s3client.doesBucketExist(this.bucketName))
		{
			System.out.println("==\nRetrieving list of all files in bucket:");
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
					.withBucketName(this.bucketName).withPrefix("");
			ObjectListing objectListing;

			do
			{
				objectListing = this.s3client.listObjects(listObjectsRequest);
				for (S3ObjectSummary objectSummary : objectListing
						.getObjectSummaries())
				{
					this.bucketList.add(new KeyVersion(objectSummary.getKey()));
					System.out.println(" - " + objectSummary.getKey() + "  "
							+ "(size = " + objectSummary.getSize()
							+ ") => Queued for Deletion");
				}
				listObjectsRequest.setMarker(objectListing.getNextMarker());
			}
			while (objectListing.isTruncated());
			;
		}
		else
		{
			System.out.println("Bucket does not exist");
		}
	}

	public void wipeOut()
	{
		if (this.s3client.doesBucketExist(this.bucketName))
		{
			System.out.println("==\nwiping out:");
			DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(
					this.bucketName);

			multiObjectDeleteRequest.setKeys(this.bucketList);

			try
			{
				DeleteObjectsResult delObjRes = this.s3client
						.deleteObjects(multiObjectDeleteRequest);
				System.out.format("Successfully deleted all the %s items.\n",
						delObjRes.getDeletedObjects().size());

			}
			catch (MultiObjectDeleteException e)
			{
				System.out.format("%s \n", e.getMessage());
				System.out.format("No. of objects successfully deleted = %s\n",
						e.getDeletedObjects().size());
				System.out.format("No. of objects failed to delete = %s\n", e
						.getErrors().size());
				System.out.format("Printing error data...\n");
				for (DeleteError deleteError : e.getErrors())
				{
					System.out.format("Object Key: %s\t%s\t%s\n",
							deleteError.getKey(), deleteError.getCode(),
							deleteError.getMessage());
				}
			}
		}
		else
		{
			System.out.println("Bucket does not exist");
		}
	}

	public void deleteBucketContents()
	{
		if (this.s3client.doesBucketExist(this.bucketName))
		{
			System.out.println("==\nDeletion Operation Start:");
			this.getBucketList();
			this.wipeOut();
		}
		else
		{
			System.out.println("Bucket does not exist");
		}
	}

	// Utils
	public void downloadImage(String httpaddress, String fileName)
	{
		String fileType = httpaddress.substring(httpaddress.length() - 3,
				httpaddress.length());
		BufferedImage image = null;
		try
		{
			URL url = new URL(httpaddress);
			// read the url
			image = ImageIO.read(url);
			// Switches with strings only allowed in JDK 7 not 6 (as far as I
			// remember)
			ImageIO.write(image, fileType, new File("./src/assets/" + fileName));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
