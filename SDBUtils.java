import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.simpledb.model.InvalidParameterValueException;
import com.amazonaws.services.simpledb.model.MissingParameterException;
import com.amazonaws.services.simpledb.model.NoSuchDomainException;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;

public class SDBUtils
{
	private Credentials localFile;
	private AWSCredentials awsCredentials;
	private AmazonSimpleDBClient sdbClient;
	private CreateDomainRequest createDomainRequest;
	private DeleteDomainRequest deleteDomainRequest;
	private GetAttributesRequest getAttributesRequest;
	private PutAttributesRequest putAttributesRequest;

	public SDBUtils()
	{
		this.localFile = new Credentials();
		this.awsCredentials = localFile.loadMyCredentials();
		this.sdbClient = new AmazonSimpleDBClient(this.awsCredentials);
		this.createDomainRequest = new CreateDomainRequest();
		this.deleteDomainRequest = new DeleteDomainRequest();
	}

	public void createSDBDomain(String domainName)
	{
		this.createDomainRequest.setDomainName(domainName);
		this.sdbClient.createDomain(this.createDomainRequest);
	}

	public void deleteSDBDomain(String domainName)
	{
		this.deleteDomainRequest.setDomainName(domainName);
		this.sdbClient.deleteDomain(this.deleteDomainRequest);
	}

	public GetAttributesResult getAttributesList(String itemName,
			String domainName)
	{
		this.getAttributesRequest = new GetAttributesRequest(domainName,
				itemName);
		this.getAttributesRequest.setDomainName(domainName);
		try
		{
			return this.sdbClient.getAttributes(this.getAttributesRequest);
		}
		catch (InvalidParameterValueException e)
		{
			System.out.println("InvalidParameterValueException");
			return null;
		}
		catch (NoSuchDomainException e)
		{
			System.out.println("NoSuchDomainException");
			return null;
		}
		catch (MissingParameterException e)
		{
			System.out.println("MissingParameterException");
			return null;
		}
		catch (AmazonClientException e)
		{
			System.out.println("AmazonClientException");
			return null;
		}
	}

	public void putAttributes(String itemName,
			List<ReplaceableAttribute> attributes, String domainName)
	{
		this.putAttributesRequest = new PutAttributesRequest(domainName,
				itemName, attributes);
		this.sdbClient.putAttributes(this.putAttributesRequest);
	}

	public void listDomains()
	{
		System.out.println(this.sdbClient.listDomains());
	}
}
